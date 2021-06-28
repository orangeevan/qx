//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.haipaite.common.ramcache.orm.impl;

import com.haipaite.common.ramcache.orm.Paging;
import com.haipaite.common.ramcache.orm.Querier;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateQuerier extends HibernateDaoSupport implements Querier {
    public HibernateQuerier() {
    }
    @Override
    public <T> List<T> all(Class<T> clz) {
        return this.getHibernateTemplate().loadAll(clz);
    }
    @Override
    public <T> List<T> list(Class<T> clz, String queryname, Object... params) {
        return (List<T>)this.getHibernateTemplate().findByNamedQuery(queryname, params);
    }
    @Override
    public <E> List<E> list(Class entityClz, Class<E> retClz, String queryname, Object... params) {
        return (List<E>)this.getHibernateTemplate().findByNamedQuery(queryname, params);
    }
    @Override
    public <T> T unique(Class<T> clz, final String queryname, final Object... params) {
        return this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<T>() {
            @Override
            public T doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(queryname);

                for(int i = 0; i < params.length; ++i) {
                    query.setParameter(i, params[i]);
                }

                return (T)query.uniqueResult();
            }
        });
    }
    @Override
    public <E> E unique(Class entityClz, Class<E> retClz, final String queryname, final Object... params) {
        return this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<E>() {
            @Override
            public E doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(queryname);

                for(int i = 0; i < params.length; ++i) {
                    query.setParameter(i, params[i]);
                }

                return (E)query.uniqueResult();
            }
        });
    }
    @Override
    public <T> List<T> paging(Class<T> clz, final String queryname, final Paging paging, final Object... params) {
        return (List)this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<T>>() {
            @Override
            public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(queryname);

                for(int i = 0; i < params.length; ++i) {
                    query.setParameter(i, params[i]);
                }

                query.setFirstResult(paging.getFirst());
                query.setMaxResults(paging.getSize());
                return query.list();
            }
        });
    }
    @Override
    public <E> List<E> paging(Class entityClz, Class<E> retClz, final String queryname, final Paging paging, final Object... params) {
        return (List)this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<E>>() {
            @Override
            public List<E> doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(queryname);

                for(int i = 0; i < params.length; ++i) {
                    query.setParameter(i, params[i]);
                }

                query.setFirstResult(paging.getFirst());
                query.setMaxResults(paging.getSize());
                return query.list();
            }
        });
    }
}
