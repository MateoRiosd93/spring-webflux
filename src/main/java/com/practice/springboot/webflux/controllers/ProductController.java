package com.practice.springboot.webflux.controllers;

import com.practice.springboot.webflux.models.documents.Product;
import com.practice.springboot.webflux.models.repository.ProductRepository;
import com.practice.springboot.webflux.services.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/** @SessionAttributes se utiliza para almacenar atributos del modelo (Model) en la sesión HTTP, de modo que puedan
 * mantenerse disponibles entre múltiples peticiones (requests) dentro de la misma sesión de usuario. */

@SessionAttributes("product")
@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductServiceImpl productService;
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @GetMapping({"/list-products", "/"})
    public Mono<String> listProducts(Model model) {
        Flux<Product> productFluxUpperCase = productService.findAllNameToUpperCase();
        productFluxUpperCase.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", productFluxUpperCase);
        model.addAttribute("title", "List of products");

        return Mono.just("list");
    }

    @GetMapping("/form")
    public Mono<String> createProduct(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("title", "Create products");

        return Mono.just("form");
    }

    @GetMapping("/form/{id}")
    public Mono<String> editProduct(@PathVariable String id, Model model) {
        Mono<Product> productMono = productService.findById(id)
                .doOnNext(product -> log.info("Edit product: {}", product.getName()))
                .defaultIfEmpty(new Product());

        model.addAttribute("title", "Edit product");
        model.addAttribute("product", productMono);

        return Mono.just("form");
    }

    @PostMapping("/form")
    public Mono<String> saveProduct(Product product, SessionStatus sessionStatus){
// sessionStatus.setComplete() se utiliza en Spring MVC para indicar que has terminado de usar los atributos almacenados en la sesión mediante @SessionAttributes
        sessionStatus.setComplete();

        return productService.save(product)
                .doOnNext(productSaved -> log.info("Product save: {} id: {}", productSaved.getName(), productSaved.getId()))
                .thenReturn("redirect:/list-products");
    }

    @GetMapping("/list-products-data-driver")
    public Mono<String> listProductsDataDriver(Model model) {
        Flux<Product> productFluxUpperCase = productService.findAllNameToUpperCase()
                .delayElements(Duration.ofSeconds(1));

        productFluxUpperCase.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", new ReactiveDataDriverContextVariable(productFluxUpperCase, 2));
        model.addAttribute("title", "List of products");

        return Mono.just("list");
    }


    @GetMapping("/list-full-products")
    public Mono<String> listFullProducts(Model model) {
        Flux<Product> productFluxUpperCase = productService.findAllNameToUpperCaseRepeat();

        model.addAttribute("products", productFluxUpperCase);
        model.addAttribute("title", "List of products");

        return Mono.just("list");
    }

    @GetMapping("/list-chunked-products")
    public Mono<String> listChunkedProducts(Model model) {
        Flux<Product> productFluxUpperCase = productService.findAllNameToUpperCaseRepeat();

        model.addAttribute("products", productFluxUpperCase);
        model.addAttribute("title", "List of products");

        return Mono.just("list-chunked");
    }

}
