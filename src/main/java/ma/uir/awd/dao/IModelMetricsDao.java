package ma.uir.awd.dao;

import java.util.List;

import ma.uir.awd.model.ModelMetrics;
import ma.uir.awd.model.ModelType;

/** Contrat figé (01_Decisions/Contrat_interfaces.md §2.1). */
public interface IModelMetricsDao {

    /** Insère les métriques d'un entraînement ; ensuite m.getId() est l'id généré. */
    void save(ModelMetrics m);

    /** Le dernier entraînement de ce type (trainedAt le plus récent), ou null. */
    ModelMetrics findLatest(ModelType type);

    /** Tous les entraînements de ce type, du plus récent au plus ancien. */
    List<ModelMetrics> findByType(ModelType type);
}
