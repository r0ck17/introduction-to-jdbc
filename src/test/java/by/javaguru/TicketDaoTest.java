package by.javaguru;

import by.javaguru.dao.TicketDao;
import by.javaguru.entity.Ticket;
import by.javaguru.util.ConnectionManager;
import by.javaguru.util.ScriptLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TicketDaoTest {
    private static TicketDao ticketDao = new TicketDao();
    private static final Connection connection = ConnectionManager.open();

    @BeforeAll
    public static void openConnection() throws Exception {
        ticketDao = new TicketDao();
        Path path = Path.of("src", "test", "resources", "create-database.sql").toAbsolutePath();
        ScriptLoader.runScript(path.toString(), connection);
        connection.setSchema("flights_test");
    }

    @AfterAll
    public static void closeConnection() throws Exception {
        Path path = Path.of("src", "test", "resources", "drop-database.sql").toAbsolutePath();
        ScriptLoader.runScript(path.toString(), connection);
        ConnectionManager.close();
    }

    @Test
    public void saveAndDeleteTicket() {
        Ticket ticket = generateTicket();
        Ticket savedTicket = ticketDao.save(ticket);
        Long id = savedTicket.getId();
        assertNotNull(id);
        assertTrue(ticketDao.delete(id));
    }

    @Test
    public void updateTicketAndFindById() {
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
        assertEquals(55L, tickets.size());
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