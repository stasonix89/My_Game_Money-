package com.themoneygame.personal.tasks.controller;

import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import com.themoneygame.personal.tasks.dto.*;
import com.themoneygame.personal.tasks.service.TaskService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;
    private final UserRepository userRepo;

    public TaskController(TaskService service,
                          UserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }

    private Long uid(UserDetails ud) {
        User u = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        return u.getId();
    }

    // ---------- Типы ----------
    @PostMapping("/types")
    public TaskTypeResponse createType(@RequestBody TaskTypeRequest req) {
        return service.createType(req);
    }

    @GetMapping("/types")
    public List<TaskTypeResponse> getTypes() {
        return service.getTypes();
    }

    @DeleteMapping("/types/{id}")
    public void deleteType(@PathVariable Long id) {
        service.deleteType(id);
    }

    // ---------- Задачи ----------
    @PostMapping
    public TaskResponse create(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody TaskRequest req
    ) {
        return service.createTask(uid(ud), req);
    }

    @GetMapping
    public List<TaskResponse> list(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        return service.getActiveTasks(uid(ud), typeId, month, year);
    }

    @GetMapping("/history")
    public List<TaskResponse> history(@AuthenticationPrincipal UserDetails ud) {
        return service.getHistory(uid(ud));
    }

    @PostMapping("/{id}/done")
    public TaskResponse done(@AuthenticationPrincipal UserDetails ud,
                             @PathVariable Long id) {
        return service.markDone(uid(ud), id);
    }

    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal UserDetails ud,
                       @PathVariable Long id) {
        service.deleteTask(uid(ud), id);
    }
}
