package by.javaguru.entity;

import lombok.*;

@Data
@Builder
public class Ticket {
    private Long id;
    private String passportNo;
    private String passengerName;
    private Long flightId;
    private String seatNo;
    private int cost;
}
