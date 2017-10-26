package me.zhouzhuo810.zzapidoc.version.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.version.entity.VersionEntity;

/**
 * Created by zz on 2017/10/26.
 */
public interface VersionService extends BaseService<VersionEntity> {

    BaseResult addVersion(String projectId, String versionName, int versionCode, String versionDesc, String userId);

    BaseResult updateVersion(String versionId, String projectId, String versionName, int versionCode, String versionDesc, String userId);

    BaseResult deleteVersion(String id, String userId);

    BaseResult getAllVersion(String userId, String projectId);
}
