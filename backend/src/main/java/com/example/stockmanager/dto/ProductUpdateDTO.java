package com.example.stockmanager.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class ProductUpdateDTO {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Pattern(regexp = "LEGUMES|FRUITS|VIANDES|LAITIERS")
    private String category;

    @NotNull
    @Min(0)
    private Integer quantity;

    @NotNull
    private LocalDate expirationDate;

    // Getters & Setters
} 