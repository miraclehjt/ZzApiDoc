package me.zhouzhuo810.zzapidoc.project.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.StringUtils;
import me.zhouzhuo810.zzapidoc.android.utils.ZipUtils;
import me.zhouzhuo810.zzapidoc.android.widget.apicreator.ApiTool;
import me.zhouzhuo810.zzapidoc.cache.entity.CacheEntity;
import me.zhouzhuo810.zzapidoc.cache.service.CacheService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.result.WebResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.InterfaceDao;
import me.zhouzhuo810.zzapidoc.project.entity.*;
import me.zhouzhuo810.zzapidoc.project.service.*;
import me.zhouzhuo810.zzapidoc.project.utils.InterfaceUtils;
import me.zhouzhuo810.zzapidoc.project.utils.JSONTool;
import me.zhouzhuo810.zzapidoc.project.utils.PdfReportM1HeaderFooter;
import me.zhouzhuo810.zzapidoc.project.utils.ResponseArgUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.*;
import org.apache.log4j.Logger;
import org.aspectj.util.FileUtil;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Created by admin on 2017/7/22.
 */
@Service
public class InterfaceServiceImpl extends BaseServiceImpl<InterfaceEntity> implements InterfaceService {

    private static Logger LOGGER = Logger.getLogger(InterfaceServiceImpl.class.getSimpleName());

    @Resource(name = "userServiceImpl")
    UserService mUserService;

    @Resource(name = "requestArgServiceImpl")
    RequestArgService mRequestArgService;

    @Resource(name = "requestHeaderServiceImpl")
    RequestHeaderService mRequestHeaderService;

    @Resource(name = "responseArgServiceImpl")
    ResponseArgService mResponseArgService;

    @Resource(name = "projectServiceImpl")
    ProjectService mProjectService;

    @Resource(name = "interfaceGroupServiceImpl")
    InterfaceGroupService mInterfaceGroupService;

    @Resource(name = "cacheServiceImpl")
    CacheService mCacheService;

    @Resource(name = "errorCodeServiceImpl")
    ErrorCodeService mErrorCodeService;

    @Override
    @Resource(name = "interfaceDaoImpl")
    public void setBaseDao(BaseDao<InterfaceEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public InterfaceDao getBaseDao() {
        return (InterfaceDao) baseDao;
    }

    @Override
    public BaseResult addInterface(String name, String path, String projectId, String groupId, String httpMethodId, String note, String userId, String requestArgs, String responseArgs) {
        UserEntity user = mUserService.get(userId);
        if (user == null)
            return new BaseResult(0, "用户不合法！");
        InterfaceEntity entity = new InterfaceEntity();
        entity.setName(name);
        entity.setPath(path);
        entity.setProjectId(projectId);
        entity.setGroupId(groupId);
        entity.setHttpMethodId(httpMethodId);
        entity.setNote(note);
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        try {
            getBaseDao().save(entity);
            MapUtils map = new MapUtils();
            map.put("id", entity.getId());
            if (requestArgs != null && requestArgs.length() > 0) {
                JSONArray array = new JSONArray(requestArgs);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    String argName = jsonObject.getString("name");
                    int typeId = jsonObject.getInt("typeId");
                    String argNote = jsonObject.getString("note");
                    RequestArgEntity args = new RequestArgEntity();
                    args.setPid("0");
                    args.setName(argName);
                    args.setProjectId(projectId);
                    args.setTypeId(typeId);
                    args.setNote(argNote);
                    args.setInterfaceId(entity.getId());
                    args.setCreateUserID(user.getId());
                    args.setCreateUserName(user.getName());
                    mRequestArgService.save(args);
                }
            }
            if (responseArgs != null && responseArgs.length() > 0) {
                JSONArray array = new JSONArray(responseArgs);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    saveResponseArg(jsonObject, projectId, entity.getId(), user.getId(), user.getName(), "0");
                }
            }
            return new BaseResult(1, "添加成功！", map.build());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败！", new HashMap<String, String>());
        }
    }

    private void saveResponseArg(JSONObject jsonObject, String projectId, String interfaceId, String userId, String userName, String pid) {
        String argName = jsonObject.getString("name");
        int typeId = jsonObject.getInt("typeId");
        String argNote = jsonObject.getString("note");
        ResponseArgEntity args = new ResponseArgEntity();
        args.setPid(pid);
        args.setName(argName);
        args.setProjectId(projectId);
        args.setTypeId(typeId);
        args.setNote(argNote);
        args.setInterfaceId(interfaceId);
        args.setCreateUserID(userId);
        args.setCreateUserName(userName);
        try {
            mResponseArgService.save(args);
            String id = args.getId();
            switch (typeId) {
                case ResponseArgEntity.TYPE_STRING:

                    break;
                case ResponseArgEntity.TYPE_INT:

                    break;
                case ResponseArgEntity.TYPE_FLOAT:

                    break;
                case ResponseArgEntity.TYPE_OBJECT:
                    JSONObject child = jsonObject.getJSONObject("child");
                    saveResponseArg(child, projectId, interfaceId, userId, userName, id);
                    break;
                case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                    JSONArray childs = jsonObject.getJSONArray("child");
                    for (int i = 0; i < childs.length(); i++) {
                        JSONObject jsonObject1 = childs.getJSONObject(i);
                        saveResponseArg(jsonObject1, projectId, interfaceId, userId, userName, id);
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_STRING:

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseResult deleteInterface(String id, String userId) {
        InterfaceEntity entity = getBaseDao().get(id);
        if (entity == null) {
            return new BaseResult(0, "该接口不存在或已被删除！");
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
            getBaseDao().deleteLogicByIds(new String[]{id});
            return new BaseResult(1, "刪除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "刪除失败！");
        }
    }

    @Override
    public BaseResult setTestFinish(String interfaceId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null)
            return new BaseResult(0, "用户不合法！");
        InterfaceEntity entity = getBaseDao().get(interfaceId);
        if (entity == null) {
            return new BaseResult(0, "该接口不存在或已被删除！");
        }
        entity.setTest(true);
        entity.setTestTime(new Date());
        entity.setTestUserId(userId);
        try {
            getBaseDao().update(entity);
            return new BaseResult(1, "保存成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "保存失败！");
        }
    }


    @Override
    public BaseResult updateInterface(String interfaceId, String name, String path, String projectId, String groupId, String httpMethodId,
                                      String note, String userId, String requestArgs, String responseArgs) {
        UserEntity user = mUserService.get(userId);
        if (user == null)
            return new BaseResult(0, "用户不合法！");
        InterfaceEntity entity = getBaseDao().get(interfaceId);
        if (entity == null) {
            return new BaseResult(0, "该接口不存在或已被删除！");
        }
        entity.setName(name);
        if (path.length() > 0) {
            entity.setPath(path);
        }
        entity.setProjectId(projectId);
        entity.setGroupId(groupId);
        if (httpMethodId.length() > 0) {
            entity.setHttpMethodId(httpMethodId);
        }
        entity.setNote(note);
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        if (requestArgs != null && requestArgs.length() > 0) {
            mRequestArgService.deleteByInterfaceId(interfaceId);
            JSONArray array = new JSONArray(requestArgs);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String pid = jsonObject.getString("pid");
                String argName = jsonObject.getString("name");
                int typeId = jsonObject.getInt("typeId");
                String argNote = jsonObject.getString("note");
                RequestArgEntity args = new RequestArgEntity();
                args.setPid(pid);
                args.setName(argName);
                args.setProjectId(projectId);
                args.setTypeId(typeId);
                args.setNote(argNote);
                args.setInterfaceId(entity.getId());
                args.setCreateUserID(user.getId());
                args.setCreateUserName(user.getName());
                try {
                    mRequestArgService.save(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (responseArgs != null && responseArgs.length() > 0) {
            mResponseArgService.deleteByInterfaceId(interfaceId);
            JSONArray array = new JSONArray(responseArgs);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                saveResponseArg(jsonObject, projectId, interfaceId, user.getId(), user.getName(), "0");
            }
        }
        try {
            getBaseDao().update(entity);
            return new BaseResult(1, "添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败！");
        }
    }

    @Override
    public BaseResult getAllInterface(String projectId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        List<InterfaceEntity> list = getBaseDao().executeCriteria(InterfaceUtils.getInterfaceByProjectId(projectId));
        if (list == null) {
            return new BaseResult(0, "暂无数据");
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (InterfaceEntity entity : list) {
            MapUtils map = new MapUtils();
            map.put("id", entity.getId());
            map.put("name", entity.getName());
            map.put("path", entity.getPath());
            map.put("method", entity.getHttpMethodName());
            map.put("group", entity.getGroupName());
            map.put("note", entity.getNote() == null ? "" : entity.getNote());
            map.put("createTime", DataUtils.formatDate(entity.getCreateTime()));
            map.put("createUserName", entity.getCreateUserName());
            map.put("requestParamsNo", entity.getRequestParamsNo());
            map.put("responseParamsNo", entity.getResponseParamsNo());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }

    @Override
    public BaseResult getInterfaceByGroupId(String projectId, String groupId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        InterfaceGroupEntity group = mInterfaceGroupService.get(groupId);
        if (group == null) {
            return new BaseResult(0, "分组不存在或已被删除");
        }
        List<InterfaceEntity> list = getBaseDao().executeCriteria(InterfaceUtils.getInterfaceByGroupId(groupId));
        List<RequestHeaderEntity> globalRequestHeaders = mRequestHeaderService.getGlobalRequestHeaders(projectId);
        int globalHeadSize = globalRequestHeaders == null ? 0 : globalRequestHeaders.size();
        List<RequestArgEntity> globalRequestArgs = mRequestArgService.getGlobalRequestArgs(projectId);
        int globalReqSize = globalRequestArgs == null ? 0 : globalRequestArgs.size();
        List<ResponseArgEntity> globalResponseArgs = mResponseArgService.getGlobalResponseArgs(projectId);
        int globalResSize = globalResponseArgs == null ? 0 : globalResponseArgs.size();
        if (list == null) {
            return new BaseResult(0, "暂无数据");
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (InterfaceEntity entity : list) {
            MapUtils map = new MapUtils();
            map.put("id", entity.getId());
            map.put("name", entity.getName());
            map.put("method", entity.getHttpMethodName());
            map.put("group", entity.getGroupName());
            map.put("ip", group.getIp());
            map.put("path", entity.getPath());
            map.put("testUserName", entity.getTestUserName());
            map.put("isTest", entity.getTest() == null ? false : entity.getTest());
            map.put("note", entity.getNote() == null ? "" : entity.getNote());
            map.put("createTime", DataUtils.formatDate(entity.getCreateTime()));
            map.put("createUserName", entity.getCreateUserName());
            map.put("requestHeadersNo", entity.getRequestHeadersNo() + globalHeadSize);
            map.put("requestParamsNo", entity.getRequestParamsNo() + globalReqSize);
            map.put("responseParamsNo", entity.getResponseParamsNo() + globalResSize);
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }

    @Override
    public WebResult getInterfaceByGroupIdWeb(String projectId, String groupId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new WebResult(0);
        }
        InterfaceGroupEntity group = mInterfaceGroupService.get(groupId);
        if (group == null) {
            return new WebResult(0);
        }
        List<InterfaceEntity> list = getBaseDao().executeCriteria(InterfaceUtils.getInterfaceByGroupId(groupId));
        List<RequestHeaderEntity> globalRequestHeaders = mRequestHeaderService.getGlobalRequestHeaders(projectId);
        int globalHeadSize = globalRequestHeaders == null ? 0 : globalRequestHeaders.size();
        List<RequestArgEntity> globalRequestArgs = mRequestArgService.getGlobalRequestArgs(projectId);
        int globalReqSize = globalRequestArgs == null ? 0 : globalRequestArgs.size();
        List<ResponseArgEntity> globalResponseArgs = mResponseArgService.getGlobalResponseArgs(projectId);
        int globalResSize = globalResponseArgs == null ? 0 : globalResponseArgs.size();
        if (list == null) {
            return new WebResult(0);
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (InterfaceEntity entity : list) {
            MapUtils map = new MapUtils();
            map.put("id", entity.getId());
            map.put("name", entity.getName());
            map.put("method", entity.getHttpMethodName());
            map.put("group", entity.getGroupName());
            map.put("ip", group.getIp());
            map.put("path", entity.getPath());
            map.put("testUserName", entity.getTestUserName());
            map.put("isTest", entity.getTest() == null ? false : entity.getTest());
            map.put("note", entity.getNote() == null ? "" : entity.getNote());
            map.put("createTime", DataUtils.formatDate(entity.getCreateTime()));
            map.put("createUserName", entity.getCreateUserName());
            map.put("requestHeadersNo", entity.getRequestHeadersNo() + globalHeadSize);
            map.put("requestParamsNo", entity.getRequestParamsNo() + globalReqSize);
            map.put("responseParamsNo", entity.getResponseParamsNo() + globalResSize);
            result.add(map.build());
        }
        return new WebResult(list.size(), result);
    }

    @Override
    public BaseResult getInterfaceDetails(String interfaceId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        InterfaceEntity entity = getBaseDao().get(interfaceId);
        if (entity == null) {
            return new BaseResult(0, "接口不存在或已被删除！", new HashMap<String, String>());
        }
        MapUtils map = new MapUtils();
        map.put("id", entity.getId());
        map.put("name", entity.getName());
        map.put("path", entity.getPath());
        map.put("groupName", entity.getGroupName());
        map.put("note", entity.getNote() == null ? "" : entity.getNote());
        map.put("testUserName", entity.getTestUserName());
        map.put("isTest", entity.getTest() == null ? false : entity.getTest());
        map.put("httpMethod", entity.getHttpMethodName());
        map.put("projectName", entity.getProjectName());
        map.put("example", entity.getExample());
        map.put("createTime", DataUtils.formatDate(entity.getCreateTime()));
        map.put("createUserName", entity.getCreateUserName());
        List<RequestHeaderEntity> globalRequestHeaders = mRequestHeaderService.getGlobalRequestHeaders(entity.getProjectId());
        if (globalRequestHeaders != null) {
            List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
            for (RequestHeaderEntity arg : globalRequestHeaders) {
                MapUtils m = new MapUtils();
                m.put("id", arg.getId());
                m.put("name", arg.getName());
                m.put("value", arg.getValue());
                m.put("note", arg.getNote() == null ? "" : arg.getNote());
                m.put("createTime", DataUtils.formatDate(arg.getCreateTime()));
                m.put("createUserName", arg.getCreateUserName());
                m.put("interfaceId", arg.getInterfaceId());
                m.put("projectId", arg.getProjectId());
                m.put("isGlobal", true);
                response.add(m.build());
            }
            map.put("globalrequestHeader", response);
        }

        List<RequestHeaderEntity> requestHeaderEntities = mRequestHeaderService.getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceId(interfaceId));
        if (requestHeaderEntities != null) {
            List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
            for (RequestHeaderEntity arg : requestHeaderEntities) {
                MapUtils m = new MapUtils();
                m.put("id", arg.getId());
                m.put("name", arg.getName());
                m.put("value", arg.getValue());
                m.put("note", arg.getNote() == null ? "" : arg.getNote());
                m.put("createTime", DataUtils.formatDate(arg.getCreateTime()));
                m.put("createUserName", arg.getCreateUserName());
                m.put("interfaceId", arg.getInterfaceId());
                m.put("projectId", arg.getProjectId());
                m.put("isGlobal", false);
                response.add(m.build());
            }
            map.put("requestHeader", response);
        }
        List<RequestArgEntity> globalRequestArgs = mRequestArgService.getGlobalRequestArgs(entity.getProjectId());
        if (globalRequestArgs != null) {
            List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
            for (RequestArgEntity requestArgEntity : globalRequestArgs) {
                MapUtils m = new MapUtils();
                m.put("id", requestArgEntity.getId());
                m.put("name", requestArgEntity.getName());
                m.put("defValue", requestArgEntity.getDefaultValue() == null ? "" : requestArgEntity.getDefaultValue());
                m.put("global", requestArgEntity.getGlobal() == null ? false : requestArgEntity.getGlobal());
                m.put("note", requestArgEntity.getNote() == null ? "" : requestArgEntity.getNote());
                m.put("pid", requestArgEntity.getPid());
                m.put("typeId", requestArgEntity.getTypeId());
                response.add(m.build());
            }
            map.put("globalRequestArgs", response);
        }
        List<RequestArgEntity> requestArgEntities = mRequestArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceId(interfaceId));
        if (requestArgEntities != null) {
            List<Map<String, Object>> request = new ArrayList<Map<String, Object>>();
            for (RequestArgEntity requestArgEntity : requestArgEntities) {
                MapUtils m = new MapUtils();
                m.put("id", requestArgEntity.getId());
                m.put("name", requestArgEntity.getName());
                m.put("pid", requestArgEntity.getPid());
                m.put("defValue", requestArgEntity.getDefaultValue() == null ? "" : requestArgEntity.getDefaultValue());
                m.put("global", requestArgEntity.getGlobal() == null ? false : requestArgEntity.getGlobal());
                m.put("require", requestArgEntity.getRequire());
                m.put("note", requestArgEntity.getNote() == null ? "" : requestArgEntity.getNote());
                m.put("typeId", requestArgEntity.getTypeId());
                request.add(m.build());
            }
            map.put("requestArgs", request);
        }
        List<ResponseArgEntity> globalResponseArgs = mResponseArgService.getGlobalResponseArgs(entity.getProjectId());
        if (globalResponseArgs != null) {
            List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
            for (ResponseArgEntity requestArgEntity : globalResponseArgs) {
                MapUtils m = new MapUtils();
                m.put("id", requestArgEntity.getId());
                m.put("name", requestArgEntity.getName());
                m.put("defValue", requestArgEntity.getDefaultValue() == null ? "" : requestArgEntity.getDefaultValue());
                m.put("global", requestArgEntity.getGlobal() == null ? false : requestArgEntity.getGlobal());
                m.put("note", requestArgEntity.getNote() == null ? "" : requestArgEntity.getNote());
                m.put("pid", requestArgEntity.getPid());
                m.put("typeId", requestArgEntity.getTypeId());
                response.add(m.build());
            }
            map.put("globalResponseArgs", response);
        }
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, "0"));
        if (responseArgEntities != null && responseArgEntities.size() > 0) {
            List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
            for (ResponseArgEntity requestArgEntity : responseArgEntities) {
                MapUtils m = new MapUtils();
                m.put("id", requestArgEntity.getId());
                m.put("name", requestArgEntity.getName());
                m.put("defValue", requestArgEntity.getDefaultValue() == null ? "" : requestArgEntity.getDefaultValue());
                m.put("global", requestArgEntity.getGlobal() == null ? false : requestArgEntity.getGlobal());
                m.put("note", requestArgEntity.getNote() == null ? "" : requestArgEntity.getNote());
                m.put("pid", requestArgEntity.getPid());
                m.put("typeId", requestArgEntity.getTypeId());
                response.add(m.build());
                addChildResponseArg(response, "    ", interfaceId, requestArgEntity.getId());
            }
            map.put("responseArgs", response);
        }
        return new BaseResult(1, "ok", map.build());
    }

    private void addChildResponseArg(List<Map<String, Object>> response, String space, String interfaceId, String id) {
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, id));
        if (responseArgEntities != null && responseArgEntities.size() > 0) {
            for (ResponseArgEntity requestArgEntity : responseArgEntities) {
                MapUtils m = new MapUtils();
                m.put("id", requestArgEntity.getId());
                m.put("name", space + requestArgEntity.getName());
                m.put("defValue", requestArgEntity.getDefaultValue() == null ? "" : requestArgEntity.getDefaultValue());
                m.put("global", requestArgEntity.getGlobal() == null ? false : requestArgEntity.getGlobal());
                m.put("note", requestArgEntity.getNote() == null ? "" : requestArgEntity.getNote());
                m.put("pid", requestArgEntity.getPid());
                m.put("typeId", requestArgEntity.getTypeId());
                response.add(m.build());
                addChildResponseArg(response, space + "    ", interfaceId, requestArgEntity.getId());
            }
        }
    }


    @Override
    public BaseResult addInterfaceExample(String interfaceId, String example, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法", new HashMap<String, String>());
        }
        InterfaceEntity entity = getBaseDao().get(interfaceId);
        if (entity == null) {
            return new BaseResult(0, "接口不存在或已被删除！", new HashMap<String, String>());
        }
        entity.setExample(example);
        try {
            getBaseDao().update(entity);
            return new BaseResult(1, "添加成功！", new HashMap<String, String>());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败！", new HashMap<String, String>());
        }
    }

    @Override
    public BaseResult generateEmptyExample(String interfaceId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法", new HashMap<String, String>());
        }
        InterfaceEntity entity = getBaseDao().get(interfaceId);
        if (entity == null) {
            return new BaseResult(0, "接口不存在或已被删除！", new HashMap<String, String>());
        }
        List<ResponseArgEntity> globalResponseArgs = mResponseArgService.getGlobalResponseArgs(entity.getProjectId());
        JSONStringer stringer = new JSONStringer();
        stringer.object();
        if (globalResponseArgs != null) {
            for (ResponseArgEntity resG : globalResponseArgs) {
                if (resG.getTypeId() == null) {
                    resG.setTypeId(7);
                }
                switch (resG.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        try {
                            stringer.key(resG.getName()).value(resG.getNote() == null ? "" : resG.getNote());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        try {
                            stringer.key(resG.getName()).value(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        try {
                            stringer.key(resG.getName()).value(0.1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        try {
                            stringer.key(resG.getName()).array().endArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_INT:
                        try {
                            stringer.key(resG.getName());
                            stringer.array();
                            stringer.value(0);
                            stringer.value(1);
                            stringer.endArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_FLOAT:
                        try {
                            stringer.key(resG.getName());
                            stringer.array();
                            stringer.value(0.1);
                            stringer.value(0.2);
                            stringer.endArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_STRING:
                        try {
                            stringer.key(resG.getName());
                            stringer.array();
                            stringer.value("string1");
                            stringer.value("string2");
                            stringer.endArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        try {
                            stringer.key(resG.getName()).value("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        try {
                            stringer.key(resG.getName()).value("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, "0"));
        for (ResponseArgEntity res : responseArgEntities) {
            if (res.getTypeId() == null) {
                res.setTypeId(7);
            }
            switch (res.getTypeId()) {
                case ResponseArgEntity.TYPE_STRING:
                    try {
                        stringer.key(res.getName()).value(res.getNote() == null ? "" : res.getNote());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_INT:
                    try {
                        stringer.key(res.getName()).value(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_FLOAT:
                    try {
                        stringer.key(res.getName()).value(0.0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_OBJECT:
                    try {
                        stringer.key(res.getName());
                        stringer.object();
                        generateChildExeample(stringer, interfaceId, res.getId());
                        stringer.endObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.object();
                        generateChildExeample(stringer, interfaceId, res.getId());
                        stringer.endObject();
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_INT:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.value(0);
                        stringer.value(1);
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_FLOAT:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.value(0.1);
                        stringer.value(0.2);
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_STRING:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.value("string1");
                        stringer.value("string2");
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY:
                    try {
                        stringer.key(res.getName()).array().endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_FILE:
                    try {
                        stringer.key(res.getName()).value("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    try {
                        stringer.key(res.getName()).value("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
        stringer.endObject();
        String result = stringer.toString();
        String formatJson = new JSONTool().stringToJSON(result);
        entity.setExample(formatJson);
        try {
            getBaseDao().update(entity);
            return new BaseResult(1, "生成成功！", new HashMap<String, String>());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "生成失败！", new HashMap<String, String>());
        }
    }

    private void generateChildExeample(JSONStringer stringer, String interfaceId, String pid) {
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid));
        for (ResponseArgEntity res : responseArgEntities) {
            switch (res.getTypeId()) {
                case ResponseArgEntity.TYPE_STRING:
                    try {
                        stringer.key(res.getName()).value(res.getNote() == null ? "" : res.getNote());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_INT:
                    try {
                        stringer.key(res.getName()).value(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_FLOAT:
                    try {
                        stringer.key(res.getName()).value(0.0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_OBJECT:
                    try {
                        stringer.key(res.getName());
                        stringer.object();
                        generateChildExeample(stringer, interfaceId, res.getId());
                        stringer.endObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.object();
                        generateChildExeample(stringer, interfaceId, res.getId());
                        stringer.endObject();
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_INT:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.value(0);
                        stringer.value(1);
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_FLOAT:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.value(0.1);
                        stringer.value(0.2);
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_STRING:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.value("string1");
                        stringer.value("string2");
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY:
                    try {
                        stringer.key(res.getName()).array().endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_FILE:
                    try {
                        stringer.key(res.getName()).value("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    try {
                        stringer.key(res.getName()).value("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    /***********************************下载JSON 开始**************************************/
    @Override
    public ResponseEntity<byte[]> download(String projectId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return null;
        }

        ProjectEntity project = mProjectService.get(projectId);
        if (project == null) {
            return null;
        }
        String json = convertToJson(project);
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            URL resource = classLoader.getResource("../../empty_file.txt");
            if (resource != null) {
                String path = resource.getPath();
                if (path != null) {
                    String mPath = new File(path).getParent() + File.separator + "JSON";
                    CacheEntity cacheEntity = new CacheEntity();
                    cacheEntity.setCachePath(mPath);
                    try {
                        List<CacheEntity> cacheEntities = mCacheService.executeCriteria(new Criterion[]{
                                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                                Restrictions.eq("cachePath", mPath)});
                        if (cacheEntities == null || cacheEntities.size() == 0) {
                            mCacheService.save(cacheEntity);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String fileName = me.zhouzhuo810.zzapidoc.common.utils.FileUtils.saveFileToPathWithRandomName(json, mPath);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    headers.setContentDispositionFormData("attachment", fileName);
                    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(mPath + File.separator + fileName)), headers, HttpStatus.CREATED);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public String convertToJson(ProjectEntity project) {

        JSONStringer stringer = new JSONStringer();
        /*工程*/
        stringer.object()
                .key("project").object()
                .key("name").value(project.getName())
                .key("userId").value(project.getCreateUserID())
                .key("description").value(project.getNote() == null ? "" : project.getNote())
                .key("permission").value(project.getProperty())
                .key("createTime").value(DataUtils.formatDate(project.getCreateTime()))
                .endObject();
        List<InterfaceGroupEntity> groups = mInterfaceGroupService.executeCriteria(InterfaceUtils.getInterfaceByProjectId(project.getId()));
        /*模块数组开始*/
        stringer
                .key("modules").array();
        if (groups != null) {
            /*分组数组开始*/
            stringer.object()
                    .key("name").value("默认模块")
                   /*请求参数*/
                    .key("requestHeaders").value(globalHeaderToJson(project.getId()))
                    .key("requestArgs").value(globalRequestToJson(project.getId()))
                    /*返回参数*/
                    .key("responseArgs").value(globalResponseToJson(project.getId()))
                    .key("folders").array();
            for (InterfaceGroupEntity interfaceGroupEntity : groups) {
                /*分组基本信息*/
                stringer.object()
                        .key("id").value(interfaceGroupEntity.getId())
                        .key("name").value(interfaceGroupEntity.getName())
                        .key("ip").value(interfaceGroupEntity.getIp())
                        .key("createTime").value(DataUtils.formatDate(interfaceGroupEntity.getCreateTime()))
                        .key("createUser").value(interfaceGroupEntity.getCreateUserName());
                List<InterfaceEntity> list = getBaseDao().executeCriteria(InterfaceUtils.getInterfaceByGroupId(interfaceGroupEntity.getId()));
                if (list == null) {
                    continue;
                }
                /*组的接口数组*/
                stringer.key("children").array();
                for (InterfaceEntity entity : list) {
                    /*接口基本信息*/
                    stringer.object()
                            .key("id").value(entity.getId())
                            .key("name").value(entity.getName())
                            .key("example").value(entity.getExample() == null ? "" : entity.getExample())
                            .key("requestMethod").value(entity.getHttpMethodName())
                            .key("requestHeaders").value(headersToJson(entity.getId()))
                            .key("url").value(entity.getPath())
                            .key("description").value((entity.getName() == null ? "" : entity.getName()) + (entity.getNote() == null ? "" : "(" + entity.getNote() + ")"));

                    /*请求参数*/
                    stringer.key("requestArgs").value(requestToJson(entity.getId(), "0"));

                    /*返回参数*/
                    stringer.key("responseArgs").value(responseAndGlobalToJson(project.getId(), entity.getId(), "0"));

                    /*接口信息结束*/
                    stringer.endObject();
                }
                /*接口数组结束*/
                stringer.endArray();
                /*接口分组结束*/
                stringer.endObject();
            }
            /*接口分组数组结束*/
            stringer.endArray();
            /*模块结束*/
            stringer.endObject();
        }
        /*模块分组结束*/
        stringer.endArray();
        /*工程结束*/
        stringer.endObject();
        return stringer.toString();
    }


    @Override
    public String convertToJson(ProjectEntity project, InterfaceEntity entity) {

        JSONStringer stringer = new JSONStringer();
        /*工程*/
        stringer.object()
                .key("project").object()
                .key("name").value(project.getName())
                .key("userId").value(project.getCreateUserID())
                .key("description").value(project.getNote() == null ? "" : project.getNote())
                .key("permission").value(project.getProperty())
                .key("createTime").value(DataUtils.formatDate(project.getCreateTime()))
                .endObject();
        /*模块数组开始*/
        stringer
                .key("modules").array();
            /*分组数组开始*/
        stringer.object()
                .key("name").value("默认模块")
                   /*请求参数*/
                .key("requestHeaders").value(globalHeaderToJson(project.getId()))
                .key("requestArgs").value(globalRequestToJson(project.getId()))
                    /*返回参数*/
                .key("responseArgs").value(globalResponseToJson(project.getId()))
                .key("folders").array();
                /*分组基本信息*/
        stringer.object()
                .key("id").value("")
                .key("name").value("默认分组")
                .key("ip").value("http://www.baidu.com/")
                .key("createTime").value(DataUtils.formatDate(new Date()))
                .key("createUser").value("System");
                /*组的接口数组*/
        stringer.key("children").array();
                    /*接口基本信息*/
        stringer.object()
                .key("id").value(entity.getId())
                .key("name").value(entity.getName())
                .key("example").value(entity.getExample() == null ? "" : entity.getExample())
                .key("requestMethod").value(entity.getHttpMethodName())
                .key("requestHeaders").value(headersToJson(entity.getId()))
                .key("url").value(entity.getPath())
                .key("description").value((entity.getName() == null ? "" : entity.getName()) + (entity.getNote() == null ? "" : "(" + entity.getNote() + ")"));

                    /*请求参数*/
        stringer.key("requestArgs").value(requestToJson(entity.getId(), "0"));

                    /*返回参数*/
        stringer.key("responseArgs").value(responseAndGlobalToJson(project.getId(), entity.getId(), "0"));

                    /*接口信息结束*/
        stringer.endObject();
                /*接口数组结束*/
        stringer.endArray();
                /*接口分组结束*/
        stringer.endObject();
            /*接口分组数组结束*/
        stringer.endArray();
            /*模块结束*/
        stringer.endObject();
        /*模块分组结束*/
        stringer.endArray();
        /*工程结束*/
        stringer.endObject();
        return stringer.toString();
    }


    private String headersToJson(String interfaceId) {
        JSONStringer stringer = new JSONStringer();
        List<RequestHeaderEntity> headers = mRequestHeaderService.executeCriteria(ResponseArgUtils.getArgByInterfaceId(interfaceId));
        stringer.array();
        if (headers != null) {
            for (RequestHeaderEntity req : headers) {
                stringer.object()
                        .key("id").value(req.getId())
                        .key("name").value(req.getName())
                        .key("defaultValue").value(req.getValue())
                        .key("description").value(req.getNote() == null ? "" : req.getNote())
                        .key("require").value(true)
                        .key("children").array().endArray();
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }


    private String requestToJson(String interfaceId, String pid) {
        JSONStringer stringer = new JSONStringer();
        List<RequestArgEntity> requestArgEntities = mRequestArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid));
        stringer.array();
        if (requestArgEntities != null) {
            for (RequestArgEntity requestArgEntity : requestArgEntities) {
                stringer.object()
                        .key("id").value(requestArgEntity.getId())
                        .key("name").value(requestArgEntity.getName())
                        .key("pid").value(requestArgEntity.getPid())
                        .key("defaultValue").value(requestArgEntity.getDefaultValue() == null ? "" : requestArgEntity.getDefaultValue())
                        .key("require").value(requestArgEntity.getRequire() == null ? true : requestArgEntity.getRequire())
                        .key("typeId").value(requestArgEntity.getTypeId())
                        .key("description").value(requestArgEntity.getNote() == null ? "" : requestArgEntity.getNote());
                if (requestArgEntity.getTypeId() == null) {
                    requestArgEntity.setTypeId(7);
                }
                switch (requestArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                        stringer.key("type").value("array[object]");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        stringer.key("type").value("array");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        stringer.key("type").value("file");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }

    private String responseAndGlobalToJson(String projectId, String interfaceId, String pid) {
        JSONStringer stringer = new JSONStringer();
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid));
        stringer.array();

        List<ResponseArgEntity> globals = mResponseArgService.executeCriteria(ResponseArgUtils.getGlobal(projectId));
        if (globals != null) {
            for (ResponseArgEntity responseArgEntity : globals) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
                        .key("defaultValue").value(responseArgEntity.getDefaultValue() == null ? "" : responseArgEntity.getDefaultValue())
                        .key("pid").value(responseArgEntity.getPid())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                switch (responseArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                        stringer.key("type").value("array[object]");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }

        if (responseArgEntities != null) {
            for (ResponseArgEntity responseArgEntity : responseArgEntities) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
                        .key("defaultValue").value(responseArgEntity.getDefaultValue() == null ? "" : responseArgEntity.getDefaultValue())
                        .key("pid").value(responseArgEntity.getPid())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                if (responseArgEntity.getTypeId() == null) {
                    responseArgEntity.setTypeId(7);
                }
                switch (responseArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                        stringer.key("type").value("array[object]");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        stringer.key("type").value("array");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        stringer.key("type").value("file");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }


    private String responseToJson(String projectId, String interfaceId, String pid) {
        JSONStringer stringer = new JSONStringer();
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid));
        stringer.array();

/*        List<ResponseArgEntity> globals = mResponseArgService.executeCriteria(ResponseArgUtils.getGlobal(projectId));
        if (globals != null) {
            for (ResponseArgEntity responseArgEntity : globals) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
                        .key("defaultValue").value(responseArgEntity.getDefaultValue()==null?"":responseArgEntity.getDefaultValue())
                        .key("pid").value(responseArgEntity.getPid())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                switch (responseArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_NUMBER:
                        stringer.key("type").value("number");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                        stringer.key("type").value("array[object]");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }*/

        if (responseArgEntities != null) {
            for (ResponseArgEntity responseArgEntity : responseArgEntities) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
                        .key("defaultValue").value(responseArgEntity.getDefaultValue() == null ? "" : responseArgEntity.getDefaultValue())
                        .key("pid").value(responseArgEntity.getPid())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                if (responseArgEntity.getTypeId() == null) {
                    responseArgEntity.setTypeId(7);
                }
                switch (responseArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                        stringer.key("type").value("array[object]");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        stringer.key("type").value("array");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        stringer.key("type").value("file");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }

    private String globalHeaderToJson(String projectId) {
        JSONStringer stringer = new JSONStringer();
        List<RequestHeaderEntity> headers = mRequestHeaderService.executeCriteria(ResponseArgUtils.getGlobal(projectId));
        stringer.array();
        if (headers != null) {
            for (RequestHeaderEntity req : headers) {
                stringer.object()
                        .key("id").value(req.getId())
                        .key("name").value(req.getName())
                        .key("defaultValue").value(req.getValue())
                        .key("description").value(req.getNote() == null ? "" : req.getNote())
                        .key("require").value(true)
                        .key("children").array().endArray();
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }


    private String globalRequestToJson(String projectId) {
        JSONStringer stringer = new JSONStringer();
        List<RequestArgEntity> requestArgEntities = mRequestArgService.executeCriteria(ResponseArgUtils.getGlobal(projectId));
        stringer.array();
        if (requestArgEntities != null) {
            for (RequestArgEntity req : requestArgEntities) {
                stringer.object()
                        .key("id").value(req.getId())
                        .key("name").value(req.getName())
                        .key("pid").value(req.getPid())
                        .key("defaultValue").value(req.getDefaultValue() == null ? "" : req.getDefaultValue())
                        .key("require").value(req.getRequire() == null ? true : req.getRequire())
                        .key("typeId").value(req.getTypeId())
                        .key("description").value(req.getNote() == null ? "" : req.getNote());
                if (req.getTypeId() == null) {
                    req.setTypeId(7);
                }
                switch (req.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        stringer.key("type").value("array");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        stringer.key("type").value("file");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }

    private String globalResponseToJson(String projectId) {
        JSONStringer stringer = new JSONStringer();
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getGlobal(projectId));
        stringer.array();
        if (responseArgEntities != null) {
            for (ResponseArgEntity responseArgEntity : responseArgEntities) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
                        .key("pid").value(responseArgEntity.getPid())
                        .key("defaultValue").value(responseArgEntity.getDefaultValue() == null ? "" : responseArgEntity.getDefaultValue())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                if (responseArgEntity.getTypeId() == null) {
                    responseArgEntity.setTypeId(7);
                }
                switch (responseArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        stringer.key("type").value("array");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        stringer.key("type").value("file");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }

    private void responseToChildJson(JSONStringer stringer, String interfaceId, String pid) {
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid));
        stringer.key("children").array();
        if (responseArgEntities != null) {
            for (ResponseArgEntity responseArgEntity : responseArgEntities) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
                        .key("defaultValue").value(responseArgEntity.getDefaultValue() == null ? "" : responseArgEntity.getDefaultValue())
                        .key("pid").value(responseArgEntity.getPid())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                if (responseArgEntity.getTypeId() == null) {
                    responseArgEntity.setTypeId(7);
                }
                switch (responseArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                        stringer.key("type").value("array[object]");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        stringer.key("type").value("array");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        stringer.key("type").value("file");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }
        stringer.endArray();
    }

    /***********************************下载JSON 结束**************************************/


    public static String getIpAddr() throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        return addr.getHostAddress();//获得本机IP
    }

    /***********************************下载 PDF 开始**************************************/

    @Override
    public ResponseEntity<byte[]> downloadPdf(String projectId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return null;
        }
        ProjectEntity project = mProjectService.get(projectId);
        if (project == null) {
            return null;
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String realPath = request.getRealPath("");
            if (realPath != null) {
                final String fontPath = realPath + File.separator + "font" + File.separator;
                String mPath = realPath + File.separator + "PDF";
                File dir = new File(mPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                CacheEntity cacheEntity = new CacheEntity();
                cacheEntity.setCachePath(mPath);
                try {
                    List<CacheEntity> cacheEntities = mCacheService.executeCriteria(new Criterion[]{
                            Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                            Restrictions.eq("cachePath", mPath)});
                    if (cacheEntities == null || cacheEntities.size() == 0) {
                        mCacheService.save(cacheEntity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("PDF ERROR", e);
                }
                String realFileName = new String(project.getName().getBytes("UTF-8"), "iso-8859-1") + "_" + System.currentTimeMillis() % 1000 + ".pdf";
                String filePath = mPath + File.separator + realFileName;
                BaseFont bfChinese = BaseFont.createFont(fontPath + "SIMYOU.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                Font fontChinese = new Font(bfChinese, 12, Font.NORMAL);

                Document document = new Document(PageSize.A4);
                try {
                    PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePath));
                    PdfReportM1HeaderFooter footer = new PdfReportM1HeaderFooter();
                    pdfWriter.setPageEvent(footer);
                    //打开文档
                    document.open();

                    document.addTitle(project.getName());
                    document.addAuthor(project.getCreateUserName() == null ? "" : project.getCreateUserName());
                    document.addCreationDate();
                    document.addCreator("zhouzhuo810");
                    document.addSubject(project.getNote());

                    addLargeTitle(document, project.getName(), fontChinese);
                    fontChinese.setStyle(Font.NORMAL);

                    addTextLine(document, "", null);
                    addText(document, "创建人：", fontChinese, true);
                    addTextLine(document, project.getCreateUserName(), fontChinese);
                    addText(document, "创建时间：", fontChinese, true);
                    addTextLine(document, DataUtils.formatDate(project.getCreateTime()), fontChinese);
                    addText(document, "项目说明：", fontChinese, true);
                    addTextLine(document, project.getNote(), fontChinese);
                    addText(document, "文档下载及更新地址：", fontChinese, true);
                    try {
                        addUnderLineText(document, "http://" + getIpAddr() + ":8080/" + "ZzApiDoc/v1/interface/downloadPdf?userId=" + userId + "&projectId=" + projectId, fontPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    addTextLine(document, "", fontChinese);
                    addTextLine(document, "", fontChinese);

                    List<ErrorCodeEntity> globalErrorCodes = mErrorCodeService.executeCriteria(new Criterion[]{
                            Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                            Restrictions.eq("projectId", projectId),
                            Restrictions.eq("isGlobal", true)
                    });
                    if (globalErrorCodes != null && globalErrorCodes.size() > 0) {
                        //设置列宽
                        float[] columnWidths = {3f, 6f};
                        String[][] values = new String[globalErrorCodes.size()][2];
                        for (int i = 0; i < globalErrorCodes.size(); i++) {
                            ErrorCodeEntity entity = globalErrorCodes.get(i);
                            values[i][0] = entity.getCode()+"";
                            values[i][1] = entity.getNote() == null ? "" : entity.getNote();
                        }
                        addTable(document, "全局错误码说明", new String[]{"错误码", "说明"}, columnWidths, values, null, fontChinese);
                    }

                    List<InterfaceGroupEntity> groups = mInterfaceGroupService.executeCriteria(InterfaceUtils.getInterfaceByProjectId(projectId));
                        /*模块数组开始*/
                    if (groups != null) {

                        for (int i = 0; i < groups.size(); i++) {
                            InterfaceGroupEntity interfaceGroupEntity = groups.get(i);

//                                Chapter chapter = addGroup(document, (i + 1) + ". " + interfaceGroupEntity.getName(), (i+1), fontChinese);
                            Chapter chapter = addGroup(document, interfaceGroupEntity.getName(), (i + 1), fontChinese);
                            fontChinese.setStyle(Font.NORMAL);
                            addText(document, "创建人：", fontChinese, true);
                            addTextLine(document, interfaceGroupEntity.getCreateUserName(), fontChinese);
                            addText(document, "创建时间：", fontChinese, true);
                            addTextLine(document, DataUtils.formatDate(interfaceGroupEntity.getCreateTime()), fontChinese);
                            addText(document, "服务器Ip地址：", fontChinese, true);
                            addUnderLineText(document, interfaceGroupEntity.getIp(), fontPath);
                            addTextLine(document, "", fontChinese);
                            addTextLine(document, "", fontChinese);
                            List<ErrorCodeEntity> groupErrorCodes = mErrorCodeService.executeCriteria(new Criterion[]{
                                    Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                                    Restrictions.eq("groupId", interfaceGroupEntity.getId()),
                                    Restrictions.eq("isGroup", true)
                            });
                            if (groupErrorCodes != null && groupErrorCodes.size() > 0) {
                                //设置列宽
                                float[] columnWidths = {3f, 6f};
                                String[][] values = new String[groupErrorCodes.size()][2];
                                for (int j = 0; j < groupErrorCodes.size(); j++) {
                                    ErrorCodeEntity entity = groupErrorCodes.get(j);
                                    values[j][0] = entity.getCode()+"";
                                    values[j][1] = entity.getNote() == null ? "" : entity.getNote();
                                }
                                addTable(document, "错误码说明", new String[]{"错误码", "说明"}, columnWidths, values, null, fontChinese);
                            }
                            addTextLine(document, "", fontChinese);

                            List<InterfaceEntity> list = getBaseDao().executeCriteria(InterfaceUtils.getInterfaceByGroupId(interfaceGroupEntity.getId()));
                            if (list == null) {
                                continue;
                            }
                                /*组的接口数组*/
                            for (int i1 = 0; i1 < list.size(); i1++) {
                                InterfaceEntity entity = list.get(i1);

//                                    addGroupItem(document, chapter, (i + 1) + "." + (i1 + 1) + ". " + entity.getName(), (i + 1), fontChinese);
                                addGroupItem(document, chapter, entity.getName(), (i + 1), (i1 + 1), fontChinese);
                                fontChinese.setStyle(Font.NORMAL);
                                addText(document, "创建人：", fontChinese, true);
                                addTextLine(document, entity.getCreateUserName(), fontChinese);
                                addText(document, "创建时间：", fontChinese, true);
                                addTextLine(document, DataUtils.formatDate(entity.getCreateTime()), fontChinese);
                                addText(document, "请求方式：", fontChinese, true);
                                addTextLine(document, entity.getHttpMethodName(), fontChinese);
                                addText(document, "请求地址：", fontChinese, true);
                                addTextLine(document, entity.getPath(), null);
                                addText(document, "接口说明：", fontChinese, true);
                                addTextLine(document, entity.getNote(), fontChinese);
                                addTextLine(document, "", fontChinese);
                                addTextLine(document, "", fontChinese);
                                List<ErrorCodeEntity> interfaceErrorCodes = mErrorCodeService.executeCriteria(new Criterion[]{
                                        Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                                        Restrictions.eq("interfaceId", entity.getId()),
                                        Restrictions.eq("isGroup", false),
                                        Restrictions.eq("isGlobal", false)
                                });
                                if (interfaceErrorCodes != null && interfaceErrorCodes.size() > 0) {
                                    //设置列宽
                                    float[] columnWidths = {3f, 6f};
                                    String[][] values = new String[interfaceErrorCodes.size()][2];
                                    for (int j = 0; j < interfaceErrorCodes.size(); j++) {
                                        ErrorCodeEntity err = interfaceErrorCodes.get(j);
                                        values[j][0] = err.getCode()+"";
                                        values[j][1] = err.getNote() == null ? "" : err.getNote();
                                    }
                                    addTable(document, "错误码说明", new String[]{"错误码", "说明"}, columnWidths, values, null, fontChinese);
                                }

                                pdfAddRequestHeaders(document, projectId, entity.getId(), fontChinese);
                                pdfAddRequestParams(document, projectId, entity.getId(), fontChinese);
                                pdfAddResponseParams(document, projectId, entity.getId(), fontChinese);

                                addSmallTitle(document, "返回示例", fontChinese);
                                addTextLine(document, "\n", null);
                                fontChinese.setStyle(Font.NORMAL);
                                addTextLine(document, entity.getExample() == null ? "" : entity.getExample(), fontChinese);

                            }
                        }
                    }
                    //关闭文档
                    document.close();
                    //关闭书写器
                    pdfWriter.close();

                    /*尝试添加水印*/
                    String waterPath = filePath;
                    String waterName = new String(project.getName().getBytes("UTF-8"), "iso-8859-1") + "_" + System.currentTimeMillis() % 1000 + ".pdf";
                    try {
                        waterPath = addWaterMark(filePath, mPath + File.separator + waterName, "ZzApiDoc", 400, 400);
                    } catch (Exception e) {
                        e.printStackTrace();
                        waterPath = filePath;
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    headers.setContentDispositionFormData("attachment", realFileName);
                    return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(waterPath)), headers, HttpStatus.CREATED);
                } catch (DocumentException | FileNotFoundException e) {
                    e.printStackTrace();
                    LOGGER.error("PDF ERROR", e);
                }
            } else {
                LOGGER.error("path = null");
            }
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            LOGGER.error("PDF ERROR", e);
        }

        return null;
    }


    /**
     * PDF添加请求头
     *
     * @param document
     * @param projectId
     * @param id
     * @param font
     * @throws DocumentException
     */
    private void pdfAddRequestHeaders(Document document, String projectId, String id, Font font) throws DocumentException {
        List<RequestHeaderEntity> globals = mRequestHeaderService.getGlobalRequestHeaders(projectId);
        List<RequestHeaderEntity> args = mRequestHeaderService.getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceId(id));

        if (globals != null && globals.size() > 0) {
            //设置列宽
            float[] columnWidths = {3f, 1f, 5f};
            String[][] values = new String[globals.size()][3];
            for (int i = 0; i < globals.size(); i++) {
                RequestHeaderEntity entity = globals.get(i);
                values[i][0] = entity.getName();
                values[i][1] = entity.getValue() == null ? "" : entity.getValue();
                values[i][2] = entity.getNote() == null ? "" : entity.getNote();
            }
            addTable(document, "全局请求头", new String[]{"名称", "默认值", "说明"}, columnWidths, values, null, font);
        }

        if (args != null && args.size() > 0) {
            //设置列宽
            float[] columnWidths = {3f, 1f, 5f};
            String[][] values = new String[args.size()][3];
            for (int i = 0; i < args.size(); i++) {
                RequestHeaderEntity entity = args.get(i);
                values[i][0] = entity.getName();
                values[i][1] = entity.getValue() == null ? "" : entity.getValue();
                values[i][2] = entity.getNote() == null ? "" : entity.getNote();
            }
            addTable(document, "其他请求头", new String[]{"名称", "默认值", "说明"}, columnWidths, values, null, font);
        }
    }


    /**
     * 添加表格
     *
     * @param document     Document对象
     * @param tableTitle   表格标题
     * @param titles       标题
     * @param widthRatio   标题宽的比例
     * @param values       内容
     * @param titleBgColor 标题行背景颜色
     * @param font         字体
     * @throws DocumentException 异常
     */
    private List<PdfPRow> addTable(Document document, String tableTitle, String[] titles, float[] widthRatio, String[][] values, BaseColor titleBgColor, Font font) throws DocumentException {
        //添加表格标题
        font.setSize(14f);
        font.setStyle(Font.BOLD);
        Chunk paragraph = new Chunk(tableTitle == null ? "" : tableTitle, font);
        try {
            document.add(paragraph);
        } catch (DocumentException e) {
            e.printStackTrace();
            LOGGER.error("PDF ERROR", e);
        }
        font.setSize(12f);
        font.setStyle(Font.NORMAL);
        //添加表格
        PdfPTable inter = new PdfPTable(titles.length);
        inter.setWidthPercentage(100); // 宽度100%填充
        inter.setSpacingBefore(1f); // 前间距
        inter.setSpacingAfter(1f); // 后间距
        List<PdfPRow> listRow = inter.getRows(); //行
        //设置列宽比例
        inter.setWidths(widthRatio);
        //标题
        PdfPCell cells[] = new PdfPCell[titles.length];
        PdfPRow titleRow = new PdfPRow(cells);
        //单元格
        for (int i = 0; i < titles.length; i++) {
            cells[i] = new PdfPCell(new Paragraph(titles[i], font));
            cells[i].setBackgroundColor(titleBgColor == null ? BaseColor.LIGHT_GRAY : titleBgColor);
            cells[i].setPaddingTop(6f);
            cells[i].setPaddingBottom(6f);
        }
        //把第一行添加到集合
        listRow.add(titleRow);
        for (String[] value : values) {
            PdfPCell childCells[] = new PdfPCell[value.length];
            PdfPRow row = new PdfPRow(childCells);
            //添加列
            for (int i = 0; i < value.length; i++) {
                childCells[i] = new PdfPCell(new Paragraph(value[i], font));//单元格内容
                childCells[i].setPaddingTop(4f);
                childCells[i].setPaddingBottom(4f);
            }
            //添加行
            listRow.add(row);
        }
        document.add(inter);
        return listRow;
    }


    /**
     * PDF添加请求参数
     *
     * @param document
     * @param projectId
     * @param id
     * @param font
     * @throws DocumentException
     */
    private void pdfAddRequestParams(Document document, String projectId, String id, Font font) throws DocumentException {
        List<RequestArgEntity> globals = mRequestArgService.getGlobalRequestArgs(projectId);

        if (globals != null && globals.size() > 0) {
            //设置列宽
            float[] columnWidths = {3f, 1f, 2f, 1f, 4f};
            String[][] values = new String[globals.size()][columnWidths.length];
            for (int i = 0; i < globals.size(); i++) {
                RequestArgEntity entity = globals.get(i);
                values[i][0] = entity.getName() == null ? "" : entity.getName();
                values[i][1] = entity.getRequire() == null ? "true" : (entity.getRequire() ? "true" : "false");
                if (entity.getTypeId() == null) {
                    entity.setTypeId(7);
                }
                switch (entity.getTypeId()) {
                    case 0:
                        values[i][2] = "string";
                        break;
                    case 1:
                        values[i][2] = "int";
                        break;
                    case 9:
                        values[i][2] = "float";
                        break;
                    case 2:
                        values[i][2] = "object";
                        break;
                    case 3:
                        values[i][2] = "array[object]";
                        break;
                    case 4:
                        values[i][2] = "array[string]";
                        break;
                    case 5:
                        values[i][2] = "array";
                        break;
                    case 6:
                        values[i][2] = "file";
                        break;
                    case 8:
                        values[i][2] = "array[int]";
                        break;
                    case 10:
                        values[i][2] = "array[float]";
                        break;
                    default:
                        values[i][2] = "未知";
                        break;
                }
                values[i][3] = entity.getDefaultValue() == null ? "" : entity.getDefaultValue();
                values[i][4] = entity.getNote() == null ? "" : entity.getNote();
            }
            addTable(document, "全局请求参数", new String[]{"名称", "必选", "类型", "默认值", "说明"}, columnWidths, values, null, font);
        }

        List<RequestArgEntity> args = mRequestArgService.getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(id, "0"));

        if (args != null && args.size() > 0) {
            //设置列宽
            float[] columnWidths = {3f, 1f, 2f, 1f, 4f};
            String[][] values = new String[args.size()][columnWidths.length];
            for (int i = 0; i < args.size(); i++) {
                RequestArgEntity entity = args.get(i);
                values[i][0] = entity.getName() == null ? "" : entity.getName();
                values[i][1] = entity.getRequire() == null ? "true" : (entity.getRequire() ? "true" : "false");
                if (entity.getTypeId() == null) {
                    entity.setTypeId(7);
                }
                switch (entity.getTypeId()) {
                    case 0:
                        values[i][2] = "string";
                        break;
                    case 1:
                        values[i][2] = "int";
                        break;
                    case 9:
                        values[i][2] = "float";
                        break;
                    case 2:
                        values[i][2] = "object";
                        break;
                    case 3:
                        values[i][2] = "array[object]";
                        break;
                    case 4:
                        values[i][2] = "array[string]";
                        break;
                    case 5:
                        values[i][2] = "array";
                        break;
                    case 6:
                        values[i][2] = "file";
                        break;
                    case 8:
                        values[i][2] = "array[int]";
                        break;
                    case 10:
                        values[i][2] = "array[float]";
                        break;
                    default:
                        values[i][2] = "未知";
                        break;
                }
                values[i][3] = entity.getDefaultValue() == null ? "" : entity.getDefaultValue();
                values[i][4] = entity.getNote() == null ? "" : entity.getNote();
            }
            addTable(document, "其他请求参数", new String[]{"名称", "必选", "类型", "默认值", "说明"}, columnWidths, values, null, font);

        }
    }


    private void pdfAddResponseParams(Document document, String projectId, String id, Font font) throws DocumentException {
        List<ResponseArgEntity> globals = mResponseArgService.getGlobalResponseArgs(projectId);
        if (globals != null && globals.size() > 0) {

            //设置列宽
            float[] columnWidths = {4f, 2f, 1f, 4f};
            String[][] values = new String[globals.size()][columnWidths.length];
            for (int i = 0; i < globals.size(); i++) {
                ResponseArgEntity entity = globals.get(i);
                values[i][0] = entity.getName() == null ? "" : entity.getName();
                if (entity.getTypeId() == null) {
                    entity.setTypeId(7);
                }
                switch (entity.getTypeId()) {
                    case 0:
                        values[i][1] = "string";
                        break;
                    case 1:
                        values[i][1] = "int";
                        break;
                    case 9:
                        values[i][1] = "float";
                        break;
                    case 2:
                        values[i][1] = "object";
                        break;
                    case 3:
                        values[i][1] = "array[object]";
                        break;
                    case 4:
                        values[i][1] = "array[string]";
                        break;
                    case 5:
                        values[i][1] = "array";
                        break;
                    case 6:
                        values[i][1] = "file";
                        break;
                    case 8:
                        values[i][1] = "array[int]";
                        break;
                    case 10:
                        values[i][1] = "array[float]";
                        break;
                    default:
                        values[i][1] = "未知";
                        break;
                }
                values[i][2] = entity.getDefaultValue() == null ? "" : entity.getDefaultValue();
                values[i][3] = entity.getNote() == null ? "" : entity.getNote();
            }
            addTable(document, "全局响应数据", new String[]{"名称", "类型", "默认值", "说明"}, columnWidths, values, null, font);
        }
        List<ResponseArgEntity> args = mResponseArgService.getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(id, "0"));
        if (args != null && args.size() > 0) {
            addSmallTitle(document, "其他响应数据", font);
            font.setStyle(Font.NORMAL);
            font.setSize(12f);
            //设置列宽
            float[] columnWidths = {4f, 2f, 1f, 4f};
            PdfPTable inter = new PdfPTable(columnWidths.length);
            inter.setWidthPercentage(100); // 宽度100%填充
            inter.setSpacingBefore(1f); // 前间距
            inter.setSpacingAfter(4f); // 后间距
            inter.setPaddingTop(0);
            List<PdfPRow> listRow = inter.getRows();
            inter.setWidths(columnWidths);
            //标题
            PdfPCell cells1[] = new PdfPCell[columnWidths.length];
            PdfPRow row1 = new PdfPRow(cells1);
            //单元格
            cells1[0] = new PdfPCell(new Paragraph("名称", font));//单元格内容
            cells1[1] = new PdfPCell(new Paragraph("类型", font));
            cells1[2] = new PdfPCell(new Paragraph("默认值", font));
            cells1[3] = new PdfPCell(new Paragraph("说明", font));
            cells1[0].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[1].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[2].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[3].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[0].setPaddingTop(6f);
            cells1[0].setPaddingBottom(6f);
            cells1[1].setPaddingTop(6f);
            cells1[1].setPaddingBottom(6f);
            cells1[2].setPaddingTop(6f);
            cells1[2].setPaddingBottom(6f);
            cells1[3].setPaddingTop(6f);
            cells1[3].setPaddingBottom(6f);
            //把第一行添加到集合
            listRow.add(row1);
            for (ResponseArgEntity entity : args) {
                //行1
                PdfPCell cells[] = new PdfPCell[columnWidths.length];
                PdfPRow row = new PdfPRow(cells);
                //单元格
                cells[0] = new PdfPCell(new Paragraph(entity.getName(), font));//单元格内容
                if (entity.getTypeId() == null) {
                    entity.setTypeId(7);
                }
                switch (entity.getTypeId()) {
                    case 0:
                        cells[1] = new PdfPCell(new Paragraph("string", font));
                        break;
                    case 1:
                        cells[1] = new PdfPCell(new Paragraph("int", font));
                        break;
                    case 9:
                        cells[1] = new PdfPCell(new Paragraph("float", font));
                        break;
                    case 2:
                        cells[1] = new PdfPCell(new Paragraph("object", font));
                        break;
                    case 3:
                        cells[1] = new PdfPCell(new Paragraph("array[object]", font));
                        break;
                    case 4:
                        cells[1] = new PdfPCell(new Paragraph("array[string]", font));
                        break;
                    case 5:
                        cells[1] = new PdfPCell(new Paragraph("array", font));
                        break;
                    case 6:
                        cells[1] = new PdfPCell(new Paragraph("file", font));
                        break;
                    case 8:
                        cells[1] = new PdfPCell(new Paragraph("array[int]", font));
                        break;
                    case 10:
                        cells[1] = new PdfPCell(new Paragraph("array[float]", font));
                        break;
                    default:
                        cells[1] = new PdfPCell(new Paragraph("未知", font));
                        break;
                }
                cells[2] = new PdfPCell(new Paragraph(entity.getDefaultValue() == null ? "" : entity.getDefaultValue(), font));
                cells[3] = new PdfPCell(new Paragraph(entity.getNote() == null ? "" : entity.getNote(), font));
                cells[0].setPaddingTop(4f);
                cells[0].setPaddingBottom(4f);
                cells[1].setPaddingTop(4f);
                cells[1].setPaddingBottom(4f);
                cells[2].setPaddingTop(4f);
                cells[2].setPaddingBottom(4f);
                cells[3].setPaddingTop(4f);
                cells[3].setPaddingBottom(4f);
                //把第一行添加到集合
                listRow.add(row);
                pdfAddChildResponseParams("    ", listRow, id, entity.getId(), font);
            }
            document.add(inter);
        }
    }

    private void pdfAddChildResponseParams(String spance, List<PdfPRow> listRow, String id, String pid, Font font) throws DocumentException {
        List<ResponseArgEntity> args = mResponseArgService.getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(id, pid));
        if (args != null && args.size() > 0) {
            //把第一行添加到集合
            for (ResponseArgEntity entity : args) {
                //行1
                PdfPCell cells[] = new PdfPCell[4];
                PdfPRow row = new PdfPRow(cells);
                //单元格
                cells[0] = new PdfPCell(new Paragraph(spance + entity.getName(), font));//单元格内容
                if (entity.getTypeId() == null) {
                    entity.setTypeId(7);
                }
                switch (entity.getTypeId()) {
                    case 0:
                        cells[1] = new PdfPCell(new Paragraph("string", font));
                        break;
                    case 1:
                        cells[1] = new PdfPCell(new Paragraph("int", font));
                        break;
                    case 9:
                        cells[1] = new PdfPCell(new Paragraph("float", font));
                        break;
                    case 2:
                        cells[1] = new PdfPCell(new Paragraph("object", font));
                        break;
                    case 3:
                        cells[1] = new PdfPCell(new Paragraph("array[object]", font));
                        break;
                    case 4:
                        cells[1] = new PdfPCell(new Paragraph("array[string]", font));
                        break;
                    case 5:
                        cells[1] = new PdfPCell(new Paragraph("array", font));
                        break;
                    case 6:
                        cells[1] = new PdfPCell(new Paragraph("file", font));
                        break;
                    case 8:
                        cells[1] = new PdfPCell(new Paragraph("array[int]", font));
                        break;
                    case 10:
                        cells[1] = new PdfPCell(new Paragraph("array[float]", font));
                        break;
                    default:
                        cells[1] = new PdfPCell(new Paragraph("未知", font));
                        break;
                }
                cells[2] = new PdfPCell(new Paragraph(entity.getDefaultValue() == null ? "" : entity.getDefaultValue(), font));
                cells[3] = new PdfPCell(new Paragraph(entity.getNote() == null ? "" : entity.getNote(), font));
                cells[0].setPaddingTop(4f);
                cells[0].setPaddingBottom(4f);
                cells[1].setPaddingTop(4f);
                cells[1].setPaddingBottom(4f);
                cells[2].setPaddingTop(4f);
                cells[2].setPaddingBottom(4f);
                cells[3].setPaddingTop(4f);
                cells[3].setPaddingBottom(4f);
                //把第一行添加到集合
                listRow.add(row);
                pdfAddChildResponseParams(spance + "    ", listRow, id, entity.getId(), font);
            }
        }

    }


    private void addLargeTitle(Document document, String text, Font font) {
        if (font != null) {
            font.setSize(26f);
            font.setStyle(Font.BOLD);
            Paragraph paragraph = new Paragraph((text == null ? "" : text), font);
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
        } else {
            Paragraph paragraph = new Paragraph((text == null ? "" : text));
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
        }
    }

    /**
     * 添加分组标题(带标签)
     *
     * @param document Document对象
     * @param text     标题内容
     * @param number   标题数字
     * @param font     字体
     * @return Chapter对象
     */
    private Chapter addGroup(Document document, String text, int number, Font font) {
        addTextLine(document, "", font);
        addTextLine(document, "", font);
        if (font != null) {
            font.setSize(22f);
            font.setStyle(Font.BOLD);
            Paragraph paragraph = new Paragraph((text == null ? "" : text), font);
            Chapter chapter = new Chapter(paragraph, number);
            chapter.setBookmarkOpen(true);
            chapter.setBookmarkTitle(number + ". " + text);
            chapter.setTriggerNewPage(false);
            try {
                document.add(chapter);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
            return chapter;
        } else {
            Paragraph paragraph = new Paragraph((text == null ? "" : text));
            Chapter chapter = new Chapter(paragraph, number);
            chapter.setBookmarkOpen(true);
            chapter.setBookmarkTitle(number + ". " + text);
            chapter.setTriggerNewPage(false);
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
            return chapter;
        }
    }


    /**
     * 添加组内小标题(带标签)
     *
     * @param document Document对象
     * @param chapter  Chapter对象
     * @param text     标题
     * @param number   序号
     * @param font     字体
     */
    private void addGroupItem(Document document, Chapter chapter, String text, int groupNumber, int number, Font font) {
        addTextLine(document, "", font);
        if (font != null) {
            font.setSize(18f);
            font.setStyle(Font.BOLD);
            Paragraph paragraph = new Paragraph((text == null ? "" : text), font);
            Section section = chapter.addSection(paragraph);
            section.setBookmarkOpen(true);
            section.setBookmarkTitle(groupNumber + "." + number + ". " + text);
            section.setTriggerNewPage(false);
            section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
            try {
                document.add(section);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
        } else {
            Paragraph paragraph = new Paragraph((text == null ? "" : text));
            Section section = chapter.addSection(paragraph);
            section.setBookmarkOpen(true);
            section.setBookmarkTitle(groupNumber + "." + number + ". " + text);
            section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
            section.setTriggerNewPage(false);
            try {
                document.add(section);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
        }
    }

    private void addText(Document document, String text, Font font, boolean bold) {
        if (font == null) {
            Chunk chunk = new Chunk(text == null ? "" : text);
            try {
                document.add(chunk);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
        } else {
            font.setSize(12f);
            font.setStyle(bold ? Font.BOLD : Font.NORMAL);
            Chunk chunk = new Chunk(text == null ? "" : text, font);
            try {
                document.add(chunk);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
        }
    }

    private void addTextLine(Document document, String text, Font font) {
        if (font == null) {
            Chunk chunk = new Chunk((text == null ? "" : text) + "\n");
            try {
                document.add(chunk);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
        } else {
            font.setSize(12f);
            font.setStyle(Font.NORMAL);
            Chunk chunk = new Chunk((text == null ? "" : text) + "\n", font);
            try {
                document.add(chunk);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
        }
    }

    /**
     * 添加下划线(带超链接)
     *
     * @param document Document对象
     * @param text     链接文字
     * @param fontPath 字体路径
     * @throws IOException       异常
     * @throws DocumentException 异常
     */
    private void addUnderLineText(Document document, String text, String fontPath) throws IOException, DocumentException {
        Font font = new Font(BaseFont.createFont(fontPath + "Arial.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 12f, Font.UNDERLINE);
        font.setColor(BaseColor.BLUE);
        Anchor anchor = new Anchor(text, font);
        anchor.setReference(text);
        anchor.setName(text);
        document.add(anchor);
    }


    private void addSmallTitle(Document document, String text, Font font) {
        if (font != null) {
            font.setSize(14f);
            font.setStyle(Font.BOLD);
            Chunk paragraph = new Chunk(text == null ? "" : text, font);
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
        } else {
            Chunk paragraph = new Chunk(text == null ? "" : text);
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
                LOGGER.error("PDF ERROR", e);
            }
        }

    }

    /**
     * 【功能描述：添加图片和文字水印】 【功能详细描述：功能详细描述】
     *
     * @param srcFile    待加水印文件
     * @param destFile   加水印后存放地址
     * @param text       加水印的文本内容
     * @param textWidth  文字横坐标
     * @param textHeight 文字纵坐标
     * @throws Exception
     */
    public String addWaterMark(String srcFile, String destFile, String text,
                               int textWidth, int textHeight) throws Exception {
        // 待加水印的文件
        PdfReader reader = new PdfReader(srcFile);
        // 加完水印的文件
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(
                destFile));
        int total = reader.getNumberOfPages() + 1;
        PdfContentByte content;
        // 设置字体
        BaseFont font = BaseFont.createFont();
        // 循环对每页插入水印
        for (int i = 1; i < total; i++) {
            // 水印的起始
            content = stamper.getUnderContent(i);
            // 开始
            content.beginText();
            // 设置颜色 默认为蓝色
            content.setColorFill(new BaseColor(238, 238, 238));
            // content.setColorFill(Color.GRAY);
            // 设置字体及字号
            content.setFontAndSize(font, 50);
            // 设置起始位置
            // content.setTextMatrix(400, 880);
            content.setTextMatrix(textWidth, textHeight);
            // 开始写入水印
            content.showTextAligned(Element.ALIGN_CENTER, text, textWidth,
                    textHeight, 45);
            content.endText();
        }
        stamper.close();
        return destFile;
    }

    /***********************************下载 PDF 结束**************************************/


    /***********************************下载 API 开始**************************************/
    @Override
    public ResponseEntity<byte[]> downloadApi(String projectId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return null;
        }

        ProjectEntity project = mProjectService.get(projectId);
        if (project == null) {
            return null;
        }

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String realPath = request.getRealPath("");
            if (realPath != null) {
                String mPath = realPath + File.separator + "API";
                File dir = new File(mPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                CacheEntity cacheEntity = new CacheEntity();
                cacheEntity.setCachePath(mPath);
                try {
                    List<CacheEntity> cacheEntities = mCacheService.executeCriteria(new Criterion[]{
                            Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                            Restrictions.eq("cachePath", mPath)});
                    if (cacheEntities == null || cacheEntities.size() == 0) {
                        mCacheService.save(cacheEntity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("API ERROR", e);
                }
                /*Api*/
                String appName = project.getName();
                String appDirPath = mPath + File.separator + appName;
                File appDir = new File(appDirPath);
                if (!appDir.exists()) {
                    /*如果不存在，创建app目录*/
                    appDir.mkdirs();
                } else {
                    /*如果存在，删除该目录里的所有文件*/
                    me.zhouzhuo810.zzapidoc.common.utils.FileUtils.deleteFiles(appDirPath);
                }

                String packageName = project.getPackageName();
                if (packageName == null || packageName.length() == 0) {
                    packageName = "com.example.zzapidoc";
                }
                String packagePath = packageName.replace(".", File.separator);
                String javaDir = appDirPath
                        + File.separator + "app"
                        + File.separator + "src"
                        + File.separator + "main"
                        + File.separator + "java"
                        + File.separator + packagePath
                        + File.separator + "common"
                        + File.separator + "api";
                ApiTool.createApi(convertToJson(project), packageName, javaDir);

                /*压缩文件*/
                String zipName = System.currentTimeMillis() + ".zip";
                String zipPath = mPath + File.separator + zipName;
                ZipUtils.doCompress(appDirPath, zipPath);

                //压缩完毕，删除源文件
                FileUtil.deleteContents(new File(appDirPath));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", zipName);
                return new ResponseEntity<byte[]>(org.apache.commons.io.FileUtils.readFileToByteArray(new File(zipPath)), headers, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /***********************************下载 API 结束**************************************/


    /***********************************下载 单个接口API 开始**************************************/
    @Override
    public ResponseEntity<byte[]> downloadInterfaceApi(String projectId, String interfaceId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return null;
        }

        ProjectEntity project = mProjectService.get(projectId);
        if (project == null) {
            return null;
        }

        InterfaceEntity interfaceEntity = get(interfaceId);
        if (interfaceEntity == null) {
            return null;
        }

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String realPath = request.getRealPath("");
            if (realPath != null) {
                String mPath = realPath + File.separator + "API";
                File dir = new File(mPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                CacheEntity cacheEntity = new CacheEntity();
                cacheEntity.setCachePath(mPath);
                try {
                    List<CacheEntity> cacheEntities = mCacheService.executeCriteria(new Criterion[]{
                            Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                            Restrictions.eq("cachePath", mPath)});
                    if (cacheEntities == null || cacheEntities.size() == 0) {
                        mCacheService.save(cacheEntity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("API ERROR", e);
                }
                /*Api*/
                String appName = project.getName() + "_" + interfaceEntity.getName();
                String appDirPath = mPath + File.separator + appName;
                File appDir = new File(appDirPath);
                if (!appDir.exists()) {
                    /*如果不存在，创建app目录*/
                    appDir.mkdirs();
                } else {
                    /*如果存在，删除该目录里的所有文件*/
                    me.zhouzhuo810.zzapidoc.common.utils.FileUtils.deleteFiles(appDirPath);
                }

                String packageName = project.getPackageName();
                if (packageName == null || packageName.length() == 0) {
                    packageName = "com.example.zzapidoc";
                }
                String packagePath = packageName.replace(".", File.separator);
                String javaDir = appDirPath
                        + File.separator + "app"
                        + File.separator + "src"
                        + File.separator + "main"
                        + File.separator + "java"
                        + File.separator + packagePath
                        + File.separator + "common"
                        + File.separator + "api";
                ApiTool.createApi(convertToJson(project, interfaceEntity), packageName, javaDir);

                /*压缩文件*/
                String zipName = System.currentTimeMillis() + ".zip";
                String zipPath = mPath + File.separator + zipName;
                ZipUtils.doCompress(appDirPath, zipPath);

                //压缩完毕，删除源文件
                FileUtil.deleteContents(new File(appDirPath));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", zipName);
                return new ResponseEntity<byte[]>(org.apache.commons.io.FileUtils.readFileToByteArray(new File(zipPath)), headers, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /***********************************下载 单个接口API 结束**************************************/


}
