package pojo;

import entities.CabinClassType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatInventory implements Serializable {
    Long flightScheduleId;

    CabinClassType cabinClassType;

    Integer seatsTaken;

    Integer maxSeats;

    public Integer getSeatsRemaining() {
        return this.getMaxSeats() - this.getSeatsTaken();
    }
}
