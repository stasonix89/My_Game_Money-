// src/main/java/com/themoneygame/personal/tasks/web/dto/TaskDto.java
package com.themoneygame.personal.tasks.web.dto;

import com.themoneygame.personal.tasks.domain.Task;

import java.time.LocalDate;

public class TaskDto {

    private Long id;
    private String taskType;   // имя типа, а не id
    private String text;
    private boolean completed;
    private int month;
    private int year;
    private LocalDate date;    // "2025-12-15"

    public TaskDto() {
    }

    public TaskDto(Long id,
                   String taskType,
                   String text,
                   boolean completed,
                   int month,
                   int year,
                   LocalDate date) {
        this.id = id;
        this.taskType = taskType;
        this.text = text;
        this.completed = completed;
        this.month = month;
        this.year = year;
        this.date = date;
    }

    public static TaskDto fromEntity(Task task) {
        if (task == null) return null;

        return new TaskDto(
                task.getId(),
                task.getTaskType().getName(),
                task.getText(),
                task.isCompleted(),
                task.getMonth(),
                task.getYear(),
                task.getDate()
        );
    }

    // getters / setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
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

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
