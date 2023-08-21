package com.example.springboot.webflux.app;

import com.example.springboot.webflux.app.models.Category;
import com.example.springboot.webflux.app.models.Product;
import com.example.springboot.webflux.app.service.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebfluxApirestApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductService service;

    @Test
    void contextLoads() {
    }

    @Test
    public void listingTest() {

        client.get()
                .uri("/api/v2/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .consumeWith(response -> {
                    List<Product> products = response.getResponseBody();
                    products.forEach(p -> {
                        System.out.println(p.getName());
                    });
                    Assertions.assertThat(products.size() > 0).isTrue();
                });
                //.hasSize(7);
    }

    @Test
    public void findByIdTest() {

        Product product = service.findByName("TV Samsung").block();

        client.get()
                .uri("/api/v2/products/{id}", Collections.singletonMap("id", product.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .consumeWith(response -> {
                    Product p = response.getResponseBody();
                    Assertions.assertThat(p.getId()).isNotEmpty();
                    Assertions.assertThat(p.getName()).isEqualTo("TV Samsung");
                });
                /*.expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("TV Samsung");*/
    }

    @Test
    public void createTest() {

        Category category = service.findCategoryByName("Furniture").block();

        Product product = new Product("Desk", 200d, category);

        client.post().uri("/api/v2/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Desk")
                .jsonPath("$.category.name").isEqualTo("Furniture");
    }

    @Test
    public void saveTest() {

        Category category = service.findCategoryByName("Furniture").block();

        Product product = new Product("Desk", 200d, category);

        client.post().uri("/api/v2/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(response -> {
                    Product p = response.getResponseBody();
                    Assertions.assertThat(p.getId()).isNotEmpty();
                    Assertions.assertThat(p.getName()).isEqualTo("Desk");
                    Assertions.assertThat(p.getCategory().getName()).isEqualTo("Furniture");
                });
    }

}
