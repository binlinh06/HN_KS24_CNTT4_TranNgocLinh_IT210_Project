package org.example.it210_java_web_project.repository;

import org.example.it210_java_web_project.model.BorrowingRecord;
import org.example.it210_java_web_project.model.BorrowStatus; // Đã import đúng tên Enum của bạn
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

    // Lấy danh sách thiết bị một sinh viên đang mượn (để hiển thị lên dashboard sinh viên)
    List<BorrowingRecord> findByStudentIdAndStatus(Long studentId, BorrowStatus status);

}