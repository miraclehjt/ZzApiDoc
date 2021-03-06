package me.zhouzhuo810.zzapidoc.android.service;

import me.zhouzhuo810.zzapidoc.android.entity.ApplicationEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by admin on 2017/8/17.
 */
public interface ApplicationService extends BaseService<ApplicationEntity> {

    BaseResult addApplication(String chName, String appName, String versionName,
                              String packageName, MultipartFile logo,
                              String colorMain, int minSDK,
                              int compileSDK, int targetSDK,
                              int versionCode, boolean enableQrCode,
                              boolean multiDex, boolean minifyEnabled,
                              String apiId,
                              String userId);


    BaseResult deleteApplication(String id, String userId);

    BaseResult getAllMyApplication(String userId);

    ResponseEntity<byte[]> downloadApk(String appId, String userId);

    ResponseEntity<byte[]> downloadApplication(String appId, String userId);

    ResponseEntity<byte[]> downloadAppJson(String appId, String userId);

    BaseResult updateApplication(String appId, String chName, String appName,
                                 String versionName, String packageName,
                                 MultipartFile logo, String colorMain, int minSDK,
                                 int compileSDK, int targetSDK, int versionCode,
                                 boolean enableQrCode, boolean multiDex,
                                 boolean minifyEnabled, String apiId, String userId);

    BaseResult getApplicationDetail(String userId, String appId);
}
