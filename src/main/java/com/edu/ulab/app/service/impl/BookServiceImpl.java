package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dao.impl.BookDao;
import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private BookDao bookDao;
    private BookMapper bookMapper;

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book bookEntity = bookMapper.bookDtoToBookEntity(bookDto);
        Book createdBook = bookDao.add(bookEntity);
        return bookMapper.bookEntityToBookDto(createdBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book bookEntity = bookMapper.bookDtoToBookEntity(bookDto);
        Book updatedBookEntity = bookDao.update(bookEntity);
        return bookMapper.bookEntityToBookDto(updatedBookEntity);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book extractedBookEntity = bookDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with id '" + id + "' is not found"));
        return bookMapper.bookEntityToBookDto(extractedBookEntity);
    }

    @Override
    public void deleteBookById(Long id) {
        getBookById(id);
        bookDao.deleteById(id);
    }
}
