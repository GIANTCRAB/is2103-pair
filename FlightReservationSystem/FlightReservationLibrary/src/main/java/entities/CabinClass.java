
package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CabinClass implements Serializable {

    @EmbeddedId
    private CabinClassId cabinClassId;

    @NotNull
    @Min(1)
    @Max(2)
    @Column(nullable = false)
    private Integer noOfAisles;

    @NotNull
    @Min(1)
    @Max(99)
    @Column(nullable = false)
    private Integer noOfRows;
    
    @NotNull
    @Min(1)
    @Max(99)
    @Column(nullable = false)
    private Integer noOfCols;

    @NotNull
    @Size(min = 5, max = 8)
    @Column(length = 8, nullable = false)
    private String seatConfiguration;

    @NotNull
    @MapsId("aircraftConfigurationId")
    @ManyToOne
    @JoinColumn(name = "aircraftConfigurationId", nullable = false)
    private AircraftConfiguration aircraftConfiguration;

    @OneToMany(mappedBy = "cabinClass")
    private List<Fare> fares;

    // Used only in delivering data from client to ejb
    @Transient
    private CabinClassType temporaryCabinClassType;
    
    // Used to check if max capacity has exceeded
    @Transient
    private Integer maxCapacity;
}
