package by.javaguru;

import by.javaguru.dao.FlightDao;
import by.javaguru.dao.TicketDao;
import by.javaguru.entity.Flight;
import by.javaguru.entity.Ticket;
import by.javaguru.util.ConnectionManager;
import by.javaguru.util.SQLScriptRunner;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FlightDatabaseTest {
    private static TicketDao ticketDao;
    private static FlightDao flightDao;
    private static final Connection connection = ConnectionManager.open();

    @BeforeEach
    public void createDatabase() throws Exception {
        ticketDao = TicketDao.getInstance();
        flightDao = FlightDao.getInstance();
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
            Flight flight = generateFlight();
            Flight savedFlight = flightDao.save(flight);
            Long id = savedFlight.getId();

            assertNotNull(id);
            assertTrue(flightDao.delete(id));
        }

        @Test
        public void updateTicket() {
            Flight flight = generateFlight();
            Flight savedFlight = flightDao.save(flight);
            long flightId = savedFlight.getId();

            String newStatus = "CANCELLED";
            long newAircraftId = 1L;
            savedFlight.setStatus(newStatus);
            savedFlight.setAircraftId(newAircraftId);

            flightDao.update(savedFlight);

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
            Ticket ticket = generateTicket();
            Ticket savedTicket = ticketDao.save(ticket);
            Long id = savedTicket.getId();
            assertNotNull(id);
            assertTrue(ticketDao.delete(id));
        }

        @Test
        public void updateTicket() {
            Ticket ticket = generateTicket();
            Ticket savedTicket = ticketDao.save(ticket);
            long ticketId = savedTicket.getId();

            int newCost = 1500;
            String newName = "Николай Николаев";

            savedTicket.setCost(newCost);
            savedTicket.setPassengerName(newName);
            ticketDao.update(savedTicket);

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
}