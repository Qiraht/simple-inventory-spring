package com.dibimbing.apiassignment.service;

import com.dibimbing.apiassignment.dto.ProductReqDTO;
import com.dibimbing.apiassignment.dto.ProductResDTO;
import com.dibimbing.apiassignment.entity.Product;
import com.dibimbing.apiassignment.exceptions.custom.NotFoundException;
import com.dibimbing.apiassignment.exceptions.custom.ValidationException;
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

    public String addProduct(ProductReqDTO request) {
        Product product = new Product();

        BeanUtils.copyProperties(request, product);

        productRepository.save(product);

        return "Product successfully added, " +  product.getName();
    }

    public ProductResDTO getProductById(Long id) {
        ProductResDTO productResDTO = new ProductResDTO();

        productRepository.findById(id).ifPresentOrElse(
                product -> {
                    BeanUtils.copyProperties(product, productResDTO);
                },
                () -> { throw new NotFoundException("Product Not Found");}
        );


        return productResDTO;
    }

    public List<ProductResDTO> getProduct() {
        List<ProductResDTO> productResponse = new ArrayList<>();

        for (Product product : productRepository.findAllByIsDelFalse()) {
            ProductResDTO productResDTO = new ProductResDTO();
            BeanUtils.copyProperties(product, productResDTO);
            productResponse.add(productResDTO);
        }

        return productResponse;
    }

    public String addProductStock(Long id, Integer quantity) {
        Optional<Product> tempProduct = productRepository.findById(id);

        // check quantity added
        if (quantity < 1) {
            throw new ValidationException("Quantity Not Enough");
        };

        tempProduct.ifPresentOrElse(
                product -> {
                    product.setStock(product.getStock() + quantity);
                    productRepository.save(product); },
                () -> { throw new NotFoundException("Product not found"); }
        );

        return "Stock added successfully";
    }

    public String updateProduct(Long id, ProductReqDTO request) {
        productRepository.findById(id).ifPresentOrElse(
                product -> {
                    product.setName(request.getName());
                    product.setPrice(request.getPrice());
                    product.setStock(request.getStock());
                    if (StringUtils.hasText(request.getDescription()))
                    { product.setDescription(request.getDescription()); }
                    productRepository.save(product); },
                () -> { throw new NotFoundException("Product not found"); }
        );

        return "Product updated successfully";
    }

    public String deleteProduct(Long id) {
        productRepository.findByIdAndIsDelFalse(id).ifPresentOrElse(
                product -> {
                    product.setIsDel(Boolean.TRUE);
                    productRepository.save(product); },
                () -> { throw new NotFoundException("Product not found"); });

        return "Product deleted successfully";
    }
}
