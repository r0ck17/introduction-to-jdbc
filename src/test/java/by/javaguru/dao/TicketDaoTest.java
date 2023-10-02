package by.javaguru.dao;

import by.javaguru.entity.Ticket;
import by.javaguru.util.ConnectionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

class TicketDaoTest {
    private static TicketDao ticketDao = new TicketDao();

    @BeforeAll
    public static void openConnection() {
        ticketDao = new TicketDao();
    }

    @AfterAll
    public static void closeConnection() {
        ConnectionManager.close();
    }

    @Test
    public void testCrud() {
        Ticket ticket = Ticket.builder()
                .passportNo("123567")
                .passengerName("Иван Иванов")
                .flightId(8L)
                .seatNo("B1")
                .cost(1200)
                .build();

        Ticket savedTicket = ticketDao.save(ticket);

        Long id = savedTicket.getId();

        savedTicket.setCost(1500);
        savedTicket.setPassengerName("Николай Николаев");

        ticketDao.update(savedTicket);

        Optional<Ticket> ticketById = ticketDao.findById(id);

        ticketDao.delete(id);

        List<Ticket> tickets = ticketDao.findAll();
        Assertions.assertEquals(55, tickets.size());
    }
}