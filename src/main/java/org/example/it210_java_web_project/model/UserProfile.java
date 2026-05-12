package org.example.it210_java_web_project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
public class UserProfile {

    // 🌟 1. Khai báo một Interface để làm Nhóm (Group) kiểm tra
    public interface UpdateAction {}

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // Không gán group -> Áp dụng cho mọi trường hợp (Cả Đăng ký lẫn Cập nhật)
    @Column(name = "full_name")
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    // 🌟 2. Gắn groups = UpdateAction.class (Chỉ bắt lỗi khi Cập nhật)
    @NotNull(groups = UpdateAction.class, message = "Vui lòng chọn ngày sinh")
    private LocalDate dob;

    // 🌟 3. Gắn groups = UpdateAction.class (Chỉ bắt lỗi khi Cập nhật)
    @NotBlank(groups = UpdateAction.class, message = "Số điện thoại không được để trống")
    @Pattern(groups = UpdateAction.class, regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng")
    private String phone;

}