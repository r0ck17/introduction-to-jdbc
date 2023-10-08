package by.javaguru.dao.hibernate;

import by.javaguru.dao.SeatDao;
import by.javaguru.entity.Seat;
import by.javaguru.exception.DaoException;
import by.javaguru.util.ConnectionManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

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
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(entity);

            Seat seat = session.get(Seat.class, entity.getSeatId());

            session.getTransaction().commit();

            return seat;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Seat key, Seat entity) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            session.remove(key);
            session.persist(entity);

            session.getTransaction().commit();

            return true;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Seat id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Seat seat = session.get(Seat.class, id.getSeatId());
            session.remove(seat);

            session.getTransaction().commit();

            return seat != null;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Seat> findById(Seat id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Seat seat = session.get(Seat.class, id.getSeatId());

            session.getTransaction().commit();

            return Optional.ofNullable(seat);
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Seat> findAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            List<Seat> seats = session.createQuery("from Seat ", Seat.class).getResultList();

            session.getTransaction().commit();

            return seats;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }
}
