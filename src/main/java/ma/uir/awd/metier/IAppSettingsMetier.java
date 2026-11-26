package ma.uir.awd.metier;

import java.math.BigDecimal;

import ma.uir.awd.model.AppSettings;

/** Contrat figé (01_Decisions/Contrat_interfaces.md §3.1). */
public interface IAppSettingsMetier {

    AppSettings get();

    /** BusinessException si un taux n'est pas entre 0 et 1. */
    void update(BigDecimal vatRate, BigDecimal lateFeeRate);
}
