package com.example.springboot.webflux.app.repository;


import com.example.springboot.webflux.app.models.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {

    Mono<Category> findByName(String name);
}
