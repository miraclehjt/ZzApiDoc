package me.zhouzhuo810.zzapidoc.project.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.project.dao.RequestArgDao;
import me.zhouzhuo810.zzapidoc.project.dao.ResponseArgDao;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;
import me.zhouzhuo810.zzapidoc.project.service.RequestArgService;
import me.zhouzhuo810.zzapidoc.project.service.ResponseArgService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/7/22.
 */
@Service
public class RequestArgServiceImpl extends BaseServiceImpl<RequestArgEntity> implements RequestArgService {
    @Override
    @Resource(name = "requestArgDaoImpl")
    public void setBaseDao(BaseDao<RequestArgEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public RequestArgDao getBaseDao() {
        return (RequestArgDao) this.baseDao;
    }

    @Override
    public void deleteByInterfaceId(String interfaceId) {
        getBaseDao().deleteByInterfaceId(interfaceId);
    }
}
