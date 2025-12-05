// src/main/java/com/themoneygame/personal/tasks/web/dto/TaskRequest.java
package com.themoneygame.personal.tasks.web.dto;

import java.time.LocalDate;

/**
 * Тело запроса для создания / редактирования задачи.
 *
 * Пример (как в ТЗ):
 * {
 *   "taskType": "Спорт",
 *   "text": "Пробежка 5 км",
 *   "completed": false,
 *   "month": 12,
 *   "year": 2025,
 *   "date": "2025-12-15"
 * }
 */
public class TaskRequest {

    private String taskType;
    private String text;
    private boolean completed;
    private int month;
    private int year;
    private LocalDate date;

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
