package me.zhouzhuo810.zzapidoc.version.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.version.entity.VersionRecordEntity;

/**
 * Created by zz on 2017/10/26.
 */
public interface VersionRecordService extends BaseService<VersionRecordEntity> {

    BaseResult addVersionRecord(String versionId, String projectId, String note, String userId);

    BaseResult updateVersionRecord(String recordId, String versionId, String projectId, String note, String userId);

    BaseResult deleteVersionRecord(String id, String userId);

    BaseResult getAllVersionRecord(String userId, String versionId);

}
