package me.zhouzhuo810.zzapidoc.project.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.project.dao.ResponseArgDao;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;
import me.zhouzhuo810.zzapidoc.project.service.ResponseArgService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/7/22.
 */
@Service
public class ResponseArgServiceImpl extends BaseServiceImpl<ResponseArgEntity> implements ResponseArgService {
    @Override
    @Resource(name = "responseArgDaoImpl")
    public void setBaseDao(BaseDao<ResponseArgEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public ResponseArgDao getBaseDao() {
        return (ResponseArgDao) this.baseDao;
    }

    @Override
    public void deleteByInterfaceId(String interfaceId) {
        getBaseDao().deleteByInterfaceId(interfaceId);
    }
}
