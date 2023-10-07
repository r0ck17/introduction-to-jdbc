package by.javaguru.dao;

import by.javaguru.dto.FlightUpdateInfo;
import by.javaguru.dto.TicketUpdateInfo;
import by.javaguru.entity.Flight;

public interface FlightDao extends Dao<Long, Flight> {
    boolean updateDataByFlightId(Long id, FlightUpdateInfo flightInfo, TicketUpdateInfo ticketInfo);
}
