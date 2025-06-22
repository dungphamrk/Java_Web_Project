package ra.edu.entity.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.candidate.Candidate;
import ra.edu.entity.recruitmentPosition.RecruitmentPosition;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Application  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recruitmentPosition_id")
    private RecruitmentPosition recruitmentPosition;

    private String cvUrl;
    private Progress progress;
    private LocalDateTime interviewRequestDate;
    private RequestResult interviewRequestResult;
    private String interviewLink;
    private LocalDateTime interviewTime;
    private String interviewResult;
    private String interviewResultNote;
    private LocalDateTime destroyAt;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String destroyReason;

    @PrePersist
    public void prePersist() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
        progress = Progress.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        updateAt = LocalDateTime.now();
    }

}