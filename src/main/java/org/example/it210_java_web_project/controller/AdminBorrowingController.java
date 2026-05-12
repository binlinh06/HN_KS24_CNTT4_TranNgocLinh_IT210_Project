package org.example.it210_java_web_project.controller;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.BorrowingRecord;
import org.example.it210_java_web_project.model.BorrowStatus;
import org.example.it210_java_web_project.repository.BorrowingRecordRepository;
import org.example.it210_java_web_project.service.BorrowingRecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/borrowing")
@RequiredArgsConstructor
public class AdminBorrowingController {

    private final BorrowingRecordService borrowingService;
    private final BorrowingRecordRepository borrowingRepository;

    // Hiển thị danh sách Phiếu mượn thiết bị
    @GetMapping("/list")
    public String showBorrowingList(Model model) {
        // Tạm thời lấy tất cả, bạn có thể viết thêm logic lọc PENDING, APPROVED ở đây
        List<BorrowingRecord> records = borrowingRepository.findAll();

        model.addAttribute("recentRequests", records); // Đặt tên biến giống với HTML hiện tại
        return "admin/dashboard"; // Trỏ đến file giao diện admin của bạn
    }

    // Nút bấm "Xác nhận xuất kho" trên giao diện Admin
    @PostMapping("/export/{id}")
    public String exportEquipment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Gọi hàm trừ kho có Transaction
            borrowingService.confirmAllocation(id);
            redirectAttributes.addFlashAttribute("success", "Đã xuất kho thành công và cập nhật lại số lượng tồn kho!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/borrowing/list";
    }
}