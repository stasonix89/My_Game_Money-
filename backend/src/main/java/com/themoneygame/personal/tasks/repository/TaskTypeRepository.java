package com.themoneygame.personal.tasks.repository;

import com.themoneygame.personal.tasks.domain.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {
}
