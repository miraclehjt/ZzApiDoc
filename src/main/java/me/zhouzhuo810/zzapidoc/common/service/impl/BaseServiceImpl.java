package me.zhouzhuo810.zzapidoc.common.service.impl;


import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public class BaseServiceImpl<E extends BaseEntity> implements BaseService<E> {

    protected BaseDao<E> baseDao;

    @Override
    public BaseDao<E> getBaseDao() {
        // TODO Auto-generated method stub
        return this.baseDao;
    }

    @Override
    public void setBaseDao(BaseDao<E> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public void saveOrUpdate(E entity) throws Exception {
        this.getBaseDao().saveOrUpdate(entity);
    }

    @Override
    public List<E> getAll(String orderBy) {
        return this.getBaseDao().getAll(orderBy);
    }

    @Override
    public List<E> getAll() {
        return this.getBaseDao().getAll();
    }

    @Override
    public E get(String pid) {
        return this.getBaseDao().get(pid);
    }

    @Override
    public void evict(Object entity) {
        this.getBaseDao().evict(entity);

    }

    @Override
    public void save(E entity) throws Exception {
        this.getBaseDao().save(entity);
    }

    @Override
    public void update(E entity) throws Exception {
        this.getBaseDao().update(entity);
    }

    @Override
    public void deleteLogicByIds(String[] pidArray) throws Exception {
        this.getBaseDao().deleteLogicByIds(pidArray);
    }

    @Override
    public List<E> executeCriteria(Criterion[] criteria) {
        return this.getBaseDao().executeCriteria(criteria);
    }

    @Override
    public List<E> executeCriteria(Criterion[] criteria, Order order) {
        return this.getBaseDao().executeCriteria(criteria, order);
    }

    @Override
    public List<E> executeCriteria(Criterion[] criteria, int page, int rows,Order order) {
        return this.getBaseDao().executeCriteria(criteria,page,rows,order);
    }

    @Override
    public int executeCriteriaRow(Criterion[] criteria) {
        return this.getBaseDao().executeCriteriaRow(criteria);
    }

    @Override
    public E executeCriteriaForObject(Criterion[] criteria) {
        return this.getBaseDao().executeCriteriaForObject(criteria);
    }


}
