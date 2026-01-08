package com.dibimbing.apiassignment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductReqDTO {
    @NotEmpty
    private String name;
    private String description;
    @Size(min = 1)
    private Double price;
    @Size(min = 1)
    private Integer stock;
}
