package ma.uir.awd.metier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ma.uir.awd.dao.IInvoiceDao;

/**
 * Revenus par mois et par catégorie.
 * Les sommes sont calculées par le DAO en HQL (group by + sum), pas par une boucle sur les factures
 * (rapport des méthodologies §9.3, Labs 3 et 6). Le métier se contente de compléter les mois sans facture
 * et de vérifier la période.
 * IInvoiceDao est l'interface de Fatima Zahra (contrat §2.3) : cette classe compile dès qu'elle existe.
 */
public class RevenueMetierImpl implements IRevenueMetier {

    private final IInvoiceDao invoiceDao;

    public RevenueMetierImpl(IInvoiceDao invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    @Override
    public Map<Integer, BigDecimal> revenueByMonth(int year) {
        Map<Integer, BigDecimal> byMonth = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
            byMonth.put(month, BigDecimal.ZERO);   // un mois sans facture apparaît à 0
        }
        for (Object[] row : invoiceDao.revenueByMonth(year)) {   // {Integer mois, BigDecimal total}
            byMonth.put(((Number) row[0]).intValue(), toBigDecimal(row[1]));
        }
        return byMonth;
    }

    @Override
    public Map<String, BigDecimal> revenueByCategory(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to)) {
            throw new BusinessException("error.revenue.period");
        }
        Map<String, BigDecimal> byCategory = new LinkedHashMap<>();   // garde l'ordre du HQL (total décroissant)
        List<Object[]> rows = invoiceDao.revenueByCategory(from, to);  // {String catégorie, BigDecimal total}
        for (Object[] row : rows) {
            byCategory.put((String) row[0], toBigDecimal(row[1]));
        }
        return byCategory;
    }

    /** sum() d'une colonne DECIMAL renvoie un BigDecimal ; on accepte aussi un autre Number par prudence. */
    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
    }
}
