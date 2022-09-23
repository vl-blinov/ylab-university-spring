package com.edu.ulab.app.entity;

import com.edu.ulab.app.annotations.AutoIncrementedId;
import com.edu.ulab.app.annotations.OneToManyRelationship;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @AutoIncrementedId
    private Long id;

    private String fullName;

    private String title;

    private int age;

    @OneToManyRelationship(mappedBy = "user")
    List<Book> books = new ArrayList<>();
}
