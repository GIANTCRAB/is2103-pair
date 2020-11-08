package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airport implements Serializable {
    @Id
    @NotNull
    @Size(min = 2, max = 3)
    @Column(length = 3, nullable = false)
    private String iataCode;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String airportName;

    @NotNull
    @Size(min = 1, max = 127)
    @Column(length = 127, nullable = false)
    private String city;

    @NotNull
    @Size(min = 1, max = 127)
    @Column(length = 127, nullable = false)
    private String stateName;

    @NotNull
    @Size(min = 1, max = 127)
    @Column(length = 127, nullable = false)
    private String country;

    @NotNull
    @Size(min = 1, max = 127)
    @Column(length = 127, nullable = false)
    private String zoneId;

    @Transient
    public ZoneId getZoneId() {
        return ZoneId.of(this.zoneId);
    }
}
