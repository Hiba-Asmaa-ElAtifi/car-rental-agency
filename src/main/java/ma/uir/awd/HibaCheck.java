package ma.uir.awd;

import java.math.BigDecimal;

import ma.uir.awd.dao.AppSettingsDaoImpl;
import ma.uir.awd.dao.IUserDao;
import ma.uir.awd.dao.ModelMetricsDaoImpl;
import ma.uir.awd.dao.UserDaoImpl;
import ma.uir.awd.metier.AppSettingsMetierImpl;
import ma.uir.awd.metier.AuthenticationMetierImpl;
import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.IAppSettingsMetier;
import ma.uir.awd.metier.IAuthenticationMetier;
import ma.uir.awd.metier.ml.ExtensionPredictionStub;
import ma.uir.awd.metier.ml.IExtensionPredictionMetier;
import ma.uir.awd.model.AppSettings;
import ma.uir.awd.model.Role;
import ma.uir.awd.model.User;

/**
 * Test de la partie de Hiba hors de Tomcat, comme DbCheck (Lab 3) et DevMain (Lab 7).
 * À lancer dans IntelliJ (clic droit › Run) une fois le pom.xml de Salma fusionné.
 * Ne modifie pas la base, sauf le test d'inscription (ligne supprimée à la fin).
 */
public class HibaCheck {

    private static int failures = 0;

    public static void main(String[] args) {
        IUserDao userDao = new UserDaoImpl();
        IAuthenticationMetier auth = new AuthenticationMetierImpl(userDao);
        IAppSettingsMetier settings = new AppSettingsMetierImpl(new AppSettingsDaoImpl());
        IExtensionPredictionMetier prediction = new ExtensionPredictionStub(new ModelMetricsDaoImpl());

        // 1. DAO : lectures
        check("findByEmail admin", userDao.findByEmail("admin@carrental.ma") != null);
        check("findByEmail inconnu = null", userDao.findByEmail("personne@nulle.part") == null);
        check("findByRole ADMINISTRATOR non vide", !userDao.findByRole(Role.ADMINISTRATOR).isEmpty());
        check("findAll = 1446 utilisateurs", userDao.findAll().size() == 1446);

        // 2. Métier : authentification BCrypt
        check("admin / Admin123! accepté", auth.authenticate("admin@carrental.ma", "Admin123!") != null);
        check("agent / Agent123! accepté", auth.authenticate("Agent@CarRental.ma ", "Agent123!") != null);
        check("client / Client123! rôle CUSTOMER",
                auth.authenticate("client@carrental.ma", "Client123!").getRole() == Role.CUSTOMER);
        check("mauvais mot de passe = null", auth.authenticate("admin@carrental.ma", "faux") == null);
        check("e-mail inconnu = null", auth.authenticate("x@y.z", "Admin123!") == null);

        // 3. Métier : inscription (puis suppression de la ligne de test)
        User created = auth.register("Test Inscription", "test.inscription@carrental.ma", "Secret123!", "0600000099");
        check("register : id généré par AUTO_INCREMENT", created.getId() != null);
        check("register : mot de passe haché, pas en clair", !created.getPasswordHash().equals("Secret123!"));
        check("register puis authenticate", auth.authenticate("test.inscription@carrental.ma", "Secret123!") != null);
        try {
            auth.register("Doublon", "admin@carrental.ma", "x", "0600000000");
            check("e-mail déjà pris refusé", false);
        } catch (BusinessException e) {
            check("e-mail déjà pris refusé (" + e.getMessage() + ")", "error.email.taken".equals(e.getMessage()));
        }
        cleanUp(created.getId());

        // 4. Paramètres
        AppSettings s = settings.get();
        check("app_settings : TVA 0,07", s != null && s.getVatRate().compareTo(new BigDecimal("0.07")) == 0);
        try {
            settings.update(new BigDecimal("1.5"), new BigDecimal("0.15"));
            check("taux > 1 refusé", false);
        } catch (BusinessException e) {
            check("taux > 1 refusé (" + e.getMessage() + ")", true);
        }

        // 5. ML provisoire
        check("stub : predict = null", prediction.predict(1L) == null);
        System.out.println("   dernières métriques : " + prediction.getLatestMetrics());

        System.out.println(failures == 0 ? "\nTOUT EST OK" : "\n" + failures + " ÉCHEC(S)");
        System.exit(failures == 0 ? 0 : 1);
    }

    private static void check(String label, boolean ok) {
        System.out.println((ok ? "OK    " : "ÉCHEC ") + label);
        if (!ok) {
            failures++;
        }
    }

    /** Supprime l'utilisateur de test directement (UserDaoImpl.delete a besoin des entités de Fatima Zahra). */
    private static void cleanUp(Long id) {
        try (org.hibernate.Session session = ma.uir.awd.util.HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.delete(session.get(User.class, id));
            tx.commit();
        }
    }
}
