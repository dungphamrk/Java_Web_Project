package ra.edu.entity.candidate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import ra.edu.entity.technology.Technology;
import ra.edu.entity.user.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String email;
    private String phone;
    private int experience;
    private Gender gender;
    private String description;
    private LocalDate dob;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "candidate_technology",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    private Set<Technology> technologyList;

    @OneToOne(mappedBy = "candidate")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private User user;


}