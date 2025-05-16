package com.practice.springboot.webflux.services.category;

import com.practice.springboot.webflux.models.documents.Category;
import com.practice.springboot.webflux.models.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Flux<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Mono<Category> findByID(String id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Mono<Category> save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Mono<Void> delete(Category category) {
        return categoryRepository.delete(category);
    }
}
