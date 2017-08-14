package me.zhouzhuo810.zzapidoc.project.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
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
                if (property != null) {
                    UserEntity proUser = mUserService.get(proUserId);
                    if (proUser == null) {
                        mPro.setCreateUserID(user.getId());
                        mPro.setCreateUserName(user.getName());
                    } else {
                        mPro.setCreateUserID(proUser.getId());
                        mPro.setCreateUserName(proUser.getName());
                    }
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
                        //全局参数
                        String requestArgs = modulesBean.getRequestArgs();
                        if (requestArgs != null && !requestArgs.equals("[]")) {
                            ArgEntity argEntity = gson.fromJson("{\"data\":" + requestArgs + "}", ArgEntity.class);
                            if (argEntity != null && argEntity.getData() != null) {
                                //导入全局参数
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
                                    }
                                    mRequestArgService.save(mReqArg);
                                }
                                //导入全局参数完毕
                            }
                        }
                        //无全局参数

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
                                        mInterface.setNote(childrenBean.getDescription());
                                        mInterface.setProjectName(mPro.getName());
                                        mInterface.setHttpMethodId();
                                        mInterface.setHttpMethodName(childrenBean.getRequestMethod());
                                        //实体类内容
                                        StringBuilder sbEntity = new StringBuilder();
                                        sbEntity.append("package ").append(packageName).append(".entity;");
                                        sbEntity.append("\n");
                                        sbEntity.append("\nimport java.util.List;");
                                        sbEntity.append("\n/**");
                                        sbEntity.append("\n * ").append(childrenBean.getName());
                                        sbEntity.append("\n */");

                                        //接口地址
                                        String url = childrenBean.getUrl();
                                        System.out.println(url);

                                        //方法描述
                                        String desc = childrenBean.getDescription();
                                        sb.append("\n   /*");
                                        sb.append("\n    * ").append(desc);
                                        sb.append("\n    */");

                                        //方法名
                                        String m = url.substring(url.lastIndexOf("/") + 1, url.length());
                                        //请求方式GET或POST
                                        String method = childrenBean.getRequestMethod();
                                        if (method.equals("GET")) {
                                            sb.append("\n   @GET").append("(\"").append(url).append("\")");
                                        } else {
                                            sb.append("\n   @FormUrlEncoded");
                                            sb.append("\n   @POST").append("(\"").append(url).append("\")");
                                        }
//                                        System.out.println(method);

                                        String beanClazz = m.substring(0, 1).toUpperCase() + m.substring(1, m.length()) + "Result";


                                        //接口返回数据
                                        String responseData = childrenBean.getResponseArgs();
                                        try {
                                            sbEntity.append("\npublic class ").append(beanClazz).append(" {");
                                            JSONArray root = new JSONArray(responseData);
                                            generateJavaBean2(root, sbEntity);
                                            sbEntity.append("\n}");
                                            System.out.println(sbEntity.toString());
                                            FileUtil.writeFile(path + File.separator + "entity", beanClazz + ".java", sbEntity.toString());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            System.out.println(url + "接口的返回json实例解析异常");
                                        }

                                        //TODO 创建实体类
                                        //接口请求参数
                                        String requestArgs1 = childrenBean.getRequestArgs();
                                        ArgEntity argEntity1 = gson.fromJson("{\"data\":" + requestArgs1 + "}", ArgEntity.class);
                                        if (argEntity1 != null && argEntity1.getData() != null) {

                                            sb.append("\n   Observable<").append(beanClazz).append("> ").append(m).append("(");
                                            //有参数
                                            List<ArgEntity.DataBean> data = argEntity1.getData();
                                            boolean has = false;
                                            for (ArgEntity.DataBean aData : data) {
                                                has = true;
                                                String name = aData.getName();
                                                String type = aData.getType();
                                                if (method.equals("GET")) {
                                                    if (type.equals("number")) {
                                                        sb.append("@Query(\"").append(name).append("\") ").append("int ").append(name).append(",");
                                                    } else {
                                                        sb.append("@Query(\"").append(name).append("\") ").append("String ").append(name).append(",");
                                                    }
                                                } else {
                                                    if (type.equals("number")) {
                                                        sb.append("@Field(\"").append(name).append("\") ").append("int ").append(name).append(",");
                                                    } else {
                                                        sb.append("@Field(\"").append(name).append("\") ").append("String ").append(name).append(",");
                                                    }
                                                }
                                            }
                                            if (has)
                                                sb.deleteCharAt(sb.length() - 1);
                                        } else {
                                            //无参数
                                            sb.append("\n   Observable<").append(beanClazz).append("> ").append(m).append("(");
                                        }
                                        sb.append(");   ");
                                    }
                                }
                                sb.append("\n}");
                                //TODO 创建Api
                                FileUtil.writeFile(path, "Api" + i1 + ".java", sb.toString());
                            }
                            sbApi.append("\n    private static CookieManager getCookieManager() {");
                            sbApi.append("\n        CookieManager cookieManager = new CookieManager();");
                            sbApi.append("\n        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);");
                            sbApi.append("\n        return cookieManager;");
                            sbApi.append("\n    }");
                            sbApi.append("\n}");
                            //TODO 创建Api调用
                            FileUtil.writeFile(path, "Api.java", sbApi.toString());
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

}