package com.practice.springboot.webflux.models.repository;

import com.practice.springboot.webflux.models.documents.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
