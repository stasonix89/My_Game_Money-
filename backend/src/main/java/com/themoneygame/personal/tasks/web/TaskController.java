// src/main/java/com/themoneygame/personal/tasks/web/TaskController.java
package com.themoneygame.personal.tasks.web;

import com.themoneygame.auth.application.UserDetailsImpl;
import com.themoneygame.auth.domain.User;
import com.themoneygame.personal.tasks.application.TaskService;
import com.themoneygame.personal.tasks.domain.Task;
import com.themoneygame.personal.tasks.web.dto.TaskDto;
import com.themoneygame.personal.tasks.web.dto.TaskRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private User toUser(UserDetailsImpl userDetails) {
        return userDetails.toUser();
    }

    // ---------------------------------------
    // GET /api/tasks — все задачи пользователя
    // ---------------------------------------
    @GetMapping
    public List<TaskDto> getTasks(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User userRef = toUser(userDetails);
        return taskService.getAllForUser(userRef).stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ---------------------------------------
    // POST /api/tasks — создание
    // ---------------------------------------
    @PostMapping
    public TaskDto createTask(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody TaskRequest request
    ) {
        User userRef = toUser(userDetails);

        Task created = taskService.createTask(
                userRef,
                request.getTaskType(),
                request.getText(),
                request.isCompleted(),
                request.getYear(),
                request.getMonth(),
                request.getDate()
        );

        return TaskDto.fromEntity(created);
    }

    // ---------------------------------------
    // PUT /api/tasks/{id} — обновление
    // ---------------------------------------
    @PutMapping("/{id}")
    public TaskDto updateTask(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @RequestBody TaskRequest request
    ) {
        User userRef = toUser(userDetails);

        Task updated = taskService.updateTask(
                userRef,
                id,
                request.getTaskType(),
                request.getText(),
                request.isCompleted(),
                request.getYear(),
                request.getMonth(),
                request.getDate()
        );

        return TaskDto.fromEntity(updated);
    }

    // ---------------------------------------
    // DELETE /api/tasks/{id} — удаление
    // ---------------------------------------
    @DeleteMapping("/{id}")
    public void deleteTask(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        User userRef = toUser(userDetails);
        taskService.deleteTask(userRef, id);
    }
}
