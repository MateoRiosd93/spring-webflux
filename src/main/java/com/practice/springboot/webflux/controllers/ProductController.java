package com.practice.springboot.webflux.controllers;

import com.practice.springboot.webflux.models.documents.Product;
import com.practice.springboot.webflux.models.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RequiredArgsConstructor
@Controller
public class ProductController {
    private final ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @GetMapping({"/list-products", "/"})
    public String listProducts(Model model) {
        Flux<Product> productFlux = productRepository.findAll();

        Flux<Product> productFluxUpperCase = productFlux.map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                });

        productFluxUpperCase.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", productFluxUpperCase);
        model.addAttribute("title", "List of products");

        return "list";
    }

    @GetMapping("/list-products-data-driver")
    public String listProductsDataDriver(Model model) {
        Flux<Product> productFlux = productRepository.findAll();

        Flux<Product> productFluxUpperCase = productFlux.map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                })
                .delayElements(Duration.ofSeconds(1));

        productFluxUpperCase.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", new ReactiveDataDriverContextVariable(productFluxUpperCase, 2));
        model.addAttribute("title", "List of products");

        return "list";
    }
}
