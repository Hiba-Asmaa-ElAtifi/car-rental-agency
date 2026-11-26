package ma.uir.awd.web.admin;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ma.uir.awd.dao.AppSettingsDaoImpl;
import ma.uir.awd.metier.AppSettingsMetierImpl;
import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.IAppSettingsMetier;
import ma.uir.awd.model.AppSettings;
import ma.uir.awd.model.Role;
import ma.uir.awd.web.BaseAction;

/**
 * GET /admin/settings : formulaire prérempli (méthode input). POST /admin/saveSettings : enregistre,
 * puis redirige vers /admin/settings?saved=1.
 * L'admin saisit des pourcentages (7 pour 7 %) ; la base et le métier gardent des taux (0,07).
 * La conversion est une question d'affichage : elle reste ici, la règle (entre 0 et 1) est dans le métier.
 */
public class SettingsAction extends BaseAction {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final IAppSettingsMetier settingsMetier = new AppSettingsMetierImpl(new AppSettingsDaoImpl());

    private BigDecimal vatPercent;       // "abc" : Struts signale l'erreur de conversion (invalid.fieldvalue.vatPercent)
    private BigDecimal lateFeePercent;
    private Integer saved;

    /** GET : remplit le formulaire avec les valeurs actuelles. */
    @Override
    public String input() {
        String denied = checkRole(Role.ADMINISTRATOR);
        if (denied != null) {
            return denied;
        }
        AppSettings s = settingsMetier.get();
        if (s != null) {
            vatPercent = s.getVatRate().multiply(HUNDRED).stripTrailingZeros();
            lateFeePercent = s.getLateFeeRate().multiply(HUNDRED).stripTrailingZeros();
        }
        return INPUT;
    }

    @Override
    public void validate() {
        if (vatPercent == null) {
            addFieldError("vatPercent", getText("error.rate.required"));
        }
        if (lateFeePercent == null) {
            addFieldError("lateFeePercent", getText("error.rate.required"));
        }
    }

    @Override
    public String execute() {
        String denied = checkRole(Role.ADMINISTRATOR);
        if (denied != null) {
            return denied;
        }
        try {
            settingsMetier.update(toRate(vatPercent), toRate(lateFeePercent));
        } catch (BusinessException e) {
            addActionError(getText(e.getMessage()));   // error.settings.rate : hors de 0 à 100 %
            return INPUT;
        }
        return SUCCESS;   // redirection vers /admin/settings?saved=1
    }

    private BigDecimal toRate(BigDecimal percent) {
        return percent.divide(HUNDRED, 6, RoundingMode.HALF_UP);
    }

    public BigDecimal getVatPercent() {
        return vatPercent;
    }

    public void setVatPercent(BigDecimal vatPercent) {
        this.vatPercent = vatPercent;
    }

    public BigDecimal getLateFeePercent() {
        return lateFeePercent;
    }

    public void setLateFeePercent(BigDecimal lateFeePercent) {
        this.lateFeePercent = lateFeePercent;
    }

    public Integer getSaved() {
        return saved;
    }

    public void setSaved(Integer saved) {
        this.saved = saved;
    }
}
