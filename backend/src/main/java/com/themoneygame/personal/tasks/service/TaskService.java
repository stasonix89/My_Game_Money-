package com.themoneygame.personal.tasks.service;

import com.themoneygame.personal.tasks.dto.*;
import java.util.List;

public interface TaskService {

    // Типы
    TaskTypeResponse createType(TaskTypeRequest req);
    List<TaskTypeResponse> getTypes();
    void deleteType(Long id);

    // Задачи
    TaskResponse createTask(Long userId, TaskRequest req);
    List<TaskResponse> getActiveTasks(Long userId, Long typeId, Integer month, Integer year);
    List<TaskResponse> getHistory(Long userId);
    TaskResponse markDone(Long userId, Long taskId);
    void deleteTask(Long userId, Long taskId);
}
