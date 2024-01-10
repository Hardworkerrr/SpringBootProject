package com.example.app.service.impl;

import com.example.app.model.Product;
import com.example.app.repository.ProductRepository;
import com.example.app.service.DefaultEntityService;
import com.example.app.service.JsonToEntityService;
import com.example.app.utill.JsonDataConvertUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements DefaultEntityService<Product>, JsonToEntityService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    JsonDataConvertUtil jsonDataConvertUtil;

    @Override
    public void add(Product product) {
        productRepository.save(product);
    }

    @Override
    public void addAll(List<Product> products) {
        productRepository.saveAll(products);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public void convertDataAndSave(String json) throws JsonProcessingException {
        addAll(jsonDataConvertUtil.convertJsonToProduct(json));
    }
}
