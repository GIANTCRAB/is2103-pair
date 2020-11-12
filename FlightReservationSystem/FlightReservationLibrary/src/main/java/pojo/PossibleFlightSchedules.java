package pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PossibleFlightSchedules implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<PossibleFlightPathNodes> possibleFlightPathNodesSet = new ArrayList<>();
}
