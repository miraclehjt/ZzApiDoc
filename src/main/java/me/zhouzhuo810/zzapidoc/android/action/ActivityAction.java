package me.zhouzhuo810.zzapidoc.android.action;

import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.ApplicationEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.android.service.ApplicationService;
import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/8/17.
 */
@Controller
@RequestMapping(value = "v1/activity")
public class ActivityAction extends BaseController<ActivityEntity> {

    @Override
    @Resource(name = "activityServiceImpl")
    public void setBaseService(BaseService<ActivityEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public ActivityService getBaseService() {
        return (ActivityService) baseService;
    }
}
