package me.zhouzhuo810.zzapidoc.android.action;

import me.zhouzhuo810.zzapidoc.android.entity.ActionEntity;
import me.zhouzhuo810.zzapidoc.android.entity.ActionEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActionService;
import me.zhouzhuo810.zzapidoc.android.service.WidgetService;
import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/8/17.
 */
@Controller
@RequestMapping(value = "v1/action")
public class ActionAction extends BaseController<ActionEntity> {

    @Override
    @Resource(name = "actionServiceImpl")
    public void setBaseService(BaseService<ActionEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public ActionService getBaseService() {
        return (ActionService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addAction", method = RequestMethod.POST)
    public BaseResult addAction(
            @RequestParam(value = "type") int type,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "widgetId") String widgetId,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "msg") String msg,
            @RequestParam(value = "okText", required = false) String okText,
            @RequestParam(value = "cancelText", required = false) String cancelText,
            @RequestParam(value = "hintText", required = false) String hintText,
            @RequestParam(value = "defText", required = false) String defText,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "showOrHide") boolean showOrHide,
            @RequestParam(value = "items", required = false) String items,
            @RequestParam(value = "groupPosition", required = false) int groupPosition,
            @RequestParam(value = "okApiId", required = false) String okApiId,
            @RequestParam(value = "okActId", required = false) String okActId
    ) {
        return getBaseService().addAction(type, name, widgetId, title, msg, okText, cancelText, hintText, defText, showOrHide, items,  okApiId, groupPosition, okActId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/updateAction", method = RequestMethod.POST)
    public BaseResult updateAction(
            @RequestParam(value = "actionId") String actionId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "widgetId") String widgetId,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "msg") String msg,
            @RequestParam(value = "okText", required = false) String okText,
            @RequestParam(value = "cancelText", required = false) String cancelText,
            @RequestParam(value = "hintText", required = false) String hintText,
            @RequestParam(value = "defText", required = false) String defText,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "showOrHide") boolean showOrHide,
            @RequestParam(value = "items", required = false) String items,
            @RequestParam(value = "groupPosition", required = false) int groupPosition,
            @RequestParam(value = "okApiId", required = false) String okApiId,
            @RequestParam(value = "okActId", required = false) String okActId
    ) {
        return getBaseService().updateAction(actionId, type, name, widgetId, title, msg, okText, cancelText, hintText, defText, showOrHide, items, okApiId, groupPosition, okActId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteAction", method = RequestMethod.POST)
    public BaseResult deleteAction(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().deleteAction(id, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllActions", method = RequestMethod.GET)
    public BaseResult getAllActions(
            @RequestParam(value = "widgetId") String widgetId,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().getAllActions(widgetId, userId);
    }

}
