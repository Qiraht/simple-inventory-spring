package com.dibimbing.apiassignment.service;

import com.dibimbing.apiassignment.dto.ProductReqDTO;
import com.dibimbing.apiassignment.dto.ProductResDTO;
import com.dibimbing.apiassignment.entity.Product;
import com.dibimbing.apiassignment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void addProduct(ProductReqDTO request) {
        Product product = new Product();

        BeanUtils.copyProperties(request, product);

        productRepository.save(product);
    }

    public  ProductResDTO getProductById(Long id) {
        Product product = productRepository.findById(id).get();

        ProductResDTO productResDTO = new ProductResDTO();
        BeanUtils.copyProperties(product, productResDTO);

        return productResDTO;
    }

    public List<ProductResDTO> getProduct() {
        List<ProductResDTO> productResponse = new ArrayList<>();

        for (Product product : productRepository.findAllIsDelFalse()) {
            ProductResDTO productResDTO = new ProductResDTO();
            BeanUtils.copyProperties(product, productResDTO);
            productResponse.add(productResDTO);
        }

        return productResponse;
    }

    public void addProductStock(Long id, Integer quantity) {
        Optional<Product> tempProduct = productRepository.findById(id);

        // check quantity added
        if (quantity < 1) {
            System.out.println("Quantity is less than 1");
        };

        tempProduct.ifPresent(product -> {
            product.setStock(product.getStock() + quantity);
            productRepository.save(product);
        });
    }

    public void updateProduct(Long id, ProductReqDTO request) {
        productRepository.findById(id).ifPresent(product -> {
            product.setName(request.getName());
            product.setPrice(request.getPrice());
            product.setStock(request.getStock());
            if (StringUtils.hasText(request.getDescription())) {
                product.setDescription(request.getDescription());
            }
            productRepository.save(product);
        });
    }

    public void deleteProduct(Long id) {
        productRepository.findByIdAndIsDelFalse(id).ifPresent(product -> {
            product.setIsDel(Boolean.TRUE);
            productRepository.save(product);
        });
    }
}
