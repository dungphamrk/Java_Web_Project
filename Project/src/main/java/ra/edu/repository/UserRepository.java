package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.user.User;

import javax.transaction.Transactional;

@Repository
public class UserRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public User findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("FROM User  WHERE candidate.email = :email", User.class);
        query.setParameter("email", email);
        return query.uniqueResult();
    }


    public User findByUsernameAndPassword(String username, String password) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery(
                "FROM User WHERE username = :username AND password = :password", User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.uniqueResult();
    }

    public User findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }

    public int save(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.save(user);
        return user.getId();
    }

    public User getUserById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(User.class, id);
    }

    public int updateStatus(int id, String newStatus) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("UPDATE User u SET u.status = :status WHERE u.id = :id");
        query.setParameter("status", newStatus);
        query.setParameter("id", id);
        return query.executeUpdate();
    }
}
