package com.stock.manager.dto;

import com.stock.manager.entity.ProductCategory;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class ProductCreateDTO {
    @NotBlank(message = "Le nom du produit est obligatoire.")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
    private String name;

    @NotNull(message = "La catégorie est obligatoire.")
    private ProductCategory category;

    @NotNull(message = "La quantité est obligatoire.")
    @Min(value = 0, message = "La quantité ne peut pas être négative.")
    private Integer quantity;

    @NotNull(message = "La date d'expiration est obligatoire.")
    @Future(message = "La date d'expiration doit être dans le futur.")
    private LocalDate expirationDate;

    // Getters et setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
}