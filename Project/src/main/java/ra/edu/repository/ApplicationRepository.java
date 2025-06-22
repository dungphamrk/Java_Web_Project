package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.application.Application;
import ra.edu.entity.application.Progress;

import java.util.List;

@Repository
public class ApplicationRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Application> findAll(int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Query<Application> query = session.createQuery(
                    "FROM Application ", Application.class);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            List<Application> applications = query.getResultList();
            return applications;
        }
    }

    public long countAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Application WHERE progress != :progress", Long.class);
            query.setParameter("progress", Progress.CANCEL);
            return query.uniqueResult();
        }
    }

    public Application findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Application.class, id);
        }
    }

    public void save(Application application) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            if (application.getId() == 0) {
                session.save(application);
            } else {
                session.update(application);
            }
            session.getTransaction().commit();
        }
    }



    public void updateProgress(int id, Progress progress, String destroyReason) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Application application = session.get(Application.class, id);
            if (application != null) {
                application.setProgress(progress);
                if (progress == Progress.REJECT || progress == Progress.CANCEL) {
                    application.setDestroyReason(destroyReason);
                    application.setDestroyAt(application.getUpdateAt());
                }
                session.update(application);
            }
            session.getTransaction().commit();
        }
    }

    public List<Application> filterApplications(String keyword, String progress, int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("FROM Application a WHERE 1=1");

            if (keyword != null && !keyword.isEmpty()) {
                hql.append(" AND (lower(a.candidate.name) LIKE :keyword OR lower(a.cvUrl) LIKE :keyword)");
            }

            if (progress != null && !progress.isEmpty()) {
                hql.append(" AND a.progress = :progress");
            }

            Query<Application> query = session.createQuery(hql.toString(), Application.class);

            if (keyword != null && !keyword.isEmpty()) {
                query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
            }

            if (progress != null && !progress.isEmpty()) {
                query.setParameter("progress", ra.edu.entity.application.Progress.valueOf(progress));
            }

            query.setFirstResult(page * size);
            query.setMaxResults(size);

            return query.list();
        }
    }

    public long countFilteredApplications(String keyword, String progress) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("SELECT COUNT(*) FROM Application a WHERE 1=1");

            if (keyword != null && !keyword.isEmpty()) {
                hql.append(" AND (lower(a.candidate.name) LIKE :keyword OR lower(a.cvUrl) LIKE :keyword)");
            }

            if (progress != null && !progress.isEmpty()) {
                hql.append(" AND a.progress = :progress");
            }

            Query<Long> query = session.createQuery(hql.toString(), Long.class);

            if (keyword != null && !keyword.isEmpty()) {
                query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
            }

            if (progress != null && !progress.isEmpty()) {
                query.setParameter("progress", ra.edu.entity.application.Progress.valueOf(progress));
            }

            return query.uniqueResult();
        }
    }


    public List<Application> findByUserAndFilter(String username, String keyword, String progress, int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("FROM Application a WHERE a.candidate.username = :username");

            if (keyword != null && !keyword.isEmpty()) {
                hql.append(" AND a.recruitmentPosition.name LIKE :keyword");
            }

            if (progress != null && !progress.isEmpty()) {
                hql.append(" AND a.progress = :progress");
            }

            Query<Application> query = session.createQuery(hql.toString(), Application.class);
            query.setParameter("username", username);

            if (keyword != null && !keyword.isEmpty()) {
                query.setParameter("keyword", "%" + keyword + "%");
            }

            if (progress != null && !progress.isEmpty()) {
                query.setParameter("progress", Progress.valueOf(progress));
            }

            query.setFirstResult(page * size);
            query.setMaxResults(size);

            return query.getResultList();
        }
    }

    public long countByUserAndFilter(String username, String keyword, String progress) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("SELECT COUNT(a) FROM Application a WHERE a.candidate.username = :username");

            if (keyword != null && !keyword.isEmpty()) {
                hql.append(" AND a.recruitmentPosition.name LIKE :keyword");
            }

            if (progress != null && !progress.isEmpty()) {
                hql.append(" AND a.progress = :progress");
            }

            Query<Long> query = session.createQuery(hql.toString(), Long.class);
            query.setParameter("username", username);

            if (keyword != null && !keyword.isEmpty()) {
                query.setParameter("keyword", "%" + keyword + "%");
            }

            if (progress != null && !progress.isEmpty()) {
                query.setParameter("progress", Progress.valueOf(progress));
            }

            return query.uniqueResult();
        }
    }
}