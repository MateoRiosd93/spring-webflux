package com.practice.springboot.webflux.models.repository;

import com.practice.springboot.webflux.models.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {

}
