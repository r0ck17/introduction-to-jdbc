package by.javaguru.dao.jdbc;

import by.javaguru.dao.AircraftDao;
import by.javaguru.entity.Aircraft;
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

public class AircraftDaoJsonImpl implements AircraftDao {
    private static final AircraftDaoJsonImpl INSTANCE = new AircraftDaoJsonImpl();
    private static final Connection connection = ConnectionManager.open();
    private static final Logger logger = LoggerFactory.getLogger(AircraftDaoJsonImpl.class);
    private static final String INSERT_SQL = """
            INSERT INTO aircraft (model)
            VALUES (?)
            """;
    private static final String DELETE_SQL = """
            DELETE FROM aircraft
            WHERE id = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE aircraft
            SET
                model = ?
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, model
            FROM aircraft
            """;
    private static final String FIND_BY_ID_SQL =
            FIND_ALL_SQL + "WHERE id = ?";

    private AircraftDaoJsonImpl() {
    }

    public static AircraftDaoJsonImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Aircraft save(Aircraft aircraft) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                Statement.RETURN_GENERATED_KEYS)) {
            logger.info("Saving aircraft to database");
            logger.debug("{}", aircraft);

            statement.setString(1, aircraft.getModel());

            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                aircraft.setId(generatedKeys.getInt("id"));
                logger.info("Aircraft was saved");
                return aircraft;
            }

            return null;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Integer id, Aircraft aircraft) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            logger.info("Updating aircraft with id {} to model {}", id, aircraft.getModel());

            statement.setString(1, aircraft.getModel());
            statement.setInt(2, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            logger.info("Deleting aircraft with id {}", id);
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Aircraft> findById(Integer id) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            logger.info("Starting to find aircraft with id {}", id);

            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();
            Aircraft aircraft = null;

            if (result.next()) {
                aircraft = Aircraft.builder()
                        .id(result.getInt("id"))
                        .model(result.getString("model"))
                        .build();
            }
            logger.debug("{}", aircraft);

            return Optional.ofNullable(aircraft);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Aircraft> findAll() {
        try (Statement statement = connection.createStatement()) {
            logger.info("Starting to find all aircrafts");

            List<Aircraft> aircrafts = new ArrayList<>();
            ResultSet result = statement.executeQuery(FIND_ALL_SQL);

            while (result.next()) {
                aircrafts.add(Aircraft.builder()
                        .id(result.getInt("id"))
                        .model(result.getString("model"))
                        .build());
            }

            logger.debug("Found {} aircraft", aircrafts.size());
            return aircrafts;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
