package ma.uir.awd.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import ma.uir.awd.model.AppSettings;
import ma.uir.awd.util.HibernateUtil;

/** Implémentation Hibernate de IAppSettingsDao. Les règles de UserDaoImpl s'appliquent. */
public class AppSettingsDaoImpl implements IAppSettingsDao {

    private final SessionFactory factory = HibernateUtil.getSessionFactory();

    @Override
    public AppSettings find() {
        try (Session session = factory.openSession()) {
            return session.createQuery("from AppSettings s order by s.id", AppSettings.class)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }

    @Override
    public void update(AppSettings s) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(s);
            tx.commit();
        }
    }
}
