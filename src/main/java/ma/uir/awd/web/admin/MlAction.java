package ma.uir.awd.web.admin;

import ma.uir.awd.dao.ModelMetricsDaoImpl;
import ma.uir.awd.metier.ml.ExtensionPredictionStub;
import ma.uir.awd.metier.ml.IExtensionPredictionMetier;
import ma.uir.awd.model.ModelMetrics;
import ma.uir.awd.model.Role;
import ma.uir.awd.web.BaseAction;

/**
 * GET /admin/ml : dernières métriques du modèle de prolongation, comparées à la baseline.
 * Lit aussi le résultat d'un réentraînement après la redirection (retrained=1 ou 0), comme ok au Lab 8.
 * L'Action ne connaît que l'interface IExtensionPredictionMetier : quand le vrai modèle Weka remplacera
 * le stub, seule la ligne de construction ci-dessous changera.
 */
public class MlAction extends BaseAction {

    private final IExtensionPredictionMetier predictionMetier =
            new ExtensionPredictionStub(new ModelMetricsDaoImpl());

    private ModelMetrics metrics;
    private Integer retrained;   // 1 réussi, 0 refusé : lu par la redirection de RetrainAction

    @Override
    public String execute() {
        String denied = checkRole(Role.ADMINISTRATOR);
        if (denied != null) {
            return denied;
        }
        metrics = predictionMetier.getLatestMetrics();   // null si le modèle n'a jamais été entraîné
        return SUCCESS;
    }

    /** true si le modèle fait mieux que la baseline sur le F1 : la JSP choisit la classe CSS sur cette valeur. */
    public boolean isBetterThanBaseline() {
        return metrics != null && metrics.getF1Score() > metrics.getBaselineF1();
    }

    public ModelMetrics getMetrics() {
        return metrics;
    }

    public Integer getRetrained() {
        return retrained;
    }

    public void setRetrained(Integer retrained) {
        this.retrained = retrained;
    }
}
