package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Size(min = 1, max = 64)
    @Column(length = 64, nullable = false)
    @NotNull
    private String firstName;

    @Size(min = 1, max = 64)
    @Column(length = 64, nullable = false)
    @NotNull
    private String lastName;

    @NotNull
    @Size(min = 3, max = 127)
    @Column(length = 127, nullable = false, unique = true)
    private String username;

    @NotNull
    @Size(min = 3, max = 255)
    @Column(nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeRole employeeRole;
}
