package ma.uir.awd.metier.ml;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import ma.uir.awd.dao.IModelMetricsDao;
import ma.uir.awd.dao.IRentalDao;
import ma.uir.awd.dao.IReservationDao;
import ma.uir.awd.model.ModelMetrics;
import ma.uir.awd.model.ModelType;
import ma.uir.awd.model.Rental;
import ma.uir.awd.model.RentalExtension;
import ma.uir.awd.model.RentalStatus;
import ma.uir.awd.model.Reservation;

/**
 * Vraie implémentation de IExtensionPredictionMetier (remplace ExtensionPredictionStub).
 * - Données lues via les DAO (exigence de l'énoncé) : IRentalDao.findCompletedForTraining() pour l'entraînement,
 *   IReservationDao.findById() et IRentalDao.findByCustomer() pour une prédiction.
 * - Le travail Weka est dans ExtensionModel ; cette classe traduit les entités en ExtensionSample.
 * - Le modèle est sauvegardé dans un fichier hors du WAR, son chemin dans model_metrics.modelPath,
 *   puis rechargé une seule fois et gardé en mémoire (pas de réentraînement à chaque requête).
 *
 * Dépend des entités de Fatima Zahra (Rental, RentalExtension, RentalStatus) et de Salma (Reservation),
 * avec les noms du contrat §1 : compile dès qu'elles existent.
 *
 * Utilisation dans une Action :
 *   new ExtensionPredictionMetierImpl(new RentalDaoImpl(), new ReservationDaoImpl(), new ModelMetricsDaoImpl())
 */
public class ExtensionPredictionMetierImpl implements IExtensionPredictionMetier {

    /** Dossier des modèles, hors du WAR : un redéploiement ne les efface pas. */
    public static final String MODEL_DIR = System.getProperty("user.home") + File.separator
            + "car-rental" + File.separator + "models";

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    // modèle gardé en mémoire, partagé par toutes les requêtes (chargé une fois par chemin)
    private static ExtensionModel cachedModel;
    private static String cachedPath;

    private final IRentalDao rentalDao;
    private final IReservationDao reservationDao;
    private final IModelMetricsDao metricsDao;

    public ExtensionPredictionMetierImpl(IRentalDao rentalDao, IReservationDao reservationDao,
                                         IModelMetricsDao metricsDao) {
        this.rentalDao = rentalDao;
        this.reservationDao = reservationDao;
        this.metricsDao = metricsDao;
    }

    // ------------------------------------------------------------------ IExtensionPredictionMetier

    @Override
    public ExtensionPrediction predict(Long reservationId) {
        ExtensionModel model = currentModel();
        Reservation reservation = reservationId == null ? null : reservationDao.findById(reservationId);
        if (model == null || reservation == null) {
            return null;   // la page affiche « Prediction indisponible »
        }
        ExtensionSample sample = toSample(reservation);

        // historique du client : ses locations terminées parties avant cette réservation
        int previous = 0;
        int withExtension = 0;
        for (Rental r : rentalDao.findByCustomer(reservation.getCustomer().getId())) {
            Boolean label = label(r);
            if (r.getStatus() == RentalStatus.COMPLETED && label != null
                    && r.getActualPickupDate().isBefore(reservation.getPickupDate())) {
                previous++;
                if (label) {
                    withExtension++;
                }
            }
        }
        sample.setCustomerHistory(previous, withExtension);

        double probability = model.probability(sample);
        return new ExtensionPrediction(probability >= 0.5, probability);
    }

    @Override
    public synchronized ModelMetrics retrain() {
        List<ExtensionSample> samples = new ArrayList<>();
        for (Rental r : rentalDao.findCompletedForTraining()) {
            Boolean label = label(r);
            if (label != null) {   // null : demande seulement le jour du départ (observation écartée)
                samples.add(toSample(r, label));
            }
        }
        ExtensionModel.fillCustomerHistory(samples);
        ExtensionModel model = ExtensionModel.train(samples, LocalDate.now());   // BusinessException si trop peu de données

        String path = MODEL_DIR + File.separator + "extension-j48-" + LocalDateTime.now().format(STAMP) + ".model";
        model.save(path);

        ModelMetrics metrics = model.getMetrics();
        metrics.setModelPath(path);
        metricsDao.save(metrics);

        synchronized (ExtensionPredictionMetierImpl.class) {
            cachedModel = model;
            cachedPath = path;
        }
        return metrics;
    }

    @Override
    public ModelMetrics getLatestMetrics() {
        return metricsDao.findLatest(ModelType.RENTAL_EXTENSION);
    }

    // ------------------------------------------------------------------ chargement du modèle

    /** Le modèle du dernier entraînement enregistré, chargé une seule fois ; null s'il n'y en a pas. */
    private ExtensionModel currentModel() {
        ModelMetrics latest = getLatestMetrics();
        if (latest == null || latest.getModelPath() == null || !new File(latest.getModelPath()).isFile()) {
            return null;
        }
        synchronized (ExtensionPredictionMetierImpl.class) {
            if (!latest.getModelPath().equals(cachedPath)) {
                cachedModel = ExtensionModel.load(latest.getModelPath());
                cachedPath = latest.getModelPath();
            }
            return cachedModel;
        }
    }

    // ------------------------------------------------------------------ entités → observations

    /**
     * Cible (05_ML/RENTAL_EXTENSION_DATA_SPEC.md) : true si une demande de prolongation a été faite après
     * le jour du départ ; false s'il n'y en a aucune ; null si la seule demande date du jour du départ
     * (dates sans heure : on ne sait pas si elle est avant ou après le départ).
     */
    static Boolean label(Rental r) {
        if (r.getExtensions() == null || r.getExtensions().isEmpty()) {
            return Boolean.FALSE;
        }
        for (RentalExtension e : r.getExtensions()) {
            if (e.getRequestDate().isAfter(r.getActualPickupDate())) {
                return Boolean.TRUE;
            }
        }
        return null;
    }

    /** Observation d'entraînement : une location terminée. */
    private static ExtensionSample toSample(Rental r, boolean label) {
        Reservation res = r.getReservation();
        return new ExtensionSample(res.getCustomer().getId(), r.getActualPickupDate(), r.getActualReturnDate(),
                plannedDays(res), res.getVehicule().getCategory().getName(), res.getVehicule().getDailyRate(),
                res.getEquipments() == null ? 0 : res.getEquipments().size(), label);
    }

    /** Observation à prédire : une réservation acceptée, au moment du pick-up (pas de retour, pas de cible). */
    private static ExtensionSample toSample(Reservation res) {
        return new ExtensionSample(res.getCustomer().getId(), res.getPickupDate(), null,
                plannedDays(res), res.getVehicule().getCategory().getName(), res.getVehicule().getDailyRate(),
                res.getEquipments() == null ? 0 : res.getEquipments().size(), null);
    }

    private static int plannedDays(Reservation res) {
        return (int) ChronoUnit.DAYS.between(res.getPickupDate(), res.getPlannedReturnDate());
    }
}
