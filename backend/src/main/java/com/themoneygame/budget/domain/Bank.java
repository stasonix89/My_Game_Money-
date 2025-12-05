// src/main/java/com/themoneygame/budget/domain/Bank.java
package com.themoneygame.budget.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "banks")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    public Bank() {
    }

    public Bank(String name) {
        this.name = name;
    }

    // getters / setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
