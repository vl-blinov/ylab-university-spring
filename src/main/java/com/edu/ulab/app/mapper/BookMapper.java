package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.web.request.BookRequest;
import org.mapstruct.Context;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto bookRequestToBookDto(BookRequest bookRequest);

    BookRequest bookDtoToBookRequest(BookDto bookDto);

    @Mapping(source = "user", target = "user")
    Book bookDtoToBookEntity(BookDto bookDto, @Context CycleAvoidingMappingContext context);

    @InheritInverseConfiguration
    BookDto bookEntityToBookDto(Book bookEntity, @Context CycleAvoidingMappingContext context);
}
