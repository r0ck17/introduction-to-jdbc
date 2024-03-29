package by.javaguru;

import by.javaguru.dao.AircraftDao;
import by.javaguru.dao.AirportDao;
import by.javaguru.dao.FlightDao;
import by.javaguru.dao.SeatDao;
import by.javaguru.dao.TicketDao;
import by.javaguru.dto.FlightUpdateInfo;
import by.javaguru.dto.TicketUpdateInfo;
import by.javaguru.entity.Aircraft;
import by.javaguru.entity.Airport;
import by.javaguru.entity.Flight;
import by.javaguru.entity.Seat;
import by.javaguru.entity.Ticket;
import by.javaguru.util.ConnectionManager;
import by.javaguru.util.SQLScriptRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlightDatabaseTest {
    private static TicketDao ticketDao;
    private static FlightDao flightDao;
    private static AirportDao airportDao;
    private static SeatDao seatDao;
    private static AircraftDao aircraftDao;
    private static final Connection connection = ConnectionManager.open();

    @BeforeEach
    public void init() throws Exception {
        ticketDao = TicketDao.getInstance();
        flightDao = FlightDao.getInstance();
        airportDao = AirportDao.getInstance();
        seatDao = SeatDao.getInstance();
        aircraftDao = AircraftDao.getInstance();

        Path path = Path.of("src", "test", "resources", "create-database.sql").toAbsolutePath();
        SQLScriptRunner.execute(path.toString(), connection);
        connection.setSchema("flights_test");
    }

    @AfterEach
    public void dropDatabase() throws Exception {
        Path path = Path.of("src", "test", "resources", "drop-database.sql").toAbsolutePath();
        SQLScriptRunner.execute(path.toString(), connection);
    }

    @AfterAll
    public static void closeConnection() {
        ConnectionManager.close();
    }

    @Nested
    class FlightDaoTest {
        @Test
        public void saveAndDeleteTicket() {
            Flight savedFlight = flightDao.save(generateFlight());
            Long id = savedFlight.getId();

            assertNotNull(id);
            assertTrue(flightDao.delete(id));
        }

        @Test
        public void updateTicket() {
            Flight savedFlight = flightDao.save(generateFlight());
            long flightId = savedFlight.getId();

            String newStatus = "CANCELLED";
            long newAircraftId = 1L;
            savedFlight.setStatus(newStatus);
            savedFlight.setAircraftId(newAircraftId);

            flightDao.update(flightId, savedFlight);

            Optional<Flight> optionalFlight = flightDao.findById(flightId);
            assertNotNull(optionalFlight);
            Flight flightById = optionalFlight.get();

            assertEquals(newStatus, flightById.getStatus());
            assertEquals(newAircraftId, flightById.getAircraftId());
        }

        @Test
        public void findAll() {
            List<Flight> tickets = flightDao.findAll();
            assertEquals(9, tickets.size());
        }

        @Test
        public void updateDataByFlightId() {
            int newCost = 1200;
            TicketUpdateInfo ticketUpdateInfo = new TicketUpdateInfo(newCost);
            FlightUpdateInfo flightUpdateInfo = FlightUpdateInfo.builder()
                    .flightNo("NW111")
                    .aircraftId(3L)
                    .status("STATUS")
                    .build();

            long flightId = 9L;
            flightDao.updateDataByFlightId(flightId, flightUpdateInfo, ticketUpdateInfo);
            Flight flight = flightDao.findById(flightId).get();

            List<Ticket> ticketsByFlightId = ticketDao.findTicketsByFlightId(flightId);

            for (Ticket ticket : ticketsByFlightId) {
                assertEquals(newCost, ticket.getCost());
            }

            assertEquals("NW111", flight.getFlightNo());
            assertEquals(3L, flight.getAircraftId());
            assertEquals("STATUS", flight.getStatus());
        }

        private static Flight generateFlight() {
            return Flight.builder()
                    .flightNo("MP3000")
                    .departureDate(LocalDateTime.now())
                    .departureAirportCode("MNK")
                    .arrivalDate(LocalDateTime.now().plusHours(1))
                    .arrivalAirportCode("BSL")
                    .aircraftId(2L)
                    .status("SCHEDULED")
                    .build();
        }
    }

    @Nested
    class TicketDaoTest {
        @Test
        public void saveAndDeleteTicket() {
            Ticket savedTicket = ticketDao.save(generateTicket());
            Long id = savedTicket.getId();
            assertNotNull(id);
            assertTrue(ticketDao.delete(id));
        }

        @Test
        public void updateTicket() {
            Ticket savedTicket = ticketDao.save(generateTicket());
            long ticketId = savedTicket.getId();

            int newCost = 1500;
            String newName = "Николай Николаев";

            savedTicket.setCost(newCost);
            savedTicket.setPassengerName(newName);
            ticketDao.update(ticketId, savedTicket);

            Optional<Ticket> optionalTicket = ticketDao.findById(ticketId);
            assertNotNull(optionalTicket);
            Ticket ticketById = optionalTicket.get();

            assertEquals(newCost, ticketById.getCost());
            assertEquals(newName, ticketById.getPassengerName());
        }

        @Test
        public void findAll() {
            List<Ticket> tickets = ticketDao.findAll();
            assertEquals(55, tickets.size());
        }

        @Test
        public void findMostCommonNames() {
            List<String> actual = ticketDao.findMostCommonNames(3);
            List<String> expected = List.of("Иван", "Андрей", "Лариса");
            assertEquals(expected, actual);
        }

        @Test
        public void findPassengerTotalTicketCount() {
            Map<String, Integer> tickets = ticketDao.findPassengerTotalTicketCount();
            assertEquals(39, tickets.keySet().size());
            assertEquals(4, tickets.get("Иван Иванов"));
            assertEquals(3, tickets.get("Светлана Светикова"));
            assertEquals(2, tickets.get("Иван Старовойтов"));
        }

        private static Ticket generateTicket() {
            return Ticket.builder()
                    .passportNo("123567")
                    .passengerName("Иван Иванов")
                    .flightId(8L)
                    .seatNo("B1")
                    .cost(1200)
                    .build();
        }
    }

    @Nested
    class AirportDaoTest {
        @Test
        public void saveAndDeleteAirport() {
            Airport expected = generateAirport();
            Airport save = airportDao.save(expected);
            String code = save.getCode();

            Airport actual = airportDao.findById(code).get();
            assertEquals(expected, actual);

            boolean deleted = airportDao.delete(code);
            assertTrue(deleted);

            List<Airport> airports = airportDao.findAll();
            assertEquals(4, airports.size());
        }

        @Test
        public void updateTicket() {
            Airport airport = airportDao.save(generateAirport());

            String code = airport.getCode();
            airport.setCountry("China");
            airport.setCity("AAA");

            boolean isUpdated = airportDao.update(code, airport);
            assertTrue(isUpdated);

            Airport editedAirport = airportDao.findById(code).get();

            assertEquals("China", editedAirport.getCountry());
            assertEquals("AAA", editedAirport.getCity());

            Airport airportByCode = airportDao.findById(code).get();
            assertEquals(editedAirport, airportByCode);
        }

        @Test
        public void findAll() {
            List<Airport> airports = airportDao.findAll();
            assertEquals(4, airports.size());
        }

        private static Airport generateAirport() {
            return Airport.builder()
                    .code("OMS")
                    .country("Russia")
                    .city("Omsk")
                    .build();
        }
    }

    @Nested
    class SeatDaoTest {
        @Test
        public void saveAndDeleteSeat() {
            Seat seat = seatDao.save(generateSeat());

            Seat seatById = seatDao.findById(seat).get();
            assertEquals(seat, seatById);

            boolean deleted = seatDao.delete(seatById);
            assertTrue(deleted);

            List<Seat> seats = seatDao.findAll();
            assertEquals(32, seats.size());
        }

        @Test
        public void updateSeat() {
            Seat updatingSeat = seatDao.save(generateSeat());

            Seat seatWithUpdatingInfo = generateSeat();
            seatWithUpdatingInfo.setSeatNo("C5");

            seatDao.update(updatingSeat, seatWithUpdatingInfo);

            Seat updatedSeat = seatDao.findById(seatWithUpdatingInfo).get();
            assertEquals(seatWithUpdatingInfo, updatedSeat);
        }

        @Test
        public void findAll() {
            List<Airport> airports = airportDao.findAll();
            assertEquals(4, airports.size());
        }

        private static Seat generateSeat() {
            return Seat.builder()
                    .aircraftId(2)
                    .seatNo("C4")
                    .build();
        }
    }

    @Nested
    class AircraftDaoTest {
        @Test
        public void saveAndDeleteAircraft() {
            Aircraft aircraft = generateAircraft();
            Aircraft savedAircraft = aircraftDao.save(aircraft);
            Integer id = savedAircraft.getId();

            Aircraft aircraftById = aircraftDao.findById(id).get();

            assertEquals(savedAircraft, aircraftById);

            boolean isDeleted = aircraftDao.delete(id);
            assertTrue(isDeleted);

            List<Aircraft> seats = aircraftDao.findAll();
            assertEquals(4, seats.size());
        }

        @Test
        public void updateAircraft() {
            Aircraft aircraft = aircraftDao.save(generateAircraft());
            Integer id = aircraft.getId();

            aircraft.setModel("another model");
            aircraftDao.update(id, aircraft);

            Aircraft aircraftById = aircraftDao.findById(id).get();
            assertEquals(aircraft, aircraftById);
        }

        @Test
        public void findAll() {
            List<Aircraft> aircrafts = aircraftDao.findAll();
            assertEquals(4, aircrafts.size());
        }

        private static Aircraft generateAircraft() {
            return Aircraft.builder()
                    .model("super model")
                    .build();
        }
    }
}