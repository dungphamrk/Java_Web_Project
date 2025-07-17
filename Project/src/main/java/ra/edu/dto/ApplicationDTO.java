package ra.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ra.edu.entity.application.Progress;
import ra.edu.entity.application.RequestResult;
import ra.edu.entity.candidate.Candidate;
import ra.edu.entity.recruitmentPosition.RecruitmentPosition;
import ra.edu.validation.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private int id;

    private Candidate candidate;

    private RecruitmentPosition recruitmentPosition;

    @NotBlank(message = "URL CV không được để trống", groups = {
            OnPending.class, OnHandling.class, OnInterviewing.class, OnDone.class, OnReject.class, OnCancel.class
    })
    @Pattern(regexp = "^(http|https)://.*", message = "URL CV không hợp lệ", groups = {
            OnPending.class, OnHandling.class, OnInterviewing.class, OnDone.class, OnReject.class, OnCancel.class
    })
    private String cvUrl;

    @NotNull(message = "Tiến trình không được để trống", groups = {
            OnPending.class, OnHandling.class, OnInterviewing.class, OnDone.class, OnReject.class, OnCancel.class
    })
    private Progress progress;

    @NotNull(message = "Thời gian yêu cầu phỏng vấn không được để trống", groups = {
            OnHandling.class, OnInterviewing.class, OnDone.class
    })
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime interviewRequestDate;

    @NotNull(message = "Kết quả yêu cầu phỏng vấn không được để trống", groups = {
             OnInterviewing.class, OnDone.class
    })
    private RequestResult interviewRequestResult;

    @NotBlank(message = "Link phỏng vấn không được để trống", groups = {OnInterviewing.class, OnDone.class})
    @Pattern(regexp = "^(http|https)://.*", message = "Link phỏng vấn không hợp lệ", groups = {OnInterviewing.class, OnDone.class})
    private String interviewLink;

    @NotNull(message = "Thời gian phỏng vấn không được để trống", groups = {OnInterviewing.class, OnDone.class})
    @FutureOrPresent(message = "Thời gian phỏng vấn phải từ hiện tại trở đi", groups = {OnInterviewing.class, OnDone.class})
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime interviewTime;

    @NotBlank(message = "Ghi chú kết quả phỏng vấn không được để trống", groups = {OnDone.class})
    @Size(max = 1000, message = "Ghi chú kết quả phỏng vấn không được vượt quá 1000 ký tự", groups = {OnDone.class})
    private String interviewResultNote;

    @NotBlank(message = "Lý do hủy không được để trống", groups = {OnReject.class, OnCancel.class})
    @Size(max = 1000, message = "Lý do hủy không được vượt quá 1000 ký tự", groups = {OnReject.class, OnCancel.class})
    private String destroyReason;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime destroyAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updateAt;

    @NotBlank(message = "Kết quả phỏng vấn ko được để trống", groups = {OnDone.class})
    @Size(max = 1000, message = "Ghi chú kết quả phỏng vấn không được vượt quá 1000 ký tự", groups = {OnDone.class})
    private String interviewResult;

    public String getFormattedCreateAt() {
        if (createAt == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return createAt.format(formatter);
    }

    public String getFormattedUpdateAt() {
        if (updateAt == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return updateAt.format(formatter);
    }

}