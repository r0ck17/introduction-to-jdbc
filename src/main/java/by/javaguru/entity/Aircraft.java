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
public class Aircraft {
    @Id
    @GeneratedValue(generator="aircraft_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "aircraft_gen", sequenceName = "aircraft_id_seq", allocationSize = 1)
    private Integer id;
    private String model;
}
