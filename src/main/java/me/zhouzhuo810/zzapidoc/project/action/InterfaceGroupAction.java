package me.zhouzhuo810.zzapidoc.project.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceGroupEntity;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceGroupEntity;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceGroupService;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/7/22.
 */
@Controller
@RequestMapping(value = "v1/interfaceGroup")
public class InterfaceGroupAction extends BaseController<InterfaceGroupEntity> {

    @Override
    @Resource(name = "interfaceGroupServiceImpl")
    public void setBaseService(BaseService<InterfaceGroupEntity> baseService) {
        this.baseService = baseService;
    }

    public InterfaceGroupService getService() {
        return (InterfaceGroupService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addInterfaceGroup", method = RequestMethod.POST)
    public BaseResult addInterfaceGroup(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().addInterfaceGroup(name, projectId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/updateInterfaceGroup", method = RequestMethod.POST)
    public BaseResult updateInterfaceGroup(
            @RequestParam(value = "InterfaceGroupId") String InterfaceGroupId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().updateInterfaceGroup(InterfaceGroupId, name, projectId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteInterfaceGroup", method = RequestMethod.POST)
    public BaseResult deleteInterfaceGroup(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().deleteInterfaceGroup(id, userId);
    }
}
