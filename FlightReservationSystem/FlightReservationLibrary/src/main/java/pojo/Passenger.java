package pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Passenger implements Serializable {
    private String firstName;

    private String lastName;

    private String passportNumber;

    private Integer seatNumber;
}
