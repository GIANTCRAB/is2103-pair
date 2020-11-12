package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @Column(length = 32, nullable = false, unique = true)
    private String aircraftConfigurationName;

    @NotNull
    @OneToMany(mappedBy = "aircraftConfiguration")
    private List<CabinClass> cabinClasses = new ArrayList<>();

    @OneToMany(mappedBy = "aircraftConfiguration")
    private List<Flight> flights = new ArrayList<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private AircraftType aircraftType;

    @Transient
    public Integer getNoOfCabinClasses() {
        return this.getCabinClasses().size();
    }

    @Transient
    public Integer getTotalCabinClassCapacity() {
        Integer totalCapacity = 0;
        final List<CabinClass> cabinClassList = this.getCabinClasses();
        for (CabinClass cabinClass : cabinClassList) {
            totalCapacity += cabinClass.getMaxCapacity();
        }

        return totalCapacity;
    }
}
