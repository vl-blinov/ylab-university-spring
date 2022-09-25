package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.CycleAvoidingMappingContext;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Qualifier("bookServiceJpa")
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;
    private BookMapper bookMapper;

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBookEntity(bookDto, new CycleAvoidingMappingContext());
        log.info("Mapped book: {}", book);
        Book createdBook = bookRepository.save(book);
        log.info("Created book: {}", createdBook);
        return bookMapper.bookEntityToBookDto(createdBook, new CycleAvoidingMappingContext());
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBookEntity(bookDto, new CycleAvoidingMappingContext());
        log.info("Mapped book: {}", book);
        Book updatedBook = bookRepository.save(book);
        log.info("Updated book: {}", updatedBook);
        return bookMapper.bookEntityToBookDto(updatedBook, new CycleAvoidingMappingContext());
    }

    @Override
    public BookDto getBookById(Long id) {
        Book extractedBook = extractBookEntity(id);
        log.info("Extracted book: {}", extractedBook);
        return bookMapper.bookEntityToBookDto(extractedBook, new CycleAvoidingMappingContext());
    }

    @Override
    public void deleteBookById(Long id) {
        Book extractedBook = extractBookEntity(id);
        extractedBook.getUser().removeBook(extractedBook);
        bookRepository.deleteById(id);
    }

    private Book extractBookEntity(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with id '" + id + "' is not found"));
    }
}
