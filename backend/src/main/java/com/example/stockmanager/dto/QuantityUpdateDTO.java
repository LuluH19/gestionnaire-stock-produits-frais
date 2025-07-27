package com.example.stockmanager.dto;

import jakarta.validation.constraints.*;

public class QuantityUpdateDTO {
    @NotNull
    @Min(0)
    private Integer quantity;

    // Getters & Setters
} 