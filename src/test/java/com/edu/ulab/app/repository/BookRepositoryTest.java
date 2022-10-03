package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Repository tests {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {

    @Autowired
    BookRepository underTest;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @Test
    @DisplayName("Insert a book. InsertCount should be equal to 2.")
    void insertBook_thenAssertDmlCount() {

        //Given
        Person user = new Person();
        user.setFullName("new test name");
        user.setTitle("new test title");
        user.setAge(100);

        Person savedUser = userRepository.save(user);

        Book book = new Book();
        book.setTitle("new test title");
        book.setAuthor("new test author");
        book.setPageCount(1000);
        book.setUser(savedUser);

        //When
        Book result = underTest.save(book);

        //Then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("new test title");
        assertThat(result.getUser().getTitle()).isEqualTo("new test title");
        assertSelectCount(0);
        assertInsertCount(2);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Insert a book. Should throw DataIntegrityViolationException.")
    void insertBookWithUser_Failure() {

        //Given
        Book book = new Book();
        book.setTitle("new test title");
        book.setAuthor("new test author");
        book.setPageCount(1000);

        //When-Then
        assertThatThrownBy(() -> underTest.save(book))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Update a book. SelectCount should be equal to 2.")
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"})
    void updateBook_thenAssertDmlCount() {

        //Given
        Long userId = 1000L;

        Person extractedUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id '" + userId + "' is not found."));

        Book book = new Book();
        book.setId(1000L);
        book.setTitle("updated test title1");
        book.setAuthor("test author1");
        book.setPageCount(1000);
        book.setUser(extractedUser);

        //When
        Book result = underTest.save(book);

        //Then
        assertThat(result.getId()).isEqualTo(1000L);
        assertThat(result.getTitle()).isEqualTo("updated test title1");
        assertThat(result.getUser().getId()).isEqualTo(userId);
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Get a book. SelectCount should be equal to 1.")
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"})
    void getBook_thenAssertDmlCount() {

        //Given
        Long bookId = 1000L;

        //When
        Book result = underTest.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book with id '" + bookId + "' is not found."));

        //Then
        assertThat(result.getId()).isEqualTo(bookId);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Delete a book. SelectCount should be equal to 1.")
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"})
    void deleteBook_thenAssertDmlCount() {

        //Given
        Long bookId = 1000L;

        //When
        underTest.deleteById(bookId);

        //Then
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }
}
