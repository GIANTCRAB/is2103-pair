package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Size(min = 1, max = 64)
    @Column(length = 64, nullable = false)
    @NotNull
    private String firstName;

    @Size(min = 1, max = 64)
    @Column(length = 64, nullable = false)
    @NotNull
    private String lastName;

    /**
     * Email needs to be length of 255 as max size cannot exceed 254. See RFC 5321
     */
    @Size(min = 3, max = 254)
    @Column(nullable = false, unique = true)
    @NotNull
    @Email
    private String email;

    @Column(length = 64, nullable = false)
    @NotNull
    private String password;

    @Column(length = 64, nullable = false)
    @NotNull
    private String phoneNumber;

    @Column(length = 127, nullable = false)
    @NotNull
    private String address;
}
