package org.example.it210_java_web_project.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "borrowing_details")
@Getter @Setter
public class BorrowingDetail {

    @EmbeddedId
    private BorrowingDetailId id;

    @ManyToOne
    @MapsId("recordId")
    @JoinColumn(name = "record_id")

    private BorrowingRecord record;

    @ManyToOne
    @MapsId("equipmentId")
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    private Integer quantity;
    @Enumerated(EnumType.STRING)
    @Column(length = 50) // 🔥 Thêm dòng này để nới rộng cột trong DB
    private BorrowStatus status;
}
