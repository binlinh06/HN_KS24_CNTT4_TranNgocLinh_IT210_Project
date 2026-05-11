package org.example.it210_java_web_project.repository;

import org.example.it210_java_web_project.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
