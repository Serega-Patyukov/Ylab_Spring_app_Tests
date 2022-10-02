package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImplTemplate}
 */

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
class UserServiceImplTemplateTest {

    @InjectMocks
    private UserServiceImplTemplate userServiceImplTemplate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void createUser_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(-1);

        //when

        when(jdbcTemplate.update(
                any(PreparedStatementCreator.class)))
                .thenReturn(1);

        //then

        UserDto userDtoResult = userServiceImplTemplate.createUser(userDto);
        assertTrue(userDtoResult.getId() > 0);
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updateUser_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1);

        //when

        when(jdbcTemplate.update(
                anyString(),
                anyString(),
                anyString(),
                anyInt(),
                anyInt()))
                .thenReturn(1);

        when(jdbcTemplate.queryForObject(
                anyString(),
                any(RowMapper.class),
                anyInt()))
                .thenReturn(userDto);

        //then

        userServiceImplTemplate.updateUser(userDto);
    }

    @Test
    @DisplayName("Обновление пользователя. Не должно пройти успешно.")
    void updateUserException_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(-1);

        //when

        when(jdbcTemplate.queryForObject(
                anyString(),
                any(RowMapper.class),
                anyInt()))
                .thenThrow(new IncorrectResultSizeDataAccessException(-1));

        //then

        assertThrows(BadRequestException.class, () -> userServiceImplTemplate.updateUser(userDto));
    }

    @Test
    @DisplayName("Получение пользователя. Должно пройти успешно.")
    void getUserById_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1);

        //when

        when(jdbcTemplate.queryForObject(
                anyString(),
                any(RowMapper.class),
                anyInt()))
                .thenReturn(userDto);

        //then

        UserDto userDtoResult = userServiceImplTemplate.getUserById(userDto.getId());
        assertEquals(userDto, userDtoResult);
    }

    @Test
    @DisplayName("Получение пользователя. Не должно пройти успешно.")
    void getUserByIdException_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(-1);

        //when

        when(jdbcTemplate.queryForObject(
                anyString(),
                any(RowMapper.class),
                anyInt()))
                .thenThrow(new IncorrectResultSizeDataAccessException(-1));

        //then

        assertThrows(BadRequestException.class, () -> userServiceImplTemplate.getUserById(userDto.getId()));
    }

    @Test
    @DisplayName("Удаление пользователя. Должно пройти успешно.")
    void deleteUserById_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1);

        //when

        when(jdbcTemplate.update(
                anyString(),
                anyInt()))
                .thenReturn(1);

        //then

        userServiceImplTemplate.deleteUserById(userDto.getId());
    }
}