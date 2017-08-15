package me.zhouzhuo810.zzapidoc.project.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.ProjectDao;
import me.zhouzhuo810.zzapidoc.project.entity.*;
import me.zhouzhuo810.zzapidoc.project.service.*;
import me.zhouzhuo810.zzapidoc.project.utils.ProjectUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
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
public class ProjectServiceImpl extends BaseServiceImpl<ProjectEntity> implements ProjectService {

    @Resource(name = "userServiceImpl")
    UserService mUserService;

    @Resource(name = "interfaceGroupServiceImpl")
    InterfaceGroupService mInterfaceGroupService;

    @Resource(name = "interfaceServiceImpl")
    InterfaceService mInterfaceService;

    @Resource(name = "requestArgServiceImpl")
    RequestArgService mRequestArgService;

    @Resource(name = "requestHeaderServiceImpl")
    RequestHeaderService mRequestHeaderService;

    @Resource(name = "responseArgServiceImpl")
    ResponseArgService mResponseArgService;

    @Resource(name = "dictionaryServiceImpl")
    DictionaryService mDictionaryService;


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
        if (user == null) {
            return new BaseResult(0, "用户不合法！", new HashMap<String, String>());
        }
        ProjectEntity entity = new ProjectEntity();
        entity.setName(name);
        entity.setNote(note);
        entity.setProperty(property);
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
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
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！", new HashMap<String, String>());
        }
        ProjectEntity entity = getBaseDao().get(id);
        if (entity == null) {
            return new BaseResult(0, "该项目不存在或已被删除！");
        }
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        try {
            update(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getBaseDao().deleteLogicByIds(new String[]{id});
            return new BaseResult(1, "刪除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "刪除失败！");
        }
    }

    @Override
    public BaseResult updateProject(String projectId, String name, String note, String property, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！", new HashMap<String, String>());
        }
        ProjectEntity entity = getBaseDao().get(projectId);
        if (entity == null)
            return new BaseResult(0, "该工程不存在或已被删除！");
        entity.setName(name);
        entity.setNote(note);
        entity.setProperty(property);
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
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
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (ProjectEntity entity : list) {
            MapUtils map = new MapUtils();
            map.put("id", entity.getId());
            map.put("name", entity.getName());
            map.put("note", entity.getNote());
            map.put("property", entity.getProperty());
            map.put("interfaceNo", entity.getInterfaceNo());
            map.put("createUserName", entity.getCreateUserName());
            map.put("createTime", DataUtils.formatDate(entity.getCreateTime()));
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }

    @Override
    public BaseResult getProjectDetails(String projectId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！", new HashMap<String, String>());
        }
        ProjectEntity entity = getBaseDao().get(projectId);
        if (entity == null) {
            return new BaseResult(0, "项目不存在或已被删除！", new HashMap<String, String>());
        }
        MapUtils map = new MapUtils();
        map.put("id", entity.getId());
        map.put("name", entity.getName());
        map.put("property", entity.getProperty());
        map.put("note", entity.getNote());
        map.put("createUserName", entity.getCreateUserName());
        return new BaseResult(1, "ok", map.build());
    }

    @Override
    public BaseResult importProject(String json, String property, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！", new HashMap<String, String>());
        }
        try {
            Gson gson = new GsonBuilder().create();
            ApiEntity api = gson.fromJson(json, ApiEntity.class);

            if (api != null) {

                if (api.getProject() == null) {
                    return new BaseResult(0, "导入失败！JSON格式不正确");
                }
                //开始导入项目
                ProjectEntity mPro = new ProjectEntity();
                String proUserId = api.getProject().getUserId();
                if (proUserId != null) {
                    UserEntity proUser = mUserService.get(proUserId);
                    if (proUser == null) {
                        mPro.setCreateUserID(user.getId());
                        mPro.setCreateUserName(user.getName());
                    } else {
                        mPro.setCreateUserID(proUser.getId());
                        mPro.setCreateUserName(proUser.getName());
                    }
                } else {
                    mPro.setCreateUserID(user.getId());
                    mPro.setCreateUserName(user.getName());
                }
                String proName = api.getProject().getName();
                String proNote = api.getProject().getDescription();
                mPro.setName(proName);
                mPro.setProperty(property);
                mPro.setNote(proNote);
                getBaseDao().save(mPro);

                //项目导入完毕

                /*开始导入分组*/
                List<ApiEntity.ModulesBean> modules = api.getModules();
                if (modules != null) {
                    for (int i = 0; i < modules.size(); i++) {
                        ApiEntity.ModulesBean modulesBean = modules.get(i);
                        //全局请求参数
                        String requestArgs = modulesBean.getRequestArgs();
                        if (requestArgs != null && !requestArgs.equals("[]")) {
                            ArgEntity argEntity = gson.fromJson("{\"data\":" + requestArgs + "}", ArgEntity.class);
                            if (argEntity != null && argEntity.getData() != null) {
                                //导入全局请求参数
                                List<ArgEntity.DataBean> data1 = argEntity.getData();
                                for (int i4 = 0; i4 < data1.size(); i4++) {
                                    ArgEntity.DataBean dataBean1 = data1.get(i4);
                                    RequestArgEntity mReqArg = new RequestArgEntity();
                                    mReqArg.setProjectId(mPro.getId());
                                    mReqArg.setInterfaceId("");
                                    mReqArg.setGlobal(true);
                                    mReqArg.setPid("0");
                                    mReqArg.setCreateUserID(user.getId());
                                    mReqArg.setCreateUserName(user.getName());
                                    mReqArg.setRequire(dataBean1.getRequire().equals("true"));
                                    mReqArg.setName(dataBean1.getName());
                                    mReqArg.setNote(dataBean1.getDescription());
                                    String type = dataBean1.getType();
                                    switch (type) {
                                        case "string":
                                            mReqArg.setTypeId(0);
                                            break;
                                        case "number":
                                            mReqArg.setTypeId(1);
                                            break;
                                        case "object":
                                            mReqArg.setTypeId(2);
                                            break;
                                        case "array[object]":
                                            mReqArg.setTypeId(3);
                                            break;
                                        case "array[string]":
                                            mReqArg.setTypeId(4);
                                            break;
                                        case "array":
                                            mReqArg.setTypeId(5);
                                            break;
                                        case"file":
                                            mReqArg.setTypeId(6);
                                            break;

                                    }
                                    mRequestArgService.save(mReqArg);
                                }
                                //导入全局请求参数完毕
                            }
                        }
                        //全局返回参数
                        String globalRes = modulesBean.getResponseArgs();
                        if (globalRes != null && !globalRes.equals("[]")) {
                            ArgEntity responseArg = gson.fromJson("{\"data\":" + globalRes + "}", ArgEntity.class);
                            if (responseArg != null && responseArg.getData() != null) {
                                //导入全局返回参数
                                List<ArgEntity.DataBean> data1 = responseArg.getData();
                                for (ArgEntity.DataBean dataBean1 : data1) {
                                    ResponseArgEntity mResArg = new ResponseArgEntity();
                                    mResArg.setProjectId(mPro.getId());
                                    mResArg.setInterfaceId("");
                                    mResArg.setGlobal(true);
                                    mResArg.setPid("0");
                                    mResArg.setCreateUserID(user.getId());
                                    mResArg.setCreateUserName(user.getName());
                                    mResArg.setName(dataBean1.getName());
                                    mResArg.setNote(dataBean1.getDescription());
                                    String type = dataBean1.getType();
                                    switch (type) {
                                        case "string":
                                            mResArg.setTypeId(0);
                                            break;
                                        case "number":
                                            mResArg.setTypeId(1);
                                            break;
                                        case "object":
                                            mResArg.setTypeId(2);
                                            break;
                                        case "array[object]":
                                            mResArg.setTypeId(3);
                                            break;
                                        case "array[string]":
                                            mResArg.setTypeId(4);
                                            break;
                                        case "array":
                                            mResArg.setTypeId(5);
                                            break;
                                        case"file":
                                            mResArg.setTypeId(6);
                                            break;
                                    }
                                    mResponseArgService.save(mResArg);
                                    importChildResponseArgs(dataBean1.getChildren(), mPro.getId(), "", user.getId(), user.getName(), true, mResArg.getId());
                                }
                                //导入全局返回参数完毕
                            }
                        }

                        /*全局请求头*/
                        String reqHeader = modulesBean.getRequestHeaders();
                        HeaderEntity globalHead = gson.fromJson("{\"data\":" + reqHeader + "}", HeaderEntity.class);
                        if (globalHead != null && globalHead.getData() != null) {
                            List<HeaderEntity.HeaderDataEntity> data = globalHead.getData();
                            for (HeaderEntity.HeaderDataEntity he : data) {
                                RequestHeaderEntity h = new RequestHeaderEntity();
                                h.setName(he.getName());
                                h.setValue(he.getDefaultValue());
                                h.setNote(he.getDescription());
                                h.setGlobal(true);
                                h.setInterfaceId("");
                                h.setProjectId(mPro.getId());
                                mRequestHeaderService.save(h);
                            }
                        }

                        //接口分组
                        List<ApiEntity.ModulesBean.FoldersBean> folders = modulesBean.getFolders();
                        if (folders != null) {

                            //开始导入分组
                            for (int i1 = 0; i1 < folders.size(); i1++) {
                                ApiEntity.ModulesBean.FoldersBean foldersBean = folders.get(i1);

                                InterfaceGroupEntity mGroup = new InterfaceGroupEntity();
                                mGroup.setName(foldersBean.getName());
                                mGroup.setIp(foldersBean.getIp());
                                mGroup.setProjectId(mPro.getId());
                                mGroup.setCreateUserID(user.getId());
                                mGroup.setCreateUserName(user.getName());
                                mInterfaceGroupService.save(mGroup);

                                //接口
                                List<ApiEntity.ModulesBean.FoldersBean.ChildrenBean> children = foldersBean.getChildren();
                                if (children != null) {
                                    /*开始导入接口*/
                                    for (ApiEntity.ModulesBean.FoldersBean.ChildrenBean childrenBean : children) {


                                        InterfaceEntity mInterface = new InterfaceEntity();
                                        mInterface.setGroupId(mGroup.getId());
                                        mInterface.setGroupName(mGroup.getName());
                                        mInterface.setProjectId(mPro.getId());
                                        mInterface.setExample(childrenBean.getExample());
                                        mInterface.setName(childrenBean.getName());
                                        mInterface.setCreateUserID(user.getId());
                                        mInterface.setCreateUserName(user.getName());
                                        mInterface.setNote(childrenBean.getDescription());
                                        mInterface.setProjectName(mPro.getName());
                                        String method = childrenBean.getRequestMethod();
                                        List<DictionaryEntity> dics = mDictionaryService.executeCriteria(new Criterion[]{
                                                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                                                Restrictions.eq("name", method)}
                                        );
                                        if (dics != null && dics.size() > 0) {
                                            DictionaryEntity entity = dics.get(0);
                                            mInterface.setHttpMethodId(entity.getId());
                                        } else {
                                            DictionaryEntity dic = new DictionaryEntity();
                                            dic.setType("method");
                                            dic.setName(method);
                                            dic.setPosition(0);
                                            dic.setPid("0");
                                            mDictionaryService.save(dic);
                                            mInterface.setHttpMethodId(dic.getId());
                                        }
                                        mInterface.setPath(childrenBean.getUrl());
                                        mInterfaceService.save(mInterface);

                                        //接口返回数据
                                        String responseData = childrenBean.getResponseArgs();
                                        if (responseData != null && !responseData.equals("[]")) {
                                            ArgEntity responseArg = gson.fromJson("{\"data\":" + responseData + "}", ArgEntity.class);
                                            if (responseArg != null && responseArg.getData() != null) {
                                                //导入返回参数
                                                List<ArgEntity.DataBean> data1 = responseArg.getData();
                                                for (ArgEntity.DataBean dataBean1 : data1) {
                                                    ResponseArgEntity mResArg = new ResponseArgEntity();
                                                    mResArg.setProjectId(mPro.getId());
                                                    mResArg.setInterfaceId(mInterface.getId());
                                                    mResArg.setGlobal(false);
                                                    mResArg.setPid("0");
                                                    mResArg.setCreateUserID(user.getId());
                                                    mResArg.setCreateUserName(user.getName());
                                                    mResArg.setName(dataBean1.getName());
                                                    mResArg.setNote(dataBean1.getDescription());
                                                    String type = dataBean1.getType();
                                                    switch (type) {
                                                        case "string":
                                                            mResArg.setTypeId(0);
                                                            break;
                                                        case "number":
                                                            mResArg.setTypeId(1);
                                                            break;
                                                        case "object":
                                                            mResArg.setTypeId(2);
                                                            break;
                                                        case "array[object]":
                                                            mResArg.setTypeId(3);
                                                            break;
                                                        case "array[string]":
                                                            mResArg.setTypeId(4);
                                                            break;
                                                        case "array":
                                                            mResArg.setTypeId(5);
                                                            break;
                                                        case"file":
                                                            mResArg.setTypeId(6);
                                                            break;
                                                    }
                                                    mResponseArgService.save(mResArg);
                                                    importChildResponseArgs(dataBean1.getChildren(), mPro.getId(), mInterface.getId(), user.getId(), user.getName(), false, mResArg.getId());
                                                }
                                                //导入返回参数完毕
                                            }
                                        }

                                        //请求头
                                        String requestHeaders = childrenBean.getRequestHeaders();
                                        HeaderEntity headers = gson.fromJson("{\"data\":" + requestHeaders + "}", HeaderEntity.class);
                                        if (headers != null && headers.getData() != null) {
                                            List<HeaderEntity.HeaderDataEntity> data = headers.getData();
                                            for (HeaderEntity.HeaderDataEntity he : data) {
                                                RequestHeaderEntity h = new RequestHeaderEntity();
                                                h.setName(he.getName());
                                                h.setValue(he.getDefaultValue());
                                                h.setNote(he.getDescription());
                                                h.setGlobal(false);
                                                h.setInterfaceId(mInterface.getId());
                                                h.setProjectId(mPro.getId());
                                                mRequestHeaderService.save(h);
                                            }
                                        }

                                        //TODO 创建实体类
                                        //接口请求参数
                                        String requestArgs1 = childrenBean.getRequestArgs();
                                        ArgEntity argEntity1 = gson.fromJson("{\"data\":" + requestArgs1 + "}", ArgEntity.class);
                                        if (argEntity1 != null && argEntity1.getData() != null) {
                                            //有参数
                                            List<ArgEntity.DataBean> data = argEntity1.getData();
                                            for (ArgEntity.DataBean aData : data) {
                                                RequestArgEntity mReqArg = new RequestArgEntity();
                                                mReqArg.setProjectId(mPro.getId());
                                                mReqArg.setInterfaceId(mInterface.getId());
                                                mReqArg.setGlobal(false);
                                                mReqArg.setPid("0");
                                                mReqArg.setCreateUserID(user.getId());
                                                mReqArg.setCreateUserName(user.getName());
                                                mReqArg.setRequire(aData.getRequire().equals("true"));
                                                mReqArg.setName(aData.getName());
                                                mReqArg.setNote(aData.getDescription());
                                                String type = aData.getType();
                                                switch (type) {
                                                    case "string":
                                                        mReqArg.setTypeId(0);
                                                        break;
                                                    case "number":
                                                        mReqArg.setTypeId(1);
                                                        break;
                                                    case "object":
                                                        mReqArg.setTypeId(2);
                                                        break;
                                                    case "array[object]":
                                                        mReqArg.setTypeId(3);
                                                        break;
                                                    case "array[string]":
                                                        mReqArg.setTypeId(4);
                                                        break;
                                                    case "array":
                                                        mReqArg.setTypeId(5);
                                                        break;
                                                    case"file":
                                                        mReqArg.setTypeId(6);
                                                        break;
                                                }
                                                mRequestArgService.save(mReqArg);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "导入失败！JSON格式不正确");
        }
        return new BaseResult(1, "导入成功！");
    }

    private void importChildResponseArgs(List<ArgEntity.DataBean> children, String projectId, String interfaceId, String userId, String userName, boolean global, String pid) throws Exception {
        if (children != null && children.size() > 0) {
            for (ArgEntity.DataBean child : children) {
                ResponseArgEntity mResArg = new ResponseArgEntity();
                mResArg.setProjectId(projectId);
                mResArg.setInterfaceId(interfaceId);
                mResArg.setGlobal(global);
                mResArg.setPid(pid);
                mResArg.setCreateUserID(userId);
                mResArg.setCreateUserName(userName);
                mResArg.setName(child.getName());
                mResArg.setNote(child.getDescription());
                String type = child.getType();
                switch (type) {
                    case "string":
                        mResArg.setTypeId(0);
                        break;
                    case "number":
                        mResArg.setTypeId(1);
                        break;
                    case "object":
                        mResArg.setTypeId(2);
                        break;
                    case "array[object]":
                        mResArg.setTypeId(3);
                        break;
                    case "array[string]":
                        mResArg.setTypeId(4);
                        break;
                    case "array":
                        mResArg.setTypeId(5);
                        break;
                    case"file":
                        mResArg.setTypeId(6);
                        break;
                }
                mResponseArgService.save(mResArg);
                importChildResponseArgs(child.getChildren(), projectId, interfaceId, userId, userName, global, mResArg.getId());
            }
        }
    }

}