package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ActivityDao;
import me.zhouzhuo810.zzapidoc.android.dao.ApplicationDao;
import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.ApplicationEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.android.service.ApplicationService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/8/17.
 */
@Service
public class ActivityServiceImpl extends BaseServiceImpl<ActivityEntity> implements ActivityService {

    @Override
    @Resource(name = "activityDaoImpl")
    public void setBaseDao(BaseDao<ActivityEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public ActivityDao getBaseDao() {
        return (ActivityDao) baseDao;
    }
}
