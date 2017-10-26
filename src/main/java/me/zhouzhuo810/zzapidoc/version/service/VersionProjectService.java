package me.zhouzhuo810.zzapidoc.version.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.version.entity.VersionProjectEntity;

/**
 * Created by zz on 2017/10/26.
 */
public interface VersionProjectService extends BaseService<VersionProjectEntity> {

    BaseResult addVersionProject(String name, String note, String userId);

    BaseResult updateVersionProject(String projectId,String name, String note, String userId);

    BaseResult deleteVersionProject(String id, String userId);

    BaseResult getAllVersionProject(String userId);
}
