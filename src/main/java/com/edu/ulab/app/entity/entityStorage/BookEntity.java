package com.edu.ulab.app.entity.entityStorage;

import lombok.Data;

@Data
public class BookEntity {
    private Integer id;

    // ManyToOne
    // Двух стороння связь.
    private Integer userId;
    private String title;
    private String author;
    private long pageCount;
}
