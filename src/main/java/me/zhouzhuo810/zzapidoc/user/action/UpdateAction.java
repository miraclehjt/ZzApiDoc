package me.zhouzhuo810.zzapidoc.user.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.user.entity.UpdateEntity;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UpdateService;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by zz on 2017/7/20.
 */
@Controller
@RequestMapping(value = "v1/update")
public class UpdateAction extends BaseController<UpdateEntity> {


    @Override
    @Resource(name = "updateServiceImpl")
    public void setBaseService(BaseService<UpdateEntity> baseService) {
        this.baseService = baseService;
    }

    public UpdateService getService() {
        return (UpdateService) baseService;
    }


    @ResponseBody
    @RequestMapping(value = "/checkUpdate", method = RequestMethod.POST)
    public BaseResult checkUpdate(
            @RequestParam(value = "versionCode") int versionCode
    ) {
        return getService().checkUpdate(versionCode);
    }

}
