
package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fare implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fareId;

    @NotNull
    @Size(min = 3, max = 7)
    @Column(length = 7, nullable = false)
    private String fareBasisCode;

    @NotNull
    @Column(nullable = false)
    private BigDecimal fareAmount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "cabinClassType", referencedColumnName = "cabinClassType"),
            @JoinColumn(name = "aircraftConfigurationId", referencedColumnName = "aircraftConfigurationId")})
    private CabinClass cabinClass;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private FlightSchedule flightSchedule;

    @OneToMany(mappedBy = "flightSchedule")
    private List<FlightReservation> flightReservations = new ArrayList<>();
}
