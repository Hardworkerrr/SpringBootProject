package com.example.app.utill;

import com.example.app.exceptions.JsonDataFormatException;
import com.example.app.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JsonDataConvertUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Product> convertJsonToProduct(String json) throws JsonProcessingException {
        List<Product> products = new ArrayList<>();
        JsonNode jsonNode = objectMapper.readTree(json);
        getRecords(jsonNode)
                .iterator()
                .forEachRemaining(node ->{
                    Product product = new Product();
                    product.setEntryDate(LocalDate.parse(node.get("entryDate")
                            .asText(),DateTimeFormatter
                            .ofPattern("dd-MM-yyyy")));
                    product.setItemCode(node.get("itemCode").asInt());
                    product.setItemName(node.get("itemName").asText());
                    product.setItemQuantity(node.get("itemQuantity").asInt());
                    product.setStatus(node.get("status").asText());
                    products.add(product);
                });
        return products;
    }

    public JsonNode getRecords(JsonNode jsonNode){
        return Optional.ofNullable(jsonNode)
                .map(j->j.get("records"))
                .orElseThrow(JsonDataFormatException::new);
    }

    public JSONObject convertProductToJson(String jsonString){
        JSONObject jsonData = new JSONObject();
        JSONArray jsonList = new JSONArray();
        JSONObject jsonObject = new JSONObject(jsonString);
        jsonList.put(jsonObject);
        jsonData.put("records",jsonList);
        return jsonData;
    }
}
