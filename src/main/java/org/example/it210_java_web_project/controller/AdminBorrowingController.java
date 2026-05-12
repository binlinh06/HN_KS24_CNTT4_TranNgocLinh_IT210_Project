package org.example.it210_java_web_project.controller;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.BorrowingRecord;
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

    // ========================================================
    // HIỂN THỊ DANH SÁCH YÊU CẦU MƯỢN
    // ========================================================
    @GetMapping("/list")
    public String showBorrowingList(Model model) {
        // Tạm thời lấy tất cả (Bạn có thể viết câu query lấy theo ngày mới nhất nếu muốn)
        List<BorrowingRecord> records = borrowingRepository.findAll();

        model.addAttribute("recentRequests", records);
        return "admin/dashboard";
    }

    // ========================================================
    // NÚT XÁC NHẬN (XUẤT KHO)
    // ========================================================
    @PostMapping("/export/{id}")
    public String exportEquipment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Gọi hàm trừ kho và đổi trạng thái thành BORROWED
            borrowingService.confirmAllocation(id);
            redirectAttributes.addFlashAttribute("success", "Đã xuất kho thành công và cập nhật lại số lượng tồn kho!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // Return về trang list thay vì dashboard để load lại danh sách mới nhất
        return "redirect:/admin/borrowing/list";
    }

    // ========================================================
    // NÚT TỪ CHỐI (ĐÃ BỔ SUNG ĐỂ SỬA LỖI 404)
    // ========================================================
    @PostMapping("/reject/{id}")
    public String rejectBorrowingRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Gọi hàm từ chối yêu cầu (Cập nhật trạng thái thành REJECTED hoặc CANCELLED)
            borrowingService.rejectRequest(id);
            redirectAttributes.addFlashAttribute("success", "Đã TỪ CHỐI phiếu xuất kho thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        // Return về trang list
        return "redirect:/admin/borrowing/list";
    }
}