// src/main/java/com/themoneygame/personal/tasks/domain/TaskType.java
package com.themoneygame.personal.tasks.domain;

import com.themoneygame.auth.domain.User;
import jakarta.persistence.*;

@Entity
@Table(name = "task_types")
public class TaskType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Владелец типов задач.
     * Каждый пользователь может иметь свой набор типов.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    public TaskType() {
    }

    public TaskType(User user, String name) {
        this.user = user;
        this.name = name;
    }

    // -------- getters / setters --------

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
