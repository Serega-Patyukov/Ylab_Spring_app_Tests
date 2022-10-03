package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.entityStorage.BookEntity;
import com.edu.ulab.app.entity.entityStorage.UserEntity;
import com.edu.ulab.app.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class Storage implements CrudRepository<UserEntity, Integer>{

    //todo создать хранилище в котором будут содержаться данные
    // сделать абстракции через которые можно будет производить операции с хранилищем
    // продумать логику поиска и сохранения
    // продумать возможные ошибки
    // учесть, что при сохранении юзера или книги, должен генерироваться идентификатор
    // продумать что у юзера может быть много книг и нужно создать эту связь
    // так же учесть, что методы хранилища принимают другой тип данных - учесть это в абстракции

    // Хранилище книг. Ключи книг < 0.
    private static Map<Integer, BookEntity> storageBookEntities = new HashMap<>();

    // Хранилище юзеров. Ключи юзеров > 0.
    private static Map<Integer, UserEntity> storageUserEntity = new HashMap<>();

    private final UserMapper userMapper;

    public Storage(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserEntity save(UserEntity entity) {
        /*
        Для сохранения юзера и книг в хранилище их id должны быть > 0.
        При сохранении книг их id будут преобразованы в < 0.
         */

        if (existsById(entity.getId())) {

            UserEntity userEntityFromBD = userMapper.userEntityToUserEntity(entity);
            userEntityFromBD.setBookEntityList(storageUserEntity.get(entity.getId()).getBookEntityList());

            entity.getBookEntityList().stream()
                    .filter(Objects::nonNull)
                    .peek(bookEntity -> bookEntity.setId(-1 * bookEntity.getId()))
                    .peek(bookEntity -> bookEntity.setUserId(entity.getId()))
                    .peek(bookEntity -> {
                        if (storageBookEntities.put(bookEntity.getId(), bookEntity) == null) {
                            userEntityFromBD.getBookEntityList().add(bookEntity);
                            //log.info("Created book entity: {}", bookEntity);
                        } else {
                            userEntityFromBD.setBookEntityList(userEntityFromBD.getBookEntityList()
                                    .stream()
                                    .map(bE -> bE.getId() == bookEntity.getId() ? bookEntity : bE)
                                    .collect(Collectors.toList()));
                            //log.info("Update book entity: {}", bookEntity);
                        }
                    })
                    .collect(Collectors.toList());

            //log.info("Update user entity: {}", userEntityFromBD);
            storageUserEntity.put(userEntityFromBD.getId(), userEntityFromBD);
            return userEntityFromBD;
        } else {
            entity.getBookEntityList().stream()
                    .filter(Objects::nonNull)
                    .peek(bookEntity -> bookEntity.setId(-1 * bookEntity.getId()))
                    .peek(bookEntity -> bookEntity.setUserId(entity.getId()))
                    .peek(bookEntity -> storageBookEntities.put(bookEntity.getId(), bookEntity))
                    //.peek(bookEntity -> log.info("Created book entity: {}", bookEntity))
                    .collect(Collectors.toList());

            storageUserEntity.put(entity.getId(), entity);
            //log.info("Created user entity: {}", entity);
            return entity;
        }
    }

    @Override
    public Optional<UserEntity> findById(Integer id) {
        return Optional.of(storageUserEntity.get(id));   // Возвращаем юзера по его id.
    }

    @Override
    public boolean existsById(Integer id) {
        /*
        Если id > 0, то ищем в хранилище юзера с таким id.
        Если id < 0, то ищем в хранилище книгу с таким id.
         */

        if (id > 0) return storageUserEntity.containsKey(id);
        else return storageBookEntities.containsKey(id);
    }

    @Override
    public Iterable<UserEntity> findAll() {
        return storageUserEntity.values().stream().collect(Collectors.toList());   // Возвращаем коллекцию юзеров.
    }

    @Override
    public void deleteById(Integer id) {
        /*
        Если id > 0, то удаляем юзера с таким id и удаляем его книги.
        Если id < 0, то удаляем книгу с таким id.
         */

        if (id > 0 && storageUserEntity.containsKey(id)) {
            storageUserEntity.get(id).getBookEntityList().stream()
                    .peek(bookEntity -> storageBookEntities.remove(bookEntity.getId()))
                    //.peek(bookEntity -> log.info("Delete book entity: {}", bookEntity))
                    .collect(Collectors.toList());

            //log.info("Delete user entity: {}", storageUserEntity.remove(id));
            storageUserEntity.remove(id);
        }

        if (id < 0 && storageBookEntities.containsKey(id)) {
            //log.info("Delete book entity: {}", storageBookEntities.get(id));
            Integer idUserEntity = storageBookEntities.get(id).getUserId();
            storageUserEntity.get(idUserEntity).getBookEntityList().remove(storageBookEntities.remove(id));
        }
    }
}