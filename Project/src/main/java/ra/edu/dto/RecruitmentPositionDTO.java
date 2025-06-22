package ra.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ra.edu.entity.technology.Technology;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentPositionDTO {
    private int id;

    @NotBlank(message = "Tên vị trí không được để trống")
    @Size(min = 2, max = 100, message = "Tên vị trí phải từ 2 đến 100 ký tự")
    private String name;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;

    @DecimalMin(value = "0", inclusive = false, message = "Lương tối thiểu phải lớn hơn 0")
    private double minSalary;

    @DecimalMin(value = "0", inclusive = false, message = "Lương tối đa phải lớn hơn 0")
    @DecimalMax(value = "1000000.0", message = "Lương tối đa không được vượt quá 1,000,000")
    private double maxSalary;

    @Min(value = 0, message = "Kinh nghiệm tối thiểu không được âm")
    private int minExperience;

    @NotNull(message = "Ngày hết hạn không được để trống")
    @FutureOrPresent(message = "Ngày hết hạn phải từ hôm nay trở đi")

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiredDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    private List<String> technologies = new ArrayList<>();

    private List<Technology> technologyList = new ArrayList<>();;
}