package com.excel.controller;

import com.excel.entity.TaskDefinition;
import com.excel.service.TaskDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-definitions")
public class TaskDefinitionController {

    @Autowired
    private TaskDefinitionService taskDefinitionService;

    @PostMapping
    public TaskDefinition createTaskDefinition(@RequestBody TaskDefinition taskDefinition) {
        taskDefinitionService.save(taskDefinition);
        return taskDefinition;
    }

    @GetMapping
    public List<TaskDefinition> getTaskDefinitions() {
        return taskDefinitionService.list();
    }

    @GetMapping("/{id}")
    public TaskDefinition getTaskDefinition(@PathVariable Long id) {
        return taskDefinitionService.getById(id);
    }

    @PutMapping("/{id}")
    public TaskDefinition updateTaskDefinition(@PathVariable Long id, @RequestBody TaskDefinition taskDefinition) {
        taskDefinition.setId(id);
        taskDefinitionService.updateById(taskDefinition);
        return taskDefinition;
    }

    @DeleteMapping("/{id}")
    public void deleteTaskDefinition(@PathVariable Long id) {
        taskDefinitionService.removeById(id);
    }

    @GetMapping("/name/{name}")
    public TaskDefinition getTaskDefinitionByName(@PathVariable String name) {
        return taskDefinitionService.getByName(name);
    }

    @GetMapping("/client/{clientId}")
    public List<TaskDefinition> getTaskDefinitionsByClientId(@PathVariable Long clientId) {
        return taskDefinitionService.getByClientId(clientId);
    }
}