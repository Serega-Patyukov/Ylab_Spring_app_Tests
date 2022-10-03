package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.entity.entityStorage.BookEntity;
import com.edu.ulab.app.entity.entityStorage.UserEntity;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.storage.CrudRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    CrudRepository crudRepository;

    @Mock
    BookMapper bookMapper;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание книги с полученным id. Должно пройти успешно.")
    void saveBook_Test() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setId(1);
        bookDto.setUserId(1);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookEntity mapperBookEntity = new BookEntity();
        mapperBookEntity.setId(1);
        mapperBookEntity.setUserId(1);
        mapperBookEntity.setAuthor("test author");
        mapperBookEntity.setTitle("test title");
        mapperBookEntity.setPageCount(1000);

        UserEntity mapperUserEntity = new UserEntity();
        mapperUserEntity.setId(1);
        mapperUserEntity.setFullName("test name");
        mapperUserEntity.setAge(11);
        mapperUserEntity.setTitle("test name");

        //when

        when(crudRepository.existsById(bookDto.getId())).thenReturn(true);
        when(bookMapper.bookDtoToBookEntity(bookDto)).thenReturn(mapperBookEntity);
        when(crudRepository.findById(bookDto.getUserId())).thenReturn(Optional.of(mapperUserEntity));
        when(userMapper.userEntityToUserEntity(mapperUserEntity)).thenReturn(mapperUserEntity);
        when(crudRepository.save(mapperUserEntity)).thenReturn(mapperUserEntity);

        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(-1, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Создание книги с рандомным id. Должно пройти успешно.")
    void saveBookRandom_Test() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setId(0);
        bookDto.setUserId(1);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookEntity mapperBookEntity = new BookEntity();
        mapperBookEntity.setId(0);
        mapperBookEntity.setUserId(1);
        mapperBookEntity.setAuthor("test author");
        mapperBookEntity.setTitle("test title");
        mapperBookEntity.setPageCount(1000);

        UserEntity mapperUserEntity = new UserEntity();
        mapperUserEntity.setId(1);
        mapperUserEntity.setFullName("test name");
        mapperUserEntity.setAge(11);
        mapperUserEntity.setTitle("test name");

        //when

        when(crudRepository.existsById(1)).thenReturn(true);
        when(bookMapper.bookDtoToBookEntity(bookDto)).thenReturn(mapperBookEntity);
        when(crudRepository.findById(bookDto.getUserId())).thenReturn(Optional.of(mapperUserEntity));
        when(userMapper.userEntityToUserEntity(mapperUserEntity)).thenReturn(mapperUserEntity);
        when(crudRepository.save(mapperUserEntity)).thenReturn(mapperUserEntity);

        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertTrue(bookDtoResult.getId() < 0);
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setId(1);
        bookDto.setUserId(1);

        BookEntity bookEntityFromBd = new BookEntity();
        bookEntityFromBd.setId(-1);
        bookEntityFromBd.setUserId(1);

        UserEntity userEntityFronBd = new UserEntity();
        userEntityFronBd.setId(1);
        userEntityFronBd.setBookEntityList(List.of(bookEntityFromBd));

        //when

        when(crudRepository.existsById(1)).thenReturn(true);
        when(crudRepository.existsById(-1)).thenReturn(true);
        when(crudRepository.findById(bookDto.getUserId())).thenReturn(Optional.of(userEntityFronBd));
        when(bookMapper.bookDtoToBookEntity(bookDto)).thenReturn(bookEntityFromBd);
        when(crudRepository.findById(bookDto.getUserId())).thenReturn(Optional.of(userEntityFronBd));
        when(userMapper.userEntityToUserEntity(userEntityFronBd)).thenReturn(userEntityFronBd);
        when(crudRepository.save(userEntityFronBd)).thenReturn(userEntityFronBd);

        //then
        bookService.updateBook(bookDto);
    }

    @Test
    @DisplayName("Обновление книги. Не должно пройти успешно.")
    void updateBookNotFoundException_Test() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setId(1);
        bookDto.setUserId(1);


        UserEntity userEntityFronBd = new UserEntity();
        userEntityFronBd.setId(1);
        userEntityFronBd.setBookEntityList(new ArrayList<>());

        //when

        when(crudRepository.existsById(1)).thenReturn(true);
        when(crudRepository.existsById(-1)).thenReturn(true);
        when(crudRepository.findById(bookDto.getUserId())).thenReturn(Optional.of(userEntityFronBd));

        //then
        assertThatThrownBy(() -> bookService.updateBook(bookDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Получение книги. Должно пройти успешно.")
    void getBook_Test() {
        //given

        BookDto bookDto0 = new BookDto();
        bookDto0.setId(1);
        bookDto0.setUserId(1);
        bookDto0.setAuthor("test author");
        bookDto0.setTitle("test title");
        bookDto0.setPageCount(1000);

        BookEntity bookEntityFromBd0 = new BookEntity();
        bookEntityFromBd0.setId(1);
        bookEntityFromBd0.setUserId(1);
        bookEntityFromBd0.setAuthor("test author");
        bookEntityFromBd0.setTitle("test title");
        bookEntityFromBd0.setPageCount(1000);

        UserEntity userEntityFronBd = new UserEntity();
        userEntityFronBd.setId(1);
        userEntityFronBd.setFullName("test name");
        userEntityFronBd.setAge(11);
        userEntityFronBd.setTitle("test name");
        userEntityFronBd.setBookEntityList(List.of(bookEntityFromBd0));

        //when

        when(crudRepository.existsById(1)).thenReturn(true);
        when(crudRepository.findById(1)).thenReturn(Optional.of(userEntityFronBd));
        when(bookMapper.bookEntityToBookDot(any(BookEntity.class))).thenReturn(bookDto0);

        //then
        List<BookDto> bookDtoListResult = bookService.getBookById(1);
        assertEquals(List.of(bookDto0), bookDtoListResult);
    }

    @Test
    @DisplayName("Удаление книги. Должно пройти успешно.")
    void deleteBook_Test() {
        //given

        BookDto bookDto0 = new BookDto();
        bookDto0.setId(1);
        bookDto0.setUserId(1);
        bookDto0.setAuthor("test author");
        bookDto0.setTitle("test title");
        bookDto0.setPageCount(1000);

        BookEntity bookEntityFromBd0 = new BookEntity();
        bookEntityFromBd0.setId(1);
        bookEntityFromBd0.setUserId(1);
        bookEntityFromBd0.setAuthor("test author");
        bookEntityFromBd0.setTitle("test title");
        bookEntityFromBd0.setPageCount(1000);

        UserEntity userEntityFronBd = new UserEntity();
        userEntityFronBd.setId(1);
        userEntityFronBd.setFullName("test name");
        userEntityFronBd.setAge(11);
        userEntityFronBd.setTitle("test name");
        userEntityFronBd.setBookEntityList(List.of(bookEntityFromBd0));

        //when
        when(crudRepository.existsById(1)).thenReturn(true);
        when(crudRepository.findById(1)).thenReturn(Optional.of(userEntityFronBd));
        when(bookMapper.bookEntityToBookEntity(any(BookEntity.class))).thenReturn(bookEntityFromBd0);

        //then
        bookService.deleteBookById(1);
    }

    @Test
    @DisplayName("Удаление книги.Не должно пройти успешно.")
    void deleteBook_NotFoundException() {
        //when
        when(crudRepository.existsById(anyInt())).thenReturn(false);

        //then
        assertThatThrownBy(() -> bookService.deleteBookById(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("id user entity not found");
    }

    @Test
    @DisplayName("Создание книги.Не должно пройти успешно.")
    void savedBook_Null_BadRequestException() {
        assertThatThrownBy(() -> bookService.createBook(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Bad request");
    }

    @Test
    @DisplayName("Создание книги.Не должно пройти успешно.")
    void savedBook_1_BadRequestException() {
        //given
        BookDto bookDto = new BookDto();
        bookDto.setId(-1);

        //then
        assertThatThrownBy(() -> bookService.createBook(bookDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Bad request");
    }

    @Test
    @DisplayName("Создание книги.Не должно пройти успешно.")
    void savedBook_BadRequestException() {
        //given
        BookDto bookDto = new BookDto();
        bookDto.setId(10);

        //when
        when(crudRepository.existsById(anyInt())).thenReturn(true);

        //then
        assertThatThrownBy(() -> bookService.createBook(bookDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("id book entity busy");
    }

    @Test
    @DisplayName("Обновление книги.Не должно пройти успешно.")
    void updateBook_BadRequestException() {
        assertThatThrownBy(() -> bookService.updateBook(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Bad request");
    }

    @Test
    @DisplayName("Обновление книги.Не должно пройти успешно.")
    void updateBook_1_BadRequestException() {
        //given
        BookDto bookDto = new BookDto();
        bookDto.setId(-1);

        //then
        assertThatThrownBy(() -> bookService.updateBook(bookDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Bad request");
    }

    @Test
    @DisplayName("Обновление книги.Не должно пройти успешно.")
    void updateBook_NotFoundException() {
        //given
        BookDto bookDto = new BookDto();
        bookDto.setId(10);

        //when
        when(crudRepository.existsById(anyInt())).thenReturn(false);

        //then
        assertThatThrownBy(() -> bookService.updateBook(bookDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("id book entity not found");
    }
}
