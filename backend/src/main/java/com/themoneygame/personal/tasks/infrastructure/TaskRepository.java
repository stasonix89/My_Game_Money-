// src/main/java/com/themoneygame/personal/tasks/infrastructure/TaskRepository.java
package com.themoneygame.personal.tasks.infrastructure;

import com.themoneygame.auth.domain.User;
import com.themoneygame.personal.tasks.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUserOrderByYearDescMonthDescDateDesc(User user);
}
