package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceGroupEntity;

/**
 * Created by admin on 2017/7/22.
 */
public interface InterfaceGroupService extends BaseService<InterfaceGroupEntity> {

    BaseResult addInterfaceGroup(String name, String projectId, String userId);

    BaseResult updateInterfaceGroup(String interfaceGroupId, String name, String projectId, String userId);

    BaseResult deleteInterfaceGroup(String id, String userId);
}
