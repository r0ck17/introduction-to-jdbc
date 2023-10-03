package by.javaguru.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FlightUpdateInfo {
    String flightNo;
    Long aircraftId;
    String status;
}
