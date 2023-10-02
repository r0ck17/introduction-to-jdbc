package by.javaguru.dao;

import by.javaguru.entity.Ticket;
import by.javaguru.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDao implements Dao<Long, Ticket> {
    private static Connection connection = ConnectionManager.open();
    private static final Logger logger = LoggerFactory.getLogger(TicketDao.class);

    private static final String INSERT_SQL = """
            INSERT INTO ticket (passport_no, passenger_name, flight_id, seat_no, cost)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String DELETE_SQL = """
            DELETE FROM ticket
            WHERE id = ?
            """;

    private static final String UPDATE_SQL = """
            UPDATE ticket
            SET
                passport_no = ?,
                passenger_name = ?,
                flight_id = ?,
                seat_no = ?,
                cost = ?
            WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id, passport_no, passenger_name, flight_id, seat_no, cost
            FROM ticket
            """;
    private static final String FIND_BY_ID_SQL =
            FIND_ALL_SQL + "WHERE ID = ?";

    @Override
    public Ticket save(Ticket ticket) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            logger.info("Saving ticket to database");
            logger.debug("{}", ticket);
            statement.setString(1, ticket.getPassportNo());
            statement.setString(2, ticket.getPassengerName());
            statement.setLong(3, ticket.getFlightId());
            statement.setString(4, ticket.getSeatNo());
            statement.setInt(5, ticket.getCost());

            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                logger.info("Saving ticket now have ID {}", generatedKeys.getLong(1));
                ticket.setId(generatedKeys.getLong(1));
            }

            return ticket;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(Ticket ticket) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            logger.info("Updating ticket with ID {}", ticket.getId());
            statement.setString(1, ticket.getSeatNo());
            statement.setString(2, ticket.getPassengerName());
            statement.setLong(3, ticket.getFlightId());
            statement.setString(4, ticket.getSeatNo());
            statement.setInt(5, ticket.getCost());
            statement.setLong(6, ticket.getId());

            logger.debug("{}", ticket);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            logger.info("Deleting ticket with ID {}", id);
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            logger.info("Starting to find ticket with ID {}", id);
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            Ticket ticket = null;

            if (result.next()) {
                ticket = readTicket(result);
            }
            logger.debug("{}", ticket);

            return Optional.ofNullable(ticket);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Ticket> findAll() {
        try (Statement statement = connection.createStatement()) {
            logger.info("Starting to find all tickets");
            List<Ticket> tickets = new ArrayList<>();
            ResultSet result = statement.executeQuery(FIND_ALL_SQL);

            while (result.next()) {
                tickets.add(readTicket(result));
            }
            logger.debug("Found {} tickets", tickets.size());

            return tickets;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Ticket readTicket(ResultSet result) throws SQLException {
        return Ticket.builder()
                .id(result.getLong("id"))
                .passportNo(result.getString("passport_no"))
                .passengerName(result.getString("passenger_name"))
                .flightId(result.getLong("flight_id"))
                .seatNo(result.getString("seat_no"))
                .cost(result.getInt("cost"))
                .build();
    }
}
