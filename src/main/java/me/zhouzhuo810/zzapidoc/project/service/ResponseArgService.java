package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by admin on 2017/7/22.
 */
public interface ResponseArgService extends BaseService<ResponseArgEntity> {

    void deleteByInterfaceId(String interfaceId);
    
    BaseResult addResponseArg(String pid, String name, String defValue, int type, String projectId, String interfaceId, String note, String userId, boolean isGlobal);

    BaseResult updateResponseArg(String pid, String responseArgId, String name, String defValue, int type, String interfaceId, String note, String userId, boolean isGlobal);

    BaseResult deleteResponseArg(String id, String userId);

    BaseResult deleteResponseArgWeb(String ids, String userId);

    BaseResult getResponseArgByInterfaceIdAndPid(String interfaceId, String projectId, String pid, boolean global, String userId);

    BaseResult getResponseArgDetails(String id, String userId);

    List<ResponseArgEntity> getGlobalResponseArgs(String projectId);

    BaseResult importResponseArg(String interfaceId, String userId, String pid, String json);

    void parseObj(String projectId, String interfaceId, String userId, String userName, JSONObject obj, final String pid);

    BaseResult updateResponseArgPid(String pid, String responseArgIds, String userId);

    BaseResult getResponseArgByInterfaceId(String interfaceId, String userId);
}
