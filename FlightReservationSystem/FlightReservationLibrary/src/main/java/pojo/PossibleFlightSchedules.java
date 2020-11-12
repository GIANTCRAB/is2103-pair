package pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PossibleFlightSchedules implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<PossibleFlightPathNodes> possibleFlightPathNodesSet = new HashSet<>();
}
