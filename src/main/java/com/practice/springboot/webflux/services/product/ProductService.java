package com.practice.springboot.webflux.services.product;

import com.practice.springboot.webflux.models.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<Product> findAll();
    Flux<Product> findAllNameToUpperCase();
    Flux<Product> findAllNameToUpperCaseRepeat();
    Mono<Product> findById(String id);
    Mono<Product> save(Product product);
    Mono<Void> delete(Product product);
}
