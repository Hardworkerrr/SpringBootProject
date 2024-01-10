package com.example.app.controller;


import com.example.app.model.Product;
import com.example.app.service.impl.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody String payload) throws JsonProcessingException {
        productService.convertDataAndSave(payload);
        return new ResponseEntity<>("New product successfully added!",HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<Product> getProducts() {
        return productService.getAll();
    }
}
