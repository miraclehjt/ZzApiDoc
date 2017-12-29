package me.zhouzhuo810.zzapidoc.project.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.ErrorCodeEntity;
import me.zhouzhuo810.zzapidoc.project.service.ErrorCodeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by zz on 2017/12/29.
 */
@Controller
@RequestMapping(value = "v1/errorCode")
public class ErrorCodeAction extends BaseController<ErrorCodeEntity> {

    @Override
    @Resource(name = "errorCodeServiceImpl")
    public void setBaseService(BaseService<ErrorCodeEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public ErrorCodeService getBaseService() {
        return (ErrorCodeService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addErrorCode", method = RequestMethod.POST)
    public BaseResult addErrorCode(
            @RequestParam(value = "code") int code,
            @RequestParam(value = "note") String note,
            @RequestParam(value = "interfaceId", required = false) String interfaceId,
            @RequestParam(value = "groupId", required = false) String groupId,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "isGlobal", required = false) boolean isGlobal,
            @RequestParam(value = "isGroup", required = false) boolean isGroup,
            @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().addErrorCode(code, note, interfaceId, groupId, projectId, isGlobal, isGroup, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllErrorCode", method = RequestMethod.GET)
    public BaseResult getAllErrorCode(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "groupId", required = false) String groupId,
            @RequestParam(value = "interfaceId", required = false) String interfaceId,
            @RequestParam(value = "global") boolean global,
            @RequestParam(value = "group") boolean group,
            @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().getAllErrorCode(global, group, projectId, groupId, interfaceId, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/deleteErrorCode", method = RequestMethod.POST)
    public BaseResult deleteErrorCode(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().deleteErrorCode(id, userId);
    }

}
