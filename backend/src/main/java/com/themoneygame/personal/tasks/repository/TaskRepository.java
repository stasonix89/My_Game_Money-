package com.themoneygame.personal.tasks.repository;

import com.themoneygame.personal.tasks.domain.Task;
import com.themoneygame.personal.tasks.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, TaskStatus status);

    List<Task> findByUserIdAndStatusAndTypeIdAndMonthAndYear(
            Long userId,
            TaskStatus status,
            Long typeId,
            Integer month,
            Integer year
    );
}
