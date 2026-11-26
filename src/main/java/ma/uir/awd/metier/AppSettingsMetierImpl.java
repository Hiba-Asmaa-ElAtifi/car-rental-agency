package ma.uir.awd.metier;

import java.math.BigDecimal;

import ma.uir.awd.dao.IAppSettingsDao;
import ma.uir.awd.model.AppSettings;

/** Paramètres de l'agence (TVA, pénalités). Ne connaît que IAppSettingsDao. */
public class AppSettingsMetierImpl implements IAppSettingsMetier {

    private final IAppSettingsDao settingsDao;

    public AppSettingsMetierImpl(IAppSettingsDao settingsDao) {
        this.settingsDao = settingsDao;
    }

    @Override
    public AppSettings get() {
        return settingsDao.find();
    }

    @Override
    public void update(BigDecimal vatRate, BigDecimal lateFeeRate) {
        if (!isRate(vatRate) || !isRate(lateFeeRate)) {
            throw new BusinessException("error.settings.rate");
        }
        AppSettings settings = settingsDao.find();
        if (settings == null) {
            throw new BusinessException("error.settings.missing");
        }
        settings.setVatRate(vatRate);
        settings.setLateFeeRate(lateFeeRate);
        settingsDao.update(settings);
    }

    /** Un taux est entre 0 et 1 (0,07 = 7 %). */
    private boolean isRate(BigDecimal rate) {
        return rate != null && rate.signum() >= 0 && rate.compareTo(BigDecimal.ONE) <= 0;
    }
}
