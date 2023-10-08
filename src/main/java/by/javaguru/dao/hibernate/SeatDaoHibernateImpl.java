package by.javaguru.dao.hibernate;

import by.javaguru.dao.SeatDao;
import by.javaguru.entity.Seat;
import by.javaguru.exception.DaoException;
import by.javaguru.util.ConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

@Slf4j
public class SeatDaoHibernateImpl implements SeatDao {
    private final SessionFactory sessionFactory = ConnectionManager.getSessionFactory();
    private static final SeatDaoHibernateImpl INSTANCE = new SeatDaoHibernateImpl();

    private SeatDaoHibernateImpl() {

    }

    public static SeatDaoHibernateImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Seat save(Seat entity) {
        log.debug("Saving entity = {}", entity);
        log.info("Saving entity to database");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(entity);

            Seat seat = session.get(Seat.class, entity.getSeatId());

            session.getTransaction().commit();

            return seat;
        } catch (HibernateException e) {
            log.error("Error on saving entity");
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Seat key, Seat entity) {
        log.debug("Updating entity = {}. ID = {}", entity, key);
        log.info("Updating entity");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            session.remove(key);
            session.persist(entity);

            session.getTransaction().commit();

            return true;
        } catch (HibernateException e) {
            log.debug("{}", entity);
            log.error("Error on updating entity");
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Seat id) {
        log.debug("Deleting entity with ID {}", id);
        log.info("Deleting entity");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Seat seat = session.get(Seat.class, id.getSeatId());
            session.remove(seat);

            session.getTransaction().commit();

            return seat != null;
        } catch (HibernateException e) {
            log.error("Error on deleting entity");
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Seat> findById(Seat id) {
        log.debug("ID = {}", id);
        log.info("Find entity by id");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Seat seat = session.get(Seat.class, id.getSeatId());

            session.getTransaction().commit();

            return Optional.ofNullable(seat);
        } catch (HibernateException e) {
            log.error("Error on find entity by ID");
            throw new DaoException(e);
        }
    }

    @Override
    public List<Seat> findAll() {
        log.info("Finding all entities");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            List<Seat> seats = session.createQuery("from Seat ", Seat.class).getResultList();

            session.getTransaction().commit();

            return seats;
        } catch (HibernateException e) {
            log.error("Error on finding all entities");
            throw new DaoException(e);
        }
    }
}
