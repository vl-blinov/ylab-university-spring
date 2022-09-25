package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.CycleAvoidingMappingContext;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.util.SqlConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Qualifier("userServiceJdbc")
@AllArgsConstructor
public class UserServiceImplTemplate implements UserService {

    private final JdbcTemplate jdbcTemplate;
    private final BookMapper bookMapper;

    private static final String INSERT_USER_QUERY;
    private static final String UPDATE_USER_QUERY;
    private static final String SELECT_USER_QUERY;
    private static final String SELECT_BOOKS_QUERY;
    private static final String DELETE_BOOKS_QUERY;
    private static final String DELETE_USER_QUERY;



    static {
        INSERT_USER_QUERY = SqlConverter.loadResourceToString("queries/insertUserQuery.sql");
        UPDATE_USER_QUERY = SqlConverter.loadResourceToString("queries/updateUserQuery.sql");
        SELECT_USER_QUERY = SqlConverter.loadResourceToString("queries/selectUserQuery.sql");
        SELECT_BOOKS_QUERY = SqlConverter.loadResourceToString("queries/selectBooksQuery.sql");
        DELETE_BOOKS_QUERY = SqlConverter.loadResourceToString("queries/deleteBooksQuery.sql");
        DELETE_USER_QUERY = SqlConverter.loadResourceToString("queries/deleteUserQuery.sql");
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_USER_QUERY, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);
        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Created user: {}", userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        jdbcTemplate.update(
                UPDATE_USER_QUERY,
                userDto.getFullName(),
                userDto.getTitle(),
                userDto.getAge(),
                userDto.getId());
        log.info("Updated user: {}", userDto);
        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {

        Person extractedUser = jdbcTemplate.queryForObject(
                SELECT_USER_QUERY,
                (rs, rowNum) -> {
                    Person user = new Person();
                    user.setId(rs.getLong("ID"));
                    user.setFullName(rs.getString("FULL_NAME"));
                    user.setTitle(rs.getString("TITLE"));
                    user.setAge(rs.getInt("AGE"));
                    user.setBooks(new ArrayList<>());
                    return user;
                    }, id);

        List<Book> books = jdbcTemplate.query(
                SELECT_BOOKS_QUERY,
                ((rs, rowNum) -> {
                    Book book = new Book();
                    book.setId(rs.getLong("ID"));
                    book.setTitle(rs.getString("TITLE"));
                    book.setAuthor(rs.getString("AUTHOR"));
                    book.setPageCount(rs.getLong("PAGE_COUNT"));
                    return book;
                }), id);

        List<BookDto> bookDtoList = books
                .stream()
                .peek(book -> Objects.requireNonNull(extractedUser).addBook(book))
                .map(book -> bookMapper.bookEntityToBookDto(book, new CycleAvoidingMappingContext()))
                .toList();

        UserDto userDto = new UserDto();
        userDto.setId(Objects.requireNonNull(extractedUser).getId());
        userDto.setFullName(extractedUser.getFullName());
        userDto.setTitle(extractedUser.getTitle());
        userDto.setAge(extractedUser.getAge());
        userDto.setBooks(bookDtoList);
        log.info("Extracted user: {}", userDto);

        return userDto;
    }

    @Override
    public void deleteUserById(Long id) {
        jdbcTemplate.update(DELETE_BOOKS_QUERY, id);
        jdbcTemplate.update(DELETE_USER_QUERY, id);
    }
}
