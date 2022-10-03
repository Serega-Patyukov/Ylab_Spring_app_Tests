package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.BadRequestException;
import com.edu.ulab.app.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;

//@Service
@Slf4j
@AllArgsConstructor
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public BookDto createBook(BookDto bookDto) {

        final String INSERT_SQL = "INSERT INTO ULAB_EDU.BOOK(ID, TITLE, AUTHOR, PAGE_COUNT, PERSON_ID) VALUES (?,?,?,?,?)";
        int randomId = (int) (Math.random() * 1_000_000) + 1_000_000;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
            ps.setInt(1, randomId);
            ps.setString(2, bookDto.getTitle());
            ps.setString(3, bookDto.getAuthor());
            ps.setLong(4, bookDto.getPageCount());
            ps.setInt(5, bookDto.getUserId());
            return ps;
        });

        bookDto.setId(randomId);
        log.info("Save book : {}", bookDto);

        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {

        ((List<BookDto>) getBookById(bookDto.getUserId())).stream()
                .filter(bD -> bD.getId().equals(bookDto.getId()))
                .findAny()
                .orElseThrow(() -> new BadRequestException("id book not found"));

        final String UPDATE_SQL = "UPDATE ULAB_EDU.BOOK SET PERSON_ID = ?, TITLE = ?, AUTHOR = ?, PAGE_COUNT = ? WHERE ID = ?";
        jdbcTemplate.update(UPDATE_SQL,
                bookDto.getUserId(),
                bookDto.getTitle(),
                bookDto.getAuthor(),
                bookDto.getPageCount(),
                bookDto.getId());

        log.info("Update book : {}", bookDto);

        return bookDto;
    }

    @Override
    public Iterable<BookDto> getBookById(Integer id) {
        final String SELECT_SQL = "SELECT PERSON_ID, ID, TITLE, AUTHOR, PAGE_COUNT FROM ULAB_EDU.BOOK WHERE PERSON_ID = ?";
        return jdbcTemplate.query(
                SELECT_SQL,
                (rs, rowNum) -> {
                    BookDto bookDto = new BookDto();
                    bookDto.setUserId(rs.getInt("PERSON_ID"));
                    bookDto.setId(rs.getInt("ID"));
                    bookDto.setTitle(rs.getString("TITLE"));
                    bookDto.setAuthor(rs.getString("AUTHOR"));
                    bookDto.setPageCount(rs.getLong("PAGE_COUNT"));
                    log.info("Get id book: {}", id);
                    return bookDto;
                },
                id);
    }

    @Override
    public void deleteBookById(Integer id) {
        final String DELETE_SQL = "DELETE FROM ULAB_EDU.BOOK WHERE PERSON_ID = ?";
        int amountDeleteBook = jdbcTemplate.update(DELETE_SQL, id);
        log.info("Amount delete book on personId = " + id + ": {}", amountDeleteBook);
    }
}
