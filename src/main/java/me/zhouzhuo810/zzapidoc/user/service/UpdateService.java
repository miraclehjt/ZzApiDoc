package me.zhouzhuo810.zzapidoc.user.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.user.entity.UpdateEntity;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;

import java.util.Date;

/**
 * Created by zz on 2017/7/20.
 */
public interface UpdateService extends BaseService<UpdateEntity> {

    BaseResult checkUpdate(int versionCode);

    BaseResult publishVersion(int versionCode, String versionName, String updateInfo, String releaseDate, String userId);
}
