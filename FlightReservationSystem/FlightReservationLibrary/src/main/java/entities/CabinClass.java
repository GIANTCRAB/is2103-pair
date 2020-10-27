
package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
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
    private int noOfAisles;
    
    @NotNull
    @Min(1)
    @Max(99)
    @Column(nullable = false)
    private int noOfRows;
    
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
    
}
