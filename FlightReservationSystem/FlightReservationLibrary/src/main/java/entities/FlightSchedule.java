package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightSchedule implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightScheduleId;

    @Column
    @NotNull
    private Date date;

    @Column
    @NotNull
    private Time time;

    @Column
    @Min(0)
    @NotNull
    private Integer estimatedDuration = 0;

    @Transient
    public Date getArrivalDate() {

    }

    @Transient
    public Time getArrivalTime() {

    }
}
