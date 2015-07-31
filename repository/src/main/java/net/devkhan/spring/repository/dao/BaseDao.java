package net.devkhan.spring.repository.dao;

import net.devkhan.spring.repository.model.BaseModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by KHAN on 2015-07-30.
 */
public interface BaseDao<T extends BaseModel> extends CrudRepository<T, Long> {
    @Override
    <S extends T> List<S> save(Iterable<S> entities);

    @Override
    List<T> findAll();

    @Override
    List<T> findAll(Iterable<Long> longs);
}
