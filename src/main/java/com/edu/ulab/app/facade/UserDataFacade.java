package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;

import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserDataFacade {
    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserService userService,
                          BookService bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        if (userBookRequest.getUserRequest() == null) throw new BadRequestException("Bad request");
        log.info("Got user book create request: {}", userBookRequest);

        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Integer> bookIdList = new ArrayList<>();

        if (userBookRequest.getBookRequests() != null) {
            bookIdList = userBookRequest.getBookRequests()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(bookMapper::bookRequestToBookDto)
                    .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                    .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                    .map(bookService::createBook)
                    .peek(createdBook -> log.info("Created book: {}", createdBook))
                    .map(BookDto::getId)
                    .toList();
            log.info("Collected book ids: {}", bookIdList);
        }

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest) {
        if (userBookRequest.getUserRequest() == null) throw new BadRequestException("Bad request");
        log.info("Got user book update request: {}", userBookRequest);

        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.updateUser(userDto);
        log.info("Update user: {}", createdUser);

        List<Integer> bookIdList = new ArrayList<>();

        if (userBookRequest.getBookRequests() != null) {
            bookIdList = userBookRequest.getBookRequests()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(bookMapper::bookRequestToBookDto)
                    .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                    .peek(mappedBookDto -> log.info("Mapped book: {}", mappedBookDto))
                    .map(bookService::updateBook)
                    .peek(createdBook -> log.info("Update book: {}", createdBook))
                    .map(BookDto::getId)
                    .toList();
            log.info("Collected book ids: {}", bookIdList);
        }

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse getUserWithBooks(Integer userId) {        log.info("Got user book get request: {}", userId);

        UserDto userDto = userService.getUserById(userId);
        log.info("Mapped userDto response: {}", userDto);

        List<Integer> bookDtoList = ((List<BookDto>) bookService.getBookById(userId))
                .stream()
                .peek(bookDto -> log.info("Mapped bookDto response: {}", bookDto))
                .map(bookDto -> bookDto.getId())
                .collect(Collectors.toList());

        return UserBookResponse.builder()
                .userId(userDto.getId())
                .booksIdList(bookDtoList)
                .build();
    }

    public void deleteUserWithBooks(Integer userId) {
        log.info("Got user book delete request: userId {}", userId);
        bookService.deleteBookById(userId);
        userService.deleteUserById(userId);
    }
}
