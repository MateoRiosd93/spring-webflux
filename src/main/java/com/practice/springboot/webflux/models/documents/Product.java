package com.practice.springboot.webflux.models.documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private Double price;
    private Date createAt;
}
