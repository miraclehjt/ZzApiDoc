package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ActivityDao;
import me.zhouzhuo810.zzapidoc.android.dao.WidgetDao;
import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.WidgetEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.android.service.WidgetService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/8/17.
 */
@Service
public class WidgetServiceImpl extends BaseServiceImpl<WidgetEntity> implements WidgetService {

    @Override
    @Resource(name = "widgetDaoImpl")
    public void setBaseDao(BaseDao<WidgetEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public WidgetDao getBaseDao() {
        return (WidgetDao) baseDao;
    }
}
