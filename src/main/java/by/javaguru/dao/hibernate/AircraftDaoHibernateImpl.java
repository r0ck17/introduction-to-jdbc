package by.javaguru.dao.hibernate;

import by.javaguru.dao.AircraftDao;
import by.javaguru.entity.Aircraft;
import by.javaguru.exception.DaoException;
import by.javaguru.util.ConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

@Slf4j
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
        log.debug("Saving entity = {}", entity);
        log.info("Saving entity to database");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(entity);

            Aircraft aircraft = session.get(Aircraft.class, entity.getId());

            session.getTransaction().commit();

            return aircraft;
        } catch (HibernateException e) {
            log.error("Error on saving entity");
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Integer key, Aircraft entity) {
        log.debug("Updating entity = {}. ID = {}", entity, key);
        log.info("Updating entity");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            entity.setId(key);
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
    public boolean delete(Integer id) {
        log.debug("Deleting entity with ID = {}", id);
        log.info("Deleting entity");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Aircraft aircraft = session.get(Aircraft.class, id);
            session.remove(aircraft);

            session.getTransaction().commit();

            return aircraft != null;
        } catch (HibernateException e) {
            log.error("Error on deleting entity");
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Aircraft> findById(Integer id) {
        log.debug("ID = {}", id);
        log.info("Find entity by id");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Aircraft aircraft = session.get(Aircraft.class, id);

            session.getTransaction().commit();

            return Optional.ofNullable(aircraft);

        } catch (HibernateException e) {
            log.error("Error on find entity by ID");
            throw new DaoException(e);
        }
    }

    @Override
    public List<Aircraft> findAll() {
        log.info("Finding all entities");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            List<Aircraft> aircrafts = session.createQuery("from Aircraft", Aircraft.class).getResultList();

            session.getTransaction().commit();

            return aircrafts;
        } catch (HibernateException e) {
            log.error("Error on finding all entities");
            throw new DaoException(e);
        }
    }
}
