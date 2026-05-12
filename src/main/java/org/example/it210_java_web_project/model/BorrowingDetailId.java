package org.example.it210_java_web_project.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode; // Thêm cái này
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode // 🔥 BẮT BUỘC CÓ CHO KHÓA CHÍNH KÉP
public class BorrowingDetailId implements Serializable {

    private Long recordId;
    private Long equipmentId;
}