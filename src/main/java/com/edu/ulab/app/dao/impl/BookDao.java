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
    public Book add(Book book) {
        return storage.add(book);
    }

    @Override
    public Book update(Book book) {
        return storage.update(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        Object object = storage.findById(id, Book.class);
        if (object == null) {
            return Optional.empty();
        }
        Book book = (Book) object;
        return Optional.of(book);
    }

    @Override
    public void deleteById(Long id) {
        storage.deleteById(id, Book.class);
    }
}
