package com.dibimbing.apiassignment.controller;

import com.dibimbing.apiassignment.dto.ProductReqDTO;
import com.dibimbing.apiassignment.dto.ProductResDTO;
import com.dibimbing.apiassignment.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Validated
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public void postProduct(@Valid @RequestBody ProductReqDTO request) {
        productService.addProduct(request);
    }

    @GetMapping("/{id}")
    public ProductResDTO getProductById(@PathVariable("id") Long id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public List<ProductResDTO> getProducts() {
        return productService.getProduct();
    }

    @PatchMapping("/{id}")
    public void addProductStock(@PathVariable("id") Long id, @RequestBody Integer quantity) {
        productService.addProductStock(id, quantity);
    }

    @PutMapping("/{id}")
    public void updateProduct(@PathVariable("id") Long id, @RequestBody ProductReqDTO request) {
        productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
    }

}
