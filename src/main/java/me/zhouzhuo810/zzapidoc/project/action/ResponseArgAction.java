package me.zhouzhuo810.zzapidoc.project.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;
import me.zhouzhuo810.zzapidoc.project.service.ResponseArgService;
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
@RequestMapping(value = "v1/responseArg")
public class ResponseArgAction extends BaseController<ResponseArgEntity> {

    @Override
    @Resource(name = "responseArgServiceImpl")
    public void setBaseService(BaseService<ResponseArgEntity> baseService) {
        this.baseService = baseService;
    }

    public ResponseArgService getService() {
        return (ResponseArgService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addResponseArg", method = RequestMethod.POST)
    public BaseResult addResponseArg(
            @RequestParam(value = "pid") String pid,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "defValue", required = false) String defValue,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "isGlobal") boolean isGlobal
    ) {
        return getService().addResponseArg(pid, name, defValue == null ? "" : defValue, type, projectId, interfaceId, note, userId, isGlobal);
    }

    @ResponseBody
    @RequestMapping(value = "/updateResponseArg", method = RequestMethod.POST)
    public BaseResult updateResponseArg(
            @RequestParam(value = "responseArgId") String responseArgId,
            @RequestParam(value = "pid") String pid,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "defValue", required = false) String defValue,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "note") String note,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "isGlobal") boolean isGlobal
    ) {
        return getService().updateResponseArg(pid, responseArgId, name, defValue == null ? "" : defValue, type, interfaceId, note, userId, isGlobal);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteResponseArg", method = RequestMethod.POST)
    public BaseResult deleteInterface(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().deleteResponseArg(id, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getResponseArgByInterfaceIdAndPid", method = RequestMethod.GET)
    public BaseResult getResponseArgByInterfaceIdAndPid(
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "pid") String pid,
            @RequestParam(value = "global") boolean global,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getResponseArgByInterfaceIdAndPid(interfaceId, projectId, pid, global, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/getResponseArgDetails", method = RequestMethod.GET)
    public BaseResult getResponseArgDetails(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getResponseArgDetails(id, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/importResponseArg", method = RequestMethod.POST)
    public BaseResult importResponseArg(
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "json") String json
    ) {
        return getService().importResponseArg(interfaceId, userId, json);
    }


}
