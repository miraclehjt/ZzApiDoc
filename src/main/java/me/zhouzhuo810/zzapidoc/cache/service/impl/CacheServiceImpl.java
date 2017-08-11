package me.zhouzhuo810.zzapidoc.cache.service.impl;

import me.zhouzhuo810.zzapidoc.cache.dao.CacheDao;
import me.zhouzhuo810.zzapidoc.cache.entity.CacheEntity;
import me.zhouzhuo810.zzapidoc.cache.service.CacheService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zz on 2017/8/11.
 */
@Service
public class CacheServiceImpl extends BaseServiceImpl<CacheEntity> implements CacheService{

    @Override
    @Resource(name = "cacheDaoImpl")
    public void setBaseDao(BaseDao<CacheEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public CacheDao getBaseDao() {
        return (CacheDao) baseDao;
    }
}
