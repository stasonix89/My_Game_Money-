package com.themoneygame.personal.tasks.dto;

import com.themoneygame.personal.tasks.domain.TaskStatus;

public class TaskResponse {

    private Long id;
    private String typeName;
    private Integer month;
    private Integer year;
    private String text;
    private TaskStatus status;

    // getters/setters
    public Long getId() { return id; }
    public String getTypeName() { return typeName; }
    public Integer getMonth() { return month; }
    public Integer getYear() { return year; }
    public String getText() { return text; }
    public TaskStatus getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setText(String text) { this.text = text; }
    public void setStatus(TaskStatus status) { this.status = status; }
}
