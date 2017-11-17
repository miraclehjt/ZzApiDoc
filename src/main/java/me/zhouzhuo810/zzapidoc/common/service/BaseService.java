package me.zhouzhuo810.zzapidoc.common.service;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Created by Administrator on 2017/6/27.
 */
public interface BaseService<E extends BaseEntity> {

    BaseDao<E> getBaseDao();

    void setBaseDao(BaseDao<E> dao);

    /**
     *
     * @param entity
     */
    void saveOrUpdate(E entity) throws Exception;

    /**
     * 根据orderBy 排序查询所有实体信息
     * @param orderBy
     * @return
     */
    List<E> getAll(String orderBy);

    /**
     * 查询所有实体信息
     * @return
     */
    List<E> getAll();

    /**
     * 根据主键获取对应实体信息
     * @param pid
     * @return
     */
    E get(String pid);

    void evict(Object entity);

    /**
     * 保存 entity
     *
     * @param entity
     */
    void save(E entity) throws Exception;

    /**
     * 实体更新
     * @param entity
     * @throws Exception
     */
    void update(E entity) throws Exception;

    /**
     * 逻辑删除，入参为pid主键数组
     * @param pidArray
     * @throws Exception
     */
    void deleteLogicByIds(String[] pidArray) throws Exception;

    List<E> executeCriteria(Criterion[] criteria);

    List<E> executeCriteria(Criterion[] criteria, Order order);

    List<E> executeCriteria(Criterion[] criteria, int page, int rows, Order order);

    int executeCriteriaRow(Criterion[] criteria);

    E executeCriteriaForObject(Criterion[] criteria);

}
