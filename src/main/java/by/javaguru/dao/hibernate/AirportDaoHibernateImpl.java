package by.javaguru.dao.hibernate;

import by.javaguru.dao.AirportDao;
import by.javaguru.entity.Airport;
import by.javaguru.exception.DaoException;
import by.javaguru.util.ConnectionManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class AirportDaoHibernateImpl implements AirportDao {
    private final SessionFactory sessionFactory = ConnectionManager.getSessionFactory();
    private static final AirportDaoHibernateImpl INSTANCE = new AirportDaoHibernateImpl();

    private AirportDaoHibernateImpl() {

    }

    public static AirportDaoHibernateImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Airport save(Airport entity) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(entity);

            Airport airport = session.get(Airport.class, entity.getCode());

            session.getTransaction().commit();

            return airport;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(String key, Airport entity) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            entity.setCode(key);
            session.merge(entity);

            session.getTransaction().commit();

            return true;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(String id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Airport airport = session.get(Airport.class, id);
            session.remove(airport);

            session.getTransaction().commit();

            return airport != null;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Airport> findById(String id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Airport airport = session.get(Airport.class, id);

            session.getTransaction().commit();

            return Optional.ofNullable(airport);
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Airport> findAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            List<Airport> airports = session.createQuery("from Airport", Airport.class).getResultList();

            session.getTransaction().commit();

            return airports;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }
}
