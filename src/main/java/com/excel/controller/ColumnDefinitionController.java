package com.excel.controller;

import com.excel.entity.ColumnDefinition;
import com.excel.service.ColumnDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/column-definitions")
public class ColumnDefinitionController {

    @Autowired
    private ColumnDefinitionService columnDefinitionService;

    @PostMapping
    public ColumnDefinition createColumnDefinition(@RequestBody ColumnDefinition columnDefinition) {
        columnDefinitionService.save(columnDefinition);
        return columnDefinition;
    }

    @GetMapping
    public List<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitionService.list();
    }

    @GetMapping("/{id}")
    public ColumnDefinition getColumnDefinition(@PathVariable Long id) {
        return columnDefinitionService.getById(id);
    }

    @PutMapping("/{id}")
    public ColumnDefinition updateColumnDefinition(@PathVariable Long id, @RequestBody ColumnDefinition columnDefinition) {
        columnDefinition.setId(id);
        columnDefinitionService.updateById(columnDefinition);
        return columnDefinition;
    }

    @DeleteMapping("/{id}")
    public void deleteColumnDefinition(@PathVariable Long id) {
        columnDefinitionService.removeById(id);
    }

    @GetMapping("/task-definition/{taskDefinitionId}")
    public List<ColumnDefinition> getColumnDefinitionsByTaskDefinitionId(@PathVariable Long taskDefinitionId) {
        return columnDefinitionService.getByTaskDefinitionId(taskDefinitionId);
    }

    @DeleteMapping("/task-definition/{taskDefinitionId}")
    public void deleteColumnDefinitionsByTaskDefinitionId(@PathVariable Long taskDefinitionId) {
        columnDefinitionService.deleteByTaskDefinitionId(taskDefinitionId);
    }
}