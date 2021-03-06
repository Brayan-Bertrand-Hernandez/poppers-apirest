package com.project.springapirest.repository;

import com.project.springapirest.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String term);
}
