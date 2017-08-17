package me.zhouzhuo810.zzapidoc.android.action;

import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.FragmentEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.android.service.FragmentService;
import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/8/17.
 */
@Controller
@RequestMapping(value = "v1/fragment")
public class FragmentAction extends BaseController<FragmentEntity> {

    @Override
    @Resource(name = "fragmentServiceImpl")
    public void setBaseService(BaseService<FragmentEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public FragmentService getBaseService() {
        return (FragmentService) baseService;
    }
}
