package com.edu.ulab.app.service;

import com.edu.ulab.app.config.UnitTest;
import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.CycleAvoidingMappingContext;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link BookServiceImpl}.
 */
@UnitTest
public class BookServiceImplTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @InjectMocks
    BookServiceImpl underTest;

    @Test
    @DisplayName("Create a new book. Should be successful.")
    void createBook_Test() {

        //Given
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setTitle("test title");
        bookDto.setAuthor("test author");
        bookDto.setPageCount(1000);
        bookDto.setUser(userDto);

        Person user = new Person();
        user.setId(1L);

        Book book = new Book();
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPageCount(1000);
        book.setUser(user);

        Book createdBook = new Book();
        createdBook.setId(1L);
        createdBook.setTitle("test title");
        createdBook.setAuthor("test author");
        createdBook.setPageCount(1000);
        createdBook.setUser(user);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setTitle("test title");
        result.setAuthor("test author");
        result.setPageCount(1000);
        result.setUser(userDto);

        //When
        when(bookMapper.bookDtoToBookEntity(any(BookDto.class), any(CycleAvoidingMappingContext.class)))
                .thenReturn(book);
        when(bookRepository.save(book)).thenReturn(createdBook);
        when(bookMapper.bookEntityToBookDto(any(Book.class), any(CycleAvoidingMappingContext.class)))
                .thenReturn(result);

        BookDto bookDtoResult = underTest.createBook(bookDto);

        //Then
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Update an existing book. Should be successful.")
    void updateBook_Test() {

        //Given
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("test title");
        bookDto.setAuthor("test author");
        bookDto.setPageCount(1000);
        bookDto.setUser(userDto);

        Person user = new Person();
        user.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPageCount(1000);
        book.setUser(user);

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("test title");
        updatedBook.setAuthor("test author");
        updatedBook.setPageCount(1000);
        updatedBook.setUser(user);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setTitle("test title");
        result.setAuthor("test author");
        result.setPageCount(1000);
        result.setUser(userDto);

        //When
        when(bookMapper.bookDtoToBookEntity(any(BookDto.class), any(CycleAvoidingMappingContext.class)))
                .thenReturn(book);
        when(bookRepository.save(book)).thenReturn(updatedBook);
        when(bookMapper.bookEntityToBookDto(any(Book.class), any(CycleAvoidingMappingContext.class)))
                .thenReturn(result);

        BookDto bookDtoResult = underTest.createBook(bookDto);

        //Then
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Get a book by the given ID. Should be successful.")
    void getBookById_Test_Success() {

        //Given
        Long bookId = 1L;

        Person user = new Person();
        user.setId(1L);

        Book extractedBook = new Book();
        extractedBook.setId(bookId);
        extractedBook.setTitle("test title");
        extractedBook.setAuthor("test author");
        extractedBook.setPageCount(1000);
        extractedBook.setUser(user);

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        BookDto result = new BookDto();
        result.setId(bookId);
        result.setTitle("test title");
        result.setAuthor("test author");
        result.setPageCount(1000);
        result.setUser(userDto);

        //When
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(extractedBook));
        when(bookMapper.bookEntityToBookDto(any(Book.class), any(CycleAvoidingMappingContext.class)))
                .thenReturn(result);

        BookDto bookDtoResult = underTest.getBookById(bookId);

        //Then
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Get a book by the given ID. Should throw NotFoundException.")
    void getBookById_Test_Failure() {

        //Given
        Long bookId = 1L;
        String message = "Book with id '" + bookId + "' is not found";

        //When
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //Then
        assertThrows(NotFoundException.class, () -> underTest.getBookById(bookId), message);
    }

    @Test
    @DisplayName("Delete a book by the given ID. Should be successful.")
    void deleteBookById_Test_Success() {

        //Given
        Long bookId = 1L;

        Book extractedBook = new Book();
        extractedBook.setId(bookId);

        Book book = new Book();
        book.setId(2L);

        Person user = new Person();
        user.setId(1L);
        user.setBooks(new ArrayList<>());
        user.addBook(extractedBook);
        user.addBook(book);

        //When
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(extractedBook));
        underTest.deleteBookById(bookId);

        //Then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(bookRepository).deleteById(captor.capture());
        assertEquals(bookId, captor.getValue());

        assertFalse(user.getBooks().contains(extractedBook));
        assertTrue(user.getBooks().contains(book));
    }

    @Test
    @DisplayName("Delete a book by the given ID. Should throw NotFoundException.")
    void deleteBookById_Test_Failure() {

        //Given
        Long bookId = 1L;
        String message = "Book with id '" + bookId + "' is not found";

        //When
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //Then
        assertThrows(NotFoundException.class, () -> underTest.deleteBookById(bookId), message);
    }
}
