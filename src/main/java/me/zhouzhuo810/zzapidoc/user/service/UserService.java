package me.zhouzhuo810.zzapidoc.user.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zz on 2017/7/20.
 */
public interface UserService extends BaseService<UserEntity> {

    BaseResult doLogin(String phone, String password);

    BaseResult doRegister(String phone, String password, String name, String sex, String email);
}
