package ma.uir.awd.model;

import java.math.BigDecimal;
import javax.persistence.*;

/** Paramètres de l'agence (une seule ligne) : table app_settings de schema.sql. Les taux sont entre 0 et 1. */
@Entity
@Table(name = "app_settings")
public class AppSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** TVA : 0,07 = 7 % du sous-total. */
    @Column(name = "vatRate", nullable = false, precision = 10, scale = 6)
    private BigDecimal vatRate;

    /** Pénalité par jour de retard : 0,15 = 15 % du tarif journalier. */
    @Column(name = "lateFeeRate", nullable = false, precision = 10, scale = 6)
    private BigDecimal lateFeeRate;

    public AppSettings() {
    }

    public AppSettings(BigDecimal vatRate, BigDecimal lateFeeRate) {
        this.vatRate = vatRate;
        this.lateFeeRate = lateFeeRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getLateFeeRate() {
        return lateFeeRate;
    }

    public void setLateFeeRate(BigDecimal lateFeeRate) {
        this.lateFeeRate = lateFeeRate;
    }

    @Override
    public String toString() {
        return "AppSettings{id=" + id + ", vatRate=" + vatRate + ", lateFeeRate=" + lateFeeRate + "}";
    }
}
