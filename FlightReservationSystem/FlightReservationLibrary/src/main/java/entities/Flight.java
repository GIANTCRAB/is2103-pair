package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flight implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightId;

    @Size(min = 5, max = 32)
    @Column(length = 32, nullable = false, unique = true)
    @NotNull
    private String flightCode;

    @Column(nullable = false)
    private BigDecimal totalAmountPaid;

    @NotNull
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "routeOrigin", referencedColumnName = "originId"),
            @JoinColumn(name = "routeDest", referencedColumnName = "destId")
    })
    private FlightRoute flightRoute;
}
