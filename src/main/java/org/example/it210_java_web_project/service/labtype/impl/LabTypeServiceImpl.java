package org.example.it210_java_web_project.service.labtype.impl;

import lombok.RequiredArgsConstructor;
import org.example.it210_java_web_project.model.LabType;
import org.example.it210_java_web_project.repository.LabTypeRepository;

import org.example.it210_java_web_project.service.labtype.LabTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabTypeServiceImpl implements LabTypeService {

    private final LabTypeRepository labTypeRepository;

    @Override
    public List<LabType> findAll() {
        return labTypeRepository.findAll();
    }

    @Override
    public LabType findById(Long id) {
        return labTypeRepository.findById(id).orElse(null);
    }
}
