package com.example.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JsonToEntityService {
    void convertDataAndSave(String json) throws JsonProcessingException;
}
