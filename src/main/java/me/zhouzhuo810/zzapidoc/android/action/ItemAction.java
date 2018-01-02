package me.zhouzhuo810.zzapidoc.android.action;

import me.zhouzhuo810.zzapidoc.android.entity.ItemEntity;
import me.zhouzhuo810.zzapidoc.android.service.ItemService;
import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/8/17.
 */
@Controller
@RequestMapping(value = "v1/item")
public class ItemAction extends BaseController<ItemEntity> {

    @Override
    @Resource(name = "itemServiceImpl")
    public void setBaseService(BaseService<ItemEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public ItemService getBaseService() {
        return (ItemService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addItem", method = RequestMethod.POST)
    public BaseResult addItem(
            @RequestParam(value = "type") int type,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "resId") String resId,
            @RequestParam(value = "widgetId") String widgetId,
            @RequestParam(value = "widgetPid") String widgetPid,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().addItem(type, name, resId, widgetId, widgetPid, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/updateItem", method = RequestMethod.POST)
    public BaseResult updateItem(
            @RequestParam(value = "itemId") String itemId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "resId") String resId,
            @RequestParam(value = "widgetId") String widgetId,
            @RequestParam(value = "widgetPid") String widgetPid,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().updateItem(itemId, type, name, resId, widgetId, widgetPid, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteItem", method = RequestMethod.POST)
    public BaseResult deleteItem(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().deleteItem(id, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllItems", method = RequestMethod.GET)
    public BaseResult getAllItems(
            @RequestParam(value = "widgetId") String widgetId,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().getAllItems(widgetId, userId);
    }

}
