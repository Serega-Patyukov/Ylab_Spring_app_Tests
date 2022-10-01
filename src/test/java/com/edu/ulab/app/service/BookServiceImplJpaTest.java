package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImplJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImplJpa}
 */

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
class BookServiceImplJpaTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImplJpa bookServiceImplJpa;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void createBook() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        Book mapperBook = new Book();
        mapperBook.setPageCount(1000);
        mapperBook.setTitle("test title");
        mapperBook.setAuthor("test author");

        Person person  = new Person();
        person.setId(1);

        Book book = mapperBook;
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        BookDto result = new BookDto();
        result.setId(1);
        result.setUserId(1);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(mapperBook);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        //then

        BookDto bookDtoResult = bookServiceImplJpa.createBook(bookDto);
        assertEquals(1, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setId(1);
        bookDto.setUserId(1);
        bookDto.setAuthor("test author update");
        bookDto.setTitle("test title update");
        bookDto.setPageCount(1010);

        Person person  = new Person();
        person.setId(1);

        Book getBookFromBd = new Book();
        getBookFromBd.setId(1);
        getBookFromBd.setPageCount(1000);
        getBookFromBd.setTitle("test title");
        getBookFromBd.setAuthor("test author");
        getBookFromBd.setPerson(person);

        Book mapperBook = new Book();
        mapperBook.setId(1);
        mapperBook.setPageCount(1010);
        mapperBook.setTitle("test title update");
        mapperBook.setAuthor("test author update");

        Book book = mapperBook;
        book.setId(1);
        book.setPageCount(1010);
        book.setTitle("test title update");
        book.setAuthor("test author update");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1);
        savedBook.setPageCount(1010);
        savedBook.setTitle("test title update");
        savedBook.setAuthor("test author update");
        savedBook.setPerson(person);

        BookDto result = new BookDto();
        result.setId(1);
        result.setUserId(1);
        result.setAuthor("test author update");
        result.setTitle("test title update");
        result.setPageCount(1010);

        //when

        when(bookRepository.findByIdForUpdate(bookDto.getId())).thenReturn(Optional.of(getBookFromBd));
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(mapperBook);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        //then

        BookDto bookDtoResult = bookServiceImplJpa.createBook(bookDto);
        assertEquals(result, bookDtoResult);
    }

    @Test
    @DisplayName("Обновление книги. Не должно пройти успешно.")
    void updateBookException() {
        //given

        BookDto bookDto = new BookDto();
        bookDto.setId(-1);

        //when

        when(bookRepository.findByIdForUpdate(bookDto.getId())).thenReturn(Optional.empty());

        //then

        assertThrows(BadRequestException.class, () -> bookServiceImplJpa.updateBook(bookDto));
    }

    @Test
    @DisplayName("Получение книги. Должно пройти успешно.")
    void getBookById() {
        //given

        Person person  = new Person();
        person.setId(1);

        Book book0 = new Book();
        book0.setId(1);
        book0.setAuthor("test author");
        book0.setTitle("test title");
        book0.setPageCount(1010);
        book0.setPerson(person);

        Book book1 = new Book();
        book1.setId(2);
        book1.setAuthor("test author new");
        book1.setTitle("test title new");
        book1.setPageCount(1010);
        book1.setPerson(person);

        BookDto bookDto0 = new BookDto();
        bookDto0.setId(1);
        bookDto0.setUserId(1);
        bookDto0.setAuthor("test author");
        bookDto0.setTitle("test title");
        bookDto0.setPageCount(1010);

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(2);
        bookDto1.setUserId(1);
        bookDto1.setAuthor("test author new");
        bookDto1.setTitle("test title new");
        bookDto1.setPageCount(1010);

        //when

        when(bookRepository.findAll()).thenReturn((Iterable<Book>) List.of(book0, book1));
        when(bookMapper.bookToBookDto(book0)).thenReturn(bookDto0);
        when(bookMapper.bookToBookDto(book1)).thenReturn(bookDto1);

        //then

        Iterable<BookDto> resultIterable = bookServiceImplJpa.getBookById(person.getId());
        assertEquals((Iterable<BookDto>) List.of(bookDto0, bookDto1), resultIterable);
    }

    @Test
    @DisplayName("Удаление книги. Должно пройти успешно.")
    void deleteBookById() {
        //given

        Person person  = new Person();
        person.setId(1);

        Book book0 = new Book();
        book0.setId(1);
        book0.setAuthor("test author");
        book0.setTitle("test title");
        book0.setPageCount(1010);
        book0.setPerson(person);

        Book book1 = new Book();
        book1.setId(2);
        book1.setAuthor("test author new");
        book1.setTitle("test title new");
        book1.setPageCount(1010);
        book1.setPerson(person);

        //when

        when(bookRepository.findAll()).thenReturn((Iterable<Book>) List.of(book0, book1));
    }
}