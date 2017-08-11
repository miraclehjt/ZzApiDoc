package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;

/**
 * Created by admin on 2017/7/22.
 */
public interface ResponseArgService extends BaseService<ResponseArgEntity> {

    void deleteByInterfaceId(String interfaceId);
    
    BaseResult addResponseArg(String pid, String name, int type, String projectId, String interfaceId, String note, String userId);

    BaseResult updateResponseArg(String pid, String responseArgId, String name, int type, String interfaceId, String note, String userId);

    BaseResult deleteResponseArg(String id, String userId);

    BaseResult getResponseArgByInterfaceIdAndPid(String interfaceId, String pid, String userId);

    BaseResult getResponseArgDetails(String id, String userId);
}
