package ma.uir.awd.metier.ml;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.model.ModelMetrics;
import ma.uir.awd.model.ModelType;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.supervised.instance.ClassBalancer;

/**
 * Le cœur du composant ML, avec Weka (05_ML/Conception_ML_prolongation.md §4).
 * - Aucun accès à la base ni au web : il reçoit des ExtensionSample déjà lus par les DAO (via le métier).
 * - J48 (arbre de décision) dans un FilteredClassifier + ClassBalancer : les classes sont rééquilibrées
 *   pendant l'entraînement seulement (30 % de prolongations seulement dans les données).
 * - Découpage temporel : test = dernière année civile complète, entraînement = les années d'avant ;
 *   les locations à cheval sur la frontière sont écartées (protocole 05_ML/TRAIN_TEST_PROTOCOL.md).
 * - Baseline = classe majoritaire de l'entraînement.
 * - Le modèle et la structure des données (header) sont sauvegardés ensemble dans un fichier, puis rechargés.
 */
public class ExtensionModel {

    public static final String ALGORITHM = "J48 (C4.5, minNumObj=20) + ClassBalancer";
    public static final int MIN_LEAF = 20;
    public static final int MIN_TRAIN = 200;
    public static final int MIN_TEST = 50;
    private static final String YES = "yes";
    private static final String NO = "no";

    private final Classifier classifier;
    private final Instances header;   // structure des attributs (dont la liste des catégories vues à l'entraînement)
    private ModelMetrics metrics;     // rempli par train(), pas par load()

    private ExtensionModel(Classifier classifier, Instances header) {
        this.classifier = classifier;
        this.header = header;
    }

    // ------------------------------------------------------------------ historique client

    /**
     * Remplit previousRentals et previousExtensionRate de chaque observation, en ne regardant que les
     * locations du même client parties AVANT elle (pas de fuite du futur).
     */
    public static void fillCustomerHistory(List<ExtensionSample> samples) {
        List<ExtensionSample> sorted = new ArrayList<>(samples);
        sorted.sort(Comparator.comparing(ExtensionSample::getPickupDate));
        Map<Long, int[]> seen = new HashMap<>();   // client → {locations précédentes, avec prolongation}
        LocalDate currentDay = null;
        List<ExtensionSample> sameDay = new ArrayList<>();
        for (ExtensionSample s : sorted) {
            if (!s.getPickupDate().equals(currentDay)) {   // les locations du même jour ne se « voient » pas
                addAll(seen, sameDay);
                sameDay.clear();
                currentDay = s.getPickupDate();
            }
            int[] h = seen.getOrDefault(s.getCustomerId(), new int[2]);
            s.setCustomerHistory(h[0], h[1]);
            sameDay.add(s);
        }
        addAll(seen, sameDay);
    }

    private static void addAll(Map<Long, int[]> seen, List<ExtensionSample> samples) {
        for (ExtensionSample s : samples) {
            int[] h = seen.computeIfAbsent(s.getCustomerId(), k -> new int[2]);
            h[0]++;
            if (Boolean.TRUE.equals(s.getExtensionRequested())) {
                h[1]++;
            }
        }
    }

    // ------------------------------------------------------------------ entraînement et évaluation

    /** Entraîne sur les années d'avant, évalue sur la dernière année complète avant today. */
    public static ExtensionModel train(List<ExtensionSample> samples, LocalDate today) {
        int testYear = today.getYear() - 1;
        LocalDate testStart = LocalDate.of(testYear, 1, 1);
        LocalDate testEnd = LocalDate.of(testYear + 1, 1, 1);

        List<ExtensionSample> train = new ArrayList<>();
        List<ExtensionSample> test = new ArrayList<>();
        for (ExtensionSample s : samples) {
            if (s.getExtensionRequested() == null || s.getReturnDate() == null) {
                continue;
            }
            if (s.getPickupDate().isBefore(testStart) && s.getReturnDate().isBefore(testStart)) {
                train.add(s);
            } else if (!s.getPickupDate().isBefore(testStart) && s.getPickupDate().isBefore(testEnd)
                    && s.getReturnDate().isBefore(testEnd)) {
                test.add(s);
            }   // les autres (à cheval sur une frontière, ou après la fin du test) sont écartés
        }
        if (train.size() < MIN_TRAIN || test.size() < MIN_TEST) {
            throw new BusinessException("error.ml.notEnoughData");
        }

        TreeSet<String> categories = new TreeSet<>();
        for (ExtensionSample s : train) {
            categories.add(s.getCategory());
        }
        Instances header = buildHeader(new ArrayList<>(categories));
        Instances trainSet = toInstances(header, train);
        Instances testSet = toInstances(header, test);

        try {
            J48 tree = new J48();
            tree.setMinNumObj(MIN_LEAF);
            FilteredClassifier classifier = new FilteredClassifier();
            classifier.setFilter(new ClassBalancer());
            classifier.setClassifier(tree);
            classifier.buildClassifier(trainSet);

            Evaluation eval = new Evaluation(trainSet);
            eval.evaluateModel(classifier, testSet);

            ExtensionModel model = new ExtensionModel(classifier, new Instances(header, 0));
            model.metrics = buildMetrics(eval, header, train, test);
            return model;
        } catch (Exception e) {
            throw new IllegalStateException("Entraînement Weka impossible : " + e.getMessage(), e);
        }
    }

    private static ModelMetrics buildMetrics(Evaluation eval, Instances header,
                                             List<ExtensionSample> train, List<ExtensionSample> test) {
        int yes = header.classAttribute().indexOfValue(YES);

        // baseline : on prédit toujours la classe majoritaire de l'entraînement
        long trainPositives = train.stream().filter(s -> s.getExtensionRequested()).count();
        boolean majorityIsYes = trainPositives * 2 > train.size();
        double testPositiveRate = (double) test.stream().filter(s -> s.getExtensionRequested()).count() / test.size();
        double baselineAccuracy = majorityIsYes ? testPositiveRate : 1 - testPositiveRate;
        double baselineF1 = majorityIsYes ? 2 * testPositiveRate / (testPositiveRate + 1) : 0.0;

        ModelMetrics m = new ModelMetrics();
        m.setModelType(ModelType.RENTAL_EXTENSION);
        m.setAlgorithm(ALGORITHM);
        m.setTrainedAt(LocalDateTime.now().withNano(0));
        m.setTrainingSize(train.size());
        m.setTestSize(test.size());
        m.setAccuracy(eval.pctCorrect() / 100.0);
        m.setPrecisionScore(zeroIfNaN(eval.precision(yes)));
        m.setRecall(zeroIfNaN(eval.recall(yes)));
        m.setF1Score(zeroIfNaN(eval.fMeasure(yes)));
        m.setBaselineAccuracy(baselineAccuracy);
        m.setBaselineF1(baselineF1);
        return m;
    }

    private static double zeroIfNaN(double value) {
        return Double.isNaN(value) ? 0.0 : value;
    }

    // ------------------------------------------------------------------ prédiction

    /** Probabilité (0 à 1) que le client demande une prolongation. */
    public double probability(ExtensionSample s) {
        try {
            Instance instance = toInstance(header, s);
            double[] distribution = classifier.distributionForInstance(instance);
            return distribution[header.classAttribute().indexOfValue(YES)];
        } catch (Exception e) {
            throw new IllegalStateException("Prédiction Weka impossible : " + e.getMessage(), e);
        }
    }

    /** Les métriques de l'entraînement qui vient d'avoir lieu (null pour un modèle rechargé d'un fichier). */
    public ModelMetrics getMetrics() {
        return metrics;
    }

    /** Le texte de l'arbre (ses règles), pratique pour le rapport et la soutenance. */
    public String describe() {
        return classifier.toString();
    }

    // ------------------------------------------------------------------ sauvegarde et rechargement

    public void save(String path) {
        try {
            java.io.File file = new java.io.File(path);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            SerializationHelper.writeAll(path, new Object[]{classifier, header});
        } catch (Exception e) {
            throw new IllegalStateException("Sauvegarde du modèle impossible : " + path, e);
        }
    }

    public static ExtensionModel load(String path) {
        try {
            Object[] saved = SerializationHelper.readAll(path);
            return new ExtensionModel((Classifier) saved[0], (Instances) saved[1]);
        } catch (Exception e) {
            throw new IllegalStateException("Chargement du modèle impossible : " + path, e);
        }
    }

    // ------------------------------------------------------------------ conversion vers Weka

    private static Instances buildHeader(List<String> categories) {
        List<String> months = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            months.add(String.valueOf(m));
        }
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("plannedDays"));
        attributes.add(new Attribute("month", months));
        attributes.add(new Attribute("category", categories));
        attributes.add(new Attribute("dailyRate"));
        attributes.add(new Attribute("equipmentCount"));
        attributes.add(new Attribute("previousRentals"));
        attributes.add(new Attribute("previousExtensionRate"));
        attributes.add(new Attribute("extension", List.of(NO, YES)));
        Instances header = new Instances("rental_extension", attributes, 0);
        header.setClassIndex(header.numAttributes() - 1);
        return header;
    }

    private static Instances toInstances(Instances header, List<ExtensionSample> samples) {
        Instances data = new Instances(header, samples.size());
        for (ExtensionSample s : samples) {
            data.add(toInstance(data, s));
        }
        return data;
    }

    private static Instance toInstance(Instances dataset, ExtensionSample s) {
        Instance inst = new DenseInstance(dataset.numAttributes());
        inst.setDataset(dataset);
        inst.setValue(0, s.getPlannedDays());
        inst.setValue(1, String.valueOf(s.getMonth()));
        if (dataset.attribute(2).indexOfValue(s.getCategory()) >= 0) {
            inst.setValue(2, s.getCategory());
        } else {
            inst.setMissing(2);   // catégorie créée après l'entraînement : J48 sait traiter une valeur manquante
        }
        inst.setValue(3, s.getDailyRate() == null ? 0.0 : s.getDailyRate().doubleValue());
        inst.setValue(4, s.getEquipmentCount());
        inst.setValue(5, s.getPreviousRentals());
        inst.setValue(6, s.getPreviousExtensionRate());
        if (s.getExtensionRequested() == null) {
            inst.setClassMissing();
        } else {
            inst.setValue(7, s.getExtensionRequested() ? YES : NO);
        }
        return inst;
    }
}
