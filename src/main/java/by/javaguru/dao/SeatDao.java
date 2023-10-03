package by.javaguru.dao;

import by.javaguru.entity.Seat;
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

public class SeatDao implements Dao<Seat, Seat> {
    private static final SeatDao INSTANCE = new SeatDao();
    private static final Connection connection = ConnectionManager.open();
    private static final Logger logger = LoggerFactory.getLogger(SeatDao.class);
    private static final String INSERT_SQL = """
            INSERT INTO seat (aircraft_id, seat_no)
            VALUES (?, ?)
            """;
    private static final String DELETE_SQL = """
            DELETE FROM seat
            WHERE aircraft_id = ? AND seat_no = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE seat
            SET
                seat_no = ?
            WHERE aircraft_id = ? AND seat_no = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT aircraft_id, seat_no
            FROM seat
            """;
    private static final String FIND_BY_ID_SQL =
            FIND_ALL_SQL + "WHERE aircraft_id = ? AND seat_no = ?";

    private SeatDao() {
    }

    public static SeatDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Seat save(Seat entity) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                Statement.RETURN_GENERATED_KEYS)) {
            logger.info("Saving seat to database");
            logger.debug("{}", entity);

            statement.setInt(1, entity.getAircraftId());
            statement.setString(2, entity.getSeatNo());

            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                logger.info("Seat was saved");
                return entity;
            }

            return null;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Seat id, Seat seat) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            logger.info("Updating seat {} with seatNo {}", id, seat.getSeatNo());

            statement.setString(1, seat.getSeatNo());
            statement.setInt(2, id.getAircraftId());
            statement.setString(3, id.getSeatNo());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Seat seat) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            logger.info("Deleting seat {}", seat);
            statement.setInt(1, seat.getAircraftId());
            statement.setString(2, seat.getSeatNo());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Seat> findById(Seat seat) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            logger.info("Starting to find seat {}", seat);

            statement.setInt(1, seat.getAircraftId());
            statement.setString(2, seat.getSeatNo());

            ResultSet result = statement.executeQuery();
            Seat foundedSeat = null;

            if (result.next()) {
                foundedSeat = Seat.builder()
                        .aircraftId(result.getInt("aircraft_id"))
                        .seatNo(result.getString("seat_no"))
                        .build();
            }
            logger.debug("{}", foundedSeat);

            return Optional.ofNullable(foundedSeat);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Seat> findAll() {
        try (Statement statement = connection.createStatement()) {
            logger.info("Starting to find all seat");

            List<Seat> seats = new ArrayList<>();
            ResultSet result = statement.executeQuery(FIND_ALL_SQL);

            while (result.next()) {
                seats.add(Seat.builder()
                        .aircraftId(result.getInt("aircraft_id"))
                        .seatNo(result.getString("seat_no"))
                        .build());
            }

            logger.debug("Found {} seats", seats.size());
            return seats;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
