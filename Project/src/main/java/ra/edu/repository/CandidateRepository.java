package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.candidate.Candidate;
import ra.edu.entity.user.Status;
import ra.edu.entity.user.User;

import java.util.List;

@Repository
public class CandidateRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Candidate> findAll(int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Query<Candidate> query = session.createQuery("FROM Candidate", Candidate.class);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.list();
        }
    }

    public long countAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Candidate", Long.class);
            return query.uniqueResult();
        }
    }

    public Candidate findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Candidate.class, id);
        }
    }

    public void save(Candidate candidate) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            if (candidate.getId() == 0) {
                session.save(candidate);
            } else {
                Candidate existing = session.get(Candidate.class, candidate.getId());
                if (existing != null) {
                    session.merge(candidate); // hoặc session.update(candidate);
                } else {
                    // Trường hợp không có trong DB thì lưu mới
                    candidate.setId(0); // đặt lại id để Hibernate thực hiện insert
                    session.save(candidate);
                }
            }
            session.getTransaction().commit();
        }
    }

    public void delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Candidate candidate = session.get(Candidate.class, id);
            if (candidate != null) {
                session.delete(candidate);
            }
            session.getTransaction().commit();
        }
    }

    public boolean existsByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Candidate WHERE email = :email", Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        }
    }

    public List<Candidate> searchByName(String keyword, int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Query<Candidate> query = session.createQuery(
                    "FROM Candidate WHERE lower(name) LIKE :kw", Candidate.class);
            query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.list();
        }
    }

    public long countByName(String keyword) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Candidate WHERE lower(name) LIKE :kw", Long.class);
            query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            return query.uniqueResult();
        }
    }

    public void toggleUserStatusByCandidateId(int candidateId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Candidate candidate = session.get(Candidate.class, candidateId);
            if (candidate != null && candidate.getUser() != null) {
                User user = candidate.getUser();
                if (user.getStatus() == Status.ACTIVE) {
                    user.setStatus(Status.INACTIVE);
                } else {
                    user.setStatus(Status.ACTIVE);
                }
                session.update(user); // hoặc session.update(candidate);
            }

            session.getTransaction().commit();
        }
    }

    public List<Candidate> filterCandidates(String keyword, String technology, String gender, String age, String experience, int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("SELECT DISTINCT c FROM Candidate c JOIN c.technologyList t WHERE 1=1");

            if (keyword != null && !keyword.isEmpty()) {
                hql.append(" AND lower(c.name) LIKE :keyword");
            }
            if (technology != null && !technology.isEmpty()) {
                hql.append(" AND t.name = :technology");
            }
            if (gender != null && !gender.isEmpty()) {
                hql.append(" AND c.gender = :gender");
            }
            if (age != null && !age.isEmpty()) {
                hql.append(" AND ");
                switch (age) {
                    case "18-24":
                        hql.append("YEAR(current_date()) - YEAR(c.dob) BETWEEN 18 AND 24");
                        break;
                    case "25-30":
                        hql.append("YEAR(current_date()) - YEAR(c.dob) BETWEEN 25 AND 30");
                        break;
                    case "31-40":
                        hql.append("YEAR(current_date()) - YEAR(c.dob) BETWEEN 31 AND 40");
                        break;
                    case "41+":
                        hql.append("YEAR(current_date()) - YEAR(c.dob) >= 41");
                        break;
                }
            }
            if (experience != null && !experience.isEmpty()) {
                hql.append(" AND ");
                switch (experience) {
                    case "0-1":
                        hql.append("c.experience BETWEEN 0 AND 1");
                        break;
                    case "2-3":
                        hql.append("c.experience BETWEEN 2 AND 3");
                        break;
                    case "4-5":
                        hql.append("c.experience BETWEEN 4 AND 5");
                        break;
                    case "5+":
                        hql.append("c.experience >= 5");
                        break;
                }
            }

            Query<Candidate> query = session.createQuery(hql.toString(), Candidate.class);

            if (keyword != null && !keyword.isEmpty()) {
                query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
            }
            if (technology != null && !technology.isEmpty()) {
                query.setParameter("technology", technology);
            }
            if (gender != null && !gender.isEmpty()) {
                query.setParameter("gender", ra.edu.entity.candidate.Gender.valueOf(gender));
            }

            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.list();
        }
    }

    public long countFilteredCandidates(String keyword, String technology, String gender, String age, String experience) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("SELECT COUNT(DISTINCT c) FROM Candidate c JOIN c.technologyList t WHERE 1=1");

            if (keyword != null && !keyword.isEmpty()) {
                hql.append(" AND lower(c.name) LIKE :keyword");
            }
            if (technology != null && !technology.isEmpty()) {
                hql.append(" AND t.name = :technology");
            }
            if (gender != null && !gender.isEmpty()) {
                hql.append(" AND c.gender = :gender");
            }
            if (age != null && !age.isEmpty()) {
                hql.append(" AND ");
                switch (age) {
                    case "18-24":
                        hql.append("YEAR(current_date()) - YEAR(c.dob) BETWEEN 18 AND 24");
                        break;
                    case "25-30":
                        hql.append("YEAR(current_date()) - YEAR(c.dob) BETWEEN 25 AND 30");
                        break;
                    case "31-40":
                        hql.append("YEAR(current_date()) - YEAR(c.dob) BETWEEN 31 AND 40");
                        break;
                    case "41+":
                        hql.append("YEAR(current_date()) - YEAR(c.dob) >= 41");
                        break;
                }
            }
            if (experience != null && !experience.isEmpty()) {
                hql.append(" AND ");
                switch (experience) {
                    case "0-1":
                        hql.append("c.experience BETWEEN 0 AND 1");
                        break;
                    case "2-3":
                        hql.append("c.experience BETWEEN 2 AND 3");
                        break;
                    case "4-5":
                        hql.append("c.experience BETWEEN 4 AND 5");
                        break;
                    case "5+":
                        hql.append("c.experience >= 5");
                        break;
                }
            }

            Query<Long> query = session.createQuery(hql.toString(), Long.class);

            if (keyword != null && !keyword.isEmpty()) {
                query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
            }
            if (technology != null && !technology.isEmpty()) {
                query.setParameter("technology", technology);
            }
            if (gender != null && !gender.isEmpty()) {
                query.setParameter("gender", ra.edu.entity.candidate.Gender.valueOf(gender));
            }

            return query.uniqueResult();
        }
    }

    public Candidate findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Candidate c WHERE c.user.username = :username";
            Query<Candidate> query = session.createQuery(hql, Candidate.class);
            query.setParameter("username", username);
            return query.uniqueResult(); // Trả về 1 Candidate duy nhất
        }
    }

}