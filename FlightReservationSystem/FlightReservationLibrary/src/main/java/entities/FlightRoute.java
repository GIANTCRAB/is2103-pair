package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "originId", nullable = false)
    private Airport origin;

    @NotNull
    @MapsId("destId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destId", nullable = false)
    private Airport dest;

    @OneToMany(mappedBy = "flightRoute")
    private List<Flight> flights = new ArrayList<>();

    @NotNull
    @Column(nullable = false)
    private Boolean enabled = true;

    @OneToOne(optional = true)
    @PrimaryKeyJoinColumns({
            @PrimaryKeyJoinColumn(name = "originId", referencedColumnName = "originId"),
            @PrimaryKeyJoinColumn(name = "destId", referencedColumnName = "destId")
    })
    private FlightRoute mainFlightRoute;

    @OneToOne(optional = true, mappedBy = "mainFlightRoute")
    private FlightRoute returnFlightRoute;

}
