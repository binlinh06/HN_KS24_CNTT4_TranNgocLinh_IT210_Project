package org.example.it210_java_web_project.service.labtype;


import org.example.it210_java_web_project.model.LabType;
import java.util.List;

public interface LabTypeService {
    List<LabType> findAll();
    LabType findById(Long id);
}