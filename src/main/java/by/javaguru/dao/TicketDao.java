package by.javaguru.dao;

import by.javaguru.dto.TicketUpdateInfo;
import by.javaguru.entity.Ticket;
import by.javaguru.dto.TicketFilter;
import by.javaguru.exception.DaoException;
import by.javaguru.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class TicketDao implements Dao<Long, Ticket> {
    private static final TicketDao INSTANCE = new TicketDao();
    private static final Connection connection = ConnectionManager.open();
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
    private static final String COMMON_NAMES_SQL = """
            SELECT split_part(passenger_name, ' ', 1) AS name,  count(*) AS count
            FROM ticket
            GROUP BY name
            ORDER BY count DESC, name
            LIMIT ?
            """;
    private static final String COUNT_TICKETS_SQL = """
            SELECT passenger_name, count(*) AS ticket_count
            FROM ticket
            GROUP BY passport_no, passenger_name
            ORDER BY ticket_count DESC;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, passport_no, passenger_name, flight_id, seat_no, cost
            FROM ticket
            """;
    private static final String FILTERED_UPDATE_SQL = """
            UPDATE ticket
            SET %s
            WHERE %s
            """;
    private static final String FIND_BY_ID_SQL =
            FIND_ALL_SQL + "WHERE ID = ?";

    private static final String FIND_BY_FLIGHT_ID_SQL =
            FIND_ALL_SQL + "WHERE flight_id = ?";

    private TicketDao() {
    }

    public static TicketDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Ticket save(Ticket ticket) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                Statement.RETURN_GENERATED_KEYS)) {
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
                logger.info("Ticket was saved. Ticket ID = {}", generatedKeys.getLong(1));
                ticket.setId(generatedKeys.getLong(1));
                return ticket;
            }

            return null;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Long id, Ticket ticket) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            logger.info("Updating ticket with ID {}", id);

            statement.setString(1, ticket.getSeatNo());
            statement.setString(2, ticket.getPassengerName());
            statement.setLong(3, ticket.getFlightId());
            statement.setString(4, ticket.getSeatNo());
            statement.setInt(5, ticket.getCost());
            statement.setLong(6, id);

            logger.debug("Ticket with ID {} after updating {}", id, ticket);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            logger.info("Deleting ticket with ID {}", id);
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
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
            throw new DaoException(e);
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
            throw new DaoException(e);
        }
    }

    public int updateTickets(TicketFilter ticketFilter, TicketUpdateInfo updateInfo) {
        Map<String, Object> whereParams = new HashMap<>();
        Map<String, Object> setParams = new HashMap<>();

        if (ticketFilter.getPassportNo() != null) {
            whereParams.put("passport_no", "'" + ticketFilter.getPassportNo() + "'");
        }

        if (ticketFilter.getPassengerName() != null) {
            whereParams.put("passenger_name", "'" + ticketFilter.getPassengerName() + "'");
        }

        if (ticketFilter.getFlightId() != null) {
            whereParams.put("flight_id", ticketFilter.getFlightId());
        }

        if (ticketFilter.getSeatNo() != null) {
            whereParams.put("seat_no", "'" + ticketFilter.getSeatNo() + "'");
        }

        if (ticketFilter.getCost() != null) {
            whereParams.put("cost", ticketFilter.getCost());
        }

        setParams.put("cost", updateInfo.getCost());
        String setSql = setParams.entrySet()
                .stream().map(e -> e.getKey() + " = " + e.getValue())
                .collect(Collectors.joining(" , "));

        String whereSql = whereParams.entrySet()
                .stream().map(e -> e.getKey() + " = " + e.getValue())
                .collect(Collectors.joining(" AND "));

        try (PreparedStatement statement = connection.prepareStatement(
                FILTERED_UPDATE_SQL.formatted(setSql, whereSql))) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ticket> findTicketsByFlightId(Long flightId) {
        try (PreparedStatement statement =
                     connection.prepareStatement(FIND_BY_FLIGHT_ID_SQL)) {
            logger.info("Starting to find all tickets with flight_id = {}", flightId);
            List<Ticket> tickets = new ArrayList<>();
            statement.setLong(1, flightId);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                tickets.add(readTicket(result));
            }

            logger.debug("Found {} tickets", tickets.size());
            return tickets;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<String> findMostCommonNames(int limit) {
        try (PreparedStatement statement =
                     connection.prepareStatement(COMMON_NAMES_SQL)) {
            logger.info("Finding first {} most common names", limit);

            List<String> names = new ArrayList<>();

            statement.setInt(1, limit);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
            }

            logger.debug("Found {} names", names.size());
            return names;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> findPassengerTotalTicketCount() {
        try (Statement statement = connection.createStatement()) {
            logger.info("Finding ticket counts for every user");

            Map<String, Integer> tickets = new HashMap<>();

            ResultSet result = statement.executeQuery(COUNT_TICKETS_SQL);
            while (result.next()) {
                tickets.put(result.getString("passenger_name"),
                        result.getInt("ticket_count"));
            }

            logger.debug("Counted tickets for {} names", tickets.keySet().size());
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
