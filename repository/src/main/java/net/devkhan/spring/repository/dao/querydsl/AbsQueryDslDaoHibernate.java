package net.devkhan.spring.repository.dao.querydsl;

import com.google.common.collect.Lists;
import com.mysema.query.jpa.hibernate.HibernateDeleteClause;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.mysema.query.jpa.hibernate.HibernateUpdateClause;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import net.devkhan.spring.repository.dao.BaseDao;
import net.devkhan.spring.repository.model.BaseModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 * Hibernate QueryDSL
 * Created by KHAN on 2015-07-31.
 */
public abstract class AbsQueryDslDaoHibernate<T extends BaseModel, Q extends EntityPathBase<T>> implements BaseDao<T> {

    @Autowired
    protected SessionFactory sessionFactory;
    protected final Q q;

    protected AbsQueryDslDaoHibernate(Q q) {
        this.q = q;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    protected HibernateQuery getSelectQuery() {
        return new HibernateQuery(getSession()).from(q);
    }

    protected HibernateDeleteClause getDeleteQuery() {
        return new HibernateDeleteClause(getSession(), q);
    }

    protected HibernateUpdateClause getUpdateQuery() {
        return new HibernateUpdateClause(getSession(), q);
    }

    protected abstract NumberPath<Long> getIdPath();

    @Override
    public <S extends T> S save(S entity) {
        getUpdateQuery().set(q, entity).execute();
        return entity;
    }

    @Override
    public <S extends T> List<S> save(Collection<S> entities) {
        entities.forEach(this::save);
        return Lists.newArrayList(entities);
    }

    @Override
    public List<T> findAll() {
        return getSelectQuery().list(q);
    }

    @Override
    public List<T> findAll(Collection<Long> ids) {
        return getSelectQuery().where(getIdPath().in(ids)).list(q);
    }

    @Override
    public T findOne(Long id) {
        return getSelectQuery().where(getIdPath().eq(id)).uniqueResult(q);
    }

    @Override
    public boolean exists(Long id) {
        return getSelectQuery().where(getIdPath().eq(id)).exists();
    }

    @Override
    public long count() {
        return getSelectQuery().count();
    }

    @Override
    public void delete(Long id) {
        getDeleteQuery().where(getIdPath().eq(id)).execute();
    }

    @Override
    public void delete(T entity) {
        getDeleteQuery().where(q.eq(entity)).execute();
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        getDeleteQuery().execute();
    }

}
