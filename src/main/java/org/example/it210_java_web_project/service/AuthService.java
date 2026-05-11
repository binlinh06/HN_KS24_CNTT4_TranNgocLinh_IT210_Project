package org.example.it210_java_web_project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.dto.LoginRequest;
import org.example.it210_java_web_project.dto.RegisterRequest;
import org.example.it210_java_web_project.model.Role;
import org.example.it210_java_web_project.model.User;
import org.example.it210_java_web_project.model.UserProfile; // Bắt buộc import
import org.example.it210_java_web_project.model.UserStatus;
import org.example.it210_java_web_project.repository.RoleRepository;
import org.example.it210_java_web_project.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String register(RegisterRequest request) {

        // 1. Chỉ kiểm tra duy nhất tên đăng nhập
        if (userRepository.existsByUsername(request.getUsername())) {
            return "username đã tồn tại";
        }

        // 2. Kiểm tra mật khẩu xác nhận
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "password không khớp";
        }

        // 3. Lấy Role mặc định là STUDENT
        Role role = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // 4. Khởi tạo đối tượng User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);

        // ==========================================
        // FIX CỨNG DỮ LIỆU: EMAIL (Lưu ở bảng User)
        // ==========================================
        user.setEmail(request.getUsername() + "@st.edu.vn");

        // ==========================================
        // FIX CỨNG DỮ LIỆU: PROFILE (Lưu ở bảng UserProfile)
        // ==========================================
        UserProfile profile = new UserProfile();
        profile.setFullName("Chưa cập nhật tên");
        profile.setPhone("Chưa cập nhật SĐT");

        // Liên kết 2 chiều (Cực kỳ quan trọng để Cascade hoạt động)
        profile.setUser(user);
        user.setProfile(profile);

        // 5. Lưu vào Database
        // Nhờ cascade = CascadeType.ALL, lệnh này sẽ Insert cả bảng 'users' lẫn 'user_profiles'
        userRepository.save(user);

        return "SUCCESS";
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return "username không tồn tại";
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return "password sai";
        }

        return "SUCCESS";
    }
}