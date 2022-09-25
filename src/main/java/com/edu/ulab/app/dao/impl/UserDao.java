package com.edu.ulab.app.dao.impl;

import com.edu.ulab.app.dao.Dao;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.storage.Storage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserDao implements Dao<Person, Long> {

    private Storage storage;

    @Override
    public Person add(Person person) {
        person.setBooks(new ArrayList<>());
        return storage.add(person);
    }

    @Override
    public Person update(Person person) {
        person.setBooks(new ArrayList<>());
        return storage.update(person);
    }

    @Override
    public Optional<Person> findById(Long id) {
        Object object = storage.findById(id, Person.class);
        if (object == null) {
            return Optional.empty();
        }
        Person person = (Person) object;
        return Optional.of(person);
    }

    @Override
    public void deleteById(Long id) {
        storage.deleteById(id, Person.class);
    }
}
