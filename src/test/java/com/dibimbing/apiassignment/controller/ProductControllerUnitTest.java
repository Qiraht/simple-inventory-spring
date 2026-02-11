package com.dibimbing.apiassignment.controller;

import com.dibimbing.apiassignment.dto.ProductPatchDTO;
import com.dibimbing.apiassignment.dto.ProductReqDTO;
import com.dibimbing.apiassignment.dto.ProductResDTO;
import com.dibimbing.apiassignment.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;

    private ProductReqDTO productReq;
    private ProductResDTO productRes;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();

        productReq = new ProductReqDTO();
        productReq.setName("Test Product");
        productReq.setPrice(100.0);
        productReq.setStock(10);

        productRes = new ProductResDTO();
        productRes.setId(1L);
        productRes.setName("Test Product");
    }

    @Test
    void postProduct_ShouldReturnOk() throws Exception {
        when(productService.addProduct(any(ProductReqDTO.class))).thenReturn("Success");

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productReq)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productRes);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getProducts_ShouldReturnList() throws Exception {
        when(productService.getProduct()).thenReturn(List.of(productRes));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void addProductStock_ShouldReturnOk() throws Exception {
        ProductPatchDTO patch = new ProductPatchDTO();
        patch.setQuantity(5);
        when(productService.addProductStock(eq(1L), eq(5))).thenReturn("Stock Added");

        mockMvc.perform(patch("/products/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock Added"));
    }

    @Test
    void updateProduct_ShouldReturnOk() throws Exception {
        when(productService.updateProduct(eq(1L), any(ProductReqDTO.class))).thenReturn("Updated");

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productReq)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated"));
    }

    @Test
    void deleteProduct_ShouldReturnOk() throws Exception {
        when(productService.deleteProduct(1L)).thenReturn("Deleted");

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted"));
    }

    @Test
    void postProductSales_ShouldReturnOk() throws Exception {
        ProductPatchDTO patch = new ProductPatchDTO();
        patch.setQuantity(5);
        when(productService.addProductSales(eq(1L), anyInt())).thenReturn("Sold");

        mockMvc.perform(post("/products/1/sale")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk());
    }
}
