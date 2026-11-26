package ma.uir.awd.metier.ml;

/** Résultat affiché « Prediction » à l'agent : simple objet de valeur, calculé par le métier. */
public class ExtensionPrediction {

    private final boolean likelyExtension;
    private final double probability;   // de 0 à 1

    public ExtensionPrediction(boolean likelyExtension, double probability) {
        this.likelyExtension = likelyExtension;
        this.probability = probability;
    }

    public boolean isLikelyExtension() {
        return likelyExtension;
    }

    public double getProbability() {
        return probability;
    }

    /** Pourcentage arrondi, pratique pour la JSP (<s:property value="prediction.percent"/>). */
    public int getPercent() {
        return (int) Math.round(probability * 100);
    }

    @Override
    public String toString() {
        return "ExtensionPrediction{likelyExtension=" + likelyExtension + ", probability=" + probability + "}";
    }
}
