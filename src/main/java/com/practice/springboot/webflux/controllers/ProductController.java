package com.practice.springboot.webflux.controllers;

import com.practice.springboot.webflux.models.documents.Category;
import com.practice.springboot.webflux.models.documents.Product;
import com.practice.springboot.webflux.services.category.CategoryService;
import com.practice.springboot.webflux.services.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

// @SessionAttributes se utiliza para almacenar atributos del modelo (Model) en la sesión HTTP, de modo que puedan
// mantenerse disponibles entre múltiples peticiones (requests) dentro de la misma sesión de usuario.

@SessionAttributes("product")
@RequiredArgsConstructor
@Controller
public class ProductController {

    // NOTA: En caso de ser dos modelos que tienen alguna relacion se puede tener una interfaz que tenga los metodos a implementar
    // Caso contrario se puede tener diferentes interfaces e implementaciones de la misma (Aca desacople por orden, aunque tienen relacion 😂)
    private final ProductService productService;
    private final CategoryService categoryService;
        @Value("${config.uploads.path}")
    private String path;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    // Pasara el flux de categorias a la vista.
    @ModelAttribute("categories")
    public Flux<Category> getCategories(){
        return categoryService.findAll();
    }

    // Expresion regular :.+ para poder recibir el .png, .jpg, etc ...de la image
    @SneakyThrows
    @GetMapping("/upload/image/{image:.+}")
    public Mono<ResponseEntity<Resource>> getProductImage(@PathVariable String image){
        Path pathImage = Paths.get(path).resolve(image).toAbsolutePath();

        Resource imageResponse = new UrlResource(pathImage.toUri());

        return Mono.just(
                ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageResponse.getFilename() + "\"")
                        .body(imageResponse)
        );
    }

    @GetMapping("/product/detail/{id}")
    public Mono<String> detailProduct(@PathVariable String id, Model model){
        return productService.findById(id)
                .doOnNext(product -> {
                    model.addAttribute("product", product);
                    model.addAttribute("title", "Product details");
                })
                .switchIfEmpty(Mono.just(new Product()))
                .flatMap(product -> {
                    if (product.getId() == null) {
                        return Mono.error(new InterruptedException("Product no exist."));
                    }
                    return Mono.just(product);
                })
                .then(Mono.just("product-detail"))
                .onErrorResume(error -> Mono.just("redirect:/list-products?error=product+not+found"));
    }

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
        model.addAttribute("textButton", "Create");
        model.addAttribute("title", "Create products");

        return Mono.just("form");
    }

    @GetMapping("/form/{id}")
    public Mono<String> editProduct(@PathVariable String id, Model model) {
        Mono<Product> productMono = productService.findById(id)
                .doOnNext(product -> log.info("Edit product: {}", product.getName()))
                .defaultIfEmpty(new Product());

        model.addAttribute("title", "Edit product");
        model.addAttribute("textButton", "Edit");
        model.addAttribute("product", productMono);

        return Mono.just("form");
    }

    @GetMapping("/form/v2/{id}")
    public Mono<String> editProductV2(@PathVariable String id, Model model) {
        return productService.findById(id)
                .doOnNext(product -> {
                    log.info("Edit product: {}", product.getName());
                    // En este caso el SessionAttributes no guardaria los atributos ya que se esta agregando el atributo dentro de otro contexto.
                    model.addAttribute("title", "Edit product");
                    model.addAttribute("textButton", "Edit");
                    model.addAttribute("product", product);
                })
                .defaultIfEmpty(new Product())
                .flatMap(product -> {
                    if (product.getId() == null) {
                        return Mono.error(new InterruptedException("Product no exist."));
                    }
                    return Mono.just(product);
                })
                .then(Mono.just("form"))
                .onErrorResume(error -> Mono.just("redirect:/list-products?error=product+not+found"));
    }

   // Al Usar el @NotEmpty en el modelo, debemos hacer uso de la anotacion @Valid para el Product y ademas de eso usar BindingResult para validar si hay errores
   // IMPORTANTE: El BindingResult debe ir al lado del Modelo que se le estan agregando las validaciones. es importante el orden de los parametros.
    @PostMapping("/form")
    public Mono<String> saveProduct(@Valid Product product, BindingResult bindingResult, Model model,@RequestPart FilePart file, SessionStatus sessionStatus){

        if (bindingResult.hasErrors()){
            model.addAttribute("title", "Errors when creating the product");
            model.addAttribute("textButton", "Save");

            return Mono.just("Form");
        }

        // sessionStatus.setComplete() se utiliza en Spring MVC para indicar que has terminado de usar los atributos almacenados en la sesión mediante @SessionAttributes
        sessionStatus.setComplete();

        Mono<Category> categoryMono = categoryService.findByID(product.getCategory().getId());

        return categoryMono.flatMap(category -> {
                    if (product.getCreateAt() == null){
                        product.setCreateAt(new Date());
                    }

                    if (!file.filename().isEmpty()) {
                        product.setImage(
                                UUID.randomUUID().toString() + file.filename()
                                        .replace(" ", "")
                                        .replace(":", "")
                                        .replace("\\", "")
                        );
                    }

                    product.setCategory(category);
                    return productService.save(product);
                })
                .doOnNext(productSaved -> {
                    log.info("Product save: {} id: {}", productSaved.getName(), productSaved.getId());
                    log.info("Category assigned: {} id: {}", productSaved.getCategory().getName(), productSaved.getCategory().getId());
                })
                .flatMap(product1 -> {
                    if (!file.filename().isEmpty()){
                        return file.transferTo(new File(path + product1.getImage()));
                    }

                    return Mono.empty();
                })
                .thenReturn("redirect:/list-products?success=product+created+succes");
    }

    @GetMapping("delete/{id}")
    public Mono<String> deleteProduct(@PathVariable String id) {
        return productService.findById(id)
                .defaultIfEmpty(new Product())
                .flatMap(product -> {
                    if (product.getId() == null) {
                        return Mono.error(new InterruptedException("The product to be eliminated does not exist."));
                    }
                    return Mono.just(product);
                })
                .flatMap(productService::delete)
                .then(Mono.just("redirect:/list-products?success=product+successfully+removed"))
                .onErrorResume(error -> Mono.just("redirect:/list-products?error=The+product+to+be+deleted+cannot+be+found"));
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
