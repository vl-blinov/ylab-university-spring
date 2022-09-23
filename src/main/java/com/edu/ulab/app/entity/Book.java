package com.edu.ulab.app.entity;

import com.edu.ulab.app.annotations.AutoIncrementedId;
import com.edu.ulab.app.annotations.ManyToOneRelationship;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @AutoIncrementedId
    private Long id;

    @ManyToOneRelationship
    private User user;

    private String title;

    private String author;

    private long pageCount;
}
