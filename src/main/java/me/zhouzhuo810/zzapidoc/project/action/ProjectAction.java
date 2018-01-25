package me.zhouzhuo810.zzapidoc.project.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.result.WebResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.ProjectEntity;
import me.zhouzhuo810.zzapidoc.project.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/7/22.
 */
@Controller
@RequestMapping(value = "v1/project")
public class ProjectAction extends BaseController<ProjectEntity> {

    @Override
    @Resource(name = "projectServiceImpl")
    public void setBaseService(BaseService<ProjectEntity> baseService) {
        this.baseService = baseService;
    }

    public ProjectService getService() {
        return (ProjectService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addProject", method = RequestMethod.POST)
    public BaseResult addProject(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "property") String property,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "packageName", required = false) String packageName,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().addProject(name, note, property, packageName, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/updateProject", method = RequestMethod.POST)
    public BaseResult updateProject(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "property") String property,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "packageName", required = false) String packageName,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().updateProject(projectId, name, note, property, packageName, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteProject", method = RequestMethod.POST)
    public BaseResult deleteProject(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().deleteProject(id, userId);
    }
    @ResponseBody
    @RequestMapping(value = "/deleteProjectWeb", method = RequestMethod.POST)
    public BaseResult deleteProjectWeb(
            @RequestParam(value = "ids") String ids,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().deleteProjectWeb(ids, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllProject", method = RequestMethod.GET)
    public BaseResult getAllProject(
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getAllProject(userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllProjectWeb", method = RequestMethod.GET)
    public WebResult getAllProjectWeb(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "page") int page
    ) {
        return getService().getAllProjectWeb(userId, page);
    }

    @ResponseBody
    @RequestMapping(value = "/getProjectDetails", method = RequestMethod.GET)
    public BaseResult getProjectDetails(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getProjectDetails(projectId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/importProject", method = RequestMethod.POST)
    public BaseResult importProject(
            @RequestParam(value = "json") String json,
            @RequestParam(value = "property") String property,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().importProject(json, property, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/restoreResponseArgFromExample", method = RequestMethod.POST)
    public BaseResult restoreResponseArgFromExample(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().restoreResponseArgFromExample(projectId, userId);
    }
}
