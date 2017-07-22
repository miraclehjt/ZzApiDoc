package me.zhouzhuo810.zzapidoc.project.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.ProjectDao;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ProjectEntity;
import me.zhouzhuo810.zzapidoc.project.service.ProjectService;
import me.zhouzhuo810.zzapidoc.project.utils.ProjectUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/22.
 */
@Service
public class ProjectServiceImpl extends BaseServiceImpl<ProjectEntity> implements ProjectService{

    @Resource(name = "userServiceImpl")
    UserService mUserService;

    @Override
    @Resource(name = "projectDaoImpl")
    public void setBaseDao(BaseDao<ProjectEntity> baseDao) {
        super.setBaseDao(baseDao);
    }

    @Override
    public ProjectDao getBaseDao() {
        return (ProjectDao) this.baseDao;
    }

    @Override
    public BaseResult addProject(String name, String note, String property, String userId) {
        UserEntity user = mUserService.get(userId);
        ProjectEntity entity = new ProjectEntity();
        entity.setName(name);
        entity.setNote(note);
        entity.setProperty(property);
        if (user != null) {
            entity.setCreateUserID(user.getId());
            entity.setCreateUserName(user.getName());
        }
        try {
            getBaseDao().save(entity);
            String id = entity.getId();
            MapUtils map = new MapUtils();
            map.put("id", id);
            return new BaseResult(1, "添加成功！", map.build());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败!", new HashMap<String, String>());
        }
    }

    @Override
    public BaseResult deleteProject(String id, String userId) {
        ProjectEntity entity = getBaseDao().get(id);
        if (entity == null) {
            return new BaseResult(0 , "该项目不存在或已被删除！");
        }
        UserEntity user = mUserService.get(userId);
        if (user != null) {
            entity.setModifyUserID(user.getId());
            entity.setModifyUserName(user.getName());
        }
        try {
            update(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getBaseDao().deleteLogicByIds(new String[] {id});
            return new BaseResult(1, "刪除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "刪除失败！");
        }
    }

    @Override
    public BaseResult updateProject(String projectId, String name, String note, String property, String userId) {
        UserEntity user = mUserService.get(userId);
        ProjectEntity entity = getBaseDao().get(projectId);
        if (entity == null)
            return new BaseResult(0 , "该工程不存在或已被删除！");
        entity.setName(name);
        entity.setNote(note);
        entity.setProperty(property);
        if (user != null) {
            entity.setModifyUserID(user.getId());
            entity.setModifyUserName(user.getName());
        }
        try {
            getBaseDao().update(entity);
            return new BaseResult(1, "修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "修改失败!");
        }
    }

    @Override
    public BaseResult getAllProject(String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        List<ProjectEntity> list = getBaseDao().executeCriteria(ProjectUtils.getProjectByUserId(userId));
        if (list == null) {
            return new BaseResult(0, "暂无数据");
        }
        List<Map<String,Object>> result  = new ArrayList<Map<String, Object>>();
        for (ProjectEntity entity : list) {
            MapUtils map = new MapUtils();
            map.put("id", entity.getId());
            map.put("name", entity.getName());
            map.put("note", entity.getNote());
            map.put("property", entity.getProperty());
            map.put("createUserName", entity.getCreateUserName());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }

    @Override
    public BaseResult getProjectDetails(String projectId, String userId) {
        ProjectEntity entity = getBaseDao().get(projectId);
        if (entity == null) {
            return new BaseResult(0, "项目不存在或已被删除！", new HashMap<String,String>());
        }
        MapUtils map = new MapUtils();
        map.put("id", entity.getId());
        map.put("name", entity.getName());
        map.put("property", entity.getProperty());
        map.put("note", entity.getNote());
        map.put("createUserName", entity.getCreateUserName());
        return new BaseResult(1, "ok", map.build());
    }
}
