package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.entityStorage.UserEntity;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.storage.CrudRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userServiceIml;

    @Mock
    private CrudRepository crudRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя с полученным id. Должно пройти успешно.")
    void saveUserEntity_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test name");

        UserEntity mapperUserEntity = new UserEntity();
        mapperUserEntity.setId(1);
        mapperUserEntity.setFullName("test name");
        mapperUserEntity.setAge(11);
        mapperUserEntity.setTitle("test name");

        UserEntity savedUserEntity = mapperUserEntity;

        UserDto result = new UserDto();
        result.setId(1);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test name");

        //when

        when(crudRepository.existsById(userDto.getId())).thenReturn(false);
        when(userMapper.userDtoToUserEntity(userDto)).thenReturn(mapperUserEntity);
        when(crudRepository.save(mapperUserEntity)).thenReturn(savedUserEntity);

        //then

        UserDto userDtoResult = userServiceIml.createUser(userDto);
        assertEquals(userDto, userDtoResult);
    }

    @Test
    @DisplayName("Создание пользователя с рандомным id. Должно пройти успешно.")
    void saveUserEntityRandomId_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(0);
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test name");

        UserEntity mapperUserEntity = new UserEntity();
        mapperUserEntity.setId(0);
        mapperUserEntity.setFullName("test name");
        mapperUserEntity.setAge(11);
        mapperUserEntity.setTitle("test name");

        UserEntity savedUserEntity = mapperUserEntity;

        //when

        when(crudRepository.existsById(userDto.getId())).thenReturn(false);
        when(userMapper.userDtoToUserEntity(userDto)).thenReturn(mapperUserEntity);
        when(crudRepository.save(mapperUserEntity)).thenReturn(savedUserEntity);

        //then

        UserDto userDtoResult = userServiceIml.createUser(userDto);
        assertTrue(userDtoResult.getId() > 0);
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updateUserEntity_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test name");

        UserEntity mapperUserEntity = new UserEntity();
        mapperUserEntity.setId(1);
        mapperUserEntity.setFullName("test name");
        mapperUserEntity.setAge(11);
        mapperUserEntity.setTitle("test name");

        UserEntity savedUserEntity = mapperUserEntity;

        //when

        when(crudRepository.existsById(userDto.getId())).thenReturn(true);
        when(userMapper.userDtoToUserEntity(userDto)).thenReturn(mapperUserEntity);
        when(crudRepository.save(mapperUserEntity)).thenReturn(savedUserEntity);

        //then

        userServiceIml.updateUser(userDto);
    }

    @Test
    @DisplayName("Получение пользователя. Должно пройти успешно.")
    void getUserEntity_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test name");

        UserEntity getUserEntityFromBd = new UserEntity();
        getUserEntityFromBd.setId(1);
        getUserEntityFromBd.setFullName("test name");
        getUserEntityFromBd.setAge(11);
        getUserEntityFromBd.setTitle("test name");

        UserDto result = new UserDto();
        result.setId(1);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test name");

        //when

        when(crudRepository.existsById(userDto.getId())).thenReturn(true);
        when(crudRepository.findById(userDto.getId())).thenReturn(Optional.of(getUserEntityFromBd));
        when(userMapper.userEntityToUserDto(getUserEntityFromBd)).thenReturn(result);

        //then

        UserDto userDtoResult = userServiceIml.getUserById(userDto.getId());
        assertEquals(result, userDtoResult);
    }

    @Test
    @DisplayName("Удаление пользователя. Должно пройти успешно.")
    void deleteUserEntity_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test name");

        UserEntity getUserEntityFromBd = new UserEntity();
        getUserEntityFromBd.setId(1);
        getUserEntityFromBd.setFullName("test name");
        getUserEntityFromBd.setAge(11);
        getUserEntityFromBd.setTitle("test name");

        //when

        when(crudRepository.existsById(userDto.getId())).thenReturn(true);
        when(crudRepository.findById(userDto.getId())).thenReturn(Optional.of(getUserEntityFromBd));
        when(userMapper.userEntityToUserEntity(getUserEntityFromBd)).thenReturn(getUserEntityFromBd);

        //then

        userServiceIml.deleteUserById(userDto.getId());
    }

    @Test
    @DisplayName("Создание пользователя.Не должно пройти успешно")
    void saveUserEntity_BadRequestException_Null() {
        assertThatThrownBy(() -> userServiceIml.createUser(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Bad request");
    }

    @Test
    @DisplayName("Создание пользователя.Не должно пройти успешно")
    void saveUserEntity_BadRequestException_1() {
        //given
        UserDto userDto = new UserDto();
        userDto.setId(-1);

        //then
        assertThatThrownBy(() -> userServiceIml.createUser(userDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Bad request");
    }

    @Test
    @DisplayName("Создание пользователя.Не должно пройти успешно")
    void saveUserEntity_BadRequestException() {
        //given
        UserDto userDto = new UserDto();
        userDto.setId(1);

        //when
        when(crudRepository.existsById(userDto.getId())).thenReturn(true);

        //then
        assertThatThrownBy(() -> userServiceIml.createUser(userDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("id user entity busy");
    }

    @Test
    @DisplayName("Обновление пользователя.Не должно пройти успешно")
    void updateUserEntity_BadRequestException_Null() {
        assertThatThrownBy(() -> userServiceIml.updateUser(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Bad request");
    }

    @Test
    @DisplayName("Обновление пользователя.Не должно пройти успешно")
    void updateUserEntity_BadRequestException_1() {
        //given
        UserDto userDto = new UserDto();
        userDto.setId(0);

        //then
        assertThatThrownBy(() -> userServiceIml.updateUser(userDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Bad request");
    }

    @Test
    @DisplayName("Обновление пользователя.Не должно пройти успешно")
    void updateUserEntity_NotFoundException() {
        //given
        UserDto userDto = new UserDto();
        userDto.setId(10);

        //when
        when(crudRepository.existsById(userDto.getId())).thenReturn(false);

        //then
        assertThatThrownBy(() -> userServiceIml.updateUser(userDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("id user entity not found");
    }

    @Test
    @DisplayName("Получение пользователя.Не должно пройти успешно")
    void getUserEntity_NotFoundException() {
        //given
        UserDto userDto = new UserDto();
        userDto.setId(10);

        //when
        when(crudRepository.existsById(userDto.getId())).thenReturn(false);

        //then
        assertThatThrownBy(() -> userServiceIml.getUserById(userDto.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("id not found");
    }

    @Test
    @DisplayName("Удаление пользователя.Не должно пройти успешно")
    void deleteUserEntity_NotFoundException() {
        //given
        UserDto userDto = new UserDto();
        userDto.setId(10);

        //when
        when(crudRepository.existsById(userDto.getId())).thenReturn(false);

        //then
        assertThatThrownBy(() -> userServiceIml.deleteUserById(userDto.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("id not found");
    }
}
