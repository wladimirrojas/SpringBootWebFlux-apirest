package com.example.springboot.webflux.app;

import com.example.springboot.webflux.app.models.Category;
import com.example.springboot.webflux.app.models.Product;
import com.example.springboot.webflux.app.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) //.RANDOM_PORT
class SpringBootWebfluxApirestApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductService service;

    @Value("${config.base.endpoint}")
    private String URL;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Test
    public void listingTest() {

        client.get()
                .uri(URL)
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
                .uri(URL + "/{id}", Collections.singletonMap("id", product.getId()))
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

        client.post().uri(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.product.id").isNotEmpty()
                .jsonPath("$.product.name").isEqualTo("Desk")
                .jsonPath("$.product.category.name").isEqualTo("Furniture");
    }

    @Test
    public void saveTest() {

        Category category = service.findCategoryByName("Furniture").block();

        Product product = new Product("Desk", 200d, category);

        client.post().uri(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {})
                .consumeWith(response -> {
                    Object o = response.getResponseBody().get("product");
                    Product p = objectMapper.convertValue(o, Product.class);
                    Assertions.assertThat(p.getId()).isNotEmpty();
                    Assertions.assertThat(p.getName()).isEqualTo("Desk");
                    Assertions.assertThat(p.getCategory().getName()).isEqualTo("Furniture");
                });
    }

    @Test
    public void updateTest() {
        Product product = service.findByName("Kindle 11").block();
        Category category = service.findCategoryByName("Book").block();

        Product updatedProduct = new Product("Kindle v11", 190.99, category);

        client.put().uri(URL + "/{id}", Collections.singletonMap("id", product.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedProduct), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Kindle v11")
                .jsonPath("$.category.name").isEqualTo("Book");
    }

    @Test
    public void deleteTest() {

        Product product = service.findByName("Monitor 4k Ozone").block();

        client.delete()
                .uri(URL + "/{id}", Collections.singletonMap("id", product.getId()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();

        client.get()
                .uri(URL + "/{id}", Collections.singletonMap("id", product.getId()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
    }

}
