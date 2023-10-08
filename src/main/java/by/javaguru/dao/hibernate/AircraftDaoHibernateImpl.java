package by.javaguru.dao.hibernate;

import by.javaguru.dao.AircraftDao;
import by.javaguru.entity.Aircraft;
import by.javaguru.exception.DaoException;
import by.javaguru.util.ConnectionManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class AircraftDaoHibernateImpl implements AircraftDao {
    private final SessionFactory sessionFactory = ConnectionManager.getSessionFactory();
    private static final AircraftDaoHibernateImpl INSTANCE = new AircraftDaoHibernateImpl();

    private AircraftDaoHibernateImpl() {

    }

    public static AircraftDaoHibernateImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Aircraft save(Aircraft entity) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(entity);

            Aircraft aircraft = session.get(Aircraft.class, entity.getId());

            session.getTransaction().commit();

            return aircraft;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Integer key, Aircraft entity) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            entity.setId(key);
            session.merge(entity);

            session.getTransaction().commit();

            return true;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Aircraft aircraft = session.get(Aircraft.class, id);
            session.remove(aircraft);

            session.getTransaction().commit();

            return aircraft != null;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Aircraft> findById(Integer id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Aircraft aircraft = session.get(Aircraft.class, id);

            session.getTransaction().commit();

            return Optional.ofNullable(aircraft);
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Aircraft> findAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            List<Aircraft> aircrafts = session.createQuery("from Aircraft", Aircraft.class).getResultList();

            session.getTransaction().commit();

            return aircrafts;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }
}
