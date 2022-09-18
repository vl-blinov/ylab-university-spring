package com.edu.ulab.app.dao.impl;

import com.edu.ulab.app.dao.Dao;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.storage.Storage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserDao implements Dao<User, Long> {

    private Storage storage;

    @Override
    public User add(User userEntity) {
        userEntity.setBooks(new ArrayList<>());
        return storage.add(userEntity);
    }

    @Override
    public User update(User userEntity) {
        userEntity.setBooks(new ArrayList<>());
        return storage.update(userEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        Object objectEntity = storage.findById(id, User.class);
        if (objectEntity == null) {
            return Optional.empty();
        }
        User userEntity = (User) objectEntity;
        return Optional.of(userEntity);
    }

    @Override
    public void deleteById(Long id) {
        storage.deleteById(id, User.class);
    }
}
