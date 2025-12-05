// src/main/java/com/themoneygame/personal/tasks/web/TaskTypeController.java
package com.themoneygame.personal.tasks.web;

import com.themoneygame.auth.application.UserDetailsImpl;
import com.themoneygame.auth.domain.User;
import com.themoneygame.personal.tasks.application.TaskTypeService;
import com.themoneygame.personal.tasks.domain.TaskType;
import com.themoneygame.personal.tasks.web.dto.TaskTypeDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks/types")
public class TaskTypeController {

    private final TaskTypeService taskTypeService;

    public TaskTypeController(TaskTypeService taskTypeService) {
        this.taskTypeService = taskTypeService;
    }

    private User toUser(UserDetailsImpl userDetails) {
        return userDetails.toUser();
    }

    @GetMapping
    public List<TaskTypeDto> getTaskTypes(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User userRef = toUser(userDetails);
        return taskTypeService.getAllForUser(userRef).stream()
                .map(TaskTypeDto::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public TaskTypeDto createTaskType(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody TaskTypeDto dto
    ) {
        User userRef = toUser(userDetails);
        TaskType created = taskTypeService.createType(userRef, dto.getName());
        return TaskTypeDto.fromEntity(created);
    }

    @PutMapping("/{id}")
    public TaskTypeDto updateTaskType(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @RequestBody TaskTypeDto dto
    ) {
        User userRef = toUser(userDetails);
        TaskType updated = taskTypeService.updateType(userRef, id, dto.getName());
        return TaskTypeDto.fromEntity(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteTaskType(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        User userRef = toUser(userDetails);
        taskTypeService.deleteType(userRef, id);
    }
}
