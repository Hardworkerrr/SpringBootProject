package com.example.app;

import com.example.app.exceptions.JsonDataFormatException;
import com.example.app.model.Product;
import com.example.app.repository.ProductRepository;
import com.example.app.service.impl.ProductService;
import com.example.app.service.impl.UserDetailService;
import com.example.app.utill.JsonDataConvertUtil;
import com.example.app.utill.JwtTokenUtil;
import jakarta.persistence.EntityNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductApiTest extends ApplicationTests {

    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private JsonDataConvertUtil jsonDataConvertUtil;
    @Value("${jwt.expiration}")
    private Duration jwtLifeTime;

    @Test
    void postProductAndGetSameProduct() throws Exception {
        Product product = new Product();
        product.setItemName("Products 10");
        product.setItemCode(623212);
        product.setItemQuantity(51);
        product.setStatus("Paid");
        JSONObject jsonData = jsonDataConvertUtil.convertProductToJson(objectMapper.writeValueAsString(product));
        jsonData.getJSONArray("records").getJSONObject(0).put("entryDate", "01-01-2024");
        String token = jwtTokenUtil.generateToken(userDetailService.loadUserByUsername("testUser"));
        mvc.perform(post("/products/add")
                        .contentType("application/json")
                        .content(jsonData.toString())
                        .header("Authorization", String.format("Bearer %s", token)))
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result
                        .getResponse()
                        .getContentAsString())
                        .isEqualTo("New product successfully added!"));
        Product productEntity = productRepository
                .findProductByItemName(product.getItemName())
                .stream()
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);
        assertThat(productEntity.getItemCode()).isEqualTo(product.getItemCode());
    }

    @Test
    void failPostProductWithBadFormat() throws Exception {
        Product product = new Product();
        product.setItemName("Products 10");
        product.setItemCode(623212);
        product.setItemQuantity(51);
        product.setStatus("Paid");
        String token = jwtTokenUtil.generateToken(userDetailService.loadUserByUsername("testUser"));
        mvc.perform(post("/products/add")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(product))
                        .header("Authorization", String.format("Bearer %s", token)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> Assertions.assertInstanceOf(JsonDataFormatException.class, result.getResolvedException()));
    }

    @Test
    void failPostProductInvalidJwt() throws Exception {
        Product product = new Product();
        product.setItemName("Products 10");
        product.setItemCode(623212);
        product.setItemQuantity(51);
        product.setStatus("Paid");
        JSONObject jsonData = jsonDataConvertUtil.convertProductToJson(objectMapper.writeValueAsString(product));
        jsonData.getJSONArray("records").getJSONObject(0).put("entryDate", "01-01-2024");
        String token = jwtTokenUtil.generateToken(userDetailService.loadUserByUsername("testUser"));
        token+="test";
        mvc.perform(post("/products/add")
                        .contentType("application/json")
                        .content(jsonData.toString())
                        .header("Authorization", String.format("Bearer %s", token)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualTo("Invalid JWT token!"));
    }

    @Test
    void getAllProducts() throws Exception {
        String token = jwtTokenUtil.generateToken(userDetailService.loadUserByUsername("testUser"));
        MvcResult mvcResult = mvc.perform(get("/products/all")
                        .contentType("application/json")
                        .header("Authorization", String.format("Bearer %s", token)))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isNotNull();
        JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
        Assertions.assertNotNull(jsonArray);
        assertThat(jsonArray).isInstanceOf(JSONArray.class);
    }

    @Test
    void failGetAllProductsExpiredJwt() throws Exception {
        jwtTokenUtil.setJwtLifeTime(Duration.ofMillis(50));
        String token = jwtTokenUtil.generateToken(userDetailService.loadUserByUsername("testUser"));
        jwtTokenUtil.setJwtLifeTime(jwtLifeTime);
        mvc.perform(get("/products/all")
                        .contentType("application/json")
                        .header("Authorization", String.format("Bearer %s", token)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualTo("Expired JWT token, generate new one!"));
    }

    @Test
    void failGetAllProductsInvalidJwt() throws Exception {
        String token = jwtTokenUtil.generateToken(userDetailService.loadUserByUsername("testUser"));
        token+="test";
        mvc.perform(get("/products/all")
                        .contentType("application/json")
                        .header("Authorization", String.format("Bearer %s", token)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualTo("Invalid JWT token!"));
    }
}
