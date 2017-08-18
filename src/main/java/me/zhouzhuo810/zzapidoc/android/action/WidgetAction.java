package me.zhouzhuo810.zzapidoc.android.action;

import me.zhouzhuo810.zzapidoc.android.entity.WidgetEntity;
import me.zhouzhuo810.zzapidoc.android.entity.WidgetEntity;
import me.zhouzhuo810.zzapidoc.android.service.WidgetService;
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
@RequestMapping(value = "v1/widget")
public class WidgetAction extends BaseController<WidgetEntity> {

    @Override
    @Resource(name = "widgetServiceImpl")
    public void setBaseService(BaseService<WidgetEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public WidgetService getBaseService() {
        return (WidgetService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addWidget", method = RequestMethod.POST)
    public BaseResult addWidget(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "defValue") String defValue,
            @RequestParam(value = "hint", required = false) String hint,
            @RequestParam(value = "leftTitleText", required = false) String leftTitleText,
            @RequestParam(value = "rightTitleText", required = false) String rightTitleText,
            @RequestParam(value = "leftTitleImg", required = false) String leftTitleImg,
            @RequestParam(value = "rightTitleImg", required = false) String rightTitleImg,
            @RequestParam(value = "showLeftTitleImg", required = false) boolean showLeftTitleImg,
            @RequestParam(value = "showRightTitleImg", required = false) boolean showRightTitleImg,
            @RequestParam(value = "showLeftTitleText", required = false) boolean showLeftTitleText,
            @RequestParam(value = "showRightTitleText", required = false) boolean showRightTitleText,
            @RequestParam(value = "showLeftTitleLayout", required = false) boolean showLeftTitleLayout,
            @RequestParam(value = "showRightTitleLayout", required = false) boolean showRightTitleLayout,
            @RequestParam(value = "targetActId", required = false) String targetActId,
            @RequestParam(value = "relativeId") String relativeId,
            @RequestParam(value = "appId", required = false) String appId,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().addWidget(name, title, type, defValue, hint,
                leftTitleText, rightTitleText, leftTitleImg, rightTitleImg, showLeftTitleImg,
                showRightTitleImg, showLeftTitleText, showRightTitleText, showLeftTitleLayout, showRightTitleLayout,
                targetActId, relativeId, appId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllMyWidget", method = RequestMethod.GET)
    public BaseResult getAllMyWidget(
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().getAllMyWidget(userId);
    }


    @ResponseBody
    @RequestMapping(value = "/deleteWidget", method = RequestMethod.POST)
    public BaseResult deleteWidget(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "id") String id
    ) {
        return getBaseService().deleteWidget(id, userId);
    }

}
