
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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aircraftTypeId;

    @NotNull
    @Size(min = 1, max = 32)
    @Column(length = 32, nullable = false)
    private String aircraftTypeName;
    
    @NotNull
    @Min(1)
    @Max(999)
    @Column(nullable = false)
    private int maxCapacity;
    
    @OneToMany(mappedBy = "aircraftType")
    private List<AircraftConfiguration> aircraftConfigurations;
}
