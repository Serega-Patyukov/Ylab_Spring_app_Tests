package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImplJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImplJpa}
 */

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
class UserServiceImplJpaTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImplJpa userServiceImplJpa;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void createUser_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person personMapper  = new Person();
        personMapper.setFullName("test name");
        personMapper.setAge(11);
        personMapper.setTitle("test title");

        Person savedPerson  = new Person();
        savedPerson.setId(1);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(personMapper);
        when(userRepository.save(personMapper)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        //then

        UserDto userDtoResult = userServiceImplJpa.createUser(userDto);
        assertEquals(1, userDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updateUser_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setAge(111);
        userDto.setFullName("test name update");
        userDto.setTitle("test title update");

        Person getPersonFromDb  = new Person();
        getPersonFromDb.setId(1);
        getPersonFromDb.setFullName("test name");
        getPersonFromDb.setAge(11);
        getPersonFromDb.setTitle("test title");

        Person mapperPerson  = new Person();
        mapperPerson.setId(1);
        mapperPerson.setFullName("test name update");
        mapperPerson.setAge(111);
        mapperPerson.setTitle("test title update");

        Person savedPerson  = new Person();
        savedPerson.setId(1);
        savedPerson.setFullName("test name update");
        savedPerson.setAge(111);
        savedPerson.setTitle("test title update");

        UserDto result = new UserDto();
        result.setId(1);
        result.setAge(111);
        result.setFullName("test update");
        result.setTitle("test update");

        //when

        when(userRepository.findByIdForUpdate(userDto.getId())).thenReturn(Optional.of(getPersonFromDb));
        when(userMapper.userDtoToPerson(userDto)).thenReturn(mapperPerson);
        when(userRepository.save(mapperPerson)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        //then

        UserDto userDtoResult = userServiceImplJpa.updateUser(userDto);
        assertEquals(result, userDtoResult);
    }

    @Test
    @DisplayName("Обновление пользователя. Не должно пройти успешно.")
    void updateUserException_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(-1);

        //when

        when(userRepository.findByIdForUpdate(userDto.getId())).thenReturn(Optional.empty());

        //then

        assertThrows(BadRequestException.class, () -> userServiceImplJpa.updateUser(userDto));
    }

    @Test
    @DisplayName("Получение пользователя. Должно пройти успешно.")
    void getUserById_Test() {
        //given

        Person getPersonFromBd  = new Person();
        getPersonFromBd.setId(1);
        getPersonFromBd.setFullName("test name");
        getPersonFromBd.setAge(11);
        getPersonFromBd.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        //when

        when(userRepository.findById(1)).thenReturn(Optional.of(getPersonFromBd));
        when(userMapper.personToUserDto(getPersonFromBd)).thenReturn(result);

        //then

        UserDto userDtoResult = userServiceImplJpa.getUserById(1);
        assertEquals(1, userDtoResult.getId());
    }

    @Test
    @DisplayName("Получение пользователя. Не должно пройти успешно.")
    void getUserByIdException_Test() {
        //when

        when(userRepository.findById(-1)).thenReturn(Optional.empty());

        //then

        assertThrows(BadRequestException.class, () -> userServiceImplJpa.getUserById(-1));
    }

    @Test
    @DisplayName("Удаление пользователя. Должно пройти успешно.")
    void deleteUserById_Test() {
        //given

        Person getPersonFromBd  = new Person();
        getPersonFromBd.setId(1);
        getPersonFromBd.setFullName("test name");
        getPersonFromBd.setAge(11);
        getPersonFromBd.setTitle("test title");

        //when

        when(userRepository.findById(1)).thenReturn(Optional.of(getPersonFromBd));
    }

    @Test
    @DisplayName("Удаление пользователя. Не должно пройти успешно.")
    void deleteUserByIdException_Test() {
        //when

        when(userRepository.findById(-1)).thenReturn(Optional.empty());

        //then

        assertThrows(BadRequestException.class, () -> userServiceImplJpa.deleteUserById(-1));
    }
}