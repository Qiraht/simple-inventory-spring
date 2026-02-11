package com.dibimbing.apiassignment.repository;

import com.dibimbing.apiassignment.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    private Product activeProduct;
    private Product deletedProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        activeProduct = new Product(null, "Active Product", "Active desc", 100.0, 50, false);
        deletedProduct = new Product(null, "Deleted Product", "Deleted desc", 200.0, 10, true);

        activeProduct = productRepository.save(activeProduct);
        deletedProduct = productRepository.save(deletedProduct);
    }

    @Test
    void findByIdAndIsDelFalse_ShouldReturnProduct() {
        Optional<Product> result = productRepository.findByIdAndIsDelFalse(activeProduct.getId());

        assertTrue(result.isPresent());
        assertEquals("Active Product", result.get().getName());
    }

    @Test
    void findByIdAndIsDelFalse_ShouldReturnEmpty_WhenDeleted() {
        Optional<Product> result = productRepository.findByIdAndIsDelFalse(deletedProduct.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByIsDelFalse_ShouldReturnOnlyNonDeleted() {
        List<Product> result = productRepository.findAllByIsDelFalse();

        assertEquals(1, result.size());
        assertEquals("Active Product", result.get(0).getName());
    }
}
