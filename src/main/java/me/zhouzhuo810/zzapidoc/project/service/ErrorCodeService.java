package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.ErrorCodeEntity;

/**
 * Created by zz on 2017/12/29.
 */
public interface ErrorCodeService extends BaseService<ErrorCodeEntity> {

    BaseResult addErrorCode(int code, String note, String interfaceId, String groupId, String projectId, boolean isGlobal, boolean isGroup, String userId);

    BaseResult getAllErrorCode(boolean global, boolean group, String projectId, String groupId, String interfaceId, String userId);

    BaseResult deleteErrorCode(String id, String userId);
}
