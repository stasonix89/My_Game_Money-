// src/main/java/com/themoneygame/budget/web/dto/BankDto.java
package com.themoneygame.budget.web.dto;

import com.themoneygame.budget.domain.Bank;

public class BankDto {

    private Long id;
    private String name;

    public BankDto() {
    }

    public BankDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static BankDto fromEntity(Bank bank) {
        return new BankDto(bank.getId(), bank.getName());
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
