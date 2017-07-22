package me.zhouzhuo810.zzapidoc.project.dao;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;

/**
 * Created by admin on 2017/7/22.
 */
public interface ResponseArgDao extends BaseDao<ResponseArgEntity> {

    void deleteByInterfaceId(String interfaceId);

}
