package me.zhouzhuo810.zzapidoc.project.dao;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;

/**
 * Created by admin on 2017/7/22.
 */
public interface RequestArgDao extends BaseDao<RequestArgEntity> {
    void deleteByInterfaceId(String interfaceId);
}
