package com.dibimbing.apiassignment.controller;

import com.dibimbing.apiassignment.dto.ProductPatchDTO;
import com.dibimbing.apiassignment.dto.ProductReqDTO;
import com.dibimbing.apiassignment.entity.Product;
import com.dibimbing.apiassignment.entity.Role;
import com.dibimbing.apiassignment.entity.User;
import com.dibimbing.apiassignment.repository.ProductRepository;
import com.dibimbing.apiassignment.repository.UserRepository;
import com.dibimbing.apiassignment.service.JwtService;
import com.dibimbing.apiassignment.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisService redisService;

    private String userToken;
    private String adminToken;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        productRepository.deleteAll();
        userRepository.deleteAll();
        redisService.delete("products");

        // Create a product
        savedProduct = productRepository.save(
                new Product(null, "Test Product", "Test desc", 50.0, 100, false));
        redisService.delete("product::" + savedProduct.getId());

        // Create USER role user and generate token
        User regularUser = new User();
        regularUser.setUsername("testuser");
        regularUser.setPassword(passwordEncoder.encode("password123"));
        regularUser.setEmail("user@test.com");
        regularUser.setRole(Role.USER);
        userRepository.save(regularUser);
        userToken = jwtService.generateToken(regularUser);

        // Create ADMIN role user and generate token
        User adminUser = new User();
        adminUser.setUsername("testadmin");
        adminUser.setPassword(passwordEncoder.encode("password123"));
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(Role.ADMIN);
        userRepository.save(adminUser);
        adminToken = jwtService.generateToken(adminUser);
    }

    // --- Public Endpoints ---

    @Test
    void getProducts_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        mockMvc.perform(get("/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(50.0));
    }

    // --- Authenticated Endpoints (USER role) ---

    @Test
    void postProduct_ShouldCreateProduct_WithUserToken() throws Exception {
        ProductReqDTO request = new ProductReqDTO("New Product", "New desc", 25.0, 10);

        mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("New Product")));
    }

    @Test
    void postProduct_ShouldReturn401_WhenNoAuth() throws Exception {
        ProductReqDTO request = new ProductReqDTO("New Product", "New desc", 25.0, 10);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchProductStock_ShouldIncreaseStock_WithUserToken() throws Exception {
        ProductPatchDTO request = new ProductPatchDTO();
        request.setQuantity(20);

        mockMvc.perform(patch("/products/" + savedProduct.getId() + "/stock")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock added successfully"));
    }

    @Test
    void postProductSales_ShouldDecreaseStock_WithUserToken() throws Exception {
        ProductPatchDTO request = new ProductPatchDTO();
        request.setQuantity(5);

        mockMvc.perform(post("/products/" + savedProduct.getId() + "/sale")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock added successfully"));
    }

    // --- ADMIN-only Endpoints ---

    @Test
    void deleteProduct_ShouldSoftDelete_WithAdminToken() throws Exception {
        mockMvc.perform(delete("/products/" + savedProduct.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));
    }

    @Test
    void deleteProduct_ShouldReturn403_WithUserToken() throws Exception {
        mockMvc.perform(delete("/products/" + savedProduct.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
