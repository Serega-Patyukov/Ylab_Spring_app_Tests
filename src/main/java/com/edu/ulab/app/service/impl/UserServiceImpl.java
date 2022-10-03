package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.entityStorage.UserEntity;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.storage.CrudRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

//@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final CrudRepository crudRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {

        if (userDto == null) throw new BadRequestException("Bad request");
        if (userDto.getId() < 0) throw new BadRequestException("Bad request");
        if (userDto.getId() != 0 && crudRepository.existsById(userDto.getId())) throw new BadRequestException("id user entity busy");

        Integer id = (int) (Math.random() * 1_000_000) + 1_000_000;

        UserEntity userEntity = userMapper.userDtoToUserEntity(userDto);
        log.info("Mapped userDto: {}", userDto);
        if (userDto.getId() == 0) userEntity.setId(id);
        log.info("Created id user entity: {}", userEntity.getId());
        userEntity.setBookEntityList(new ArrayList<>());
        log.info("Created user entity: {}", userEntity);

        crudRepository.save(userEntity);
        log.info("Save user entity: {}", userEntity);

        userDto.setId(userEntity.getId());
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {

        if (userDto == null) throw new BadRequestException("Bad request");
        if (userDto.getId() < 1) throw new BadRequestException("Bad request");
        if (!crudRepository.existsById(userDto.getId())) throw new NotFoundException("id user entity not found");

        UserEntity userEntity = userMapper.userDtoToUserEntity(userDto);
        log.info("Mapped userDto: {}", userDto);
        userEntity.setBookEntityList(new ArrayList<>());
        log.info("Update user entity: {}", userEntity);

        crudRepository.save(userEntity);
        log.info("Save user entity: {}", userEntity);

        return userDto;
    }

    @Override
    public UserDto getUserById(Integer id) {
        if (!crudRepository.existsById(id)) throw new NotFoundException("id not found");
        UserDto userDto = userMapper.userEntityToUserDto((UserEntity) crudRepository.findById(id).get());
        log.info("Get id user entity: {}", id);
        return userDto;
    }

    @Override
    public void deleteUserById(Integer id) {
        if (!crudRepository.existsById(id)) throw new NotFoundException("id not found");
        UserEntity userEntity = userMapper.userEntityToUserEntity((UserEntity) crudRepository.findById(id).get());
        crudRepository.deleteById(id);
        log.info("Delete user entity: {}", userEntity);
    }
}