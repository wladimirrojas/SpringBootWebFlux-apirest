package com.example.springboot.webflux.app.handler;

import com.example.springboot.webflux.app.models.Product;
import com.example.springboot.webflux.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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
}
