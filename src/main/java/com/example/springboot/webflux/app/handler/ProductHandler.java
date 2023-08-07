package com.example.springboot.webflux.app.handler;

import com.example.springboot.webflux.app.models.Product;
import com.example.springboot.webflux.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.springframework.web.reactive.function.BodyInserters.*;


@Component
public class ProductHandler {

    @Autowired
    private ProductService service;

    public Mono<ServerResponse> toList(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Product.class);
    }

    public Mono<ServerResponse> details(ServerRequest request) {

        String id = request.pathVariable("id");


       return service.findById(id).flatMap(p ->
               ServerResponse
                       .ok()
                       .contentType(MediaType.APPLICATION_JSON)
                       .body(fromValue(p)))
               .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<Product> product = request.bodyToMono(Product.class);

        return product.flatMap(p -> {
            if (p.getCreatedAt() == null) p.setCreatedAt(LocalDateTime.now());
            return service.save(p);
        }).flatMap(p -> ServerResponse.created(URI.create("/api/v2/products/" + p.getId()))
                .body(fromValue(p)));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Mono<Product> product = request.bodyToMono(Product.class);
        String id = request.pathVariable("id");

        Mono<Product> productFromDb = service.findById(id);

        return productFromDb.zipWith(product, (db, req) -> {
            db.setName(req.getName());
            db.setPrice(req.getPrice());
            db.setCategory(req.getCategory());
            return db;
        }).flatMap(p -> ServerResponse.created(URI.create("/api/v2/products/" + p.getId()))
                .body(service.save(p), Product.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> productFromDb = service.findById(id);

        return productFromDb.flatMap(product -> service.delete(product).then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());


    }

}
