package com.dibimbing.apiassignment.service;

import com.dibimbing.apiassignment.dto.ProductReqDTO;
import com.dibimbing.apiassignment.dto.ProductResDTO;
import com.dibimbing.apiassignment.entity.Product;
import com.dibimbing.apiassignment.exceptions.custom.NotFoundException;
import com.dibimbing.apiassignment.exceptions.custom.ValidationException;
import com.dibimbing.apiassignment.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisService redisService;

    private Product savedProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        // Clean up Redis cache keys used by ProductService
        redisService.delete("products");

        Product product = new Product(null, "Test Product", "Test desc", 50.0, 100, false);
        savedProduct = productRepository.save(product);

        // Clear individual product cache
        redisService.delete("product::" + savedProduct.getId());
    }

    @Test
    void addProduct_ShouldPersistToDatabase() {
        ProductReqDTO request = new ProductReqDTO("New Product", "New desc", 25.0, 10);

        String result = productService.addProduct(request);

        assertTrue(result.contains("New Product"));
        List<Product> all = productRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        ProductResDTO result = productService.getProductById(savedProduct.getId());

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(50.0, result.getPrice());
    }

    @Test
    void getProductById_ShouldReturnFromCache_OnSecondCall() {
        // First call populates cache
        ProductResDTO first = productService.getProductById(savedProduct.getId());
        // Second call should hit cache
        ProductResDTO second = productService.getProductById(savedProduct.getId());

        assertEquals(first.getName(), second.getName());
        assertEquals(first.getId(), second.getId());
    }

    @Test
    void getProductById_ShouldThrow_WhenNotFound() {
        assertThrows(NotFoundException.class, () -> productService.getProductById(99999L));
    }

    @Test
    void getProduct_ShouldReturnAllNonDeleted() {
        // Add a deleted product
        productRepository.save(new Product(null, "Deleted", "del", 10.0, 5, true));

        List<ProductResDTO> result = productService.getProduct();

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void addProductStock_ShouldIncreaseStock() {
        String result = productService.addProductStock(savedProduct.getId(), 20);

        assertEquals("Stock added successfully", result);
        Product updated = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertEquals(120, updated.getStock());
    }

    @Test
    void addProductStock_ShouldThrow_WhenQuantityLessThanOne() {
        assertThrows(ValidationException.class,
                () -> productService.addProductStock(savedProduct.getId(), 0));
    }

    @Test
    void addProductSales_ShouldDecreaseStock() {
        String result = productService.addProductSales(savedProduct.getId(), 5);

        assertEquals("Stock added successfully", result);
        Product updated = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertEquals(95, updated.getStock());
    }

    @Test
    void addProductSales_ShouldThrow_WhenNotEnoughStock() {
        assertThrows(ValidationException.class,
                () -> productService.addProductSales(savedProduct.getId(), 999));
    }

    @Test
    void updateProduct_ShouldModifyFields() {
        ProductReqDTO updateReq = new ProductReqDTO("Updated Name", "Updated desc", 75.0, 200);

        String result = productService.updateProduct(savedProduct.getId(), updateReq);

        assertEquals("Product updated successfully", result);
        Product updated = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertEquals("Updated Name", updated.getName());
        assertEquals(75.0, updated.getPrice());
        assertEquals(200, updated.getStock());
    }

    @Test
    void deleteProduct_ShouldSoftDelete() {
        String result = productService.deleteProduct(savedProduct.getId());

        assertEquals("Product deleted successfully", result);
        Product deleted = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertTrue(deleted.getIsDel());

        // Should not appear in non-deleted list
        List<Product> nonDeleted = productRepository.findAllByIsDelFalse();
        assertTrue(nonDeleted.isEmpty());
    }
}
