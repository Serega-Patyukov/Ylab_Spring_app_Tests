package com.edu.ulab.app.entity.entityStorage;

import lombok.Data;

import java.util.List;

@Data
public class UserEntity {
    private Integer id;
    private String fullName;
    private String title;
    private int age;

    // OneToMany.
    // Двух стороння связь.
    private List<BookEntity> bookEntityList;
}
