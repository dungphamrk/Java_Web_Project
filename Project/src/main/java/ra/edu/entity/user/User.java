package ra.edu.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.candidate.Candidate;

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

    @OneToOne(cascade = CascadeType.ALL) // Cascade để tự động lưu Candidate khi lưu User
    @JoinColumn(name = "candidate_id", referencedColumnName = "id") // Khóa ngoại trong bảng User
    private Candidate candidate;

    @PrePersist
    public void prePersist() {
        status = Status.ACTIVE;
        role = UserRole.CANDIDATE;
    }
}
