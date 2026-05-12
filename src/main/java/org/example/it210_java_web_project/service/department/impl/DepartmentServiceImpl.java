package org.example.it210_java_web_project.service.department.impl;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.Department;
import org.example.it210_java_web_project.repository.DepartmentRepository;

import org.example.it210_java_web_project.service.department.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public Department findById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }
}