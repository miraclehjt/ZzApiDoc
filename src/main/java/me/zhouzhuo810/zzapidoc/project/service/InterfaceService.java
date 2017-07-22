package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceEntity;
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

    BaseResult getInterfaceDetails(String interfaceId, String userId);

    ResponseEntity<byte[]> download(String projectId, String userId);
}
