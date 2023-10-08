package by.javaguru.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(generator="ticket_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ticket_gen", sequenceName = "ticket_id_seq", allocationSize = 1)
    private Long id;
    private String passportNo;
    private String passengerName;
    private Long flightId;
    private String seatNo;
    private int cost;
}
