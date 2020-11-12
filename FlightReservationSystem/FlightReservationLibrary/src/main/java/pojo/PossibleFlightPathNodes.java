package pojo;

import entities.FlightSchedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PossibleFlightPathNodes implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<FlightSchedule> flightSchedules = new ArrayList<>();
}
