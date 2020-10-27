
package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CabinClassId implements Serializable {
    
    @Enumerated(EnumType.STRING)
    private CabinClassType cabinClassType;
    private Long aircraftConfigurationId;
    
}
