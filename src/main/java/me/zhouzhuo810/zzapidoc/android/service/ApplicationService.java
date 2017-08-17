package me.zhouzhuo810.zzapidoc.android.service;

import me.zhouzhuo810.zzapidoc.android.entity.ApplicationEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;

/**
 * Created by admin on 2017/8/17.
 */
public interface ApplicationService extends BaseService<ApplicationEntity> {

    BaseResult addApplication(String appName, String versionName,
                              String packageName, String logo,
                              String colorMain, int minSDK,
                              int compileSDK, int targetSDK,
                              int versionCode, boolean multiDex,
                              boolean minifyEnabled,
                              String apiId,
                              String userId);


    BaseResult deleteApplication(String id, String userId);

    BaseResult getAllMyApplication(String userId);
}
