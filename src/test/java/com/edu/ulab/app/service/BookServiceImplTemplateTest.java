package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImplTemplate}
 */

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
class BookServiceImplTemplateTest {

    @InjectMocks
    private BookServiceImplTemplate bookServiceImplTemplate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void createBook_Test() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setUserId(-1);

        //when

        when(jdbcTemplate.update(
                any(PreparedStatementCreator.class)))
                .thenReturn(1);

        //then

        BookDto bookDtoResult = bookServiceImplTemplate.createBook(bookDto);
        assertTrue(bookDtoResult.getId() > 0);
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setId(1);
        bookDto.setUserId(1);

        BookDto bookDto0 = new BookDto();
        bookDto0.setId(1);
        bookDto0.setUserId(1);

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(2);
        bookDto1.setUserId(1);

        //when

        when(jdbcTemplate.update(
                anyString(),
                anyInt(),
                anyString(),
                anyString(),
                anyLong(),
                anyInt()))
                .thenReturn(1);

        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                anyInt()))
                .thenReturn(List.of(bookDto1, bookDto0));

        //then

        bookServiceImplTemplate.updateBook(bookDto);
    }

    @Test
    @DisplayName("Обновление книги. Не должно пройти успешно.")
    void updateBookException_Test() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setId(3);
        bookDto.setUserId(1);

        BookDto bookDto0 = new BookDto();
        bookDto0.setId(1);
        bookDto0.setUserId(1);

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(2);
        bookDto1.setUserId(1);

        //when

        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                anyInt()))
                .thenReturn(List.of(bookDto1, bookDto0));

        //then

        assertThrows(BadRequestException.class, () -> bookServiceImplTemplate.updateBook(bookDto));
    }

    @Test
    @DisplayName("Получение книги. Должно пройти успешно.")
    void getBookById_Test() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setId(1);
        bookDto.setUserId(1);

        BookDto bookDto0 = new BookDto();
        bookDto0.setId(1);
        bookDto0.setUserId(1);

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(2);
        bookDto1.setUserId(1);

        //when

        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                anyInt()))
                .thenReturn(List.of(bookDto1, bookDto0));

        //then

        Iterable<BookDto> bookDtoIterableResult = bookServiceImplTemplate.getBookById(bookDto.getId());
        assertEquals(List.of(bookDto1, bookDto0), bookDtoIterableResult);
    }

    @Test
    @DisplayName("Удаление книги. Должно пройти успешно.")
    void deleteBookById_Test() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setId(1);

        //when

        when(jdbcTemplate.update(
                anyString(),
                anyInt()))
                .thenReturn(1);

        //then

        bookServiceImplTemplate.deleteBookById(bookDto.getId());
    }
}