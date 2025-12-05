package com.themoneygame.personal.tasks.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "task_types")
public class TaskType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // например: "Работа", "Спорт", "Музыка"

    // --- GET/SET ---
    public Long getId() { return id; }
    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }
}
