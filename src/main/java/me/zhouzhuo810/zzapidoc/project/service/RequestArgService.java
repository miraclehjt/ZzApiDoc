package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;

/**
 * Created by admin on 2017/7/22.
 */
public interface RequestArgService extends BaseService<RequestArgEntity> {

    void deleteByInterfaceId(String interfaceId);

}
