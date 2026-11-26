package ma.uir.awd.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import ma.uir.awd.model.Role;
import ma.uir.awd.model.User;
import ma.uir.awd.util.HibernateUtil;

/**
 * Implémentation Hibernate de IUserDao. Règles du Lab 7 pour chaque méthode :
 * - une Session ouverte avec try-with-resources (toujours fermée) ;
 * - chaque écriture dans une transaction validée ;
 * - HQL sur les classes et les champs (User, fullName), jamais sur les tables ;
 * - valeurs en paramètres nommés, jamais concaténées ;
 * - tri en HQL (order by), pas en Java.
 */
public class UserDaoImpl implements IUserDao {

    private final SessionFactory factory = HibernateUtil.getSessionFactory();

    @Override
    public void save(User u) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(u);
            tx.commit();
        }
    }

    @Override
    public User findById(Long id) {
        try (Session session = factory.openSession()) {
            return session.get(User.class, id);
        }
    }

    @Override
    public User findByEmail(String email) {
        try (Session session = factory.openSession()) {
            return session.createQuery("from User u where u.email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = factory.openSession()) {
            return session.createQuery("from User u order by u.fullName", User.class)
                    .getResultList();
        }
    }

    @Override
    public List<User> findByRole(Role role) {
        try (Session session = factory.openSession()) {
            return session.createQuery("from User u where u.role = :role order by u.fullName", User.class)
                    .setParameter("role", role)
                    .getResultList();
        }
    }

    @Override
    public void update(User u) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(u);   // u est détaché (lu dans une autre session) : update() le rattache
            tx.commit();
        }
    }

    /**
     * Comme CoachDao.delete au Lab 7 : on compte d'abord l'historique en HQL et on renvoie false,
     * au lieu de laisser la clé étrangère lever une exception.
     * Les entités Reservation, Rental et RentalExtension sont celles de Fatima Zahra (noms du contrat §1) :
     * cette méthode fonctionne dès qu'elles sont mappées.
     */
    @Override
    public boolean delete(Long id) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();

            User user = session.get(User.class, id);
            if (user == null) {
                tx.commit();
                return false;
            }

            long history = count(session, "select count(r) from Reservation r where r.customer.id = :id", id)
                    + count(session, "select count(r) from Rental r where r.handledByAgent.id = :id"
                            + " or r.inspectionValidatedBy.id = :id", id)
                    + count(session, "select count(e) from RentalExtension e where e.decidedBy.id = :id", id);
            if (history > 0) {
                tx.commit();
                return false;
            }

            session.delete(user);
            tx.commit();
            return true;
        }
    }

    private long count(Session session, String hql, Long id) {
        return session.createQuery(hql, Long.class).setParameter("id", id).getSingleResult();
    }
}
