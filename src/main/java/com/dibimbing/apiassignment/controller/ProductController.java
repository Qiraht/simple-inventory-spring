package com.dibimbing.apiassignment.controller;

import com.dibimbing.apiassignment.dto.ProductPatchDTO;
import com.dibimbing.apiassignment.dto.ProductReqDTO;
import com.dibimbing.apiassignment.dto.ProductResDTO;
import com.dibimbing.apiassignment.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String postProduct(@Valid @RequestBody ProductReqDTO request) {
        return productService.addProduct(request);
    }

    @GetMapping("/{id}")
    public ProductResDTO getProductById(@PathVariable("id") Long id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public List<ProductResDTO> getProducts() {
        return productService.getProduct();
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String addProductStock(@PathVariable("id") Long id, @RequestBody ProductPatchDTO request) {
        return productService.addProductStock(id, request.getQuantity());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN)'")
    public String updateProduct(@PathVariable("id") Long id, @RequestBody ProductReqDTO request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProduct(@PathVariable("id") Long id) {
        return productService.deleteProduct(id);
    }

}
