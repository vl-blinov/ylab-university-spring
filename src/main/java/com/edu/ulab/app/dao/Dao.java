package com.edu.ulab.app.dao;

import java.util.Optional;

public interface Dao<T, ID extends Number> {

    T add(T entity);

    T update(T entity);

    Optional<T> findById(ID id);

    void deleteById(ID id);
}
