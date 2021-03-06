package me.zhouzhuo810.zzapidoc.project.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.ResponseArgDao;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceEntity;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceService;
import me.zhouzhuo810.zzapidoc.project.service.ResponseArgService;
import me.zhouzhuo810.zzapidoc.project.utils.ProjectUtils;
import me.zhouzhuo810.zzapidoc.project.utils.ResponseArgUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.hibernate.criterion.Order;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by admin on 2017/7/22.
 */
@Service
public class ResponseArgServiceImpl extends BaseServiceImpl<ResponseArgEntity> implements ResponseArgService {

    @Resource(name = "userServiceImpl")
    UserService mUserService;

    @Resource(name = "interfaceServiceImpl")
    InterfaceService mInterfaceService;

    @Override
    @Resource(name = "responseArgDaoImpl")
    public void setBaseDao(BaseDao<ResponseArgEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public ResponseArgDao getBaseDao() {
        return (ResponseArgDao) this.baseDao;
    }

    @Override
    public void deleteByInterfaceId(String interfaceId) {
        getBaseDao().deleteByInterfaceId(interfaceId);
    }


    @Override
    public BaseResult addResponseArg(String pid, String name, String defValue, int type, String projectId, String interfaceId, String note, String userId, boolean isGlobal) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ResponseArgEntity arg = new ResponseArgEntity();
        arg.setName(name);
        arg.setNote(note);
        arg.setDefaultValue(defValue);
        arg.setProjectId(projectId);
        arg.setInterfaceId(interfaceId);
        arg.setCreateUserID(user.getId());
        arg.setGlobal(isGlobal);
        arg.setCreateUserName(user.getName());
        arg.setTypeId(type);
        arg.setPid(pid == null ? "0" : pid);
        try {
            getBaseDao().save(arg);
            return new BaseResult(1, "添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败！");
        }
    }

    @Override
    public BaseResult updateResponseArg(String pid, String responseArgId, String name, String defValue, int type, String interfaceId, String note, String userId, boolean isGlobal) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ResponseArgEntity arg = getBaseDao().get(responseArgId);
        if (arg == null) {
            return new BaseResult(0, "参数不存在或已被删除！");
        }
        arg.setName(name);
        arg.setNote(note);
        arg.setDefaultValue(defValue);
        arg.setInterfaceId(interfaceId);
        arg.setTypeId(type);
        arg.setGlobal(isGlobal);
        arg.setModifyTime(new Date());
        arg.setModifyUserID(user.getId());
        arg.setModifyUserName(user.getName());
        arg.setPid(pid == null ? "0" : pid);
        try {
            getBaseDao().update(arg);
            return new BaseResult(1, "修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "修改失败！");
        }
    }

    @Override
    public BaseResult deleteResponseArg(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ResponseArgEntity arg = getBaseDao().get(id);
        if (arg == null) {
            return new BaseResult(0, "参数不存在或已被删除！");
        }
        arg.setModifyUserID(user.getId());
        arg.setModifyUserName(user.getName());
        arg.setModifyTime(new Date());
        arg.setDeleteFlag(BaseEntity.DELETE_FLAG_YES);
        try {
            getBaseDao().update(arg);
            return new BaseResult(1, "删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "删除失败");
        }
    }

    @Override
    public BaseResult deleteResponseArgWeb(String ids, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！", new HashMap<String, String>());
        }
        if (ids != null && ids.length() > 0) {
            if (ids.contains(",")) {
                String[] id = ids.split(",");
                for (String s : id) {
                    ResponseArgEntity entity = getBaseDao().get(s);
                    if (entity == null) {
                        continue;
                    }
                    entity.setModifyUserID(user.getId());
                    entity.setModifyUserName(user.getName());
                    entity.setDeleteFlag(BaseEntity.DELETE_FLAG_YES);
                    try {
                        update(entity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ResponseArgEntity entity = getBaseDao().get(ids);
                entity.setModifyUserID(user.getId());
                entity.setModifyUserName(user.getName());
                entity.setDeleteFlag(BaseEntity.DELETE_FLAG_YES);
                try {
                    update(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            return new BaseResult(0, "请先选择要删除的行！", new HashMap<String, String>());
        }
        return new BaseResult(1, "刪除成功！", new HashMap<String, String>());
    }

    @Override
    public BaseResult getResponseArgByInterfaceIdAndPid(String interfaceId, String projectId, String pid, boolean global, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        if (projectId == null) {
            InterfaceEntity entity = mInterfaceService.get(interfaceId);
            if (entity == null) {
                return new BaseResult(0, "接口不存在或已被删除！");
            }
            projectId = entity.getProjectId();
        }

        List<ResponseArgEntity> globals = getBaseDao().executeCriteria(ResponseArgUtils.getGlobalWithProjectIdAndPid(projectId, pid),
                Order.asc("createTime"));

        List<ResponseArgEntity> args = getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid),
                Order.asc("createTime"));
        if (args == null) {
            if (pid != null && pid.equals("0") && globals == null) {
                return new BaseResult(0, "暂无数据");
            }
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        if (pid != null && pid.equals("0") && globals != null) {
            for (ResponseArgEntity arg : globals) {
                MapUtils map = new MapUtils();
                map.put("id", arg.getId());
                map.put("name", arg.getName());
                map.put("note", arg.getNote());
                map.put("defValue", arg.getDefaultValue() == null ? "" : arg.getDefaultValue());
                map.put("pid", arg.getPid());
                map.put("type", arg.getTypeId());
                map.put("createTime", DataUtils.formatDate(arg.getCreateTime()));
                map.put("createUserName", arg.getCreateUserName());
                map.put("interfaceId", arg.getInterfaceId());
                map.put("isGlobal", arg.getGlobal());
                result.add(map.build());
            }
        }
        if (args != null && !global) {
            for (ResponseArgEntity arg : args) {
                MapUtils map = new MapUtils();
                map.put("id", arg.getId());
                map.put("name", arg.getName());
                map.put("note", arg.getNote());
                map.put("defValue", arg.getDefaultValue() == null ? "" : arg.getDefaultValue());
                map.put("pid", arg.getPid());
                map.put("type", arg.getTypeId());
                map.put("createTime", DataUtils.formatDate(arg.getCreateTime()));
                map.put("createUserName", arg.getCreateUserName());
                map.put("interfaceId", arg.getInterfaceId());
                map.put("isGlobal", arg.getGlobal());
                result.add(map.build());
            }
        }
        return new BaseResult(1, "ok", result);
    }

    @Override
    public BaseResult getResponseArgDetails(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法", new HashMap<String, String>());
        }
        ResponseArgEntity arg = getBaseDao().get(id);
        if (arg == null) {
            return new BaseResult(0, "参数不存在或已被删除！", new HashMap<String, String>());
        }
        MapUtils map = new MapUtils();
        map.put("id", arg.getId());
        map.put("name", arg.getName());
        map.put("note", arg.getNote());
        map.put("pid", arg.getPid());
        map.put("defValue", arg.getDefaultValue() == null ? "" : arg.getDefaultValue());
        map.put("type", arg.getTypeId());
        map.put("createTime", DataUtils.formatDate(arg.getCreateTime()));
        map.put("createUserName", arg.getCreateUserName());
        map.put("interfaceId", arg.getInterfaceId());
        return new BaseResult(1, "ok", map.build());
    }

    @Override
    public List<ResponseArgEntity> getGlobalResponseArgs(String projectId) {
        return getBaseDao().executeCriteria(ResponseArgUtils.getGlobal(projectId));
    }

    @Override
    public BaseResult importResponseArg(String interfaceId, String userId, String pid, String json) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        InterfaceEntity interfaceEntity = mInterfaceService.get(interfaceId);
        if (interfaceEntity == null) {
            return new BaseResult(0, "接口不存在或已被删除！");
        }
        JSONObject obj = new JSONObject(json);
        parseObj(interfaceEntity.getProjectId(), interfaceId, userId, user.getName(), obj, pid == null ? "0" : pid);
        return new BaseResult(1, "参数导入成功！");
    }

    @Override
    public void parseObj(String projectId, String interfaceId, String userId, String userName, JSONObject obj, final String pid) {
        if (obj != null) {
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object o = obj.get(key);
                if (o instanceof JSONObject) {
                    JSONObject o1 = (JSONObject) o;
                    String mPid = addArg(pid, projectId, interfaceId, userId, userName, key, "", ResponseArgEntity.TYPE_OBJECT);
                    parseObj(projectId, interfaceId, userId, userName, o1, mPid);
                } else if (o instanceof JSONArray) {
                    JSONArray o1 = (JSONArray) o;
                    String mPid = addArg(pid, projectId, interfaceId, userId, userName, key, "", ResponseArgEntity.TYPE_ARRAY_OBJECT);
                    if (o1.length() > 0) {
                        Object o2 = o1.get(0);
                        if (o2 instanceof JSONObject) {
                            parseObj(projectId, interfaceId, userId, userName, (JSONObject) o2, mPid);
                        }
                    }
                } else if (o instanceof String) {
                    addArg(pid, projectId, interfaceId, userId, userName, key, (String) o, ResponseArgEntity.TYPE_STRING);
                } else if (o instanceof Integer) {
                    addArg(pid, projectId, interfaceId, userId, userName, key, "" + ((Integer) o), ResponseArgEntity.TYPE_INT);
                } else if (o instanceof Double) {
                    addArg(pid, projectId, interfaceId, userId, userName, key, "" + ((Double) o), ResponseArgEntity.TYPE_FLOAT);
                }
            }
        }
    }

    @Override
    public BaseResult updateResponseArgPid(String pid, String responseArgIds, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        if (responseArgIds != null && responseArgIds.length() > 0) {
            if (responseArgIds.contains(",")) {
                String[] id = responseArgIds.split(",");
                for (String s : id) {
                    ResponseArgEntity arg = getBaseDao().get(s);
                    if (arg != null) {
                        arg.setModifyTime(new Date());
                        arg.setModifyUserID(user.getId());
                        arg.setModifyUserName(user.getName());
                        arg.setPid(pid == null ? "0" : pid);
                        try {
                            update(arg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                ResponseArgEntity arg = getBaseDao().get(responseArgIds);
                if (arg != null) {
                    arg.setModifyTime(new Date());
                    arg.setModifyUserID(user.getId());
                    arg.setModifyUserName(user.getName());
                    arg.setPid(pid == null ? "0" : pid);
                    try {
                        update(arg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    return new BaseResult(0, "返回参数不存在或已被删除！");
                }
            }
        } else {
            return new BaseResult(0, "请先选择要修改的行！");
        }
        return new BaseResult(1, "修改成功！");
    }

    @Override
    public BaseResult getResponseArgByInterfaceId(String interfaceId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        List<ResponseArgEntity> args = getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceId(interfaceId),
                Order.asc("createTime"));
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (args != null) {
            for (ResponseArgEntity arg : args) {
                MapUtils map = new MapUtils();
                map.put("id", arg.getId());
                map.put("name", arg.getName());
                map.put("note", arg.getNote());
                map.put("defValue", arg.getDefaultValue() == null ? "" : arg.getDefaultValue());
                map.put("pid", arg.getPid());
                map.put("type", arg.getTypeId());
                map.put("createTime", DataUtils.formatDate(arg.getCreateTime()));
                map.put("createUserName", arg.getCreateUserName());
                map.put("interfaceId", arg.getInterfaceId());
                map.put("isGlobal", arg.getGlobal());
                result.add(map.build());
            }
        }
        return new BaseResult(1, "ok", result);
    }

    private String addArg(String pid, String projectId, String interfaceId, String userId, String userName, String name, String note, int typeId) {
        ResponseArgEntity arg = new ResponseArgEntity();
        arg.setName(name);
        arg.setNote(note);
        arg.setProjectId(projectId);
        arg.setInterfaceId(interfaceId);
        arg.setCreateUserID(userId);
        arg.setGlobal(false);
        arg.setCreateUserName(userName);
        arg.setTypeId(typeId);
        arg.setPid(pid == null ? "0" : pid);
        try {
            getBaseDao().save(arg);
            return arg.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
