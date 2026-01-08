package com.dibimbing.apiassignment.repository;

import com.dibimbing.apiassignment.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product>  findByIdAndIsDelFalse(Long id);

    List<Product> findAllIsDelFalse();
}
