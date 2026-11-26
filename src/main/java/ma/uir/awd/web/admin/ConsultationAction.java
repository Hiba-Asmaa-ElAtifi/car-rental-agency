package ma.uir.awd.web.admin;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import ma.uir.awd.dao.InvoiceDaoImpl;
import ma.uir.awd.dao.RentalDaoImpl;
import ma.uir.awd.dao.ReservationDaoImpl;
import ma.uir.awd.metier.IInvoiceMetier;
import ma.uir.awd.metier.IRentalMetier;
import ma.uir.awd.metier.IReservationMetier;
import ma.uir.awd.metier.InvoiceMetierImpl;
import ma.uir.awd.metier.RentalMetierImpl;
import ma.uir.awd.metier.ReservationMetierImpl;
import ma.uir.awd.model.Rental;
import ma.uir.awd.model.ReservationStatus;
import ma.uir.awd.model.Role;
import ma.uir.awd.web.BaseAction;

/**
 * GET /admin/rentals?view=reservations|rentals|invoices : consultation globale de l'administrateur
 * (énoncé : « the administrator sees everything »).
 * - réservations : filtrées par statut (IReservationMetier.findByStatus, Salma) ;
 * - locations : actives, en attente d'inspection, en retard ou toutes (IRentalMetier, Fatima Zahra) ;
 * - factures : toutes, de la plus récente à la plus ancienne (IInvoiceMetier, Fatima Zahra).
 * Il y a plus de 3 000 lignes par liste : l'Action affiche une page de 50 lignes à la fois.
 *
 * Les trois métiers appartiennent à Salma et à Fatima Zahra : les lignes de construction ci-dessous
 * sont à ajuster sur leurs vrais constructeurs à l'intégration.
 */
public class ConsultationAction extends BaseAction {

    public static final int PAGE_SIZE = 50;

    // À AJUSTER à l'intégration selon les constructeurs réels de Salma et de Fatima Zahra
    private final IReservationMetier reservationMetier = new ReservationMetierImpl(new ReservationDaoImpl());
    private final IRentalMetier rentalMetier = new RentalMetierImpl(new RentalDaoImpl());
    private final IInvoiceMetier invoiceMetier = new InvoiceMetierImpl(new InvoiceDaoImpl());

    private String view = "rentals";                               // onglet affiché
    private ReservationStatus reservationStatus = ReservationStatus.PENDING;
    private String rentalFilter = "active";                        // active, inspection, late, all
    private int page = 1;

    private List<?> all = Collections.emptyList();                 // la liste complète de l'onglet
    private final LocalDate today = LocalDate.now();

    @Override
    public String execute() {
        String denied = checkRole(Role.ADMINISTRATOR);
        if (denied != null) {
            return denied;
        }
        switch (view) {
            case "reservations":
                all = reservationMetier.findByStatus(reservationStatus);
                break;
            case "invoices":
                all = invoiceMetier.findAll();
                break;
            default:
                view = "rentals";
                all = loadRentals();
        }
        if (page < 1 || page > getPageCount()) {
            page = 1;
        }
        return SUCCESS;
    }

    private List<Rental> loadRentals() {
        switch (rentalFilter) {
            case "inspection": return rentalMetier.findPendingInspection();
            case "late":       return rentalMetier.findLate();
            case "all":        return rentalMetier.findHistory();
            default:
                rentalFilter = "active";
                return rentalMetier.findActive();
        }
    }

    // ---- valeurs calculées pour la JSP (elle ne calcule rien) ----

    /** Les lignes de la page courante. */
    public List<?> getPageItems() {
        int from = Math.min((page - 1) * PAGE_SIZE, all.size());
        return all.subList(from, Math.min(from + PAGE_SIZE, all.size()));
    }

    public int getPageCount() {
        return Math.max(1, (all.size() + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    public int getTotal() {
        return all.size();
    }

    /** Signal visuel « en retard », décidé par le métier de Fatima Zahra. */
    public boolean late(Rental rental) {
        return rentalMetier.isLate(rental, today);
    }

    public ReservationStatus[] getReservationStatuses() {
        return ReservationStatus.values();
    }

    // ---- getters / setters ----

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public String getRentalFilter() {
        return rentalFilter;
    }

    public void setRentalFilter(String rentalFilter) {
        this.rentalFilter = rentalFilter;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
