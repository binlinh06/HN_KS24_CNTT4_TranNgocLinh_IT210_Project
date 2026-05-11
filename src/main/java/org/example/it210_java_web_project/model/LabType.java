package org.example.it210_java_web_project.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lab_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
