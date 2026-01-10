package com.dibimbing.apiassignment.service;

import com.dibimbing.apiassignment.dto.ProductReqDTO;
import com.dibimbing.apiassignment.dto.ProductResDTO;
import com.dibimbing.apiassignment.entity.Product;
import com.dibimbing.apiassignment.exceptions.custom.NotFoundException;
import com.dibimbing.apiassignment.exceptions.custom.ValidationException;
import com.dibimbing.apiassignment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PRODUCTS_KEY = "products";
    private static final String PRODUCT_ID_KEY = "product::";

    public String addProduct(ProductReqDTO request) {
        Product product = new Product();

        BeanUtils.copyProperties(request, product);

        productRepository.save(product);

        try {
            redisTemplate.delete(PRODUCTS_KEY);
            log.info("deleting cache from redis");
        } catch (Exception e) {
            log.error("error in redis ",e);
        }

        return "Product successfully added, " +  product.getName();
    }

    public ProductResDTO getProductById(Long id) {
        try {
            ProductResDTO cacheResponse = (ProductResDTO) redisTemplate.opsForValue().get(PRODUCT_ID_KEY+id);
            if(Objects.nonNull(cacheResponse)) {
                log.info("Cache response from redis");
                return cacheResponse;
            }
        } catch (Exception e) {
            log.error("error in redis ",e);
        }

        ProductResDTO productResDTO = new ProductResDTO();


        productRepository.findByIdAndIsDelFalse(id).ifPresentOrElse(
                product -> {
                    BeanUtils.copyProperties(product, productResDTO);
                    try {
                        redisTemplate.opsForValue().set(PRODUCT_ID_KEY+id, product);
                    } catch (Exception e) {
                        log.error("error in redis ",e);
                    }
                },
                () -> { throw new NotFoundException("Product Not Found");}
        );


        return productResDTO;
    }

    public List<ProductResDTO> getProduct() {
        try {
            Object cacheObject = redisTemplate.opsForValue().get(PRODUCTS_KEY);
            if (cacheObject instanceof List) {
                List<?> cacheList = (List<?>) cacheObject;

                if (!cacheList.isEmpty()) {
                    log.info("Cache response from redis");
                    return (List<ProductResDTO>) cacheList;
                }
            }
        } catch (Exception e) {
            log.error("error in redis ",e);
        }

        List<ProductResDTO> productResponse = new ArrayList<>();

        for (Product product : productRepository.findAllByIsDelFalse()) {
            ProductResDTO productResDTO = new ProductResDTO();
            BeanUtils.copyProperties(product, productResDTO);
            productResponse.add(productResDTO);

            try {
                redisTemplate.opsForValue().set(PRODUCTS_KEY, productResponse);
            } catch (Exception e) {
                log.error("error in redis ",e);
            }
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
                    productRepository.save(product);

                    try {
                        redisTemplate.delete(PRODUCTS_KEY);
                        redisTemplate.delete(PRODUCT_ID_KEY+id);
                        log.info("deleting cache from redis");
                    } catch (Exception e) {
                        log.error("error in redis ",e);
                    }
                    },
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
                    productRepository.save(product);

                    try {
                        redisTemplate.delete(PRODUCTS_KEY);
                        redisTemplate.delete(PRODUCT_ID_KEY+id);
                        log.info("deleting cache from redis");
                    } catch (Exception e) {
                        log.error("error in redis ",e);
                    }},
                () -> { throw new NotFoundException("Product not found"); }
        );

        return "Product updated successfully";
    }

    public String deleteProduct(Long id) {
        productRepository.findByIdAndIsDelFalse(id).ifPresentOrElse(
                product -> {
                    product.setIsDel(Boolean.TRUE);
                    productRepository.save(product);

                    try {
                        redisTemplate.delete(PRODUCTS_KEY);
                        redisTemplate.delete(PRODUCT_ID_KEY + id);
                        log.info("deleting cache from redis");
                    } catch (Exception e) {
                        log.error("error in redis ",e);
                    }},
                () -> { throw new NotFoundException("Product not found"); });

        return "Product deleted successfully";
    }
}
