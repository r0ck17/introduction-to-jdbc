package by.javaguru.dao.hibernate;

import by.javaguru.dao.FlightDao;
import by.javaguru.entity.Flight;
import by.javaguru.exception.DaoException;
import by.javaguru.util.ConnectionManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class FlightDaoHibernateImpl implements FlightDao {
    private final SessionFactory sessionFactory = ConnectionManager.getSessionFactory();
    private static final FlightDaoHibernateImpl INSTANCE = new FlightDaoHibernateImpl();

    private FlightDaoHibernateImpl() {

    }

    public static FlightDaoHibernateImpl getInstance() {
        return INSTANCE;
    }
    @Override
    public Flight save(Flight entity) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(entity);

            Flight flight = session.get(Flight.class, entity.getId());

            session.getTransaction().commit();

            return flight;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Long key, Flight entity) {
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
    public boolean delete(Long id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Flight flight = session.get(Flight.class, id);
            session.remove(flight);

            session.getTransaction().commit();

            return flight != null;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Flight> findById(Long id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Flight flight = session.get(Flight.class, id);

            session.getTransaction().commit();

            return Optional.ofNullable(flight);
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Flight> findAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            List<Flight> flights = session.createQuery("from Flight", Flight.class).getResultList();

            session.getTransaction().commit();

            return flights;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }
}
