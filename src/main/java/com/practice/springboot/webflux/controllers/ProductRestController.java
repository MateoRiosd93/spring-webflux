package com.practice.springboot.webflux.controllers;

import com.practice.springboot.webflux.models.documents.Product;
import com.practice.springboot.webflux.models.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @GetMapping()
    public Flux<Product> getProducts() {
        return productRepository.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                }).doOnNext(product -> log.info(product.getName()));
    }

    @GetMapping("/{id}")
    public Mono<Product> getProduct(@PathVariable String id) {
//        NOTA: Retornando directamente el Product con el findById
//        return productRepository.findById(id);

//        Haciendolo con Flux para practicar un poco mas los metodos.
        Flux<Product> productFlux = productRepository.findAll();

        return productFlux.filter(product -> product.getId().equalsIgnoreCase(id))
                .next()
                .doOnNext(product -> log.info(product.getName()));
    }

}
