package by.javaguru.dao.hibernate;

import by.javaguru.dao.TicketDao;
import by.javaguru.entity.Ticket;
import by.javaguru.exception.DaoException;
import by.javaguru.util.ConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

@Slf4j
public class TicketDaoHibernateImpl implements TicketDao {
    private final SessionFactory sessionFactory = ConnectionManager.getSessionFactory();
    private static final TicketDaoHibernateImpl INSTANCE = new TicketDaoHibernateImpl();

    private TicketDaoHibernateImpl() {

    }

    public static TicketDaoHibernateImpl getInstance() {
        return INSTANCE;
    }


    @Override
    public Ticket save(Ticket entity) {
        log.debug("Saving entity = {}", entity);
        log.info("Saving entity to database");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(entity);

            Ticket ticket = session.get(Ticket.class, entity.getId());

            session.getTransaction().commit();

            return ticket;
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Long key, Ticket entity) {
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
    public boolean delete(Long id) {
        log.debug("Deleting entity with ID {}", id);
        log.info("Deleting entity");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Ticket ticket = session.get(Ticket.class, id);
            session.remove(ticket);

            session.getTransaction().commit();

            return ticket != null;
        } catch (HibernateException e) {
            log.error("Error on deleting entity");
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        log.debug("ID = {}", id);
        log.info("Find entity by id");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Ticket ticket = session.get(Ticket.class, id);

            session.getTransaction().commit();

            return Optional.ofNullable(ticket);
        } catch (HibernateException e) {
            log.error("Error on find entity by ID");
            throw new DaoException(e);
        }
    }

    @Override
    public List<Ticket> findAll() {
        log.info("Finding all entities");
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            List<Ticket> tickets = session.createQuery("from Ticket", Ticket.class).getResultList();

            session.getTransaction().commit();

            return tickets;
        } catch (HibernateException e) {
            log.error("Error on finding all entities");
            throw new DaoException(e);
        }
    }
}
