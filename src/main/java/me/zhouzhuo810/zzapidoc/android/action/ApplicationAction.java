package me.zhouzhuo810.zzapidoc.android.action;

import me.zhouzhuo810.zzapidoc.android.entity.ApplicationEntity;
import me.zhouzhuo810.zzapidoc.android.service.ApplicationService;
import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

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
            @RequestParam(value = "appName") String appName,
            @RequestParam(value = "versionName") String versionName,
            @RequestParam(value = "packageName") String packageName,
            @RequestParam(value = "logo", required = false) String logo,
            @RequestParam(value = "colorMain", required = false) String colorMain,
            @RequestParam(value = "minSDK") int minSDK,
            @RequestParam(value = "compileSDK") int compileSDK,
            @RequestParam(value = "targetSDK") int targetSDK,
            @RequestParam(value = "versionCode") int versionCode,
            @RequestParam(value = "multiDex", required = false) boolean multiDex,
            @RequestParam(value = "minifyEnabled", required = false) boolean minifyEnabled,
            @RequestParam(value = "apiId", required = false) String apiId,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().addApplication(appName, versionName, packageName, logo,
                colorMain, minSDK, compileSDK, targetSDK, versionCode, multiDex, minifyEnabled, apiId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllMyApplication", method = RequestMethod.GET)
    public BaseResult getAllMyApplication(
            @RequestParam(value = "userId") String userId
    ){
        return getBaseService().getAllMyApplication(userId);
    }


    @ResponseBody
    @RequestMapping(value = "/deleteApplication", method = RequestMethod.POST)
    public BaseResult deleteApplication(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "id") String id
            ){
        return getBaseService().deleteApplication(id, userId);
    }

}