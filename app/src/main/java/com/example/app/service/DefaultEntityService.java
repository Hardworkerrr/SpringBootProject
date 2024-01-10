package com.example.app.service;

import java.util.List;

public interface DefaultEntityService<E> {
    void add(E e);
    void addAll(List<E> e);
    List<E> getAll();

}
