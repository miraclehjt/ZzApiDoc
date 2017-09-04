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
            @RequestParam(value = "resId") String resId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "defValue") String defValue,
            @RequestParam(value = "hint", required = false) String hint,
            @RequestParam(value = "leftTitleText", required = false) String leftTitleText,
            @RequestParam(value = "rightTitleText", required = false) String rightTitleText,
            @RequestBody(required = false) MultipartFile leftTitleImg,
            @RequestBody(required = false) MultipartFile rightTitleImg,
            @RequestParam(value = "showLeftTitleImg", required = false) boolean showLeftTitleImg,
            @RequestParam(value = "showRightTitleImg", required = false) boolean showRightTitleImg,
            @RequestParam(value = "showLeftTitleText", required = false) boolean showLeftTitleText,
            @RequestParam(value = "showRightTitleText", required = false) boolean showRightTitleText,
            @RequestParam(value = "showLeftTitleLayout", required = false) boolean showLeftTitleLayout,
            @RequestParam(value = "showRightTitleLayout", required = false) boolean showRightTitleLayout,
            @RequestParam(value = "pid", required = false) String pid,
            @RequestParam(value = "background", required = false) String background,
            @RequestParam(value = "width", required = false) int width,
            @RequestParam(value = "height", required = false) int height,
            @RequestParam(value = "weight", required = false) double weight,
            @RequestParam(value = "marginLeft", required = false) int marginLeft,
            @RequestParam(value = "marginRight", required = false) int marginRight,
            @RequestParam(value = "marginTop", required = false) int marginTop,
            @RequestParam(value = "marginBottom", required = false) int marginBottom,
            @RequestParam(value = "paddingLeft", required = false) int paddingLeft,
            @RequestParam(value = "paddingRight", required = false) int paddingRight,
            @RequestParam(value = "paddingTop", required = false) int paddingTop,
            @RequestParam(value = "paddingBottom", required = false) int paddingBottom,
            @RequestParam(value = "gravity", required = false) String gravity,
            @RequestParam(value = "targetActId", required = false) String targetActId,
            @RequestParam(value = "relativeId") String relativeId,
            @RequestParam(value = "targetApiId", required = false) String targetApiId,
            @RequestParam(value = "appId", required = false) String appId,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().addWidget(name, title, resId, type, defValue, hint,
                leftTitleText, rightTitleText, leftTitleImg, rightTitleImg, showLeftTitleImg,
                showRightTitleImg, showLeftTitleText, showRightTitleText, showLeftTitleLayout, showRightTitleLayout,
                pid, background, width, height, weight, marginLeft, marginRight, marginTop, marginBottom, paddingLeft,
                paddingRight, paddingTop, paddingBottom, gravity, targetActId, relativeId, targetApiId, appId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllMyWidget", method = RequestMethod.GET)
    public BaseResult getAllMyWidget(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "pid") String pid,
            @RequestParam(value = "relativeId") String relativeId
    ) {
        return getBaseService().getAllMyWidget(relativeId, pid, userId);
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
