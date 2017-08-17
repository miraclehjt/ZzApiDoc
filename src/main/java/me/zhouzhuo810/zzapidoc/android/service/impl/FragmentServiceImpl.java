package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ApplicationDao;
import me.zhouzhuo810.zzapidoc.android.dao.FragmentDao;
import me.zhouzhuo810.zzapidoc.android.entity.ApplicationEntity;
import me.zhouzhuo810.zzapidoc.android.entity.FragmentEntity;
import me.zhouzhuo810.zzapidoc.android.service.FragmentService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/8/17.
 */
@Service
public class FragmentServiceImpl extends BaseServiceImpl<FragmentEntity> implements FragmentService {
    @Override
    @Resource(name = "fragmentDaoImpl")
    public void setBaseDao(BaseDao<FragmentEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public FragmentDao getBaseDao() {
        return (FragmentDao) baseDao;
    }
}
