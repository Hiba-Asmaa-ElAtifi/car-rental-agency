package ma.uir.awd.metier.ml;

import ma.uir.awd.model.ModelMetrics;

/**
 * Contrat figé (01_Decisions/Contrat_interfaces.md §3.1).
 * L'énoncé exige que le ML soit utilisé « through a business interface in the metier package » :
 * les Actions l'appellent comme n'importe quel service métier.
 */
public interface IExtensionPredictionMetier {

    /** Prédiction pour une réservation ACCEPTED, montrée à l'agent au pick-up ; null si aucun modèle n'est entraîné. */
    ExtensionPrediction predict(Long reservationId);

    /**
     * Réentraîne à partir de IRentalDao.findCompletedForTraining(), sauvegarde le modèle dans un fichier
     * et les métriques (avec la baseline) via IModelMetricsDao.
     */
    ModelMetrics retrain();

    /** Les dernières métriques, ou null. */
    ModelMetrics getLatestMetrics();
}
