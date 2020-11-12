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
    private static final long serialVersionUID = 1L;

    private Long flightScheduleId;

    private CabinClassType cabinClassType;

    private Integer seatsTaken;

    private Integer maxSeats;

    public Integer getSeatsRemaining() {
        return this.getMaxSeats() - this.getSeatsTaken();
    }
}
