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

    //todo:
    // 1) Создать хранилище в котором будут содержаться данные.
    // ------------------------------------------------------------------------------
    // SOLUTION: Storage сохраняет данные в HashMap, где key - имя класса, value - Block.
    // Block сохраняет данные в HashMap, где key - Entity id, value - Entity.
    // В итоге получаем хранилище, состоящее из блоков с сущностями одинакового типа.
    // ------------------------------------------------------------------------------
    // 2) Сделать абстракции через которые можно будет производить операции с хранилищем.
    // ------------------------------------------------------------------------------
    // SOLUTION: создал слой DAO: UserDao, BookDao.
    // ------------------------------------------------------------------------------
    // 3) Продумать логику поиска и сохранения.
    // ------------------------------------------------------------------------------
    // SOLUTION: поиск осуществляется по ключам HashMap хранилища и блока,
    // сохранение - с использование рефлексии.
    // ------------------------------------------------------------------------------
    // 4) Продумать возможные ошибки.
    // ------------------------------------------------------------------------------
    // SOLUTION: создал FieldAnnotationException, которое выбрасывается, если
    // поле Entity не помечено необходимой аннотацией.
    // ------------------------------------------------------------------------------
    // 5) Учесть, что при сохранеии юзера или книги, должен генерироваться идентификатор.
    // ------------------------------------------------------------------------------
    // SOLUTION: генерация id пердусмотрена в Block при добавления нового элемента,
    // если его поле отмечено созданной аннотацией @AutoIncrementedId.
    // ------------------------------------------------------------------------------
    // 6) Продумать что у юзера может быть много книг и нужно создать эту связь.
    // ------------------------------------------------------------------------------
    // SOLUTION: добавил поле books (List<Books>) в Person Entity и поле user (Person) в Book Entity.
    // Примененил к этим полям созданные аннотации @OneToManyRelationship и @ManyToOneRelationship.
    // Реализовал логику определения это связи с использованием рефлексии.
    // ------------------------------------------------------------------------------
    // 7) Также учесть, что методы хранилища принимают другой тип данных - учесть это в абстракции.
    // ------------------------------------------------------------------------------
    // SOLUTION: использовал Generics (type parameter T).

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
