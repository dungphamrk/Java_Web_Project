package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.technology.Status;
import ra.edu.entity.technology.Technology;

import java.util.List;

@Repository
public class TechnologyRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Technology> findAllActive(int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Query<Technology> query = session.createQuery("FROM Technology WHERE status = :status", Technology.class);
            query.setParameter("status", Status.ACTIVE);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.list();
        }
    }
    public List<Technology> findAllActiveWithoutLanding() {
        try (Session session = sessionFactory.openSession()) {
            Query<Technology> query = session.createQuery("FROM Technology WHERE status = :status", Technology.class);
            query.setParameter("status", Status.ACTIVE);
            return query.list();
        }
    }

    public List<Technology> findAllByIds(List<Integer> ids) {
        try (Session session = sessionFactory.openSession()) {
            Query<Technology> query = session.createQuery("FROM Technology WHERE id IN :ids", Technology.class);
            query.setParameter("ids", ids);
            return query.list();
        }
    }

    public List<Technology> findAll(int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Query<Technology> query = session.createQuery("FROM Technology", Technology.class);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.list();
        }
    }

    public long countActive() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Technology WHERE status = :status", Long.class);
            query.setParameter("status", Status.ACTIVE);
            return query.uniqueResult();
        }
    }

    public long countAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Technology", Long.class);
            return query.uniqueResult();
        }
    }

    public Technology findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Technology.class, id);
        }
    }

    public void save(Technology technology) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            if (technology.getId() == 0) {
                session.save(technology);
            } else {
                session.update(technology);
            }
            session.getTransaction().commit();
        }
    }

    public void updateStatus(int id, Status status) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Technology technology = session.get(Technology.class, id);
            if (technology != null) {
                technology.setStatus(status);
                session.update(technology);
            }
            session.getTransaction().commit();
        }
    }

    public boolean existsByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Technology WHERE name = :name", Long.class);
            query.setParameter("name", name);
            return query.uniqueResult() > 0;
        }
    }

    public List<Technology> searchByName(String keyword, int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Query<Technology> query = session.createQuery(
                    "FROM Technology WHERE lower(name) LIKE :kw AND status = :status", Technology.class);
            query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            query.setParameter("status", Status.ACTIVE);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.list();
        }
    }
    public long countByName(String keyword) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Technology WHERE lower(name) LIKE :kw AND status = :status", Long.class);
            query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            query.setParameter("status", Status.ACTIVE);
            return query.uniqueResult();
        }
    }


}