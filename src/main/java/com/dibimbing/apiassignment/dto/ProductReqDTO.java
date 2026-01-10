package com.dibimbing.apiassignment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductReqDTO {
    @NotEmpty(message = "Name cannot be null")
    private String name;
    private String description;

    @DecimalMin(value = "1.0", message = "Price should be more than 0")
    private Double price;

    @Min(value = 1, message = "Stock should be more than 0")
    private Integer stock;
}
