package com.dibimbing.apiassignment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductPatchDTO {
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be >= 0")
    private Integer quantity;
}
