package org.example.it210_java_web_project.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.it210_java_web_project.model.AcademicEvaluation;
import org.example.it210_java_web_project.model.BorrowingRecord;
import org.example.it210_java_web_project.model.Equipment;
import org.example.it210_java_web_project.model.MentoringSession;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AcademicProfileDTO {
    // 1. Thông tin ca tư vấn & Giảng viên
    private MentoringSession session;

    // 2. Kết quả đánh giá (Có thể null nếu giảng viên quên đánh giá)
    private AcademicEvaluation evaluation;

    // 3. Danh sách thiết bị đã mượn (Có thể rỗng)
    private List<Equipment> borrowedEquipments = new ArrayList<>();
    private BorrowingRecord borrowingRecord;
}