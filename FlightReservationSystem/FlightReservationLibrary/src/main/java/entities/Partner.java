package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Partner {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;

    @Size(min = 1, max = 127)
    @Column(length = 127, nullable = false)
    @NotNull
    private String companyName;

    @Size(min = 5, max = 32)
    @Column(length = 32, nullable = false, unique = true)
    @NotNull
    private String username;

    @NotNull
    @Size(min = 3, max = 255)
    @Column(nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerRole partnerRole = PartnerRole.EMPLOYEE;
}
