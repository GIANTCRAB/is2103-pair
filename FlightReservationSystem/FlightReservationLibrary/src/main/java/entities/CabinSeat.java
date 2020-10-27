
package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class CabinSeat implements Serializable {
    
    @NotNull
    @Size(min = 3, max = 3)
    private String seatRow;
    
    @NotNull
    @Size(min = 3, max = 3)
    private String seatCol;
    
}
