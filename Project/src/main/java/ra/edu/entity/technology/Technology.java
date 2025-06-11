package ra.edu.entity.technology;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Technology {
    @Id
    private int id;
    private String name;
    private Status status;

    @PrePersist
    public void prePersist() {
        status = Status.ACTIVE;
    }
}