package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.NoSuchElementException;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу и автора. Число select == 2, insert == 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void findAllBadges_thenAssertDmlCount() {

        //Given
        Person person = userRepository.findById(1001).get();
        userRepository.flush();

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(person);

        //When
        Book result = bookRepository.saveAndFlush(book);
        bookRepository.flush();
        userRepository.flush();

        //Then
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");

        assertSelectCount(2);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить книгу. Число select == 1, update == 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBook_thenAssertDmlCount() {

        //Given
        Book book = bookRepository.findById(2002).get();
        book.setAuthor("update");
        book.setTitle("update");
        book.setPageCount(2000);

        //When
        Book result = bookRepository.saveAndFlush(book);
        bookRepository.flush();

        //Then
        assertThat(result.getPageCount()).isEqualTo(2000);
        assertThat(result.getTitle()).isEqualTo("update");
        assertThat(result.getAuthor()).isEqualTo("update");

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(1);
        assertDeleteCount(0);
    }

    @DisplayName("Получить книгу. Число select == 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBook_thenAssertDmlCount() {

        //Given

        //When
        Book book = bookRepository.findById(2002).get();
        bookRepository.flush();

        //Then
        assertThat(book.getPageCount()).isEqualTo(5500);
        assertThat(book.getTitle()).isEqualTo("default book");
        assertThat(book.getAuthor()).isEqualTo("author");

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить все книги. Число select == 3")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllBook_thenAssertDmlCount() {
        //Given

        //When
        Book book0 = bookRepository.findById(2002).get();
        Book book1 = bookRepository.findById(3003).get();
        List<Book> bookListResult = bookRepository.findAll();
        bookRepository.flush();

        //Then
        assertThat(bookListResult).isEqualTo(List.of(book0, book1));

        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить книгу. Число select == 1, delete == 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBook__thenAssertDmlCount() {
        //Given

        //When
        bookRepository.deleteById(2002);
        bookRepository.flush();

        //Then
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @DisplayName("Попытка сохранить пустую книгу")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void saveBook_DataIntegrityViolationException() {
        assertThatThrownBy(() -> {
            Book book = bookRepository.findById(2002).get();
            bookRepository.flush();
            book.setTitle(null);
            bookRepository.saveAndFlush(book);
        })
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("Попытка получить не существующую книгу")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void getBook_NoSuchElementException() {
        assertThatThrownBy(() -> bookRepository.findById(-1).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("Попытка получить книгу с null id")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void getBook_InvalidDataAccessApiUsageException() {
        assertThatThrownBy(() -> bookRepository.findById(null).get())
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @DisplayName("Попытка получить не существующую книгу для обновления")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void updateBook_NoSuchElementException() {
        assertThatThrownBy(() -> bookRepository.findByIdForUpdate(-1).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("Попытка удалить книгу с null id")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void deleteBook_InvalidDataAccessApiUsageException() {
        assertThatThrownBy(() -> bookRepository.deleteById(null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }
}
