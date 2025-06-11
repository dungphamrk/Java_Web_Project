package ra.edu.repository.candidate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.candidate.Candidate;

import java.util.List;

@Repository
public class CandidateRepositoryImp implements CandidateRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public int changePassword(Integer userId, String oldPassword, String newPassword, String phone) {
        Session session = sessionFactory.getCurrentSession();
        Candidate candidate = session.get(Candidate.class, userId);
        if (candidate != null && candidate.getPhone().equals(phone)) {
            Query query = session.createQuery("UPDATE User u SET u.password = :newPassword " +
                    "WHERE u.id = :userId AND u.password = :oldPassword");
            query.setParameter("newPassword", newPassword);
            query.setParameter("userId", userId);
            query.setParameter("oldPassword", oldPassword);
            return query.executeUpdate();
        }
        return 0;
    }



    @Override
    public List<Candidate> searchByName(String name, int pageNumber, int pageSize) {
        Session session = sessionFactory.getCurrentSession();
        Query<Candidate> query = session.createQuery(
                "FROM Candidate c WHERE c.name LIKE :name", Candidate.class);
        query.setParameter("name", "%" + name + "%");
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public List<Candidate> filterByExperience(int experience, int pageNumber, int pageSize) {
        Session session = sessionFactory.getCurrentSession();
        Query<Candidate> query = session.createQuery(
                "FROM Candidate c WHERE c.experience = :experience", Candidate.class);
        query.setParameter("experience", experience);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

//    @Override
//    public List<Candidate> filterByAge(int age, int pageNumber, int pageSize) {
//        Session session = sessionFactory.getCurrentSession();
//        Query<Candidate> query = session.createQuery(
//                "FROM Candidate c WHERE c.age = :age", Candidate.class);
//        query.setParameter("age", age);
//        query.setFirstResult((pageNumber - 1) * pageSize);
//        query.setMaxResults(pageSize);
//        return query.getResultList();
//    }

    @Override
    public List<Candidate> filterByGender(String gender, int pageNumber, int pageSize) {
        Session session = sessionFactory.getCurrentSession();
        Query<Candidate> query = session.createQuery(
                "FROM Candidate c WHERE c.gender = :gender", Candidate.class);
        query.setParameter("gender", gender);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

//    @Override
//    public List<Candidate> filterByTechnology(String technology, int pageNumber, int pageSize) {
//        Session session = sessionFactory.getCurrentSession();
//        Query<Candidate> query = session.createQuery(
//                "FROM Candidate c WHERE c.technology LIKE :technology", Candidate.class);
//        query.setParameter("technology", "%" + technology + "%");
//        query.setFirstResult((pageNumber - 1) * pageSize);
//        query.setMaxResults(pageSize);
//        return query.getResultList();
//    }

    @Override
    public int resetPassword(int userId, String newPassword) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "UPDATE User u SET u.password = :newPassword WHERE u.id = :userId");
        query.setParameter("newPassword", newPassword);
        query.setParameter("userId", userId);
        return query.executeUpdate();
    }

    @Override
    public Candidate getCandidateById(int userId) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Candidate.class, userId);
    }

    @Override
    public int getTotalCandidatesCount() {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Candidate", Long.class);
        return query.uniqueResult().intValue();
    }

    @Override
    public int getTotalCandidatesByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Candidate c WHERE c.name LIKE :name", Long.class);
        query.setParameter("name", "%" + name + "%");
        return query.uniqueResult().intValue();
    }

    @Override
    public int getTotalCandidatesByExperience(int experience) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Candidate c WHERE c.experience = :experience", Long.class);
        query.setParameter("experience", experience);
        return query.uniqueResult().intValue();
    }

//    @Override
//    public int getTotalCandidatesByAge(int age) {
//        Session session = sessionFactory.getCurrentSession();
//        Query<Long> query = session.createQuery(
//                "SELECT COUNT(*) FROM Candidate c WHERE c.age = :age", Long.class);
//        query.setParameter("age", age);
//        return query.uniqueResult().intValue();
//    }

    @Override
    public int getTotalCandidatesByGender(String gender) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Candidate c WHERE c.gender = :gender", Long.class);
        query.setParameter("gender", gender);
        return query.uniqueResult().intValue();
    }

//    @Override
//    public int getTotalCandidatesByTechnology(String technology) {
//        Session session = sessionFactory.getCurrentSession();
//        Query<Long> query = session.createQuery(
//                "SELECT COUNT(*) FROM Candidate c WHERE c.technology LIKE :technology", Long.class);
//        query.setParameter("technology", "%" + technology + "%");
//        return query.uniqueResult().intValue();
//    }

    public void save(Candidate candidate) {
        Session session = sessionFactory.getCurrentSession();
        session.save(candidate);
    }

    public Candidate findById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Candidate.class, id);
    }
}