package me.zhouzhuo810.zzapidoc.project.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceEntity;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceService;
import me.zhouzhuo810.zzapidoc.project.service.RequestArgService;
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
@RequestMapping(value = "v1/requestArg")
public class RequestArgAction extends BaseController<RequestArgEntity> {

    @Override
    @Resource(name = "requestArgServiceImpl")
    public void setBaseService(BaseService<RequestArgEntity> baseService) {
        this.baseService = baseService;
    }

    public RequestArgService getService() {
        return (RequestArgService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addRequestArg", method = RequestMethod.POST)
    public BaseResult addInterface(
            @RequestParam(value = "pid") String pid,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "defValue", required = false) String defValue,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "isRequire") boolean isRequire,
            @RequestParam(value = "isGlobal") boolean isGlobal
    ) {
        return getService().addRequestArg(pid, name, defValue == null ? "" : defValue, type, projectId, interfaceId, note, userId, isRequire, isGlobal);
    }

    @ResponseBody
    @RequestMapping(value = "/updateRequestArg", method = RequestMethod.POST)
    public BaseResult updateRequestArg(
            @RequestParam(value = "requestArgId") String requestArgId,
            @RequestParam(value = "pid") String pid,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "defValue", required = false) String defValue,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "note") String note,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "isRequire") boolean isRequire,
            @RequestParam(value = "isGlobal") boolean isGlobal
    ) {
        return getService().updateRequestArg(pid, requestArgId, name, defValue == null ? "" : defValue, type, interfaceId, note, userId, isRequire, isGlobal);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteRequestArg", method = RequestMethod.POST)
    public BaseResult deleteInterface(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().deleteRequestArg(id, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getRequestArgByInterfaceIdAndPid", method = RequestMethod.GET)
    public BaseResult getRequestArgByInterfaceIdAndPid(
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "pid") String pid,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getRequestArgByInterfaceIdAndPid(interfaceId, projectId, pid, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/getRequestArgDetails", method = RequestMethod.GET)
    public BaseResult getRequestArgDetails(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getRequestArgDetails(id, userId);
    }
}
