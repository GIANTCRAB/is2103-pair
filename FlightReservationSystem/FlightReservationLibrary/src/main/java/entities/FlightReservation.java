package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightReservation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightReservationId;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Flight flight;

    // Allow possibility of flight reservation being unpaid
    @OneToOne(fetch = FetchType.LAZY)
    private CustomerPayment customerPayment;

    @NotNull
    @Size(min = 1, max = 64)
    @Column(length = 64, nullable = false)
    private String passengerFirstName;

    @NotNull
    @Size(min = 1, max = 64)
    @Column(length = 64, nullable = false)
    private String passengerLastName;

    @NotNull
    @Size(min = 1, max = 64)
    @Column(length = 64, nullable = false)
    private String passengerPassportNo;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private BigDecimal reservationCost;

    // first seat number is 1
    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer seatNumber;
}
