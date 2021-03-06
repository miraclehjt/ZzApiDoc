package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.result.WebResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.ProjectEntity;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;

import java.util.List;

/**
 * Created by admin on 2017/7/22.
 */
public interface ProjectService extends BaseService<ProjectEntity> {

    BaseResult addProject(String name, String note, String property, String packageName, String userId);

    BaseResult deleteProject(String id, String userId);

    BaseResult deleteProjectWeb(String ids, String userId);

    BaseResult updateProject(String projectId, String name, String note, String property,String packageName, String userId);

    BaseResult getAllProject(String userId);

    WebResult getAllProjectWeb(String userId, int page);

    BaseResult getProjectDetails(String projectId, String userId);

    BaseResult importProject(String json,String property, String userId);

    BaseResult restoreResponseArgFromExample(String projectId, String userId);
}
