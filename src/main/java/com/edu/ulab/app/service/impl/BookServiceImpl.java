package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.entityStorage.BookEntity;
import com.edu.ulab.app.entity.entityStorage.UserEntity;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.CrudRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//@Service
@Slf4j
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final CrudRepository crudRepository;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    @Override
    public BookDto createBook(BookDto bookDto) {

        if (bookDto == null) throw new BadRequestException("Bad request");
        if (bookDto.getId() < 0) throw new BadRequestException("Bad request");
        if (bookDto.getId() != 0 && crudRepository.existsById(-1 * bookDto.getId())) throw new BadRequestException("id book entity busy");

        Integer id = (int) (Math.random() * 1_000_000) + 1_000_000;

        BookEntity bookEntity = bookMapper.bookDtoToBookEntity(bookDto);
        log.info("Mapped bookDto: {}", bookDto);
        if (bookDto.getId() == 0) bookEntity.setId(id);
        log.info("Created id book entity: {}", bookEntity.getId());
        UserEntity userEntity = userMapper.userEntityToUserEntity((UserEntity) crudRepository.findById(bookDto.getUserId()).get());
        userEntity.setBookEntityList(new ArrayList<>(Arrays.asList(bookEntity)));
        log.info("Created book entity: {}", bookEntity);

        crudRepository.save(userEntity);
        bookEntity.setId(-1 * bookEntity.getId());
        log.info("Save book entity: {}", bookEntity);
        bookEntity.setId(-1 * bookEntity.getId());

        bookDto.setId(-1 * bookEntity.getId());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {

        if (bookDto == null) throw new BadRequestException("Bad request");
        if (bookDto.getId() < 1) throw new BadRequestException("Bad request");
        if (!crudRepository.existsById(-1 * bookDto.getId())) throw new NotFoundException("id book entity not found");

        long isBookInUser = ((UserEntity) crudRepository.findById(bookDto.getUserId()).get()).getBookEntityList()
                .stream()
                .filter(bookEntity -> (- 1 * bookEntity.getId()) == bookDto.getId())
                .count();

        if (isBookInUser == 0) throw new NotFoundException("The user id = " + bookDto.getUserId() + " does not have a book id = " + bookDto.getId());

        BookEntity bookEntity = bookMapper.bookDtoToBookEntity(bookDto);
        log.info("Mapped bookDto: {}", bookDto);
        UserEntity userEntity = userMapper.userEntityToUserEntity((UserEntity) crudRepository.findById(bookDto.getUserId()).get());
        userEntity.setBookEntityList(new ArrayList<>(Arrays.asList(bookEntity)));
        log.info("Update book entity: {}", bookEntity);

        crudRepository.save(userEntity);
        log.info("Save book entity: {}", bookEntity);

        return bookDto;
    }

    @Override
    public List<BookDto> getBookById(Integer id) {
        /*
        Полученный id это id юзера.
         */

        return ((UserEntity) crudRepository.findById(id).get()).getBookEntityList().stream()
                .map(bookEntity -> bookMapper.bookEntityToBookDot(bookEntity))
                .peek(bookDto -> bookDto.setId(- 1 * bookDto.getId()))
                .peek(bookDto -> log.info("Get id book entity: {}", bookDto.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBookById(Integer id) {
        /*
        Полученный id это id юзера.
         */
        if (!crudRepository.existsById(id)) throw new NotFoundException("id user entity not found");

        List<BookEntity> bookEntityList = ((UserEntity) crudRepository.findById(id).get()).getBookEntityList()
                .stream()
                .map(bookEntity -> bookMapper.bookEntityToBookEntity(bookEntity))
                .collect(Collectors.toList());

        bookEntityList.stream()
                .peek(bookEntity -> crudRepository.deleteById(bookEntity.getId()))
                .peek(bookEntity -> bookEntity.setId(-1 * bookEntity.getId()))
                .peek(bookDto -> log.info("Delete book entity: {}", bookDto))
                .collect(Collectors.toList());
    }
}
