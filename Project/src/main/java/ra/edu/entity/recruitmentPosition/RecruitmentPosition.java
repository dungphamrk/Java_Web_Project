package ra.edu.entity.recruitmentPosition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.technology.Technology;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentPosition {
    @Id
    private int id;
    private String name;
    private String description;
    private double minSalary;
    private double maxSalary;
    private int minExperience;
    private LocalDate createdDate;
    private LocalDate expiredDate;
    private Status status;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "recruitmentPosition_technology",
            joinColumns = @JoinColumn(name = "recruitmentPosition_id"),
            inverseJoinColumns = @JoinColumn(name = "technologies_id")
    )
    private List<Technology> technologies;

    @PrePersist
    public void prePersist()
    {
        createdDate = LocalDate.now();
        status = Status.ACTIVE;
    }
}