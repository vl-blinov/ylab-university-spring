package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.CycleAvoidingMappingContext;
import com.edu.ulab.app.service.BookService;
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
@Qualifier("bookServiceJdbc")
@AllArgsConstructor
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;
    private final BookMapper bookMapper;

    private static final String INSERT_BOOK_QUERY;
    private static final String UPDATE_BOOK_QUERY;
    private static final String SELECT_BOOK_WITH_USER_QUERY;
    private static final String SELECT_BOOKS_QUERY;
    private static final String DELETE_BOOK_QUERY;

    static {
        INSERT_BOOK_QUERY = SqlConverter.loadResourceToString("queries/insertBookQuery.sql");
        UPDATE_BOOK_QUERY = SqlConverter.loadResourceToString("queries/updateBookQuery.sql");
        SELECT_BOOK_WITH_USER_QUERY = SqlConverter.loadResourceToString("queries/selectBookWithUserQuery.sql");
        SELECT_BOOKS_QUERY = SqlConverter.loadResourceToString("queries/selectBooksQuery.sql");
        DELETE_BOOK_QUERY = SqlConverter.loadResourceToString("queries/deleteBookQuery.sql");
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_BOOK_QUERY, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getUser().getId());
                    return ps;
                }, keyHolder);
        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        jdbcTemplate.update(
                UPDATE_BOOK_QUERY,
                bookDto.getTitle(),
                bookDto.getAuthor(),
                bookDto.getPageCount(),
                bookDto.getId());
        return bookDto;
    }

    @Override
    public BookDto getBookById(Long id) {

        Person extractedUser = new Person();
        Book extractedBook = new Book();

        jdbcTemplate.query(
                SELECT_BOOK_WITH_USER_QUERY,
                rs -> {
                    extractedUser.setId(rs.getLong("ID_P"));
                    extractedUser.setFullName(rs.getString("FULL_NAME_P"));
                    extractedUser.setTitle(rs.getString("TITLE_P"));
                    extractedUser.setAge(rs.getInt("AGE_P"));
                    extractedUser.setBooks(new ArrayList<>());

                    extractedBook.setId(rs.getLong("ID_B"));
                    extractedBook.setTitle(rs.getString("TITLE_B"));
                    extractedBook.setAuthor(rs.getString("AUTHOR_B"));
                    extractedBook.setPageCount(rs.getLong("PAGE_COUNT_B"));
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
                }), extractedUser.getId());

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

        BookDto bookDto = new BookDto();
        bookDto.setId(extractedBook.getId());
        bookDto.setTitle(extractedBook.getTitle());
        bookDto.setAuthor(extractedBook.getAuthor());
        bookDto.setPageCount(extractedBook.getPageCount());
        bookDto.setUser(userDto);

        return bookDto;
    }

    @Override
    public void deleteBookById(Long id) {
        jdbcTemplate.update(DELETE_BOOK_QUERY, id);
    }
}