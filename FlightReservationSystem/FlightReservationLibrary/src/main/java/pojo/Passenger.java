package pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {
    private String firstName;

    private String lastName;

    private String passportNumber;

    private Integer seatNumber;
}
