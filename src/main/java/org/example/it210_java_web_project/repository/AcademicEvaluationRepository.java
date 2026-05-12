package org.example.it210_java_web_project.repository;

import org.example.it210_java_web_project.model.AcademicEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicEvaluationRepository extends JpaRepository<AcademicEvaluation, Long> {
    // Kế thừa sẵn các hàm save(), findById(), delete()...
}