package by.javaguru.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Airport {
    private String code;
    private String country;
    private String city;
}
