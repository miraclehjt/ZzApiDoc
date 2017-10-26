package me.zhouzhuo810.zzapidoc.version.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.version.entity.VersionProjectEntity;
import me.zhouzhuo810.zzapidoc.version.service.VersionProjectService;
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
@RequestMapping(value = "v1/versionProject")
public class VersionProjectAction extends BaseController<VersionProjectEntity> {
    @Override
    @Resource(name = "versionProjectServiceImpl")
    public void setBaseService(BaseService<VersionProjectEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public VersionProjectService getBaseService() {
        return (VersionProjectService) baseService;
    }


    @ResponseBody
    @RequestMapping(value = "/addVersionProject", method = RequestMethod.POST)
    public BaseResult addVersionProject(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "note") String note,
            @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().addVersionProject(name, note, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/updateVersionProject", method = RequestMethod.POST)
    public BaseResult updateVersionProject(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "note") String note,
            @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().updateVersionProject(projectId, name, note, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteVersionProject", method = RequestMethod.POST)
    public BaseResult deleteVersionProject(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().deleteVersionProject(id, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllVersionProject", method = RequestMethod.GET)
    public BaseResult getAllVersionProject(
            @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().getAllVersionProject(userId);
    }

}
