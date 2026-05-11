package org.example.it210_java_web_project.repository;

import org.example.it210_java_web_project.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}
