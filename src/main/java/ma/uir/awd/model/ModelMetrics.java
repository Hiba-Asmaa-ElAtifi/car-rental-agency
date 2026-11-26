package ma.uir.awd.model;

import java.time.LocalDateTime;
import javax.persistence.*;

/**
 * Métriques d'un entraînement du modèle ML : table model_metrics de schema.sql.
 * precisionScore (et non precision, mot réservé de MySQL) et baselineAccuracy viennent de la V16.
 */
@Entity
@Table(name = "model_metrics")
public class ModelMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "modelType", nullable = false)
    private ModelType modelType;

    @Column(name = "algorithm", nullable = false, length = 255)
    private String algorithm;

    @Column(name = "trainedAt", nullable = false)
    private LocalDateTime trainedAt;

    @Column(name = "trainingSize", nullable = false)
    private int trainingSize;

    @Column(name = "testSize", nullable = false)
    private int testSize;

    @Column(name = "accuracy", nullable = false)
    private double accuracy;

    @Column(name = "precisionScore", nullable = false)
    private double precisionScore;

    @Column(name = "recall", nullable = false)
    private double recall;

    @Column(name = "f1Score", nullable = false)
    private double f1Score;

    @Column(name = "baselineAccuracy", nullable = false)
    private double baselineAccuracy;

    @Column(name = "baselineF1", nullable = false)
    private double baselineF1;

    /** Chemin du fichier du modèle entraîné, hors du WAR. */
    @Column(name = "modelPath", nullable = false, length = 512)
    private String modelPath;

    public ModelMetrics() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public LocalDateTime getTrainedAt() {
        return trainedAt;
    }

    public void setTrainedAt(LocalDateTime trainedAt) {
        this.trainedAt = trainedAt;
    }

    public int getTrainingSize() {
        return trainingSize;
    }

    public void setTrainingSize(int trainingSize) {
        this.trainingSize = trainingSize;
    }

    public int getTestSize() {
        return testSize;
    }

    public void setTestSize(int testSize) {
        this.testSize = testSize;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getPrecisionScore() {
        return precisionScore;
    }

    public void setPrecisionScore(double precisionScore) {
        this.precisionScore = precisionScore;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getF1Score() {
        return f1Score;
    }

    public void setF1Score(double f1Score) {
        this.f1Score = f1Score;
    }

    public double getBaselineAccuracy() {
        return baselineAccuracy;
    }

    public void setBaselineAccuracy(double baselineAccuracy) {
        this.baselineAccuracy = baselineAccuracy;
    }

    public double getBaselineF1() {
        return baselineF1;
    }

    public void setBaselineF1(double baselineF1) {
        this.baselineF1 = baselineF1;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    @Override
    public String toString() {
        return "ModelMetrics{id=" + id + ", modelType=" + modelType + ", algorithm='" + algorithm
                + "', accuracy=" + accuracy + ", f1Score=" + f1Score + ", baselineAccuracy=" + baselineAccuracy + "}";
    }
}
