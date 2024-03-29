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

    //mensaje pal mau
    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler handler) {
        return route(GET("/api/v2/products").or(GET("/api/v3/products")), handler::toList)
                .andRoute(GET("/api/v2/products/{id}"), handler::details)
                .andRoute(POST("api/v2/products"), handler::save)
                .andRoute(PUT("/api/v2/products/{id}"), handler::update)
                .andRoute(DELETE("/api/v2/products/{id}"), handler::delete)
                .andRoute(POST("api/v2/products/upload/{id}"), handler::upload)
                .andRoute(POST("api/v2/products/save"), handler::saveWithPicture);
    }
}
