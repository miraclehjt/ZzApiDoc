package me.zhouzhuo810.zzapidoc.project.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.InterfaceDao;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ProjectEntity;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceService;
import me.zhouzhuo810.zzapidoc.project.service.ProjectService;
import me.zhouzhuo810.zzapidoc.project.service.RequestArgService;
import me.zhouzhuo810.zzapidoc.project.service.ResponseArgService;
import me.zhouzhuo810.zzapidoc.project.utils.InterfaceUtils;
import me.zhouzhuo810.zzapidoc.project.utils.ResponseArgUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.apache.commons.io.FileUtils;
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
import java.io.IOException;
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

    @Resource(name = "responseArgServiceImpl")
    ResponseArgService mResponseArgService;

    @Resource(name = "projectServiceImpl")
    ProjectService mProjectService;

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
            if (requestArgs != null&&requestArgs.length()>0) {
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
            if (responseArgs != null&&responseArgs.length()>0) {
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
        entity.setPath(path);
        entity.setProjectId(projectId);
        entity.setGroupId(groupId);
        entity.setHttpMethodId(httpMethodId);
        entity.setNote(note);
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        if (requestArgs != null&&requestArgs.length()>0) {
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
        if (responseArgs != null&&responseArgs.length()>0) {
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
            map.put("method", entity.getHttpMethodName());
            map.put("group", entity.getGroupName());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }

    @Override
    public BaseResult getInterfaceDetails(String interfaceId, String userId) {
        InterfaceEntity entity = getBaseDao().get(interfaceId);
        if (entity == null) {
            return new BaseResult(0, "接口不存在或已被删除！", new HashMap<String, String>());
        }
        MapUtils map = new MapUtils();
        map.put("id", entity.getId());
        map.put("name", entity.getName());
        map.put("path", entity.getPath());
        map.put("groupName", entity.getGroupName());
        map.put("httpMethod", entity.getHttpMethodName());
        map.put("note", entity.getNote());
        map.put("projectName", entity.getProjectName());
        map.put("createTime", DataUtils.formatDate(entity.getCreateTime()));
        map.put("createUserName", entity.getCreateUserName());

        List<RequestArgEntity> requestArgEntities = mRequestArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceId(interfaceId));
        if (requestArgEntities != null) {
            List<Map<String,Object>> request = new ArrayList<Map<String, Object>>();
            for (RequestArgEntity requestArgEntity : requestArgEntities) {
                MapUtils m = new MapUtils();
                m.put("id", requestArgEntity.getId());
                m.put("name", requestArgEntity.getName());
                m.put("pid", requestArgEntity.getPid());
                m.put("typeId", requestArgEntity.getTypeId());
                m.put("note", requestArgEntity.getNote());
                request.add(m.build());
            }
            map.put("requestArgs", request);
        }
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceId(interfaceId));
        if (responseArgEntities != null) {
            List<Map<String,Object>> response = new ArrayList<Map<String, Object>>();
            for (ResponseArgEntity requestArgEntity : responseArgEntities) {
                MapUtils m = new MapUtils();
                m.put("id", requestArgEntity.getId());
                m.put("name", requestArgEntity.getName());
                m.put("pid", requestArgEntity.getPid());
                m.put("typeId", requestArgEntity.getTypeId());
                m.put("note", requestArgEntity.getNote());
                response.add(m.build());
            }
            map.put("responseArgs", response);
        }
        return new BaseResult(1, "ok", map.build());
    }

    @Override
    public ResponseEntity<byte[]> download(String projectId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return null;
        }

        ProjectEntity project  = mProjectService.get(projectId);
        if (project == null) {
            return null;
        }

        JSONStringer stringer = new JSONStringer();
        stringer.object().key("projectName").value(project.getName())
                .key("note").value(project.getNote())
                .key("property").value(project.getProperty())
                .key("createTime").value(DataUtils.formatDate(project.getCreateTime()));
        List<InterfaceEntity> list = getBaseDao().executeCriteria(InterfaceUtils.getInterfaceByProjectId(projectId));
        if (list == null) {
            return null;
        }
/*        for (InterfaceEntity entity : list) {
            map.put("id", entity.getId());
            map.put("name", entity.getName());
            map.put("method", entity.getHttpMethodName());
            map.put("group", entity.getGroupName());

            List<RequestArgEntity> requestArgEntities = mRequestArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceId(entity.getId()));
            if (requestArgEntities != null) {
                List<Map<String,Object>> request = new ArrayList<Map<String, Object>>();
                for (RequestArgEntity requestArgEntity : requestArgEntities) {
                    MapUtils m = new MapUtils();
                    m.put("id", requestArgEntity.getId());
                    m.put("name", requestArgEntity.getName());
                    m.put("pid", requestArgEntity.getPid());
                    m.put("typeId", requestArgEntity.getTypeId());
                    m.put("note", requestArgEntity.getNote());
                    request.add(m.build());
                }
                map.put("requestArgs", request);
            }
            List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceId(entity.getId()));
            if (responseArgEntities != null) {
                List<Map<String,Object>> response = new ArrayList<Map<String, Object>>();
                for (ResponseArgEntity requestArgEntity : responseArgEntities) {
                    MapUtils m = new MapUtils();
                    m.put("id", requestArgEntity.getId());
                    m.put("name", requestArgEntity.getName());
                    m.put("pid", requestArgEntity.getPid());
                    m.put("typeId", requestArgEntity.getTypeId());
                    m.put("note", requestArgEntity.getNote());
                    response.add(m.build());
                }
                map.put("responseArgs", response);
            }
        }*/
        stringer.endObject();
        try {
            String path = "I://";
            String fileName = me.zhouzhuo810.zzapidoc.common.utils.FileUtils.saveFileToServer(stringer.toString(), path);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(path+fileName)), headers, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

}
