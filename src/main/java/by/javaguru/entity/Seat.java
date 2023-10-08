package by.javaguru.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @EmbeddedId
    private SeatId seatId;

    public Integer getAircraftId() {
        return seatId.getAircraftId();
    }

    public String getSeatNo() {
        return seatId.getSeatNo();
    }

    public void setAircraftId(Integer id) {
       seatId.setAircraftId(id);
    }

    public void setSeatNo(String seatNo) {
        seatId.setSeatNo(seatNo);
    }
}