package net.devkhan.spring.repository.dao;

import com.google.common.collect.Lists;
import net.devkhan.spring.repository.model.BaseModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

/**
 * Created by KHAN on 2015-07-30.
 */
public interface BaseDao<T extends BaseModel> extends CrudRepository<T, Long> {
    @Override
    default <S extends T> List<S> save(Iterable<S> entities) {
        return save(Lists.newArrayList(entities));
    }

    <S extends T> List<S> save(Collection<S> entities);

    @Override
    List<T> findAll();

    @Override
    default List<T> findAll(Iterable<Long> ids) {
        return findAll(Lists.newArrayList(ids));
    }

    List<T> findAll(Collection<Long> ids);
}
