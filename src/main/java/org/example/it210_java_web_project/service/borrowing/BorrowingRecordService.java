package org.example.it210_java_web_project.service;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.*;
import org.example.it210_java_web_project.repository.BorrowingRecordRepository;
import org.example.it210_java_web_project.repository.EquipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BorrowingRecordService {

    private final BorrowingRecordRepository borrowingRepository;
    private final EquipmentRepository equipmentRepository;

    // ========================================================
    // CORE-08: DUYỆT XUẤT KHO THIẾT BỊ (ADMIN / LAB STAFF)
    // ========================================================
    @Transactional(rollbackFor = Exception.class)
    public void confirmAllocation(Long recordId) {

        // 1. Lấy phiếu mượn ra
        BorrowingRecord record = borrowingRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Hệ thống không tìm thấy phiếu mượn này!"));

        // 2. Chặn ngay nếu phiếu không ở trạng thái hợp lệ (VD: Phải là APPROVED từ giảng viên)
        if (record.getStatus() != BorrowStatus.APPROVED) {
            throw new RuntimeException("Phiếu mượn chưa được Giảng viên phê duyệt hoặc đã được xử lý!");
        }

        // 3. Vòng lặp tử thần: Kiểm tra số lượng tồn kho của TỪNG món đồ
        for (BorrowingDetail detail : record.getDetails()) {
            Equipment equipment = detail.getEquipment();
            int requiredQuantity = detail.getQuantity(); // Số lượng sinh viên cần (hiện tại mình đang set cứng là 1)

            // Lôgic cốt lõi: NẾU KHÔNG ĐỦ HÀNG TRONG KHO
            if (equipment.getQuantity() < requiredQuantity) {
                // Fail-Fast: Quăng lỗi ngay lập tức. @Transactional sẽ nghe thấy và Rollback mọi thứ.
                throw new RuntimeException("❌ XUẤT KHO THẤT BẠI: Thiết bị [" + equipment.getName() +
                        "] chỉ còn " + equipment.getQuantity() + " chiếc trong kho, không đủ để cấp phát!");
            }

            // Lôgic cốt lõi: NẾU ĐỦ HÀNG -> Trừ lùi số lượng tồn kho
            equipment.setQuantity(equipment.getQuantity() - requiredQuantity);

            // Lưu lại số lượng mới vào bảng Equipments
            equipmentRepository.save(equipment);
        }

        // 4. Nếu vượt qua được vòng lặp trên mà không có lỗi nào ném ra
        // -> Chuyển trạng thái phiếu mượn thành ĐANG MƯỢN (Đã xuất kho)
        // Lưu ý: Đảm bảo trong Enum BorrowStatus của bạn có trạng thái BORROWED
        record.setStatus(BorrowStatus.BORROWED);

        borrowingRepository.save(record);
    }
}