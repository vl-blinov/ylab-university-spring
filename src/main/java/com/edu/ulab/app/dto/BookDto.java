package com.edu.ulab.app.dto;

import lombok.Data;
import lombok.ToString;

@Data
public class BookDto {

    private Long id;

    private String title;

    private String author;

    private long pageCount;

    @ToString.Exclude
    private UserDto user;
}
