package com.practice.springboot.webflux.services.category;

import com.practice.springboot.webflux.models.documents.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {
    Flux<Category> findAll();
    Mono<Category> findByID(String id);
    Mono<Category> save(Category category);
    Mono<Void> delete(Category category);
}
