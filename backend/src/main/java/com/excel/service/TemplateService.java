package com.excel.service;

import com.excel.dto.TemplateDTO;

import java.util.List;

public interface TemplateService {
    TemplateDTO create(TemplateDTO template);
    TemplateDTO update(String id, TemplateDTO template);
    void delete(String id);
    TemplateDTO getById(String id);
    List<TemplateDTO> list();
}
