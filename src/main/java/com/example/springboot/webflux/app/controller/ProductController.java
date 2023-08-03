package com.example.springboot.webflux.app.controller;

import com.example.springboot.webflux.app.models.Product;
import com.example.springboot.webflux.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @Value("${config.pictures.path}")
    private String path;

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Product>> uploadPicture(@PathVariable String id, @RequestPart FilePart file) {
        return service.findById(id).flatMap(p -> {
            p.setPicture(UUID.randomUUID().toString() + "-" +(file.filename()
                    .replace(" ", "")
                    .replace(":", "")
                    .replace("\\", "")));

            return file.transferTo(new File(path + p.getPicture())).then(service.save(p));
        }).map(p -> ResponseEntity.ok(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/v2")
    public Mono<ResponseEntity<Product>> uploadPictureV2(Product product, @RequestPart FilePart file) {
        if (product.getCreatedAt() == null) product.setCreatedAt(LocalDateTime.now());

        product.setPicture(UUID.randomUUID().toString() + "-" +(file.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("\\", "")));
        return file.transferTo(new File(path + product.getPicture()))
                .then(service.save(product))
                .map(p ->
                ResponseEntity.created(
                                URI.create("/api/products/"
                                        .concat(p.getId())))
                        .body(p));
    }

    /*@GetMapping
    public Flux<Product> listing() {
        return service.findAll();
    }*/

    @GetMapping
    public Mono<ResponseEntity<Flux<Product>>> listingMono() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.findAll())
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> findById(@PathVariable String id) {
        return service.findById(id).map(p -> ResponseEntity.ok().body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @PostMapping
    public Mono<ResponseEntity<Product>> save(@RequestBody Product product) {
        if (product.getCreatedAt() == null) product.setCreatedAt(LocalDateTime.now());

        return service.save(product).map(p ->
                ResponseEntity.created(
                        URI.create("/api/products/"
                                .concat(p.getId())))
                        .body(p));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> edit(@RequestBody Product product, @PathVariable String id) {
        return service.findById(id).flatMap(p -> {
            p.setName(product.getName());
            p.setPrice(product.getPrice());
            p.setPrice(product.getPrice());
            return service.save(p);
        }).map(p -> ResponseEntity.created(URI.create("/api/products/".concat(p.getId())))
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.findById(id).flatMap(p -> service.delete(p)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))  //.defaultIfEmpty(ResponseEntity.notFound().build()); we can use this way as we change Void to Object
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));


    }
}
