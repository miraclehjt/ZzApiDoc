package me.zhouzhuo810.zzapidoc.common.dao.impl;


import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.utils.GenericUtil;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.annotation.Resource;
import java.util.List;

public class BaseDaoImpl<E extends BaseEntity> extends HibernateTemplate implements BaseDao<E> {

    private Logger log = Logger.getLogger(this.getClass());

    protected Class<E> clazz;

    @Override
    @Resource
    public void setSessionFactory(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    public BaseDaoImpl() {
        try {
            clazz = GenericUtil.getActualClass(this.getClass(), 0);
        } catch (Exception e) {
            log.error("base dao can not get  clazz!", e);
        }
    }

    @Override
    public void saveOrUpdate(E entity) throws Exception {
        this.getSessionFactory().getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public List<E> getAll(String orderBy) {
        if (orderBy == null) {
            orderBy = " createTime ";
        }
        String hql = "from " + this.clazz.getName() + "  where deleteFlag = 0  order by " + orderBy;
        return this.getSessionFactory().getCurrentSession().createQuery(hql).list();
    }

    @Override
    public List<E> getAll() {
        String hql = "from " + this.clazz.getName() + "  where deleteFlag = 0  order by createTime";
        return this.getSessionFactory().getCurrentSession().createQuery(hql).list();
    }

    @Override
    public E queryForObject(String hql, Object[] parameters) {
        Query query = this.getSessionFactory().getCurrentSession().createQuery(hql);
        if (parameters != null) {
            for (int i = 0, len = parameters.length; i < len; i++) {
                query.setParameter(i, parameters[i]);
            }
        }
        return (E) query.uniqueResult();
    }

    @Override
    public List<E> queryForList(String hql, Object[] parameters) {
        Query query = this.getSessionFactory().getCurrentSession().createQuery(hql);
        if (parameters != null) {
            for (int i = 0, len = parameters.length; i < len; i++) {
                query.setParameter(i, parameters[i]);
            }
        }
        return query.list();
    }

    @Override
    public List<E> queryForList(String hql, Object[] parameters, int start, int size) {
        Query query = this.getSessionFactory().getCurrentSession().createQuery(hql);
        if (parameters != null) {
            for (int i = 0, len = parameters.length; i < len; i++) {
                query.setParameter(i, parameters[i]);
            }
        }
        return query.setFirstResult(start).setMaxResults(size).list();
    }

    @Override
    public List<E> queryForList(String hql, Object[] parameters, String inStr, List inList) {
        Query query = this.getSessionFactory().getCurrentSession().createQuery(hql);
        if (parameters != null) {
            for (int i = 0, len = parameters.length; i < len; i++) {
                query.setParameter(i, parameters[i]);
            }
        }
        query.setParameterList(inStr, inList);
        return query.list();
    }

    @Override
    public List<E> queryForList(String hql, Object[] parameters, String inStr, List inList, int start, int size) {
        Query query = this.getSessionFactory().getCurrentSession().createQuery(hql);
        if (parameters != null) {
            for (int i = 0, len = parameters.length; i < len; i++) {
                query.setParameter(i, parameters[i]);
            }
        }
        query.setParameterList(inStr, inList);
        return query.setFirstResult(start).setMaxResults(size).list();
    }

    @Override
    public List<E> executeCriteria(Criterion[] criteria) {
        return executeCriteria(criteria, null);
    }

    @Override
    public List<E> executeCriteria(Criterion[] criteria, Order order) {
        Criteria c = this.getSessionFactory().getCurrentSession().createCriteria(clazz);
        for (Criterion criterion : criteria) {
            c.add(criterion);
        }
        if (order != null)
            c.addOrder(order);
        c.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return c.list();
    }

    @Override
    public List<E> executeCriteria(Criterion[] criteria, int page, int rows, Order order) {
        Criteria c = this.getSessionFactory().getCurrentSession().createCriteria(clazz);
        for (Criterion criterion : criteria) {
            c.add(criterion);
        }
        if (order != null)
            c.addOrder(order);
        return c.setFirstResult(page * rows).setMaxResults(rows).list();
    }

    @Override
    public int executeCriteriaRow(Criterion[] criteria) {
        Criteria c = this.getSessionFactory().getCurrentSession().createCriteria(clazz);
        for (Criterion criterion : criteria) {
            c.add(criterion);
        }
        Long totalCount = (Long) c.setProjection(Projections.rowCount()).uniqueResult();
        return totalCount == null ? 0 : totalCount.intValue();
    }

    @Override
    public E executeCriteriaForObject(Criterion[] criteria) {
        Criteria c = this.getSessionFactory().getCurrentSession().createCriteria(clazz);
        for (Criterion criterion : criteria) {
            c.add(criterion);
        }
        return (E) c.uniqueResult();
    }

    @Override
    public E get(String id) {
        return (E) this.getSessionFactory().getCurrentSession().get(this.clazz, id);
    }

    @Override
    public int getRowCountByDetachedCriteria(DetachedCriteria condition) {
        Criteria criteria = condition.getExecutableCriteria(this.getSessionFactory().getCurrentSession());
        Long totalCount = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
        return totalCount == null ? 0 : totalCount.intValue();
    }

    @Override
    public List<E> findByDetachedCriteria(DetachedCriteria condition, int page, int rows) {
        Criteria criteria = condition.getExecutableCriteria(this.getSessionFactory().getCurrentSession());
        criteria.setFirstResult((page - 1) * rows).setMaxResults(rows);
        criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    public void save(E entity) throws Exception {
        this.getSessionFactory().getCurrentSession().save(entity);
    }

    @Override
    public void update(E entity) throws Exception {
        this.getSessionFactory().getCurrentSession().update(entity);
    }

    @Override
    public void deleteLogicByIds(String[] idArray) throws Exception {
        String hql = "update  " + this.clazz.getName() + " t set   t.deleteFlag = ?  where t.id in ( :ids ) ";

        Query query = this.getSessionFactory().getCurrentSession().createQuery(hql);

        query.setInteger(0, BaseEntity.DELETE_FLAG_YES);

       /* List<String> _tempList = new ArrayList<String>();
        for (int i = 0; i < idArray.length; i++) {
            _tempList.add(idArray[i]);
        }*/

        query.setParameterList("ids", idArray);

        query.executeUpdate();
    }

}
