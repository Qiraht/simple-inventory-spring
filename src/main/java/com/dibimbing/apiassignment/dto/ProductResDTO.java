package com.dibimbing.apiassignment.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
}
