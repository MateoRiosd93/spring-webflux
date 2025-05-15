package com.practice.springboot.webflux;

import com.practice.springboot.webflux.models.documents.Product;
import com.practice.springboot.webflux.models.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@RequiredArgsConstructor
@SpringBootApplication
public class WebfluxApplication implements CommandLineRunner {

	private final ProductRepository productRepository;
	private final ReactiveMongoTemplate reactiveMongoTemplate;

	private static final Logger log = LoggerFactory.getLogger(WebfluxApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(WebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		reactiveMongoTemplate.dropCollection("products").subscribe();

		Flux.just(
						new Product("Play station 5", 2590000.0),
						new Product("Monitor LG", 890000.0),
						new Product("Guayos F50", 800000.0),
						new Product("Teclado Logitech", 600000.0),
						new Product("Mouse Logitech", 190000.0),
						new Product("Silla Ergonomica", 1500000.0),
						new Product("Notebook", 1800000.0)
				)
				.flatMap(product -> {
					product.setCreateAt(new Date());
					return productRepository.save(product);
				})
				.subscribe(product -> log.info("Insert: {} {}", product.getId(), product.getName()));
	}
}
