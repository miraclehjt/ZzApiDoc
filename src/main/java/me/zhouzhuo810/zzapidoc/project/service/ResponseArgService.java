package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;

/**
 * Created by admin on 2017/7/22.
 */
public interface ResponseArgService extends BaseService<ResponseArgEntity> {

    void deleteByInterfaceId(String interfaceId);

}
