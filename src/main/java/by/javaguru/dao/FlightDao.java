package by.javaguru.dao;

import by.javaguru.entity.Flight;
import by.javaguru.exception.DaoException;
import by.javaguru.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightDao implements Dao<Long, Flight> {
    private static final FlightDao INSTANCE = new FlightDao();
    private static final Connection connection = ConnectionManager.open();
    private static final Logger logger = LoggerFactory.getLogger(FlightDao.class);
    private static final String INSERT_SQL = """
            INSERT INTO flight (flight_no, departure_date, departure_airport_code, arrival_date, arrival_airport_code, aircraft_id, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String DELETE_SQL = """
            DELETE FROM flight
            WHERE id = ?
            """;

    private static final String UPDATE_SQL = """
            UPDATE flight
            SET
                flight_no = ?,
                departure_date = ?,
                departure_airport_code = ?,
                arrival_date = ?,
                arrival_airport_code = ?,
                aircraft_id = ?,
                status = ?
            WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id, flight_no, departure_date, departure_airport_code, arrival_date, arrival_airport_code, aircraft_id, status
            FROM flight
            """;
    private static final String FIND_BY_ID_SQL =
            FIND_ALL_SQL + "WHERE ID = ?";

    private FlightDao() {
    }

    public static FlightDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Flight save(Flight flight) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            logger.info("Saving flight to database");
            logger.debug("{}", flight);

            statement.setString(1, flight.getFlightNo());
            statement.setTimestamp(2, Timestamp.valueOf(flight.getDepartureDate()));
            statement.setString(3, flight.getDepartureAirportCode());
            statement.setTimestamp(4, Timestamp.valueOf(flight.getArrivalDate()));
            statement.setString(5, flight.getArrivalAirportCode());
            statement.setLong(6, flight.getAircraftId());
            statement.setString(7, flight.getStatus());

            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                logger.info("Saving flight now have ID {}", generatedKeys.getLong(1));
                flight.setId(generatedKeys.getLong(1));
            }
            return flight;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Flight flight) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            logger.info("Updating flight with ID {}", flight.getId());

            statement.setString(1, flight.getFlightNo());
            statement.setTimestamp(2, Timestamp.valueOf(flight.getDepartureDate()));
            statement.setString(3, flight.getDepartureAirportCode());
            statement.setTimestamp(4, Timestamp.valueOf(flight.getArrivalDate()));
            statement.setString(5, flight.getArrivalAirportCode());
            statement.setLong(6, flight.getAircraftId());
            statement.setString(7, flight.getStatus());
            statement.setLong(8, flight.getId());

            logger.debug("{}", flight);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            logger.info("Deleting flight with ID {}", id);
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Flight> findById(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            logger.info("Starting to find flight with ID {}", id);

            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            Flight flight = null;

            if (result.next()) {
                flight = readFlight(result);
            }

            logger.debug("{}", flight);
            return Optional.ofNullable(flight);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Flight> findAll() {
        try (Statement statement = connection.createStatement()) {
            logger.info("Starting to find all flights");

            List<Flight> tickets = new ArrayList<>();
            ResultSet result = statement.executeQuery(FIND_ALL_SQL);

            while (result.next()) {
                tickets.add(readFlight(result));
            }
            logger.debug("Found {} flights", tickets.size());

            return tickets;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static Flight readFlight(ResultSet result) throws SQLException {
        return Flight.builder()
                .id(result.getLong("id"))
                .departureDate(result.getTimestamp("departure_date").toLocalDateTime())
                .departureAirportCode(result.getString("departure_airport_code"))
                .arrivalDate(result.getTimestamp("arrival_date").toLocalDateTime())
                .departureAirportCode(result.getString("arrival_airport_code"))
                .aircraftId(result.getLong("aircraft_id"))
                .status(result.getString("status"))
                .build();
    }
}
