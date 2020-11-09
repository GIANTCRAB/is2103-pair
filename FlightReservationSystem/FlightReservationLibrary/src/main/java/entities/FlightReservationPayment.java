package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private FlightReservation flightReservation;

    @NotNull
    @Column(nullable = false)
    private String creditCardNumber;
}
