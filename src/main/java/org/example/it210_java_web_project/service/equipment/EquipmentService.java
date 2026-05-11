package org.example.it210_java_web_project.service.equipment;

import org.example.it210_java_web_project.model.Equipment;

import java.util.List;

public interface EquipmentService {
    List<Equipment> findAll();
    Equipment save(Equipment equipment);
    Equipment findById(Long id);
    void delete(Long id);
}