package me.zhouzhuo810.zzapidoc.project.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import me.zhouzhuo810.zzapidoc.cache.entity.CacheEntity;
import me.zhouzhuo810.zzapidoc.cache.service.CacheService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.InterfaceDao;
import me.zhouzhuo810.zzapidoc.project.entity.*;
import me.zhouzhuo810.zzapidoc.project.service.*;
import me.zhouzhuo810.zzapidoc.project.utils.InterfaceUtils;
import me.zhouzhuo810.zzapidoc.project.utils.PdfReportM1HeaderFooter;
import me.zhouzhuo810.zzapidoc.project.utils.ResponseArgUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.apache.commons.io.FileUtils;
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

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/22.
 */
@Service
public class InterfaceServiceImpl extends BaseServiceImpl<InterfaceEntity> implements InterfaceService {

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
                case ResponseArgEntity.TYPE_NUMBER:

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
                m.put("global", requestArgEntity.getGlobal() == null ? false : requestArgEntity.getGlobal());
                m.put("note", requestArgEntity.getNote() == null ? "" : requestArgEntity.getNote());
                m.put("pid", requestArgEntity.getPid());
                m.put("typeId", requestArgEntity.getTypeId());
                response.add(m.build());
            }
            map.put("globalResponseArgs", response);
        }
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceId(interfaceId));
        if (responseArgEntities != null) {
            List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
            for (ResponseArgEntity requestArgEntity : responseArgEntities) {
                MapUtils m = new MapUtils();
                m.put("id", requestArgEntity.getId());
                m.put("name", requestArgEntity.getName());
                m.put("global", requestArgEntity.getGlobal() == null ? false : requestArgEntity.getGlobal());
                m.put("note", requestArgEntity.getNote() == null ? "" : requestArgEntity.getNote());
                m.put("pid", requestArgEntity.getPid());
                m.put("typeId", requestArgEntity.getTypeId());
                response.add(m.build());
            }
            map.put("responseArgs", response);
        }
        return new BaseResult(1, "ok", map.build());
    }


    @Override
    public BaseResult addInterfaceExample(String interfaceId, String example, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
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
        List<InterfaceGroupEntity> groups = mInterfaceGroupService.executeCriteria(InterfaceUtils.getInterfaceByProjectId(projectId));
        /*模块数组开始*/
        stringer
                .key("modules").array();
        if (groups != null) {
            /*分组数组开始*/
            stringer.object()
                    .key("name").value("默认模块")
                   /*请求参数*/
                    .key("requestHeaders").value(globalHeaderToJson(projectId))
                    .key("requestArgs").value(globalRequestToJson(projectId))
                    /*返回参数*/
                    .key("responseArgs").value(globalResponseToJson(projectId))
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
                            .key("description").value(entity.getNote() == null ? "" : entity.getNote());

                    /*请求参数*/
                    stringer.key("requestArgs").value(requestToJson(entity.getId(), "0"));

                    /*返回参数*/
                    stringer.key("responseArgs").value(responseToJson(projectId, entity.getId(), "0"));

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
                    String fileName = me.zhouzhuo810.zzapidoc.common.utils.FileUtils.saveFileToServer(stringer.toString(), mPath);
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
                        .key("require").value(requestArgEntity.getRequire() == null ? true : requestArgEntity.getRequire())
                        .key("typeId").value(requestArgEntity.getTypeId())
                        .key("description").value(requestArgEntity.getNote() == null ? "" : requestArgEntity.getNote());
                if (requestArgEntity.getTypeId()==null) {
                    requestArgEntity.setTypeId(7);
                }
                switch (requestArgEntity.getTypeId()) {
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
                        stringer.key("children").array().endArray();
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
        stringer.endArray();
        return stringer.toString();
    }

    private String responseToJson(String projectId, String interfaceId, String pid) {
        JSONStringer stringer = new JSONStringer();
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid));
        List<ResponseArgEntity> globals = mResponseArgService.executeCriteria(ResponseArgUtils.getGlobal(projectId));
        stringer.array();
        if (globals != null) {
            for (ResponseArgEntity responseArgEntity : globals) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
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
        }
        if (responseArgEntities != null) {
            for (ResponseArgEntity responseArgEntity : responseArgEntities) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
                        .key("pid").value(responseArgEntity.getPid())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                if (responseArgEntity.getTypeId()==null) {
                    responseArgEntity.setTypeId(7);
                }
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
                        .key("require").value(req.getRequire() == null ? true : req.getRequire())
                        .key("typeId").value(req.getTypeId())
                        .key("description").value(req.getNote() == null ? "" : req.getNote());
                if (req.getTypeId()==null) {
                    req.setTypeId(7);
                }
                switch (req.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_NUMBER:
                        stringer.key("type").value("number");
                        stringer.key("children").array().endArray();
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
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                if (responseArgEntity.getTypeId()==null) {
                    responseArgEntity.setTypeId(7);
                }
                switch (responseArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_NUMBER:
                        stringer.key("type").value("number");
                        stringer.key("children").array().endArray();
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
                        .key("pid").value(responseArgEntity.getPid())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                if (responseArgEntity.getTypeId()==null) {
                    responseArgEntity.setTypeId(7);
                }
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
        }
        stringer.endArray();
    }

    /***********************************下载JSON 结束**************************************/

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
            ClassLoader classLoader = this.getClass().getClassLoader();
            URL resource = classLoader.getResource("../../empty_file.txt");
            if (resource != null) {
                String path = resource.getPath();
                if (path != null) {
                    final String fontPath = new File(path).getParent() + File.separator + "font/";
                    String mPath = new File(path).getParent() + File.separator + "PDF";
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
                    }
                    String realFileName = System.currentTimeMillis() + ".pdf";
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
                        addText(document, "创建人：", fontChinese);
                        addTextLine(document, project.getCreateUserName(), fontChinese);
                        addText(document, "创建时间：", fontChinese);
                        addTextLine(document, DataUtils.formatDate(project.getCreateTime()), fontChinese);
                        addText(document, "项目说明：", fontChinese);
                        addTextLine(document, project.getNote(), fontChinese);

                        List<InterfaceGroupEntity> groups = mInterfaceGroupService.executeCriteria(InterfaceUtils.getInterfaceByProjectId(projectId));
                        /*模块数组开始*/
                        if (groups != null) {

                            for (int i = 0; i < groups.size(); i++) {
                                InterfaceGroupEntity interfaceGroupEntity = groups.get(i);

                                addMidTitle(document, (i + 1) + ". " + interfaceGroupEntity.getName(), fontChinese);
                                fontChinese.setStyle(Font.NORMAL);
                                addText(document, "创建人：", fontChinese);
                                addTextLine(document, interfaceGroupEntity.getCreateUserName(), fontChinese);
                                addText(document, "创建时间：", fontChinese);
                                addTextLine(document, DataUtils.formatDate(interfaceGroupEntity.getCreateTime()), fontChinese);
                                addText(document, "服务器Ip地址：", fontChinese);
                                addUnderLineText(document, interfaceGroupEntity.getIp(), fontPath);

                                List<InterfaceEntity> list = getBaseDao().executeCriteria(InterfaceUtils.getInterfaceByGroupId(interfaceGroupEntity.getId()));
                                if (list == null) {
                                    continue;
                                }
                                /*组的接口数组*/
                                for (int i1 = 0; i1 < list.size(); i1++) {
                                    InterfaceEntity entity = list.get(i1);

                                    addDuanLuo(document, (i + 1) + "." + (i1 + 1) + ". " + entity.getName(), fontChinese);
                                    fontChinese.setStyle(Font.NORMAL);
                                    addText(document, "创建人：", fontChinese);
                                    addTextLine(document, entity.getCreateUserName(), fontChinese);
                                    addText(document, "创建时间：", fontChinese);
                                    addTextLine(document, DataUtils.formatDate(entity.getCreateTime()), fontChinese);
                                    addText(document, "请求方式：", fontChinese);
                                    addTextLine(document, entity.getHttpMethodName(), null);
                                    addText(document, "请求地址：", fontChinese);
                                    addTextLine(document, entity.getPath(), null);
                                    addText(document, "接口说明：", fontChinese);
                                    addTextLine(document, entity.getNote(), fontChinese);

                                    pdfAddRequestHeaders(document, projectId, entity.getId(), fontChinese);
                                    pdfAddRequestParams(document, projectId, entity.getId(), fontChinese);
                                    pdfAddResponseParams(document, projectId, entity.getId(), fontChinese);

                                    addSmallTitle(document, "返回示例", fontChinese);
                                    addTextLine(document, "\n", null);
                                    fontChinese.setStyle(Font.NORMAL);
                                    addTextLine(document, entity.getExample() == null ? "" : entity.getExample(), null);

                                }
                            }
                        }
                        //关闭文档
                        document.close();
                        //关闭书写器
                        pdfWriter.close();

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                        headers.setContentDispositionFormData("attachment", realFileName);
                        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(mPath + File.separator + realFileName)), headers, HttpStatus.CREATED);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
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
            addSmallTitle(document, "全局请求头", font);
            font.setStyle(Font.NORMAL);
            PdfPTable inter = new PdfPTable(3);
            inter.setWidthPercentage(100); // 宽度100%填充
            inter.setSpacingBefore(1f); // 前间距
            inter.setSpacingAfter(4f); // 后间距
            List<PdfPRow> listRow = inter.getRows();
            //设置列宽
            float[] columnWidths = {3f, 2f, 5f};
            inter.setWidths(columnWidths);
            //标题
            PdfPCell cells1[] = new PdfPCell[3];
            PdfPRow row1 = new PdfPRow(cells1);
            //单元格
            cells1[0] = new PdfPCell(new Paragraph("名称", font));//单元格内容
            cells1[1] = new PdfPCell(new Paragraph("默认值", font));
            cells1[2] = new PdfPCell(new Paragraph("说明", font));
            cells1[0].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[1].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[2].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[0].setPaddingTop(6f);
            cells1[0].setPaddingBottom(6f);
            cells1[1].setPaddingTop(6f);
            cells1[1].setPaddingBottom(6f);
            cells1[2].setPaddingTop(6f);
            cells1[2].setPaddingBottom(6f);
            //把第一行添加到集合
            listRow.add(row1);
            for (RequestHeaderEntity entity : globals) {
                //行1
                PdfPCell cells[] = new PdfPCell[3];
                PdfPRow row = new PdfPRow(cells);
                //单元格
                cells[0] = new PdfPCell(new Paragraph(entity.getName()));//单元格内容
                cells[1] = new PdfPCell(new Paragraph(entity.getValue() == null ? "" : entity.getValue()));
                cells[2] = new PdfPCell(new Paragraph(entity.getNote() == null ? "" : entity.getNote(), font));
                cells[0].setPaddingTop(4f);
                cells[0].setPaddingBottom(4f);
                cells[1].setPaddingTop(4f);
                cells[1].setPaddingBottom(4f);
                cells[2].setPaddingTop(4f);
                cells[2].setPaddingBottom(4f);
                //把第一行添加到集合
                listRow.add(row);
            }
            document.add(inter);
        }

        if (args != null && args.size() > 0) {
            addSmallTitle(document, "其他请求头", font);
            font.setStyle(Font.NORMAL);
            PdfPTable inter = new PdfPTable(3);
            inter.setWidthPercentage(100); // 宽度100%填充
            inter.setSpacingBefore(1f); // 前间距
            inter.setSpacingAfter(4f); // 后间距
            List<PdfPRow> listRow = inter.getRows();
            //设置列宽
            float[] columnWidths = {3f, 2f, 5f};
            inter.setWidths(columnWidths);
            //标题
            PdfPCell cells1[] = new PdfPCell[3];
            PdfPRow row1 = new PdfPRow(cells1);
            //单元格
            cells1[0] = new PdfPCell(new Paragraph("名称", font));//单元格内容
            cells1[1] = new PdfPCell(new Paragraph("默认值", font));
            cells1[2] = new PdfPCell(new Paragraph("说明", font));
            cells1[0].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[1].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[2].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[0].setPaddingTop(6f);
            cells1[0].setPaddingBottom(6f);
            cells1[1].setPaddingTop(6f);
            cells1[1].setPaddingBottom(6f);
            cells1[2].setPaddingTop(6f);
            cells1[2].setPaddingBottom(6f);
            //把第一行添加到集合
            listRow.add(row1);
            for (RequestHeaderEntity entity : args) {
                //行1
                PdfPCell cells[] = new PdfPCell[3];
                PdfPRow row = new PdfPRow(cells);
                //单元格
                cells[0] = new PdfPCell(new Paragraph(entity.getName()));//单元格内容
                cells[1] = new PdfPCell(new Paragraph(entity.getValue() == null ? "" : entity.getValue()));
                cells[2] = new PdfPCell(new Paragraph(entity.getNote() == null ? "" : entity.getNote(), font));
                cells[0].setPaddingTop(4f);
                cells[0].setPaddingBottom(4f);
                cells[1].setPaddingTop(4f);
                cells[1].setPaddingBottom(4f);
                cells[2].setPaddingTop(4f);
                cells[2].setPaddingBottom(4f);
                //把第一行添加到集合
                listRow.add(row);
            }
            document.add(inter);
        }
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
        List<RequestArgEntity> args = mRequestArgService.getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(id, "0"));

        if (globals != null && globals.size() > 0) {
            addSmallTitle(document, "全局请求参数", font);
            font.setStyle(Font.NORMAL);
            PdfPTable inter = new PdfPTable(4);
            inter.setWidthPercentage(100); // 宽度100%填充
            inter.setSpacingBefore(1f); // 前间距
            inter.setSpacingAfter(4f); // 后间距
            List<PdfPRow> listRow = inter.getRows();
            //设置列宽
            float[] columnWidths = {3f, 1f, 2f, 5f};
            inter.setWidths(columnWidths);
            //标题
            PdfPCell cells1[] = new PdfPCell[4];
            PdfPRow row1 = new PdfPRow(cells1);
            //单元格
            cells1[0] = new PdfPCell(new Paragraph("名称", font));//单元格内容
            cells1[1] = new PdfPCell(new Paragraph("必选", font));
            cells1[2] = new PdfPCell(new Paragraph("类型", font));
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
            for (RequestArgEntity entity : globals) {
                //行1
                PdfPCell cells[] = new PdfPCell[4];
                PdfPRow row = new PdfPRow(cells);
                //单元格
                cells[0] = new PdfPCell(new Paragraph(entity.getName()));//单元格内容
                cells[1] = new PdfPCell(new Paragraph(entity.getRequire() == null ? "true" : (entity.getRequire() ? "true" : "false")));
                if (entity.getTypeId()==null) {
                    entity.setTypeId(7);
                }
                switch (entity.getTypeId()) {
                    case 0:
                        cells[2] = new PdfPCell(new Paragraph("string"));
                        break;
                    case 1:
                        cells[2] = new PdfPCell(new Paragraph("number"));
                        break;
                    case 2:
                        cells[2] = new PdfPCell(new Paragraph("object"));
                        break;
                    case 3:
                        cells[2] = new PdfPCell(new Paragraph("array[object]"));
                        break;
                    case 4:
                        cells[2] = new PdfPCell(new Paragraph("array[string]"));
                        break;
                    case 5:
                        cells[2] = new PdfPCell(new Paragraph("array"));
                        break;
                    case 6:
                        cells[2] = new PdfPCell(new Paragraph("file"));
                        break;
                    default:
                        cells[2] = new PdfPCell(new Paragraph("未知"));
                        break;
                }
                cells[3] = new PdfPCell(new Paragraph(entity.getNote(), font));
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
            }
            document.add(inter);
        }

        if (args != null && args.size() > 0) {
            addSmallTitle(document, "其他请求参数", font);
            font.setStyle(Font.NORMAL);
            PdfPTable inter = new PdfPTable(4);
            inter.setWidthPercentage(100); // 宽度100%填充
            inter.setSpacingBefore(1f); // 前间距
            inter.setSpacingAfter(4f); // 后间距
            inter.setPaddingTop(0);
            List<PdfPRow> listRow = inter.getRows();
            //设置列宽
            float[] columnWidths = {3f, 1f, 2f, 5f};
            inter.setWidths(columnWidths);
            //标题
            PdfPCell cells1[] = new PdfPCell[4];
            PdfPRow row1 = new PdfPRow(cells1);
            //单元格
            cells1[0] = new PdfPCell(new Paragraph("名称", font));//单元格内容
            cells1[1] = new PdfPCell(new Paragraph("必选", font));
            cells1[2] = new PdfPCell(new Paragraph("类型", font));
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
            for (RequestArgEntity entity : args) {
                //行1
                PdfPCell cells[] = new PdfPCell[4];
                PdfPRow row = new PdfPRow(cells);
                //单元格
                cells[0] = new PdfPCell(new Paragraph(entity.getName()));//单元格内容
                cells[1] = new PdfPCell(new Paragraph(entity.getRequire() == null ? "true" : (entity.getRequire() ? "true" : "false")));
                if (entity.getTypeId()==null) {
                    entity.setTypeId(7);
                }
                switch (entity.getTypeId()) {
                    case 0:
                        cells[2] = new PdfPCell(new Paragraph("string"));
                        break;
                    case 1:
                        cells[2] = new PdfPCell(new Paragraph("number"));
                        break;
                    case 2:
                        cells[2] = new PdfPCell(new Paragraph("object"));
                        break;
                    case 3:
                        cells[2] = new PdfPCell(new Paragraph("array[object]"));
                        break;
                    case 4:
                        cells[2] = new PdfPCell(new Paragraph("array[string]"));
                        break;
                    case 5:
                        cells[2] = new PdfPCell(new Paragraph("array"));
                        break;
                    case 6:
                        cells[2] = new PdfPCell(new Paragraph("file"));
                        break;
                    default:
                        cells[2] = new PdfPCell(new Paragraph("未知"));
                        break;
                }
                cells[3] = new PdfPCell(new Paragraph(entity.getNote(), font));
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
            }
            document.add(inter);
        }
    }


    private void pdfAddResponseParams(Document document, String projectId, String id, Font font) throws DocumentException {
        List<ResponseArgEntity> globals = mResponseArgService.getGlobalResponseArgs(projectId);
        List<ResponseArgEntity> args = mResponseArgService.getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(id, "0"));

        if (globals != null && globals.size() > 0) {
            addSmallTitle(document, "全局响应数据", font);
            font.setStyle(Font.NORMAL);
            PdfPTable inter = new PdfPTable(3);
            inter.setWidthPercentage(100); // 宽度100%填充
            inter.setSpacingBefore(1f); // 前间距
            inter.setSpacingAfter(4f); // 后间距
            inter.setPaddingTop(0);
            List<PdfPRow> listRow = inter.getRows();
            //设置列宽
            float[] columnWidths = {3f, 2f, 4f};
            inter.setWidths(columnWidths);
            //标题
            PdfPCell cells1[] = new PdfPCell[3];
            PdfPRow row1 = new PdfPRow(cells1);
            //单元格
            cells1[0] = new PdfPCell(new Paragraph("名称", font));//单元格内容
            cells1[1] = new PdfPCell(new Paragraph("类型", font));
            cells1[2] = new PdfPCell(new Paragraph("说明", font));
            cells1[0].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[1].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[2].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[0].setPaddingTop(6f);
            cells1[0].setPaddingBottom(6f);
            cells1[1].setPaddingTop(6f);
            cells1[1].setPaddingBottom(6f);
            cells1[2].setPaddingTop(6f);
            cells1[2].setPaddingBottom(6f);
            //把第一行添加到集合
            listRow.add(row1);
            for (ResponseArgEntity entity : globals) {
                //行1
                PdfPCell cells[] = new PdfPCell[3];
                PdfPRow row = new PdfPRow(cells);
                //单元格
                cells[0] = new PdfPCell(new Paragraph(entity.getName() == null ? "" : entity.getName()));//单元格内容
                if (entity.getTypeId()==null) {
                    entity.setTypeId(7);
                }
                switch (entity.getTypeId()) {
                    case 0:
                        cells[1] = new PdfPCell(new Paragraph("string"));
                        break;
                    case 1:
                        cells[1] = new PdfPCell(new Paragraph("number"));
                        break;
                    case 2:
                        cells[1] = new PdfPCell(new Paragraph("object"));
                        break;
                    case 3:
                        cells[1] = new PdfPCell(new Paragraph("array[object]"));
                        break;
                    case 4:
                        cells[1] = new PdfPCell(new Paragraph("array[string]"));
                        break;
                    case 5:
                        cells[1] = new PdfPCell(new Paragraph("array"));
                        break;
                    case 6:
                        cells[1] = new PdfPCell(new Paragraph("file"));
                        break;
                    default:
                        cells[1] = new PdfPCell(new Paragraph("未知"));
                        break;
                }
                cells[2] = new PdfPCell(new Paragraph(entity.getNote() == null ? "" : entity.getNote(), font));
                cells[0].setPaddingTop(4f);
                cells[0].setPaddingBottom(4f);
                cells[1].setPaddingTop(4f);
                cells[1].setPaddingBottom(4f);
                cells[2].setPaddingTop(4f);
                cells[2].setPaddingBottom(4f);

                //把第一行添加到集合
                listRow.add(row);
            }
            document.add(inter);
        }

        if (args != null && args.size() > 0) {
            addSmallTitle(document, "其他响应数据", font);
            font.setStyle(Font.NORMAL);
            PdfPTable inter = new PdfPTable(3);
            inter.setWidthPercentage(100); // 宽度100%填充
            inter.setSpacingBefore(1f); // 前间距
            inter.setSpacingAfter(4f); // 后间距
            inter.setPaddingTop(0);
            List<PdfPRow> listRow = inter.getRows();
            //设置列宽
            float[] columnWidths = {3f, 2f, 4f};
            inter.setWidths(columnWidths);
            //标题
            PdfPCell cells1[] = new PdfPCell[3];
            PdfPRow row1 = new PdfPRow(cells1);
            //单元格
            cells1[0] = new PdfPCell(new Paragraph("名称", font));//单元格内容
            cells1[1] = new PdfPCell(new Paragraph("类型", font));
            cells1[2] = new PdfPCell(new Paragraph("说明", font));
            cells1[0].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[1].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[2].setBackgroundColor(BaseColor.LIGHT_GRAY);
            cells1[0].setPaddingTop(6f);
            cells1[0].setPaddingBottom(6f);
            cells1[1].setPaddingTop(6f);
            cells1[1].setPaddingBottom(6f);
            cells1[2].setPaddingTop(6f);
            cells1[2].setPaddingBottom(6f);
            //把第一行添加到集合
            listRow.add(row1);
            for (ResponseArgEntity entity : args) {
                //行1
                PdfPCell cells[] = new PdfPCell[3];
                PdfPRow row = new PdfPRow(cells);
                //单元格
                cells[0] = new PdfPCell(new Paragraph(entity.getName()));//单元格内容
                if (entity.getTypeId()==null) {
                    entity.setTypeId(7);
                }
                switch (entity.getTypeId()) {
                    case 0:
                        cells[1] = new PdfPCell(new Paragraph("string"));
                        break;
                    case 1:
                        cells[1] = new PdfPCell(new Paragraph("number"));
                        break;
                    case 2:
                        cells[1] = new PdfPCell(new Paragraph("object"));
                        break;
                    case 3:
                        cells[1] = new PdfPCell(new Paragraph("array[object]"));
                        break;
                    case 4:
                        cells[1] = new PdfPCell(new Paragraph("array[string]"));
                        break;
                    case 5:
                        cells[1] = new PdfPCell(new Paragraph("array"));
                        break;
                    case 6:
                        cells[1] = new PdfPCell(new Paragraph("file"));
                        break;
                    default:
                        cells[1] = new PdfPCell(new Paragraph("未知"));
                        break;
                }
                cells[2] = new PdfPCell(new Paragraph(entity.getNote() == null ? "" : entity.getNote(), font));
                cells[0].setPaddingTop(4f);
                cells[0].setPaddingBottom(4f);
                cells[1].setPaddingTop(4f);
                cells[1].setPaddingBottom(4f);
                cells[2].setPaddingTop(4f);
                cells[2].setPaddingBottom(4f);
                //把第一行添加到集合
                listRow.add(row);
                pdfAddChildResponseParams("    ", listRow, projectId, id, entity.getId(), font);
            }
            document.add(inter);
        }
    }

    private void pdfAddChildResponseParams(String spance, List<PdfPRow> listRow, String projectId, String id, String pid, Font font) throws DocumentException {
        List<ResponseArgEntity> args = mResponseArgService.getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(id, pid));
        if (args != null && args.size() > 0) {
            //把第一行添加到集合
            for (ResponseArgEntity entity : args) {
                //行1
                PdfPCell cells[] = new PdfPCell[3];
                PdfPRow row = new PdfPRow(cells);
                //单元格
                cells[0] = new PdfPCell(new Paragraph(spance + entity.getName()));//单元格内容
                if (entity.getTypeId()==null) {
                    entity.setTypeId(7);
                }
                switch (entity.getTypeId()) {
                    case 0:
                        cells[1] = new PdfPCell(new Paragraph("string"));
                        break;
                    case 1:
                        cells[1] = new PdfPCell(new Paragraph("number"));
                        break;
                    case 2:
                        cells[1] = new PdfPCell(new Paragraph("object"));
                        break;
                    case 3:
                        cells[1] = new PdfPCell(new Paragraph("array[object]"));
                        break;
                    case 4:
                        cells[1] = new PdfPCell(new Paragraph("array[string]"));
                        break;
                    case 5:
                        cells[1] = new PdfPCell(new Paragraph("array"));
                        break;
                    case 6:
                        cells[1] = new PdfPCell(new Paragraph("file"));
                        break;
                    default:
                        cells[1] = new PdfPCell(new Paragraph("未知"));
                        break;
                }
                cells[2] = new PdfPCell(new Paragraph(entity.getNote() == null ? "" : entity.getNote(), font));
                cells[0].setPaddingTop(4f);
                cells[0].setPaddingBottom(4f);
                cells[1].setPaddingTop(4f);
                cells[1].setPaddingBottom(4f);
                cells[2].setPaddingTop(4f);
                cells[2].setPaddingBottom(4f);
                //把第一行添加到集合
                listRow.add(row);
                pdfAddChildResponseParams(spance + "    ", listRow, projectId, id, entity.getId(), font);
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
            }
        } else {
            Paragraph paragraph = new Paragraph((text == null ? "" : text));
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void addMidTitle(Document document, String text, Font font) {
        if (font != null) {
            font.setSize(22f);
            font.setStyle(Font.BOLD);
            Paragraph paragraph = new Paragraph("\n\n" + (text == null ? "" : text), font);
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        } else {
            Paragraph paragraph = new Paragraph("\n\n" + (text == null ? "" : text));
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void addText(Document document, String text, Font font) {
        if (font == null) {
            Chunk chunk = new Chunk(text == null ? "" : text);
            try {
                document.add(chunk);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        } else {
            font.setSize(12f);
            font.setStyle(Font.NORMAL);
            Chunk chunk = new Chunk(text == null ? "" : text, font);
            try {
                document.add(chunk);
            } catch (DocumentException e) {
                e.printStackTrace();
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
            }
        } else {
            font.setSize(12f);
            font.setStyle(Font.NORMAL);
            Chunk chunk = new Chunk((text == null ? "" : text) + "\n", font);
            try {
                document.add(chunk);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void addUnderLineText(Document document, String text, String path) throws IOException, DocumentException {
        Font font = new Font(BaseFont.createFont(path + "Arial.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 12f, Font.UNDERLINE);
        Chunk chunk = new Chunk(text == null ? "" : text, font);
        try {
            document.add(chunk);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void addDuanLuo(Document document, String text, Font font) {
        if (font != null) {
            font.setSize(18f);
            font.setStyle(Font.BOLD);
            Paragraph paragraph = new Paragraph("\n\n" + (text == null ? "" : text), font);
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        } else {
            Paragraph paragraph = new Paragraph("\n\n" + (text == null ? "" : text));
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
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
            }
        } else {
            Chunk paragraph = new Chunk(text == null ? "" : text);
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }

    }

    /***********************************下载 PDF 结束**************************************/
}
