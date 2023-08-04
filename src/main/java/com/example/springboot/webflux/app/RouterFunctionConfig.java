package com.example.springboot.webflux.app;

import com.example.springboot.webflux.app.models.Product;
import com.example.springboot.webflux.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class RouterFunctionConfig {

    @Autowired
    private ProductService service;

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route(GET("/api/v2/products"), request -> {
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(service.findAll(), Product.class);
        });
    }
}
