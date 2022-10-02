package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public UserDto createUser(UserDto userDto) {

        final String INSERT_SQL = "INSERT INTO ULAB_EDU.PERSON(ID, FULL_NAME, TITLE, AGE) VALUES (?,?,?,?)";
        int randomId = (int) (Math.random() * 1_000_000) + 1_000_000;

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setInt(1, randomId);
                    ps.setString(2, userDto.getFullName());
                    ps.setString(3, userDto.getTitle());
                    ps.setLong(4, userDto.getAge());
                    return ps;
                });

        userDto.setId(randomId);
        log.info("Save person: {}", userDto);

        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {

        getUserById(userDto.getId());   // Проверим, есть ли пользователь с таким id

        final String UPDATE_SQL = "UPDATE ULAB_EDU.PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ? WHERE ID = ?";
        jdbcTemplate.update(UPDATE_SQL,
                userDto.getFullName(),
                userDto.getTitle(),
                userDto.getAge(),
                userDto.getId());

        log.info("Update person: {}", userDto);

        return userDto;
    }

    @Override
    public UserDto getUserById(Integer id) {
        final String SELECT_SQL = "SELECT ID, FULL_NAME, TITLE, AGE FROM ULAB_EDU.PERSON WHERE ID = ?";
        try {
            UserDto userDto = jdbcTemplate.queryForObject(
                    SELECT_SQL,
                    (rs, rowNum) -> {
                        UserDto dto = new UserDto();
                        dto.setId(rs.getInt("ID"));
                        dto.setFullName(rs.getString("FULL_NAME"));
                        dto.setTitle(rs.getString("TITLE"));
                        dto.setAge(rs.getInt("AGE"));
                        return dto;
                    },
                    id);
            log.info("Get id person: {}", id);
            return userDto;
        } catch (IncorrectResultSizeDataAccessException exc) {
            throw new BadRequestException("id person not found");
        }
    }

    @Override
    public void deleteUserById(Integer id) {
        final String DELETE_SQL = "DELETE FROM ULAB_EDU.PERSON WHERE ID = ?";
        int amountDeletePerson = jdbcTemplate.update(DELETE_SQL, id);
        log.info("Amount delete person on id = " + id + ": {}", amountDeletePerson);
    }
}