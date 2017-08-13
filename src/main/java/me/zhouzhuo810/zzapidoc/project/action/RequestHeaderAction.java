package me.zhouzhuo810.zzapidoc.project.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.RequestHeaderEntity;
import me.zhouzhuo810.zzapidoc.project.service.RequestHeaderService;
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
@RequestMapping(value = "v1/requestHeader")
public class RequestHeaderAction extends BaseController<RequestHeaderEntity> {

    @Override
    @Resource(name = "requestHeaderServiceImpl")
    public void setBaseService(BaseService<RequestHeaderEntity> baseService) {
        this.baseService = baseService;
    }

    public RequestHeaderService getService() {
        return (RequestHeaderService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addRequestHeader", method = RequestMethod.POST)
    public BaseResult addInterface(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "isGlobal") boolean isGlobal
    ) {
        return getService().addRequestHeader(name, value, note, projectId, interfaceId, userId, isGlobal);
    }

    @ResponseBody
    @RequestMapping(value = "/updateRequestHeader", method = RequestMethod.POST)
    public BaseResult updateRequestHeader(
            @RequestParam(value = "requestHeaderId") String requestHeaderId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "isGlobal") boolean isGlobal
    ) {
        return getService().updateRequestHeader(requestHeaderId, name, value, note, projectId, interfaceId, userId, isGlobal);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteRequestHeader", method = RequestMethod.POST)
    public BaseResult deleteInterface(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().deleteRequestHeader(id, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getRequestHeaderByInterfaceId", method = RequestMethod.GET)
    public BaseResult getRequestHeaderByInterfaceIdAndPid(
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getRequestHeaderByInterfaceId(interfaceId, userId);
    }

}
