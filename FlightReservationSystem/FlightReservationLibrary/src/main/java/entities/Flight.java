package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Pattern;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flight implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightId;

    @Size(min = 5, max = 32)
    @Pattern(regexp = "ML[0-9][0-9][0-9]*", message = "Must begin with ML")
    @Column(length = 32, nullable = false, unique = true)
    @NotNull
    private String flightCode;

    @Column
    private BigDecimal totalAmountPaid;
    
    @NotNull
    @Column(nullable = false)
    private Boolean enabled = true;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "routeOrigin", referencedColumnName = "originId"),
            @JoinColumn(name = "routeDest", referencedColumnName = "destId")
    })
    private FlightRoute flightRoute;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private AircraftConfiguration aircraftConfiguration;
    
    @OneToMany(mappedBy="flight")
    private List<FlightSchedule> flightSchedules = new ArrayList<>();
}
