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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aircraftConfigurationId;

    @NotNull
    @Size(min = 1, max = 32)
    @Column(length = 32, nullable = false)
    private String aircraftConfigurationName;

    @NotNull
    @Min(1)
    @Max(4)
    @Column(nullable = false)
    private Integer noOfCabinClasses;

    @NotNull
    @OneToMany(mappedBy = "aircraftConfiguration")
    private List<CabinClass> cabinClasses;

    @OneToMany(mappedBy = "aircraftConfiguration")
    private List<Flight> flights;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AircraftType aircraftType;
}
