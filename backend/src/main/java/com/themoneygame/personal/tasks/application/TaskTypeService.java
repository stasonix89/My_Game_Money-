// src/main/java/com/themoneygame/personal/tasks/application/TaskTypeService.java
package com.themoneygame.personal.tasks.application;

import com.themoneygame.auth.domain.User;
import com.themoneygame.personal.tasks.domain.TaskType;
import com.themoneygame.personal.tasks.infrastructure.TaskTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskTypeService {

    private final TaskTypeRepository taskTypeRepository;

    public TaskTypeService(TaskTypeRepository taskTypeRepository) {
        this.taskTypeRepository = taskTypeRepository;
    }

    public List<TaskType> getAllForUser(User user) {
        return taskTypeRepository.findAllByUserOrderByNameAsc(user);
    }

    @Transactional
    public TaskType createType(User user, String name) {
        TaskType type = new TaskType(user, name);
        return taskTypeRepository.save(type);
    }

    @Transactional
    public TaskType updateType(User user, Long id, String name) {
        TaskType existing = taskTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskType not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden: type does not belong to current user");
        }

        existing.setName(name);
        return taskTypeRepository.save(existing);
    }

    @Transactional
    public void deleteType(User user, Long id) {
        TaskType existing = taskTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskType not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden: type does not belong to current user");
        }

        taskTypeRepository.delete(existing);
    }

    public TaskType getByUserAndNameOrThrow(User user, String name) {
        return taskTypeRepository.findByUserAndName(user, name)
                .orElseThrow(() -> new RuntimeException("TASK_TYPE_NOT_FOUND"));
    }
}
