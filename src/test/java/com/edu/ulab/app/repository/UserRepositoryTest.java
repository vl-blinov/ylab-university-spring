package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
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
 * Repository tests {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository underTest;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @Test
    @DisplayName("Check if a user with the given title exists. Must be true. SelectCount should be equal to 1.")
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"})
    void checkIfExistsByTitle_thenAssertDmlCount() {

        //Given
        String title = "test title";

        //When
        Boolean result = underTest.existsByTitle(title);

        //Then
        assertThat(result).isTrue();
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Check if a user with the given title exists. Must be false.")
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"})
    void checkIfExistsByTitle_False() {

        //Given
        String title = "new unique test title";

        //When
        Boolean result = underTest.existsByTitle(title);

        //Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Insert a user. InsertCount should be equal to 1.")
    void insertUser_thenAssertDmlCount() {

        //Given
        Person user = new Person();
        user.setFullName("new test name");
        user.setTitle("new test title");
        user.setAge(100);

        //When
        Person result = underTest.save(user);

        //Then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("new test title");
        assertSelectCount(0);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Insert a user. Should throw DataIntegrityViolationException.")
    void insertUser_Failure() {

        //Given
        Person user = new Person();
        user.setFullName("new test name with number of characters more than 50");
        user.setTitle("new test title");
        user.setAge(100);

        //When-Then
        assertThatThrownBy(() -> underTest.save(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Update a user. SelectCount should be equal to 1.")
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"})
    void updateUser_thenAssertDmlCount() {

        //Given
        Person user = new Person();
        user.setId(1000L);
        user.setFullName("updated test name");
        user.setTitle("test title");
        user.setAge(100);

        //When
        Person result = underTest.save(user);

        //Then
        assertThat(result.getId()).isEqualTo(1000L);
        assertThat(result.getFullName()).isEqualTo("updated test name");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Get a user. SelectCount should be equal to 1.")
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"})
    void getUser_thenAssertDmlCount() {

        //Given
        Long userId = 1000L;

        //When
        Person result = underTest.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id '" + userId + "' is not found."));

        //Then
        assertThat(result.getId()).isEqualTo(userId);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Delete a user. SelectCount should be equal to 2.")
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"})
    void deleteUser_thenAssertDmlCount() {

        //Given
        Long userId = 1000L;

        //When
        underTest.deleteById(userId);

        //Then
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }
}
