package com.themoneygame.personal.tasks.dto;

public class TaskRequest {

    private Long typeId; // тип задачи
    private Integer month;
    private Integer year;
    private String text;

    // getters/setters
    public Long getTypeId() { return typeId; }
    public void setTypeId(Long typeId) { this.typeId = typeId; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
