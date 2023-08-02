package com.example.springboot.webflux.app.repository;


import com.example.springboot.webflux.app.models.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
