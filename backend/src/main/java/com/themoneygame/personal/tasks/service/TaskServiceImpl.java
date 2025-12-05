package com.themoneygame.personal.tasks.service;

import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import com.themoneygame.personal.tasks.domain.*;
import com.themoneygame.personal.tasks.dto.*;
import com.themoneygame.personal.tasks.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepo;
    private final TaskTypeRepository typeRepo;
    private final UserRepository userRepo;

    public TaskServiceImpl(TaskRepository taskRepo,
                           TaskTypeRepository typeRepo,
                           UserRepository userRepo) {
        this.taskRepo = taskRepo;
        this.typeRepo = typeRepo;
        this.userRepo = userRepo;
    }

    // ---------- TYPE ----------
    @Override
    public TaskTypeResponse createType(TaskTypeRequest req) {
        TaskType t = new TaskType();
        t.setName(req.getName());
        typeRepo.save(t);

        TaskTypeResponse r = new TaskTypeResponse();
        r.setId(t.getId());
        r.setName(t.getName());
        return r;
    }

    @Override
    public List<TaskTypeResponse> getTypes() {
        return typeRepo.findAll().stream().map(t -> {
            TaskTypeResponse r = new TaskTypeResponse();
            r.setId(t.getId());
            r.setName(t.getName());
            return r;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteType(Long id) {
        typeRepo.deleteById(id);
    }

    // ---------- TASK ----------
    @Override
    public TaskResponse createTask(Long userId, TaskRequest req) {

        User user = userRepo.findById(userId).orElseThrow();
        TaskType type = typeRepo.findById(req.getTypeId()).orElseThrow();

        Task t = new Task();
        t.setUser(user);
        t.setType(type);
        t.setMonth(req.getMonth());
        t.setYear(req.getYear());
        t.setText(req.getText());

        taskRepo.save(t);

        return toResponse(t);
    }

    @Override
    public List<TaskResponse> getActiveTasks(Long userId, Long typeId, Integer month, Integer year) {

        if (typeId != null && month != null && year != null) {
            return taskRepo
                    .findByUserIdAndStatusAndTypeIdAndMonthAndYear(
                            userId, TaskStatus.ACTIVE, typeId, month, year
                    )
                    .stream().map(this::toResponse)
                    .collect(Collectors.toList());
        }

        return taskRepo
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, TaskStatus.ACTIVE)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getHistory(Long userId) {
        return taskRepo
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, TaskStatus.DONE)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse markDone(Long userId, Long taskId) {

        Task t = taskRepo.findById(taskId).orElseThrow();

        if (!t.getUser().getId().equals(userId))
            throw new SecurityException("No access");

        t.setStatus(TaskStatus.DONE);
        taskRepo.save(t);

        return toResponse(t);
    }

    @Override
    public void deleteTask(Long userId, Long taskId) {

        Task t = taskRepo.findById(taskId).orElseThrow();

        if (!t.getUser().getId().equals(userId))
            throw new SecurityException("No access");

        taskRepo.delete(t);
    }

    private TaskResponse toResponse(Task t) {
        TaskResponse r = new TaskResponse();
        r.setId(t.getId());
        r.setTypeName(t.getType().getName());
        r.setMonth(t.getMonth());
        r.setYear(t.getYear());
        r.setText(t.getText());
        r.setStatus(t.getStatus());

        return r;
    }
}
