package ma.uir.awd.metier.ml;

import ma.uir.awd.dao.IModelMetricsDao;
import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.model.ModelMetrics;
import ma.uir.awd.model.ModelType;

/**
 * Implémentation PROVISOIRE (point de jonction de la structure) : permet à Fatima Zahra de coder
 * l'écran de pick-up avant le vrai modèle. predict renvoie toujours null, et la page affiche
 * « Prediction indisponible ». Sera remplacée par ExtensionPredictionMetierImpl (Weka) sans que
 * les Actions changent, puisqu'elles ne connaissent que IExtensionPredictionMetier.
 *
 * Utilisation dans une Action :
 *   private final IExtensionPredictionMetier predictionMetier = new ExtensionPredictionStub(new ModelMetricsDaoImpl());
 */
public class ExtensionPredictionStub implements IExtensionPredictionMetier {

    private final IModelMetricsDao metricsDao;

    public ExtensionPredictionStub(IModelMetricsDao metricsDao) {
        this.metricsDao = metricsDao;
    }

    @Override
    public ExtensionPrediction predict(Long reservationId) {
        return null;   // aucun modèle entraîné pour l'instant
    }

    @Override
    public ModelMetrics retrain() {
        throw new BusinessException("error.ml.notAvailable");
    }

    @Override
    public ModelMetrics getLatestMetrics() {
        return metricsDao.findLatest(ModelType.RENTAL_EXTENSION);
    }
}
