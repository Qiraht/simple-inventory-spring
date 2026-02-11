package com.dibimbing.apiassignment.controller;

import com.dibimbing.apiassignment.dto.ProductPatchDTO;
import com.dibimbing.apiassignment.dto.ProductReqDTO;
import com.dibimbing.apiassignment.dto.ProductResDTO;
import com.dibimbing.apiassignment.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> postProduct(@Valid @RequestBody ProductReqDTO request) {
        String response = productService.addProduct(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResDTO> getProductById(@PathVariable("id") Long id) {
        ProductResDTO response = productService.getProductById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResDTO>> getProducts() {
        List<ProductResDTO> response = productService.getProduct();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> addProductStock(@PathVariable("id") Long id, @RequestBody ProductPatchDTO request) {
        String response = productService.addProductStock(id, request.getQuantity());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN)'")
    public ResponseEntity<String> updateProduct(@PathVariable("id") Long id, @RequestBody ProductReqDTO request) {
        String response = productService.updateProduct(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
        String response = productService.deleteProduct(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/sale")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> postProductSales(@PathVariable("id") Long id, @RequestBody ProductPatchDTO request) {
        String response = productService.addProductSales(id, request.getQuantity());

        return ResponseEntity.ok(response);
    }
}
