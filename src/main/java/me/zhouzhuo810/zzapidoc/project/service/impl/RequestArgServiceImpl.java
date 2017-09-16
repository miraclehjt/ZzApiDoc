package me.zhouzhuo810.zzapidoc.project.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.RequestArgDao;
import me.zhouzhuo810.zzapidoc.project.dao.ResponseArgDao;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceEntity;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceService;
import me.zhouzhuo810.zzapidoc.project.service.RequestArgService;
import me.zhouzhuo810.zzapidoc.project.service.ResponseArgService;
import me.zhouzhuo810.zzapidoc.project.utils.InterfaceUtils;
import me.zhouzhuo810.zzapidoc.project.utils.ProjectUtils;
import me.zhouzhuo810.zzapidoc.project.utils.ResponseArgUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by admin on 2017/7/22.
 */
@Service
public class RequestArgServiceImpl extends BaseServiceImpl<RequestArgEntity> implements RequestArgService {

    @Resource(name = "userServiceImpl")
    UserService mUserService;

    @Resource(name = "interfaceServiceImpl")
    InterfaceService mInterfaceService;

    @Override
    @Resource(name = "requestArgDaoImpl")
    public void setBaseDao(BaseDao<RequestArgEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public RequestArgDao getBaseDao() {
        return (RequestArgDao) this.baseDao;
    }

    @Override
    public void deleteByInterfaceId(String interfaceId) {
        getBaseDao().deleteByInterfaceId(interfaceId);
    }

    @Override
    public BaseResult addRequestArg(String pid, String name, String defValue, int type, String projectId, String interfaceId,
                                    String note, String userId, boolean isRequire, boolean isGlobal) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        RequestArgEntity arg = new RequestArgEntity();
        arg.setName(name);
        arg.setNote(note);
        arg.setDefaultValue(defValue);
        arg.setProjectId(projectId);
        arg.setInterfaceId(interfaceId);
        arg.setCreateUserID(user.getId());
        arg.setCreateUserName(user.getName());
        arg.setRequire(isRequire);
        arg.setTypeId(type);
        arg.setGlobal(isGlobal);
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
    public BaseResult updateRequestArg(String pid, String requestArgId, String name, String defValue, int type,
                                       String interfaceId, String note, String userId, boolean isRequire, boolean isGlobal) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        RequestArgEntity arg = getBaseDao().get(requestArgId);
        if (arg == null) {
            return new BaseResult(0, "参数不存在或已被删除！");
        }
        arg.setName(name);
        arg.setNote(note);
        arg.setInterfaceId(interfaceId);
        arg.setTypeId(type);
        arg.setDefaultValue(defValue);
        arg.setRequire(isRequire);
        arg.setModifyTime(new Date());
        arg.setModifyUserID(user.getId());
        arg.setGlobal(isGlobal);
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
    public BaseResult deleteRequestArg(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        RequestArgEntity arg = getBaseDao().get(id);
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
    public BaseResult getRequestArgByInterfaceIdAndPid(String interfaceId, String pid, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }

        InterfaceEntity entity = mInterfaceService.get(interfaceId);
        if (entity == null) {
            return new BaseResult(0, "接口不存在或已被删除！");
        }
        String projectId = entity.getProjectId();
        List<RequestArgEntity> globals = getBaseDao().executeCriteria(ResponseArgUtils.getGlobal(projectId));

        List<RequestArgEntity> args = getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid));
        if (args == null) {
            if (pid != null && pid.equals("0") && globals == null) {
                return new BaseResult(0, "暂无数据");
            }
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        if (pid != null && pid.equals("0") && globals != null) {
            for (RequestArgEntity arg : globals) {
                MapUtils map = new MapUtils();
                map.put("id", arg.getId());
                map.put("name", arg.getName());
                map.put("note", arg.getNote());
                map.put("pid", arg.getPid());
                map.put("defValue", arg.getDefaultValue() == null ? "" : arg.getDefaultValue());
                map.put("require", arg.getRequire() == null ? true : arg.getRequire());
                map.put("global", arg.getGlobal() == null ? false : arg.getGlobal());
                map.put("type", arg.getTypeId());
                map.put("createTime", DataUtils.formatDate(arg.getCreateTime()));
                map.put("createUserName", arg.getCreateUserName());
                map.put("interfaceId", arg.getInterfaceId());
                result.add(map.build());
            }
        }
        if (args != null) {
            for (RequestArgEntity arg : args) {
                MapUtils map = new MapUtils();
                map.put("id", arg.getId());
                map.put("name", arg.getName());
                map.put("note", arg.getNote());
                map.put("pid", arg.getPid());
                map.put("defValue", arg.getDefaultValue() == null ? "" : arg.getDefaultValue());
                map.put("require", arg.getRequire() == null ? true : arg.getRequire());
                map.put("global", arg.getGlobal() == null ? false : arg.getGlobal());
                map.put("type", arg.getTypeId());
                map.put("createTime", DataUtils.formatDate(arg.getCreateTime()));
                map.put("createUserName", arg.getCreateUserName());
                map.put("interfaceId", arg.getInterfaceId());
                result.add(map.build());
            }
        }
        return new BaseResult(1, "ok", result);
    }

    @Override
    public BaseResult getRequestArgDetails(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法", new HashMap<String, String>());
        }
        RequestArgEntity arg = getBaseDao().get(id);
        if (arg == null) {
            return new BaseResult(0, "参数不存在或已被删除！", new HashMap<String, String>());
        }
        MapUtils map = new MapUtils();
        map.put("id", arg.getId());
        map.put("name", arg.getName());
        map.put("note", arg.getNote());
        map.put("defValue", arg.getDefaultValue() == null ? "" : arg.getDefaultValue());
        map.put("require", arg.getRequire() == null ? true : arg.getRequire());;
        map.put("global", arg.getGlobal() == null ? false : arg.getGlobal());
        map.put("pid", arg.getPid());
        map.put("type", arg.getTypeId());
        map.put("createTime", DataUtils.formatDate(arg.getCreateTime()));
        map.put("createUserName", arg.getCreateUserName());
        map.put("interfaceId", arg.getInterfaceId());
        return new BaseResult(1, "ok", map.build());
    }

    @Override
    public List<RequestArgEntity> getGlobalRequestArgs(String projectId) {
        return getBaseDao().executeCriteria(ResponseArgUtils.getGlobal(projectId));
    }

}
