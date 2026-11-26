package ma.uir.awd.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import ma.uir.awd.model.ModelMetrics;
import ma.uir.awd.model.ModelType;
import ma.uir.awd.util.HibernateUtil;

/** Implémentation Hibernate de IModelMetricsDao. Les règles de UserDaoImpl s'appliquent. */
public class ModelMetricsDaoImpl implements IModelMetricsDao {

    private final SessionFactory factory = HibernateUtil.getSessionFactory();

    @Override
    public void save(ModelMetrics m) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(m);
            tx.commit();
        }
    }

    @Override
    public ModelMetrics findLatest(ModelType type) {
        try (Session session = factory.openSession()) {
            return session.createQuery(
                            "from ModelMetrics m where m.modelType = :type order by m.trainedAt desc, m.id desc",
                            ModelMetrics.class)
                    .setParameter("type", type)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }

    @Override
    public List<ModelMetrics> findByType(ModelType type) {
        try (Session session = factory.openSession()) {
            return session.createQuery(
                            "from ModelMetrics m where m.modelType = :type order by m.trainedAt desc, m.id desc",
                            ModelMetrics.class)
                    .setParameter("type", type)
                    .getResultList();
        }
    }
}
