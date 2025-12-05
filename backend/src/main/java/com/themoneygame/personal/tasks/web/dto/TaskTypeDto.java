// src/main/java/com/themoneygame/personal/tasks/web/dto/TaskTypeDto.java
package com.themoneygame.personal.tasks.web.dto;

import com.themoneygame.personal.tasks.domain.TaskType;

public class TaskTypeDto {

    private Long id;
    private String name;

    public TaskTypeDto() {
    }

    public TaskTypeDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TaskTypeDto fromEntity(TaskType type) {
        if (type == null) return null;
        return new TaskTypeDto(type.getId(), type.getName());
    }

    // getters / setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
