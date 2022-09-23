package com.edu.ulab.app.dao.impl;

import com.edu.ulab.app.dao.Dao;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.storage.Storage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class BookDao implements Dao<Book, Long> {

    private Storage storage;

    @Override
    public Book add(Book entity) {
        return storage.add(entity);
    }

    @Override
    public Book update(Book entity) {
        return storage.update(entity);
    }

    @Override
    public Optional<Book> findById(Long id) {
        Object objectEntity = storage.findById(id, Book.class);
        if (objectEntity == null) {
            return Optional.empty();
        }
        Book bookEntity = (Book) objectEntity;
        return Optional.of(bookEntity);
    }

    @Override
    public void deleteById(Long id) {
        storage.deleteById(id, Book.class);
    }
}
