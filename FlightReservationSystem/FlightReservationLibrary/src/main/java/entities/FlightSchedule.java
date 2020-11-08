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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

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
    private Long estimatedDuration;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Flight flight;

    @Transient
    public ZonedDateTime getDepartureDateTime() {
        final FlightRoute flightRoute = this.getFlight().getFlightRoute();
        final Airport originAirport = flightRoute.getOrigin();

        final LocalDateTime localDateTime = LocalDateTime.of(this.getDate().toLocalDate(), this.getTime().toLocalTime());
        final ZonedDateTime utcDateTime = ZonedDateTime.of(localDateTime, ZoneOffset.UTC);

        return utcDateTime.withZoneSameInstant(originAirport.getZoneId());
    }

    // Given a departure date time of the origin airport location, save the date time in UTC into the entity
    public void setDepartureDateTime(Date departureDate, Time departureTime) {
        final FlightRoute flightRoute = this.getFlight().getFlightRoute();
        final Airport originAirport = flightRoute.getOrigin();

        final LocalDateTime departureDateTime = LocalDateTime.of(departureDate.toLocalDate(), departureTime.toLocalTime());
        final ZonedDateTime originDateTime = ZonedDateTime.of(departureDateTime, originAirport.getZoneId());

        final ZonedDateTime utcDateTime = originDateTime.withZoneSameInstant(ZoneOffset.UTC);

        this.setDate(Date.valueOf(utcDateTime.toLocalDate()));
        this.setTime(Time.valueOf(utcDateTime.toLocalTime()));
    }

    @Transient
    public ZonedDateTime getArrivalDateTime() {
        final FlightRoute flightRoute = this.getFlight().getFlightRoute();
        final Airport destinationAirport = flightRoute.getDest();

        final LocalDateTime localDateTime = LocalDateTime.of(this.getDate().toLocalDate(), this.getTime().toLocalTime());
        final ZonedDateTime utcDateTime = ZonedDateTime.of(localDateTime, ZoneOffset.UTC);

        return utcDateTime.plusMinutes(this.getEstimatedDuration()).withZoneSameInstant(destinationAirport.getZoneId());
    }
}
