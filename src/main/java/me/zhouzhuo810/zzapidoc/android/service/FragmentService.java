package me.zhouzhuo810.zzapidoc.android.service;

import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.FragmentEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;

/**
 * Created by admin on 2017/8/17.
 */
public interface FragmentService extends BaseService<FragmentEntity> {

    BaseResult addFragment(String pid, String name, String title, int type, int position, String appId, String activityId, String userId);

    BaseResult getAllMyFragment( String activityId, String pid, String userId);

    BaseResult deleteFragment(String id, String userId);
}
