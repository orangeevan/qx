package com.haipaite.common.ramcache.orm.impl;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.orm.Accessor;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


public class HibernateAccessor extends HibernateDaoSupport implements Accessor {
    @Override
    public <PK extends Serializable, T extends IEntity> T load(Class<T> clz, PK id) {
        return (T) getHibernateTemplate().get(clz, (Serializable) id);
    }

    @Override
    public <PK extends Serializable, T extends IEntity> PK save(Class<T> clz, T entity) {
        return (PK) getHibernateTemplate().save(entity);
    }

    @Override
    public <PK extends Serializable, T extends IEntity> void remove(Class<T> clz, PK id) {
        T entity = load(clz, id);
        if (entity == null) {
            return;
        }
        getHibernateTemplate().delete(entity);
    }

    @Override
    public <PK extends Serializable, T extends IEntity> void update(Class<T> clz, T entity) {
        getHibernateTemplate().update(entity);
    }

    @Override
    public <PK extends Serializable, T extends IEntity> void batchSave(List<T> entitys) {
        getHibernateTemplate().execute(session -> {
            int size = entitys.size();
            session.beginTransaction();
            for (int i = 0; i < size; i++) {
                IEntity iEntity = entitys.get(i);
                session.save(iEntity);
                if ((i + 1) % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            session.flush();
            session.clear();
            session.getTransaction().commit();
            return Integer.valueOf(size);
        });
    }

    @Override
    public <PK extends Serializable, T extends IEntity> void batchUpdate(List<T> entitys) {
        getHibernateTemplate().execute(session -> {
            int size = entitys.size();
            session.beginTransaction();
            for (int i = 0; i < size; i++) {
                IEntity iEntity = entitys.get(i);
                session.update(iEntity);
                if ((i + 1) % 10 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            session.flush();
            session.clear();
            session.getTransaction().commit();
            return Integer.valueOf(size);
        });
    }

    @Override
    public <PK extends Serializable, T extends IEntity> void batchDelete(List<T> entitys) {
        getHibernateTemplate().execute(session -> {
            int size = entitys.size();
            session.beginTransaction();
            for (int i = 0; i < size; i++) {
                IEntity iEntity = entitys.get(i);
                session.delete(iEntity);
                if ((i + 1) % 1000 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            session.flush();
            session.clear();
            session.getTransaction().commit();
            return Integer.valueOf(size);
        });
    }
}