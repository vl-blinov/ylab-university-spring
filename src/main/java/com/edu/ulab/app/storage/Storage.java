package com.edu.ulab.app.storage;

import com.edu.ulab.app.annotations.AutoIncrementedId;
import com.edu.ulab.app.annotations.ManyToOneRelationship;
import com.edu.ulab.app.annotations.OneToManyRelationship;
import com.edu.ulab.app.exception.FieldAnnotationException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

@Component
public class Storage {

    private final Map<String, Block> blocks = new HashMap<>();

    public <T> T add(T entity) {
        String className = entity.getClass().getSimpleName();
        if (!blocks.containsKey(className)) {
            Block block = new Block();
            block.add(entity);
            blocks.put(className, block);
        } else {
            blocks.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().equals(className))
                    .findAny()
                    .orElseThrow()
                    .getValue()
                    .add(entity);
        }

        Optional<Field> fieldOptional =
                Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(ManyToOneRelationship.class))
                .findAny();

        if (fieldOptional.isPresent()) {
            Field field = fieldOptional.get();
            String fieldName = field.getName();
            String fieldTypeName = field.getType().getSimpleName();
            Block requiredBlock = blocks.get(fieldTypeName);

            Predicate<Field> idFieldPredicate =
                    f -> Number.class.isAssignableFrom(f.getType())
                            && f.isAnnotationPresent(AutoIncrementedId.class);
            try {
                field.setAccessible(true);
                Field idField = Arrays.stream(field.get(entity).getClass().getDeclaredFields())
                        .filter(idFieldPredicate)
                        .findAny()
                        .orElseThrow(() ->
                                new FieldAnnotationException("No field annotated with '@AutoIncrementedId'."));
                idField.setAccessible(true);
                Long idFieldValue = (Long) idField.get(field.get(entity));

                Object cascadeObject = requiredBlock.findById(idFieldValue);
                Predicate<Field> cascadePredicate
                        = f -> f.isAnnotationPresent(OneToManyRelationship.class)
                                && f.getAnnotation(OneToManyRelationship.class).mappedBy().equals(fieldName);
                Field cascadeField =
                        Arrays.stream(cascadeObject.getClass().getDeclaredFields())
                                .filter(cascadePredicate)
                                .findAny()
                                .orElseThrow(() ->
                                        new FieldAnnotationException("No field annotated with '@OneToManyRelationship'."));
                cascadeField.setAccessible(true);
                List<Object> cascadeFieldValue = (List<Object>) cascadeField.get(cascadeObject);
                cascadeFieldValue.add(entity);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return entity;
    }

    public <T> T update(T entity) {
        String className = entity.getClass().getSimpleName();
        blocks.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(className))
                .findAny()
                .orElseThrow()
                .getValue()
                .update(entity);
        return entity;
    }

    public Object findById(Long id, Class<?> requiredClass) {
        Block block = blocks.get(requiredClass.getSimpleName());
        return block.findById(id);
    }

    public void deleteById(Long id, Class<?> requiredClass) {
        Block block = blocks.get(requiredClass.getSimpleName());
        CascadeDeleteGroup cascadeDeleteGroup = block.deleteById(id);

        Class<?> elementClass = cascadeDeleteGroup.getElementClass();
        List<Long> elementIds = cascadeDeleteGroup.getElementIds();
        if (elementIds != null) {
            elementIds.forEach(elementId -> deleteById(elementId, elementClass));
        }
    }
}
