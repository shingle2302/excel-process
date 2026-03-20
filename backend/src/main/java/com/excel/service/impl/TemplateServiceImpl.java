package com.excel.service.impl;

import com.excel.dto.TemplateDTO;
import com.excel.exception.BusinessException;
import com.excel.service.TemplateService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TemplateServiceImpl implements TemplateService {

    private final Map<String, TemplateDTO> templates = new ConcurrentHashMap<>();

    @Override
    public TemplateDTO create(TemplateDTO template) {
        String id = UUID.randomUUID().toString();
        template.setId(id);
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        templates.put(id, template);
        return template;
    }

    @Override
    public TemplateDTO update(String id, TemplateDTO template) {
        TemplateDTO existing = getById(id);
        existing.setName(template.getName());
        existing.setType(template.getType());
        existing.setContent(template.getContent());
        existing.setUpdateTime(LocalDateTime.now());
        return existing;
    }

    @Override
    public void delete(String id) {
        templates.remove(id);
    }

    @Override
    public TemplateDTO getById(String id) {
        TemplateDTO template = templates.get(id);
        if (template == null) {
            throw new BusinessException(404, "模板不存在");
        }
        return template;
    }

    @Override
    public List<TemplateDTO> list() {
        return new ArrayList<>(templates.values());
    }
}
