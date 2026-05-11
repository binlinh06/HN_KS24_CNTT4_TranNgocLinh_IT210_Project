package org.example.it210_java_web_project.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class BorrowingDetailId implements Serializable {

    private Long recordId;
    private Long equipmentId;
}