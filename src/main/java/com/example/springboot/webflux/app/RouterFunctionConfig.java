package com.example.springboot.webflux.app;

import com.example.springboot.webflux.app.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler handler) {
        return route(GET("/api/v2/products").or(GET("/api/v3/products")), handler::toList)
                .andRoute(GET("api/v2/products/{id}"), handler::details);
    }
}
