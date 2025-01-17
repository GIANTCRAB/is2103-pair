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
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightReservationPayment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    // Can be either customer or partner reserving the flight
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Customer customer = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Partner partner = null;

    @NotNull
    @OneToMany(mappedBy = "flightReservationPayment", fetch = FetchType.LAZY)
    private List<FlightReservation> flightReservations = new ArrayList<>();

    @NotNull
    @Size(min = 5, max = 24)
    @Column(length = 24, nullable = false)
    private String creditCardNumber;

    @Transient
    public BigDecimal getTotalCost() {
        BigDecimal totalCost = new BigDecimal(0);

        for (FlightReservation flightReservation : flightReservations) {
            totalCost = totalCost.add(flightReservation.getReservationCost());
        }

        return totalCost;
    }
}
