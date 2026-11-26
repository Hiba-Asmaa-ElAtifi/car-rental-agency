package ma.uir.awd.metier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/** Contrat figé (01_Decisions/Contrat_interfaces.md §3.1). Tableau de bord des revenus de l'administrateur. */
public interface IRevenueMetier {

    /** 12 entrées (mois 1 à 12), à 0 pour les mois sans facture, dans l'ordre. */
    Map<Integer, BigDecimal> revenueByMonth(int year);

    /** Catégorie → total, du plus grand au plus petit (LinkedHashMap). BusinessException si from > to. */
    Map<String, BigDecimal> revenueByCategory(LocalDate from, LocalDate to);
}
