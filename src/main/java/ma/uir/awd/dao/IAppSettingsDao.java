package ma.uir.awd.dao;

import ma.uir.awd.model.AppSettings;

/** Contrat figé (01_Decisions/Contrat_interfaces.md §2.1). */
public interface IAppSettingsDao {

    /** L'unique ligne des paramètres (TVA, taux de retard), ou null si la table est vide. */
    AppSettings find();

    /** Écrit vatRate et lateFeeRate de la ligne existante. */
    void update(AppSettings s);
}
