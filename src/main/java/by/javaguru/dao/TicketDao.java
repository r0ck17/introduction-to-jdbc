package by.javaguru.dao;

import by.javaguru.dto.TicketFilter;
import by.javaguru.dto.TicketUpdateInfo;
import by.javaguru.entity.Ticket;

import java.util.List;
import java.util.Map;

public interface TicketDao extends Dao<Long, Ticket> {
    int updateTickets(TicketFilter ticketFilter, TicketUpdateInfo updateInfo);
    List<Ticket> findTicketsByFlightId(Long flightId);
    List<String> findMostCommonNames(int limit);
    Map<String, Integer> findPassengerTotalTicketCount();
}
