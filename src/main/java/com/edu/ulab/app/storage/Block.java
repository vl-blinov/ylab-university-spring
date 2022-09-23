package com.edu.ulab.app.storage;

import com.edu.ulab.app.annotations.AutoIncrementedId;
import com.edu.ulab.app.annotations.OneToManyRelationship;
import com.edu.ulab.app.exception.FieldAnnotationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Predicate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Block {
    private Long entityId = 0L;
    private Map<Long, Object> entities = new HashMap<>();

    public <T> void add(T entity) {
        Predicate<Field> idPredicate =
                field -> Number.class.isAssignableFrom(field.getType())
                        && field.isAnnotationPresent(AutoIncrementedId.class);
        try {
            Field idField = Arrays.stream(entity.getClass().getDeclaredFields())
                    .filter(idPredicate)
                    .findAny()
                    .orElseThrow(() ->
                            new FieldAnnotationException("No field annotated with '@AutoIncrementedId'."));
            idField.setAccessible(true);
            idField.set(entity, ++entityId);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        entities.put(entityId, entity);
    }

    public <T> void update(T entity) {
        Predicate<Field> idPredicate =
                field -> Number.class.isAssignableFrom(field.getType())
                        && field.isAnnotationPresent(AutoIncrementedId.class);
        Long id;
        try {
            Field idField = Arrays.stream(entity.getClass().getDeclaredFields())
                    .filter(idPredicate)
                    .findAny()
                    .orElseThrow(() ->
                            new FieldAnnotationException("No field annotated with '@AutoIncrementedId'."));
            idField.setAccessible(true);
            id = (Long) idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        entities.replace(id, entity);
    }

    public Object findById(Long id) {
        return entities.get(id);
    }

    public CascadeDeleteGroup deleteById(Long id) {
        Object entityObject = findById(id);
        Optional<Field> cascadeFieldOptional = Arrays.stream(entityObject.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(OneToManyRelationship.class))
                .findAny();
        Class<?> cascadeFieldClass = null;
        List<Long> idList = null;

        if (cascadeFieldOptional.isPresent()) {
            try {
                Field cascadeField = cascadeFieldOptional.get();
                cascadeField.setAccessible(true);
                ParameterizedType parameterizedType = (ParameterizedType) cascadeField.getGenericType();
                cascadeFieldClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                List<Object> list = (List<Object>) cascadeField.get(entityObject);
                idList = list
                        .stream()
                        .flatMap(listElement -> Arrays.stream(listElement.getClass().getDeclaredFields())
                                .filter(field -> field.isAnnotationPresent(AutoIncrementedId.class))
                                .peek(field -> field.setAccessible(true))
                                .map(field -> {
                                    try {
                                        return (Long) field.get(listElement);
                                    } catch (IllegalAccessException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                })).toList();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        entities.remove(id);
        CascadeDeleteGroup cascadeDeleteGroup = new CascadeDeleteGroup();
        cascadeDeleteGroup.setElementClass(cascadeFieldClass);
        cascadeDeleteGroup.setElementIds(idList);

        return cascadeDeleteGroup;
    }
}
