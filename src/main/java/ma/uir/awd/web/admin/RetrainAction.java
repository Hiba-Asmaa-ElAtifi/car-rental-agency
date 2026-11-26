package ma.uir.awd.web.admin;

import ma.uir.awd.dao.ModelMetricsDaoImpl;
import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.ml.ExtensionPredictionStub;
import ma.uir.awd.metier.ml.IExtensionPredictionMetier;
import ma.uir.awd.model.Role;
import ma.uir.awd.web.BaseAction;

/**
 * POST /admin/retrain : réentraîne le modèle (lecture des données via les DAO, évaluation, sauvegarde),
 * puis redirige vers /admin/ml?retrained=1 ou 0 (Post/Redirect/Get : un rafraîchissement ne relance pas l'entraînement).
 * Exigence de l'énoncé : « The administrator can trigger a retraining from the interface and see the latest metrics. »
 */
public class RetrainAction extends BaseAction {

    private final IExtensionPredictionMetier predictionMetier =
            new ExtensionPredictionStub(new ModelMetricsDaoImpl());

    private Integer retrained;

    @Override
    public String execute() {
        String denied = checkRole(Role.ADMINISTRATOR);
        if (denied != null) {
            return denied;
        }
        try {
            predictionMetier.retrain();
            retrained = 1;
        } catch (BusinessException e) {
            retrained = 0;   // stub : error.ml.notAvailable ; vrai modèle : pas assez de données
        }
        return SUCCESS;
    }

    public Integer getRetrained() {
        return retrained;
    }
}
