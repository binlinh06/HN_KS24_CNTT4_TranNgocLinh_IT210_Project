package org.example.it210_java_web_project.service.equipment.impl;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.Equipment;
import org.example.it210_java_web_project.repository.EquipmentRepository;
import org.example.it210_java_web_project.service.equipment.EquipmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Override
    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }

    @Override
    public Equipment save(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    @Override
    public Equipment findById(Long id) {
        return equipmentRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        equipmentRepository.deleteById(id);
    }
}