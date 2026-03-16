package in.prathamattri.examninja.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(name = "first_name")
    private String fName;
    @Column(name = "last_name")
    private String lName;
    private String password;
    private final String role = "ROLE_USER";

}
