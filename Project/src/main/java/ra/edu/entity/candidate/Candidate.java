package ra.edu.entity.candidate;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Candidate {
    @Id
    private int id;
    private String name;
    private String email;
    private String phone;
    private int experience;
    private Gender gender;
    private String description;
    private LocalDate dob;
}