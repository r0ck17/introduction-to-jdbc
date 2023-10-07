package by.javaguru.dao.jdbc;

import by.javaguru.dao.AirportDao;
import by.javaguru.entity.Airport;
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
import java.util.List;
import java.util.Optional;

public class AirportDaoJsonImpl implements AirportDao {
    private static final AirportDaoJsonImpl INSTANCE = new AirportDaoJsonImpl();
    private static final Connection connection = ConnectionManager.open();
    private static final Logger logger = LoggerFactory.getLogger(AirportDaoJsonImpl.class);
    private static final String INSERT_SQL = """
            INSERT INTO airport (code, country, city)
            VALUES (?, ?, ?)
            """;

    private static final String DELETE_SQL = """
            DELETE FROM airport
            WHERE code = ?
            """;

    private static final String UPDATE_SQL = """
            UPDATE airport
            SET
                country = ?,
                city = ?
            WHERE code = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT code, country, city
            FROM airport
            """;
    private static final String FIND_BY_ID_SQL =
            FIND_ALL_SQL + "WHERE code = ?";

    private AirportDaoJsonImpl() {
    }

    public static AirportDaoJsonImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Airport save(Airport airport) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            logger.info("Saving airport to database");
            logger.debug("{}", airport);

            statement.setString(1, airport.getCode());
            statement.setString(2, airport.getCountry());
            statement.setString(3, airport.getCity());

            statement.executeUpdate();

            logger.info("Airport was saved");
            return airport;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(String id, Airport airport) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            logger.info("Updating airport with code {}", airport.getCode());

            statement.setString(1, airport.getCountry());
            statement.setString(2, airport.getCity());
            statement.setString(3, airport.getCode());

            logger.debug("Airport with code {} after updating {}", airport.getCode(), airport);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(String code) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            logger.info("Deleting airport with code {}", code);
            statement.setString(1, code);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Airport> findById(String id) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            logger.info("Starting to find airport with code {}", id);

            statement.setString(1, id);
            ResultSet result = statement.executeQuery();
            Airport airport = null;

            if (result.next()) {
                airport = readAirport(result);
            }
            logger.debug("{}", airport);

            return Optional.ofNullable(airport);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Airport> findAll() {
        try (Statement statement = connection.createStatement()) {
            logger.info("Starting to find all airports");

            List<Airport> airports = new ArrayList<>();
            ResultSet result = statement.executeQuery(FIND_ALL_SQL);

            while (result.next()) {
                airports.add(readAirport(result));
            }

            logger.debug("Found {} airports", airports.size());
            return airports;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Airport readAirport(ResultSet result) throws SQLException {
        return Airport.builder()
                .code(result.getString("code"))
                .country(result.getString("country"))
                .city(result.getString("city"))
                .build();
    }
}
