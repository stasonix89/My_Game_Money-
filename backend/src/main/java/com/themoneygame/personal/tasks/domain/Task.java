// src/main/java/com/themoneygame/personal/tasks/domain/Task.java
package com.themoneygame.personal.tasks.domain;

import com.themoneygame.auth.domain.User;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Владелец задачи.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    /**
     * Тип задачи (спорт, здоровье и т.п.).
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TaskType taskType;

    /**
     * Текст задачи, например "Пробежка 5 км".
     */
    @Column(nullable = false, length = 500)
    private String text;

    /**
     * Флаг "выполнено".
     * completed = true → задача попадает в историю.
     */
    @Column(nullable = false)
    private boolean completed = false;

    /**
     * Год и месяц задачи — для удобной группировки / фильтрации.
     * При сохранении автоматически подставляются из date.
     */
    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    /**
     * Полная дата задачи (YYYY-MM-DD).
     */
    @Column(nullable = false)
    private LocalDate date;

    public Task() {
    }

    public Task(User user,
                TaskType taskType,
                String text,
                boolean completed,
                LocalDate date) {
        this.user = user;
        this.taskType = taskType;
        this.text = text;
        this.completed = completed;
        setDate(date);
    }

    // ---------- getters / setters ----------

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public LocalDate getDate() {
        return date;
    }

    /**
     * При установке даты автоматически выставляем год и месяц.
     */
    public void setDate(LocalDate date) {
        this.date = date;
        if (date != null) {
            this.year = date.getYear();
            this.month = date.getMonthValue();
        }
    }
}
