package ra.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TechnologyDTO {
    private int id;

    @NotBlank(message = "Tên công nghệ không được để trống")
    @Size(min = 2, max = 50, message = "Tên công nghệ phải từ 2 đến 50 ký tự")
    private String name;
}