package org.example.it210_java_web_project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDTO {

    private Long id;

    @NotBlank(message = "Tên thiết bị không được để trống")
    private String name;

    @NotNull(message = "Vui lòng nhập số lượng")
    @Min(value = 1, message = "Số lượng tối thiểu phải là 1")
    private Integer quantity;

    private String status;

    @NotNull(message = "Vui lòng chọn phân bổ Khoa/Ngành")
    private Long departmentId; // Lưu ý: Ở DTO mình chỉ cần lấy ID

    @NotNull(message = "Vui lòng chọn loại phòng Lab")
    private Long labTypeId;    // Lưu ý: Ở DTO mình chỉ cần lấy ID
}