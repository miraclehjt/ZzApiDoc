package me.zhouzhuo810.zzapidoc.android.action;

import me.zhouzhuo810.zzapidoc.android.entity.ApplicationEntity;
import me.zhouzhuo810.zzapidoc.android.service.ApplicationService;
import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * Created by admin on 2017/8/17.
 */
@Controller
@RequestMapping(value = "v1/application")
public class ApplicationAction extends BaseController<ApplicationEntity> {

    @Override
    @Resource(name = "applicationServiceImpl")
    public void setBaseService(BaseService<ApplicationEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public ApplicationService getBaseService() {
        return (ApplicationService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addApplication", method = RequestMethod.POST)
    public BaseResult addApplication(
            @RequestParam(value = "chName") String chName,
            @RequestParam(value = "appName") String appName,
            @RequestParam(value = "versionName") String versionName,
            @RequestParam(value = "packageName") String packageName,
            @RequestParam(value = "colorMain", required = false) String colorMain,
            @RequestParam(value = "minSDK") int minSDK,
            @RequestParam(value = "compileSDK") int compileSDK,
            @RequestParam(value = "targetSDK") int targetSDK,
            @RequestParam(value = "versionCode") int versionCode,
            @RequestParam(value = "enableQrCode", required = false) boolean enableQrCode,
            @RequestParam(value = "multiDex", required = false) boolean multiDex,
            @RequestParam(value = "minifyEnabled", required = false) boolean minifyEnabled,
            @RequestParam(value = "apiId", required = false) String apiId,
            @RequestBody(required = false) MultipartFile logo,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().addApplication(chName, appName, versionName, packageName,
                logo, colorMain, minSDK, compileSDK, targetSDK,
                versionCode, enableQrCode, multiDex, minifyEnabled,
                apiId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllMyApplication", method = RequestMethod.GET)
    public BaseResult getAllMyApplication(
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().getAllMyApplication(userId);
    }


    @ResponseBody
    @RequestMapping(value = "/deleteApplication", method = RequestMethod.POST)
    public BaseResult deleteApplication(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "id") String id
    ) {
        return getBaseService().deleteApplication(id, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/downloadApk", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadApk(
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "userId") String userId
    ) throws IOException {
        return getBaseService().downloadApk(appId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/downloadApplication", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadApplication(
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "userId") String userId
    ) throws IOException {
        return getBaseService().downloadApplication(appId, userId);
    }

}
