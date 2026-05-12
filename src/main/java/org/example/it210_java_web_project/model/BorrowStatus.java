package org.example.it210_java_web_project.model;

public enum BorrowStatus {
    PENDING,    // Chờ phê duyệt (Nếu sinh viên tự gửi yêu cầu mượn)
    APPROVED,   // Đã duyệt & Chờ cấp phát (Giảng viên đã chỉ định ở CORE-06, chờ Admin xuất kho)
    REJECTED,   // Bị từ chối (Giảng viên không duyệt mượn)
    BORROWED,   // Đang mượn / Đã xuất kho (Admin/Lab đã ấn xác nhận xuất kho ở CORE-08)
    RETURNED,   // Đã trả (Sinh viên đã đem thiết bị trả lại phòng Lab)
    CANCELLED   // Đã hủy (Sinh viên tự hủy trước khi lấy đồ, hoặc Admin hủy do hết hạn)
}