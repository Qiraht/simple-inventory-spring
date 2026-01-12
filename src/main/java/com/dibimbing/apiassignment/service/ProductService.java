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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final RedisService redisService;

    // Used for get all products
    private static final String PRODUCTS_KEY = "products";
    // Used for get product by Id
    private static final String PRODUCT_ID_KEY = "product::";

    public String addProduct(ProductReqDTO request) {
        Product product = new Product();

        BeanUtils.copyProperties(request, product);

        productRepository.save(product);
        log.info("Product {} has been added", product);

        redisService.delete(PRODUCTS_KEY);

        return "Product successfully added, " +  product.getName();
    }

    public ProductResDTO getProductById(Long id) {
        Object cacheResponse = redisService.get(PRODUCT_ID_KEY + id);
        if (cacheResponse instanceof ProductResDTO) {
            log.info("Cache response for product {} ", id);
            return (ProductResDTO) cacheResponse;
        }

        ProductResDTO productResDTO = new ProductResDTO();

        productRepository.findByIdAndIsDelFalse(id).ifPresentOrElse(
                product -> {
                    BeanUtils.copyProperties(product, productResDTO);
                    redisService.set(PRODUCT_ID_KEY + id, productResDTO);
                },
                () -> { throw new NotFoundException("Product Not Found");}
        );

        return productResDTO;
    }

    public List<ProductResDTO> getProduct() {
        Object cacheResponse = redisService.get(PRODUCTS_KEY);
        if (cacheResponse instanceof List) {
            List<?> cacheList = (List<?>) cacheResponse;
            if (!cacheList.isEmpty()) {
                log.info("Cache response for product {} ", PRODUCTS_KEY);
                return (List<ProductResDTO>) cacheList;
            }
        }

        List<ProductResDTO> productResponse = new ArrayList<>();

        for (Product product : productRepository.findAllByIsDelFalse()) {
            ProductResDTO productResDTO = new ProductResDTO();
            BeanUtils.copyProperties(product, productResDTO);
            productResponse.add(productResDTO);
        }

        redisService.set(PRODUCTS_KEY, productResponse);
        return productResponse;
    }

    public String addProductStock(Long id, Integer quantity) {
        Optional<Product> tempProduct = productRepository.findById(id);

        // check quantity added
        if (quantity < 1) {
            throw new ValidationException("Quantity Not Enough");
        }

        tempProduct.ifPresentOrElse(
                product -> {
                    product.setStock(product.getStock() + quantity);
                    productRepository.save(product);
                    },
                () -> { throw new NotFoundException("Product not found"); }
        );

        log.info("Product {} stock has been added", tempProduct.get().getName());

        redisService.delete(PRODUCT_ID_KEY + id);
        return "Stock added successfully";
    }

    public String addProductSales(Long id, Integer quantity) {
        Optional<Product> tempProduct = productRepository.findById(id);

        // check quantity added
        if (quantity < 1) {
            throw new ValidationException("Quantity Not Enough");
        }

        tempProduct.ifPresentOrElse(
                product -> {
                    if(product.getStock() < quantity) {
                        throw new ValidationException("Product Stocks Not Enough");
                    }
                    product.setStock(product.getStock() - quantity);
                    productRepository.save(product);
                },
                () -> { throw new NotFoundException("Product not found"); }
        );

        log.info("Product {} sold for {}", tempProduct.get().getName(), quantity);

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
                    productRepository.save(product);},
                () -> { throw new NotFoundException("Product not found"); }
        );

        log.info("Product {} has been updated", id);

        redisService.delete(PRODUCT_ID_KEY + id);
        return "Product updated successfully";
    }

    public String deleteProduct(Long id) {
        productRepository.findByIdAndIsDelFalse(id).ifPresentOrElse(
                product -> {
                    product.setIsDel(Boolean.TRUE);
                    productRepository.save(product);},
                () -> { throw new NotFoundException("Product not found"); }
        );

        log.info("Product {} has been deleted", id);

        redisService.deleteMultiple(PRODUCT_ID_KEY + id, PRODUCTS_KEY);

        return "Product deleted successfully";
    }
}
