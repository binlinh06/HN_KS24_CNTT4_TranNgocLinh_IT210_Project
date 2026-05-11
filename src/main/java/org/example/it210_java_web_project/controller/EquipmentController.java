package org.example.it210_java_web_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.dto.EquipmentDTO;
import org.example.it210_java_web_project.model.Equipment;
import org.example.it210_java_web_project.model.Department; // Thêm import này
import org.example.it210_java_web_project.model.LabType;    // Thêm import này
import org.example.it210_java_web_project.service.department.DepartmentService;
import org.example.it210_java_web_project.service.equipment.EquipmentService;
import org.example.it210_java_web_project.service.labtype.LabTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final DepartmentService departmentService;
    private final LabTypeService labTypeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("equipments", equipmentService.findAll());
        return "admin/equipment/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("equipment", new EquipmentDTO());
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("labTypes", labTypeService.findAll());
        return "admin/equipment/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("equipment") EquipmentDTO dto,
                       BindingResult bindingResult,
                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("labTypes", labTypeService.findAll());
            return "admin/equipment/form";
        }

        // 1. Tạo Entity mới (hoặc load lại Entity cũ nếu là Edit)
        Equipment equipment;
        if (dto.getId() != null) {
            equipment = equipmentService.findById(dto.getId());
        } else {
            equipment = new Equipment();
        }

        // 2. Map dữ liệu cơ bản
        equipment.setName(dto.getName());
        equipment.setQuantity(dto.getQuantity());
        equipment.setStatus(dto.getStatus());

        // 3. THỰC HIỆN LẤY ĐỐI TƯỢNG TỪ DB (Phần bạn đã comment)
        if (dto.getDepartmentId() != null) {
            Department dept = departmentService.findById(dto.getDepartmentId());
            equipment.setDepartment(dept);
        }

        if (dto.getLabTypeId() != null) {
            LabType lab = labTypeService.findById(dto.getLabTypeId());
            equipment.setLabType(lab);
        }

        // 4. Lưu Entity
        equipmentService.save(equipment);
        return "redirect:/admin/equipment";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Equipment equipment = equipmentService.findById(id);

        EquipmentDTO dto = new EquipmentDTO();
        dto.setId(equipment.getId());
        dto.setName(equipment.getName());
        dto.setQuantity(equipment.getQuantity());
        dto.setStatus(equipment.getStatus());

        if (equipment.getDepartment() != null) {
            dto.setDepartmentId(equipment.getDepartment().getId());
        }
        if (equipment.getLabType() != null) {
            dto.setLabTypeId(equipment.getLabType().getId());
        }

        model.addAttribute("equipment", dto);
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("labTypes", labTypeService.findAll());

        return "admin/equipment/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return "redirect:/admin/equipment";
    }
}