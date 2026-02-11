package com.dibimbing.apiassignment.service;

import com.dibimbing.apiassignment.dto.ProductReqDTO;
import com.dibimbing.apiassignment.dto.ProductResDTO;
import com.dibimbing.apiassignment.entity.Product;
import com.dibimbing.apiassignment.exceptions.custom.NotFoundException;
import com.dibimbing.apiassignment.exceptions.custom.ValidationException;
import com.dibimbing.apiassignment.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private ProductService productService;

    private Product productSingle;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        // Single Product
        productSingle = new Product();
        productSingle.setId(1L);
        productSingle.setName("Test Product");
        productSingle.setPrice(100.0);
        productSingle.setStock(10);
        productSingle.setIsDel(false);

        // List Produts
        productList = new ArrayList<>();
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setPrice(50.0);
        product2.setStock(20);
        product2.setIsDel(false);

        productList = List.of(productSingle, product2);

    }

    @Test
    void addProduct_ShouldSaveToDbAndClearCache() {
        ProductReqDTO request = new ProductReqDTO();
        request.setName("New Product");
        request.setPrice(150.0);
        request.setStock(30);

        String result = productService.addProduct(request);

        assertEquals("Product successfully added, New Product", result);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(redisService, times(1)).delete("products");
    }

    @Test
    void getProductById_ShouldReturnFromCache_WhenExist() {
        ProductResDTO expected = new ProductResDTO();
        expected.setName("Cached Product");

        when(redisService.get(anyString())).thenReturn(expected);

        ProductResDTO result = productService.getProductById(1L);

        assertEquals(expected.getName(), result.getName());
        verify(productRepository, never()).findByIdAndIsDelFalse(anyLong());
        verify(redisService, times(1)).get(anyString());
    }

    @Test
    void getProductById_ShouldReturnFromDb_WhenNotInCache() {
        when(redisService.get(anyString())).thenReturn(null);
        when(productRepository.findByIdAndIsDelFalse(1L)).thenReturn(Optional.of(productSingle));

        ProductResDTO result = productService.getProductById(1L);

        assertEquals(productSingle.getName(), result.getName());
        verify(productRepository, times(1)).findByIdAndIsDelFalse(1L);
        verify(redisService, times(1)).set(anyString(), any());
    }

    @Test
    void getProductById_ShouldThrowException_WhenNotFound() {
        when(redisService.get(anyString())).thenReturn(null);
        when(productRepository.findByIdAndIsDelFalse(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getProducts_ShouldReturnFromCache_WhenExist() {
        ProductResDTO dto = new ProductResDTO();
        dto.setName("Test Product");
        List<ProductResDTO> productListDto = List.of(dto);

        when(redisService.get("products")).thenReturn(productListDto);

        List<ProductResDTO> result = productService.getProduct();

        assertEquals(productListDto.get(0).getName(), result.get(0).getName());
        assertEquals(1, result.size());
        verify(productRepository, never()).findAllByIsDelFalse();
        verify(redisService, times(1)).get("products");
    }

    @Test
    void getProducts_ShouldReturnFromDb_WhenNotInCache() {
        when(redisService.get("products")).thenReturn(null);
        when(productRepository.findAllByIsDelFalse()).thenReturn(productList);

        List<ProductResDTO> result = productService.getProduct();

        assertEquals(productList.get(0).getName(), result.get(0).getName());
        verify(productRepository, times(1)).findAllByIsDelFalse();
        verify(redisService, times(1)).set(eq("products"), any());
    }

    @Test
    void addProductStock_ShouldWorkAndClearCache() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productSingle));

        String result = productService.addProductStock(1L, 10);

        assertEquals("Stock added successfully", result);
        assertEquals(20, productSingle.getStock());
        verify(productRepository, times(1)).save(productSingle);
        verify(redisService, times(1)).delete("product::1");
    }

    @Test
    void addProductStock_ShouldThrowException_WhenInvalidQuantity() {
        assertThrows(ValidationException.class, () -> productService.addProductStock(1L, 0));
    }

    @Test
    void addProductSales_ShouldWork() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productSingle));

        String result = productService.addProductSales(1L, 5);

        assertEquals("Stock added successfully", result);
        assertEquals(5, productSingle.getStock());
        verify(productRepository, times(1)).save(productSingle);
    }

    @Test
    void updateProduct_ShouldWorkAndClearCache() {
        ProductReqDTO request = new ProductReqDTO();
        request.setName("Updated Name");
        request.setPrice(120.0);
        request.setStock(15);
        request.setDescription("New Desc");

        when(productRepository.findById(1L)).thenReturn(Optional.of(productSingle));

        String result = productService.updateProduct(1L, request);

        assertEquals("Product updated successfully", result);
        assertEquals("Updated Name", productSingle.getName());
        verify(productRepository, times(1)).save(productSingle);
        verify(redisService, times(1)).delete("product::1");
    }

    @Test
    void deleteProduct_ShouldWorkAndClearCache() {
        when(productRepository.findByIdAndIsDelFalse(1L)).thenReturn(Optional.of(productSingle));

        String result = productService.deleteProduct(1L);

        assertEquals("Product deleted successfully", result);
        assertTrue(productSingle.getIsDel());
        verify(productRepository, times(1)).save(productSingle);
        verify(redisService, times(1)).deleteMultiple("product::1", "products");
    }
}
