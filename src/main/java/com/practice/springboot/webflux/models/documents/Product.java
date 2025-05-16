package com.practice.springboot.webflux.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private Double price;
//  Agregamos esta anotacion ya que desde el front se manda con ese formato, y sin esto genera un error al hacer el mapeo a tipo Date.
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createAt;

    public Product(String name, Double price){
        this.name = name;
        this.price = price;
    }
}
