package net.devkhan.spring.repository.dao.hibernate;

import com.google.common.collect.Lists;
import net.devkhan.spring.repository.dao.BaseDao;
import net.devkhan.spring.repository.model.BaseModel;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Hibernate Base Data access object.
 *
 * Created by KHAN on 2015-07-30.
 */
public class AbsHibernateSessionFactoryDao<T extends BaseModel> implements BaseDao<T> {

    @Autowired
    protected SessionFactory sessionFactory;

    private final Class<T> persistenceClass;

    public AbsHibernateSessionFactoryDao(Class<T> persistenceClass) {
        this.persistenceClass = persistenceClass;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    protected Criteria createCriteria() {
        return getSession().createCriteria(persistenceClass);
    }

    @Override
    public <S extends T> S save(S entity) {
        getSession().save(entity);
        return entity;
    }

    @Override
    public <S extends T> List<S> save(Iterable<S> entities) {
        entities.forEach(this::save);
        return Lists.newArrayList(entities);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return (List<T>) createCriteria().list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll(Iterable<Long> ids) {
        return (List<T>) createCriteria().add(Restrictions.in("id", Lists.newArrayList(ids))).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T findOne(Long id) {
        return (T) getSession().get(persistenceClass, id);
    }

    @Override
    public boolean exists(Long id) {
        Object entity = getSession().get(persistenceClass, id);
        return entity != null;
    }

    @Override
    public long count() {
        return (Long) createCriteria().setProjection(Projections.rowCount()).uniqueResult();
    }

    @Override
    public void delete(Long id) {
        try {
            T entity = persistenceClass.newInstance();
            entity.setId(id);
            getSession().delete(entity);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(T entity) {
        getSession().delete(entity);
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        List<T> items = findAll();
        items.parallelStream().forEach(this::delete);
    }
}
