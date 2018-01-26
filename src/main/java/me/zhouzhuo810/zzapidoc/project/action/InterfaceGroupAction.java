package me.zhouzhuo810.zzapidoc.project.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.result.WebResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceGroupEntity;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

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
            @RequestParam(value = "ip") String ip,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().addInterfaceGroup(name, projectId, ip, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllInterfaceGroup", method = RequestMethod.GET)
    public BaseResult getAllInterfaceGroup(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getAllInterfaceGroup(projectId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllInterfaceGroupWeb", method = RequestMethod.GET)
    public WebResult getAllInterfaceGroupWeb(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getAllInterfaceGroupWeb(projectId, page, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/updateInterfaceGroup", method = RequestMethod.POST)
    public BaseResult updateInterfaceGroup(
            @RequestParam(value = "interfaceGroupId") String interfaceGroupId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "ip") String ip,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().updateInterfaceGroup(interfaceGroupId, name, projectId, ip, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteInterfaceGroup", method = RequestMethod.POST)
    public BaseResult deleteInterfaceGroup(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().deleteInterfaceGroup(id, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteInterfaceGroupWeb", method = RequestMethod.POST)
    public BaseResult deleteInterfaceGroupWeb(
            @RequestParam(value = "ids") String ids,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().deleteInterfaceGroupWeb(ids, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/downloadApi", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadApi(
            @RequestParam(value = "groupId") String groupId,
            @RequestParam(value = "userId") String userId
    ) throws IOException {
        return getService().downloadApi(groupId, userId);
    }

}
