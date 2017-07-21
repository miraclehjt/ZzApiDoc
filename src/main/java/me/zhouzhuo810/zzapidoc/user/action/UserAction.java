package me.zhouzhuo810.zzapidoc.user.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
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
@RequestMapping(value = "v1/user")
public class UserAction extends BaseController<UserEntity> {


    @Override
    @Resource(name = "userServiceImpl")
    public void setBaseService(BaseService<UserEntity> baseService) {
        this.baseService = baseService;
    }

    public UserService getService() {
        return (UserService) baseService;
    }


    @ResponseBody
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public BaseResult doLogin(
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "password") String password
    ) {
        return getService().doLogin(phone, password);
    }
}
