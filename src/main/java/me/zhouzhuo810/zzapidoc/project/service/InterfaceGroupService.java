package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.result.WebResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceGroupEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ProjectEntity;
import org.springframework.http.ResponseEntity;

/**
 * Created by admin on 2017/7/22.
 */
public interface InterfaceGroupService extends BaseService<InterfaceGroupEntity> {

    BaseResult addInterfaceGroup(String name, String projectId, String ip, String userId);

    BaseResult updateInterfaceGroup(String interfaceGroupId, String name, String projectId, String ip, String userId);

    BaseResult deleteInterfaceGroup(String id, String userId);

    BaseResult getAllInterfaceGroup(String projectId, String userId);

    WebResult getAllInterfaceGroupWeb(String projectId, int indexPage, String userId);

    ResponseEntity<byte[]> downloadApi(String groupId, String userId);

    String convertToJson(ProjectEntity project, InterfaceGroupEntity group);

    BaseResult deleteInterfaceGroupWeb(String ids, String userId);
}
