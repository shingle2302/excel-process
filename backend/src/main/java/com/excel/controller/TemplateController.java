package com.excel.controller;

import com.excel.annotation.OperationLog;
import com.excel.annotation.RequirePermission;
import com.excel.dto.TemplateDTO;
import com.excel.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Tag(name = "模板系统")
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping
    @RequirePermission("template:write")
    @OperationLog(type = "模板", value = "创建模板")
    @Operation(summary = "创建模板")
    public TemplateDTO create(@RequestBody TemplateDTO template) {
        return templateService.create(template);
    }

    @GetMapping
    @RequirePermission("template:read")
    @Operation(summary = "模板列表")
    public List<TemplateDTO> list() {
        return templateService.list();
    }

    @GetMapping("/{id}")
    @RequirePermission("template:read")
    @Operation(summary = "模板详情")
    public TemplateDTO getById(@PathVariable String id) {
        return templateService.getById(id);
    }

    @PutMapping("/{id}")
    @RequirePermission("template:write")
    @OperationLog(type = "模板", value = "更新模板")
    @Operation(summary = "更新模板")
    public TemplateDTO update(@PathVariable String id, @RequestBody TemplateDTO template) {
        return templateService.update(id, template);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("template:write")
    @OperationLog(type = "模板", value = "删除模板")
    @Operation(summary = "删除模板")
    public void delete(@PathVariable String id) {
        templateService.delete(id);
    }
}
