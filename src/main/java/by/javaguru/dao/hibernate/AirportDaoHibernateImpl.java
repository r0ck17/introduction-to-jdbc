package by.javaguru.dao.hibernate;

import by.javaguru.dao.AirportDao;
import by.javaguru.entity.Airport;
import by.javaguru.exception.DaoException;
import by.javaguru.util.ConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

@Slf4j
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
        log.debug("Saving entity = {}", entity);
        log.info("Saving entity to database");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(entity);

            Airport airport = session.get(Airport.class, entity.getCode());

            session.getTransaction().commit();

            return airport;
        } catch (HibernateException e) {
            log.error("Error on saving entity");
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(String key, Airport entity) {
        log.debug("Updating entity = {}. ID = {}", entity, key);
        log.info("Updating entity");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            entity.setCode(key);
            session.merge(entity);

            session.getTransaction().commit();

            return true;
        } catch (HibernateException e) {
            log.debug("{}", entity);
            log.error("Error on updating entity");
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(String id) {
        log.debug("Deleting entity with ID {}", id);
        log.info("Deleting entity");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Airport airport = session.get(Airport.class, id);
            session.remove(airport);

            session.getTransaction().commit();

            return airport != null;
        } catch (HibernateException e) {
            log.error("Error on deleting entity");
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Airport> findById(String id) {
        log.debug("ID = {}", id);
        log.info("Find entity by id");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Airport airport = session.get(Airport.class, id);

            session.getTransaction().commit();

            return Optional.ofNullable(airport);
        } catch (HibernateException e) {
            log.error("Error on find entity by ID");
            throw new DaoException(e);
        }
    }

    @Override
    public List<Airport> findAll() {
        log.info("Finding all entities");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            List<Airport> airports = session.createQuery("from Airport", Airport.class).getResultList();

            session.getTransaction().commit();

            return airports;
        } catch (HibernateException e) {
            log.error("Error on finding all entities");
            throw new DaoException(e);
        }
    }
}
