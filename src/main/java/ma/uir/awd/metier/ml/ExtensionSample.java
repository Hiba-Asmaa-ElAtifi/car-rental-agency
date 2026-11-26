package ma.uir.awd.metier.ml;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Une observation du modèle de prolongation : les variables connues au pick-up, et la cible.
 * Objet simple, sans Hibernate : ExtensionPredictionMetierImpl le construit à partir des entités
 * lues par les DAO, et ExtensionModel l'utilise pour entraîner et prédire.
 * (05_ML/Conception_ML_prolongation.md §2)
 */
public class ExtensionSample {

    private final Long customerId;
    private final LocalDate pickupDate;       // date de départ (réelle à l'entraînement, prévue à la prédiction)
    private final LocalDate returnDate;       // date de retour réelle (entraînement seulement ; null à la prédiction)
    private final int plannedDays;            // date de retour prévue − date de départ prévue
    private final String category;
    private final BigDecimal dailyRate;
    private final int equipmentCount;
    private final Boolean extensionRequested; // cible ; null à la prédiction

    // historique du client avant pickupDate, rempli par ExtensionModel.fillCustomerHistory (ou par le métier à la prédiction)
    private int previousRentals;
    private double previousExtensionRate;

    public ExtensionSample(Long customerId, LocalDate pickupDate, LocalDate returnDate, int plannedDays,
                           String category, BigDecimal dailyRate, int equipmentCount, Boolean extensionRequested) {
        this.customerId = customerId;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.plannedDays = plannedDays;
        this.category = category;
        this.dailyRate = dailyRate;
        this.equipmentCount = equipmentCount;
        this.extensionRequested = extensionRequested;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public int getPlannedDays() {
        return plannedDays;
    }

    public int getMonth() {
        return pickupDate.getMonthValue();
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public int getEquipmentCount() {
        return equipmentCount;
    }

    public Boolean getExtensionRequested() {
        return extensionRequested;
    }

    public int getPreviousRentals() {
        return previousRentals;
    }

    public double getPreviousExtensionRate() {
        return previousExtensionRate;
    }

    public void setCustomerHistory(int previousRentals, int previousExtensions) {
        this.previousRentals = previousRentals;
        this.previousExtensionRate = previousRentals == 0 ? 0.0 : (double) previousExtensions / previousRentals;
    }
}
