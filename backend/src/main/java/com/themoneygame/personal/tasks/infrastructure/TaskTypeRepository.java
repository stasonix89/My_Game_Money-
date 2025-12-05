// src/main/java/com/themoneygame/personal/tasks/infrastructure/TaskTypeRepository.java
package com.themoneygame.personal.tasks.infrastructure;

import com.themoneygame.auth.domain.User;
import com.themoneygame.personal.tasks.domain.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {

    List<TaskType> findAllByUserOrderByNameAsc(User user);

    Optional<TaskType> findByUserAndName(User user, String name);
}
