package ra.edu.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;
    private UserRole role;
    private Status status;

    @PrePersist
    public void prePersist() {
        status = Status.ACTIVE;
        role = UserRole.CANDIDATE;
    }
}
