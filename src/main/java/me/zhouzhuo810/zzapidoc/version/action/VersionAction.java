package me.zhouzhuo810.zzapidoc.version.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.version.entity.VersionEntity;
import me.zhouzhuo810.zzapidoc.version.service.VersionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by zz on 2017/10/26.
 */
@Controller
@RequestMapping(value = "v1/version")
public class VersionAction extends BaseController<VersionEntity> {
    @Override
    @Resource(name = "versionServiceImpl")
    public void setBaseService(BaseService<VersionEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public VersionService getBaseService() {
        return (VersionService) baseService;
    }


    @ResponseBody
    @RequestMapping(value = "/addVersion", method = RequestMethod.POST)
    public BaseResult addVersion(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "versionName") String versionName,
            @RequestParam(value = "versionCode") int versionCode,
            @RequestParam(value = "versionDesc") String versionDesc,
            @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().addVersion(projectId, versionName, versionCode, versionDesc, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/updateVersion", method = RequestMethod.POST)
    public BaseResult updateVersion(
            @RequestParam(value = "versionId") String versionId,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "versionName") String versionName,
            @RequestParam(value = "versionCode") int versionCode,
            @RequestParam(value = "versionDesc") String versionDesc,
            @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().updateVersion(versionId, projectId, versionName, versionCode, versionDesc, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteVersion", method = RequestMethod.POST)
    public BaseResult deleteVersion(
        @RequestParam(value = "id") String id,
        @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().deleteVersion(id, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllVersion", method = RequestMethod.GET)
    public BaseResult getAllVersion(
        @RequestParam(value = "userId") String userId,
        @RequestParam(value = "projectId") String projectId
    ) {

        return getBaseService().getAllVersion(userId, projectId);
    }


}
