package ma.uir.awd.web.admin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

import ma.uir.awd.dao.InvoiceDaoImpl;
import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.IRevenueMetier;
import ma.uir.awd.metier.RevenueMetierImpl;
import ma.uir.awd.model.Role;
import ma.uir.awd.web.BaseAction;

/**
 * GET /admin/revenue?year=2025&from=2025-01-01&to=2025-12-31 : tableau de bord des revenus
 * (par mois de l'année choisie, et par catégorie sur la période choisie).
 * Formulaire en GET : il ne modifie rien, on peut donc garder l'adresse en favori.
 * Les dates arrivent en texte et sont lues dans validate(), comme startsAt dans AddClassAction (Lab 8).
 */
public class RevenueAction extends BaseAction {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final IRevenueMetier revenueMetier = new RevenueMetierImpl(new InvoiceDaoImpl());

    private Integer year;        // année des revenus mensuels (par défaut : l'année en cours)
    private String from;         // <input type="date"> : 2025-01-01
    private String to;
    private LocalDate fromDate;  // from une fois lue par validate()
    private LocalDate toDate;

    private Map<Integer, BigDecimal> byMonth;
    private Map<String, BigDecimal> byCategory;

    @Override
    public void validate() {
        if (checkRole(Role.ADMINISTRATOR) != null) {
            return;   // pas admin : pas de validation, execute() renverra "login" ou "forbidden"
        }
        LocalDate today = LocalDate.now();
        if (year == null) {
            year = today.getYear();
        } else if (year < 2000 || year > today.getYear() + 1) {
            addFieldError("year", getText("error.revenue.year"));
        }
        fromDate = parse(from, LocalDate.of(today.getYear(), 1, 1), "from");
        toDate = parse(to, today, "to");
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            addFieldError("to", getText("error.revenue.period"));
        }
    }

    /** Une date vide prend sa valeur par défaut ; une date mal écrite donne une erreur de champ. */
    private LocalDate parse(String text, LocalDate defaultValue, String field) {
        if (text == null || text.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            addFieldError(field, getText("error.revenue.date"));
            return null;
        }
    }

    @Override
    public String execute() {
        String denied = checkRole(Role.ADMINISTRATOR);
        if (denied != null) {
            return denied;
        }
        try {
            byMonth = revenueMetier.revenueByMonth(year);
            byCategory = revenueMetier.revenueByCategory(fromDate, toDate);
        } catch (BusinessException e) {
            addActionError(getText(e.getMessage()));
            return INPUT;
        }
        return SUCCESS;
    }


    // ---- valeurs calculées pour l'affichage (la JSP ne calcule rien) ----

    public BigDecimal getYearTotal() {
        return sum(byMonth);
    }

    public BigDecimal getPeriodTotal() {
        return sum(byCategory);
    }

    /** Largeur de la barre d'un mois, en % du meilleur mois (0 à 100). */
    public int monthBarPercent(Integer month) {
        return barPercent(byMonth, byMonth.get(month));
    }

    /** Largeur de la barre d'une catégorie, en % de la meilleure catégorie. */
    public int categoryBarPercent(String category) {
        return barPercent(byCategory, byCategory.get(category));
    }

    private static int barPercent(Map<?, BigDecimal> values, BigDecimal value) {
        BigDecimal max = values.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        if (value == null || max.signum() == 0) {
            return 0;
        }
        return value.multiply(HUNDRED).divide(max, 0, RoundingMode.HALF_UP).intValue();
    }

    private static BigDecimal sum(Map<?, BigDecimal> values) {
        return values == null ? BigDecimal.ZERO
                : values.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ---- getters / setters ----

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getFrom() {
        return fromDate != null ? fromDate.toString() : from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return toDate != null ? toDate.toString() : to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Map<Integer, BigDecimal> getByMonth() {
        return byMonth;
    }

    public Map<String, BigDecimal> getByCategory() {
        return byCategory;
    }
}
