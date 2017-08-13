package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;

import java.util.List;

/**
 * Created by admin on 2017/7/22.
 */
public interface RequestArgService extends BaseService<RequestArgEntity> {

    void deleteByInterfaceId(String interfaceId);

    BaseResult addRequestArg(String pid, String name, int type, String projectId, String interfaceId, String note, String userId, boolean isRequire, boolean isGlobal);

    BaseResult updateRequestArg(String pid, String requestArgId, String name, int type, String interfaceId, String note, String userId, boolean isRequire, boolean isGlobal);

    BaseResult deleteRequestArg(String id, String userId);

    BaseResult getRequestArgByInterfaceIdAndPid(String interfaceId, String pid, String userId);

    BaseResult getRequestArgDetails(String id, String userId);

    List<RequestArgEntity> getGlobalRequestArgs(String projectId);

}
