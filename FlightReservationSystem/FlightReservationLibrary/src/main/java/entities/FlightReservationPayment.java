package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
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
    @Column(nullable = false)
    private String creditCardNumber;
}
