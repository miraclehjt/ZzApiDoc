package me.zhouzhuo810.zzapidoc.android.action;

import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.WidgetEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.android.service.WidgetService;
import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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


}
