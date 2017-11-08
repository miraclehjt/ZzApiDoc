package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.result.WebResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ProjectEntity;
import org.springframework.http.ResponseEntity;

/**
 * Created by admin on 2017/7/22.
 */
public interface InterfaceService extends BaseService<InterfaceEntity> {

    BaseResult addInterface(String name, String path, String projectId, String groupId, String httpMethodId, String note,
                            String userId, String requestArgs, String responseArgs);

    BaseResult deleteInterface(String id, String userId);

    BaseResult updateInterface(String interfaceId, String name, String path, String projectId, String groupId, String httpMethodId,
                               String note, String userId, String requestArgs, String responseArgs);

    BaseResult getAllInterface(String projectId, String userId);

    BaseResult getInterfaceByGroupId(String projectId, String groupId, String userId);

    WebResult getInterfaceByGroupIdWeb(String projectId, String groupId, String userId);

    BaseResult getInterfaceDetails(String interfaceId, String userId);

    ResponseEntity<byte[]> download(String projectId, String userId);

    ResponseEntity<byte[]> downloadPdf(String projectId, String userId);

    BaseResult addInterfaceExample(String interfaceId, String example, String userId);

    BaseResult generateEmptyExample(String interfaceId, String userId);

    String convertToJson(ProjectEntity project);

    String convertToJson(ProjectEntity project, InterfaceEntity entity);

    BaseResult setTestFinish(String interfaceId, String userId);

    ResponseEntity<byte[]> downloadApi(String projectId, String userId);

    ResponseEntity<byte[]> downloadInterfaceApi(String projectId, String interfaceId, String userId);
}
