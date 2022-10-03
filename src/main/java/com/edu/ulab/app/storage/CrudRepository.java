package com.edu.ulab.app.storage;

import java.util.Optional;

public interface CrudRepository<T, ID> {

    <S extends T> S save(S entity);

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    Iterable<T> findAll();

    void deleteById(ID id);
}
