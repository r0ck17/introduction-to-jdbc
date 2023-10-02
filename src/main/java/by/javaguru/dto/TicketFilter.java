package by.javaguru.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class TicketFilter {
    private String passportNo;
    private String passengerName;
    private Long flightId;
    private String seatNo;
    private Integer cost;
}
