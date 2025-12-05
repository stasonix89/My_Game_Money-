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

    // пользователь-владелец задачи
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    // тип задачи
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TaskType type;

    // месяц + год (в твоём дизайне только эти 2 поля)
    private Integer month;
    private Integer year;

    @Column(nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.ACTIVE;

    private LocalDate createdAt = LocalDate.now();

    // --- GET/SET ---
    public Long getId() { return id; }
    public User getUser() { return user; }
    public TaskType getType() { return type; }
    public Integer getMonth() { return month; }
    public Integer getYear() { return year; }
    public String getText() { return text; }
    public TaskStatus getStatus() { return status; }
    public LocalDate getCreatedAt() { return createdAt; }

    public void setUser(User user) { this.user = user; }
    public void setType(TaskType type) { this.type = type; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setText(String text) { this.text = text; }
    public void setStatus(TaskStatus status) { this.status = status; }
}
