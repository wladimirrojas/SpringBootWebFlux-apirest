package com.example.springboot.webflux.app.service;

import com.example.springboot.webflux.app.models.Category;
import com.example.springboot.webflux.app.models.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<Product> findAll();

    Flux<Product> findAllToUpper();

    Mono<Product> findById(String id);

    Mono<Product> save(Product product);

    Mono<Void> delete(Product product);

    Flux<Category> findAllCategory();

    Mono<Category> findCategoryById(String id);

    Mono<Category> saveCategory(Category category);

    Mono<Product> findByName(String name);

    Mono<Category> findCategoryByName(String name);
}
