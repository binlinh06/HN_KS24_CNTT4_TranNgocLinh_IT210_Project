package org.example.it210_java_web_project.service.department;


import org.example.it210_java_web_project.model.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> findAll();
    Department findById(Long id);
}