// src/main/java/com/themoneygame/personal/tasks/application/TaskService.java
package com.themoneygame.personal.tasks.application;

import com.themoneygame.auth.domain.User;
import com.themoneygame.personal.tasks.domain.Task;
import com.themoneygame.personal.tasks.domain.TaskType;
import com.themoneygame.personal.tasks.infrastructure.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskTypeService taskTypeService;

    public TaskService(TaskRepository taskRepository,
                       TaskTypeService taskTypeService) {
        this.taskRepository = taskRepository;
        this.taskTypeService = taskTypeService;
    }

    /**
     * Все задачи пользователя (актуальные и из истории).
     * Фронт сам фильтрует по году/месяцу/статусу.
     */
    public List<Task> getAllForUser(User user) {
        return taskRepository.findAllByUserOrderByYearDescMonthDescDateDesc(user);
    }

    @Transactional
    public Task createTask(User user,
                           String taskTypeName,
                           String text,
                           boolean completed,
                           int year,
                           int month,
                           LocalDate date) {

        TaskType type = taskTypeService.getByUserAndNameOrThrow(user, taskTypeName);

        Task task = new Task();
        task.setUser(user);
        task.setTaskType(type);
        task.setText(text);
        task.setCompleted(completed);

        // дата — главный источник, из неё выставляем год/месяц
        if (date != null) {
            task.setDate(date);
        } else {
            // если по какой-то причине date не прислали — соберём из year/month и day=1
            task.setDate(LocalDate.of(year, month, 1));
        }

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(User user,
                           Long id,
                           String taskTypeName,
                           String text,
                           boolean completed,
                           int year,
                           int month,
                           LocalDate date) {

        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden: task does not belong to current user");
        }

        TaskType type = taskTypeService.getByUserAndNameOrThrow(user, taskTypeName);

        existing.setTaskType(type);
        existing.setText(text);
        existing.setCompleted(completed);

        if (date != null) {
            existing.setDate(date);
        } else {
            existing.setDate(LocalDate.of(year, month, 1));
        }

        return taskRepository.save(existing);
    }

    @Transactional
    public void deleteTask(User user, Long id) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden: task does not belong to current user");
        }

        taskRepository.delete(existing);
    }
}
