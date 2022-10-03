package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BookServiceImplJpa implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped bookDto: {}", bookDto);

        Person person = new Person();
        person.setId(bookDto.getUserId());

        book.setPerson(person);
        Book bookResponse = bookRepository.save(book);
        log.info("Save book : {}", book);

        BookDto bookDtoResponse = bookMapper.bookToBookDto(bookResponse);
        bookDtoResponse.setUserId(bookDto.getUserId());

        return bookDtoResponse;
    }

    @Override
    @Transactional
    public BookDto updateBook(BookDto bookDto) {
        Book book = bookRepository.findByIdForUpdate(bookDto.getId())
                .orElseThrow(() -> new BadRequestException("id book not found"));
        log.info("Get book from bd: {}", book);

        book = bookMapper.bookDtoToBook(bookDto);
        log.info("Update book: {}", bookDto);

        Person person = new Person();
        person.setId(bookDto.getUserId());
        book.setPerson(person);

        Book bookResponse = bookRepository.save(book);
        log.info("Save update book : {}", bookResponse);

        return bookMapper.bookToBookDto(bookResponse);
    }

    @Override
    public Iterable<BookDto> getBookById(Integer id) {
        return ((List<Book>) bookRepository.findAll()).stream()
                .filter(book -> book.getPerson().getId() == id)
                .map(book -> bookMapper.bookToBookDto(book))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBookById(Integer id) {
        ((List<Book>) bookRepository.findAll()).stream()
                .filter(book -> book.getPerson().getId() == id)
                .peek(book -> log.info("Get book : {}", book))
                .peek(book -> log.info("Delete book : {}", book))
                .peek(book -> bookRepository.deleteById(book.getId()))
                .collect(Collectors.toList());
    }
}
