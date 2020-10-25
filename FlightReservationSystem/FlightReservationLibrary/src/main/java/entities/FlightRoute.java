package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightRoute implements Serializable {
    @EmbeddedId
    private FlightRouteId flightRouteId;

    @NotNull
    @MapsId("originId")
    @Size(min = 2, max = 3)
    @ManyToOne
    @JoinColumn(name = "originId", nullable = false)
    private Airport origin;

    @NotNull
    @MapsId("destId")
    @Size(min = 2, max = 3)
    @ManyToOne
    @JoinColumn(name = "destId", nullable = false)
    private Airport dest;
}
