package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.RequestHeaderEntity;

import java.util.List;

/**
 * Created by admin on 2017/8/13.
 */
public interface RequestHeaderService extends BaseService<RequestHeaderEntity> {

    BaseResult addRequestHeader(String name, String value, String note, String projectId, String interfaceId, String userId, boolean isGlobal);

    BaseResult updateRequestHeader(String requestHeaderId, String name, String value, String note, String projectId, String interfaceId, String userId, boolean isGlobal);

    BaseResult deleteRequestHeader(String id, String userId);

    BaseResult getRequestHeaderByInterfaceId(String interfaceId, String userId);

    List<RequestHeaderEntity> getGlobalRequestHeaders(String projectId);
}
