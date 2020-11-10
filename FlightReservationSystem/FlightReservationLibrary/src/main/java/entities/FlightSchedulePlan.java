package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightSchedulePlan implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightSchedulePlanId;

    @Enumerated(EnumType.STRING)
    @Column
    @NotNull
    private FlightSchedulePlanType flightSchedulePlanType = FlightSchedulePlanType.MULTIPLE;

    @Column
    private Date recurrentEndDate;

    @NotNull
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @NotNull
    @OneToMany(mappedBy = "flightSchedulePlan")
    private List<Fare> fares = new ArrayList<>();

    @NotNull
    @OneToMany(mappedBy = "flightSchedulePlan")
    private List<FlightSchedule> flightSchedules = new ArrayList<>();
}
