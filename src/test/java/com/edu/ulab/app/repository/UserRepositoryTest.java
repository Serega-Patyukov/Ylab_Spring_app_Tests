package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.NoSuchElementException;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Получить юзера по title. Число select == 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void findByTitle_thenAssertDmlCount() {
        //Given

        //When
        Person result = userRepository.findByTitle("reader").get();
        userRepository.flush();

        //Then
        assertThat(result.getAge()).isEqualTo(55);
        assertThat(result.getFullName()).isEqualTo("default uer");
        assertThat(result.getTitle()).isEqualTo("reader");

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Попытка получить по title")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void findByTitle_NoSuchElementException() {
        assertThatThrownBy(() -> userRepository.findByTitle("reader").get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("Сохранить юзера. Число select == 1, insert == 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void insertPerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(110);
        person.setTitle("reader 10");
        person.setFullName("Test Test 2");

        //When
        Person result = userRepository.saveAndFlush(person);
        userRepository.flush();

        //Then
        assertThat(result.getAge()).isEqualTo(110);
        assertThat(result.getFullName()).isEqualTo("Test Test 2");
        assertThat(result.getTitle()).isEqualTo("reader 10");

        assertSelectCount(1);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить юзера. Число select == 1, update == 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void updatePerson_thenAssertDmlCount() {
        //Given

        //When
        Person person = userRepository.findByIdForUpdate(1001).get();
        person.setAge(111);
        person.setTitle("update");
        person.setFullName("update");

        //Then
        userRepository.saveAndFlush(person);
        userRepository.flush();

        assertThat(person.getAge()).isEqualTo(111);
        assertThat(person.getFullName()).isEqualTo("update");
        assertThat(person.getTitle()).isEqualTo("update");

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(1);
        assertDeleteCount(0);
    }

    @DisplayName("Получить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void getPerson_thenAssertDmlCount() {
        //Given

        //When
        Person result = userRepository.findById(1001).get();
        userRepository.flush();

        //Then
        assertThat(result.getAge()).isEqualTo(55);
        assertThat(result.getFullName()).isEqualTo("default uer");
        assertThat(result.getTitle()).isEqualTo("reader");

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить всех юзеров. Число select == 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void getAllPerson_thenAssertDmlCount() {
        //Given

        //When
        Person defaultPerson = userRepository.findById(1001).get();
        List<Person> result = (List<Person>) userRepository.findAll();
        userRepository.flush();

        //Then
        assertThat(result).isEqualTo(List.of(defaultPerson));

        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить юзера. Число select == 2, delete == 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void deletePerson_thenAssertDmlCount() {
        //Given

        //When
        userRepository.deleteById(1001);
        userRepository.flush();

        //Then
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @DisplayName("Попытка сохранить пустого юзера")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void savePerson_DataIntegrityViolationException() {
        assertThatThrownBy(() -> {
            Person person = userRepository.findById(1001).get();
            userRepository.flush();
            person.setFullName(null);
            userRepository.saveAndFlush(person);
        })
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("Попытка получить не существующего юзера")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void getPerson_NoSuchElementException() {
        assertThatThrownBy(() -> userRepository.findById(-1).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("Попытка получить юзера с null id")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void getPerson_InvalidDataAccessApiUsageException() {
        assertThatThrownBy(() -> userRepository.findById(null).get())
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @DisplayName("Попытка получить не существующего юзера для обновления")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void updatePerson_NoSuchElementException() {
        assertThatThrownBy(() -> userRepository.findByIdForUpdate(-1).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("Попытка удалить юзера с null id")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void deletePerson_InvalidDataAccessApiUsageException() {
        assertThatThrownBy(() -> userRepository.deleteById(null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }
}