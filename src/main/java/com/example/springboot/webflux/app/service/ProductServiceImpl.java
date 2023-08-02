package com.example.springboot.webflux.app.service;

import com.example.springboot.webflux.app.models.Category;
import com.example.springboot.webflux.app.models.Product;
import com.example.springboot.webflux.app.repository.CategoryRepository;
import com.example.springboot.webflux.app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Flux<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Flux<Product> findAllToUpper() {
        return productRepository.findAll().map(product -> {
            product.setName(product.getName().toUpperCase());
            return product;
        });
    }

    @Override
    public Mono<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Mono<Product> save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Mono<Void> delete(Product product) {
        return productRepository.delete(product);
    }

    @Override
    public Flux<Category> findAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Mono<Category> findCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Mono<Category> saveCategory(Category category) {
        return categoryRepository.save(category);
    }
}
