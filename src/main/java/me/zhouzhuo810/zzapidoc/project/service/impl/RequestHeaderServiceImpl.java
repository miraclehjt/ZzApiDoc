package me.zhouzhuo810.zzapidoc.project.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.RequestHeaderDao;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceEntity;
import me.zhouzhuo810.zzapidoc.project.entity.RequestHeaderEntity;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceService;
import me.zhouzhuo810.zzapidoc.project.service.RequestHeaderService;
import me.zhouzhuo810.zzapidoc.project.utils.ResponseArgUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/13.
 */
@Service
public class RequestHeaderServiceImpl extends BaseServiceImpl<RequestHeaderEntity> implements RequestHeaderService {

    @Resource(name = "userServiceImpl")
    UserService mUserService;

    @Resource(name = "interfaceServiceImpl")
    InterfaceService mInterfaceService;


    @Override
    @Resource(name = "requestHeaderDaoImpl")
    public void setBaseDao(BaseDao<RequestHeaderEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public RequestHeaderDao getBaseDao() {
        return (RequestHeaderDao) this.baseDao;
    }


    @Override
    public BaseResult addRequestHeader(String name, String value, String note, String projectId, String interfaceId, String userId, boolean isGlobal) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        RequestHeaderEntity arg = new RequestHeaderEntity();
        arg.setName(name);
        arg.setProjectId(projectId);
        arg.setInterfaceId(interfaceId);
        arg.setNote(note);
        arg.setCreateUserID(user.getId());
        arg.setCreateUserName(user.getName());
        arg.setValue(value);
        arg.setGlobal(isGlobal);
        try {
            getBaseDao().save(arg);
            return new BaseResult(1, "添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败！");
        }
    }

    @Override
    public BaseResult updateRequestHeader(String requestHeaderId, String name, String value, String note, String projectId, String interfaceId, String userId, boolean isGlobal) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        RequestHeaderEntity arg = getBaseDao().get(requestHeaderId);
        if (arg == null) {
            return new BaseResult(0, "请求头不存在或已被删除！");
        }
        arg.setName(name);
        arg.setInterfaceId(interfaceId);
        arg.setValue(value);
        arg.setNote(note);
        arg.setModifyTime(new Date());
        arg.setModifyUserID(user.getId());
        arg.setGlobal(isGlobal);
        arg.setModifyUserName(user.getName());
        try {
            getBaseDao().update(arg);
            return new BaseResult(1, "修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "修改失败！");
        }
    }

    @Override
    public BaseResult deleteRequestHeader(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        RequestHeaderEntity arg = getBaseDao().get(id);
        if (arg == null) {
            return new BaseResult(0, "请求头不存在或已被删除！");
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
    public BaseResult getRequestHeaderByInterfaceId(String interfaceId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }

        InterfaceEntity entity = mInterfaceService.get(interfaceId);
        if (entity == null) {
            return new BaseResult(0, "接口不存在或已被删除！");
        }
        String projectId = entity.getProjectId();
        List<RequestHeaderEntity> globals = getBaseDao().executeCriteria(ResponseArgUtils.getGlobal(projectId), Order.asc("createTime"));

        List<RequestHeaderEntity> args = getBaseDao().executeCriteria(ResponseArgUtils.getArgByInterfaceId(interfaceId), Order.asc("createTime"));
        if (args == null && globals == null) {
            return new BaseResult(0, "暂无数据");
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        if (globals != null) {
            for (RequestHeaderEntity arg : globals) {
                MapUtils map = new MapUtils();
                map.put("id", arg.getId());
                map.put("name", arg.getName());
                map.put("value", arg.getValue());
                map.put("note", arg.getNote() == null ? "" : arg.getNote());
                map.put("createTime", DataUtils.formatDate(arg.getCreateTime()));
                map.put("createUserName", arg.getCreateUserName());
                map.put("interfaceId", arg.getInterfaceId());
                map.put("projectId", arg.getProjectId());
                map.put("isGlobal", arg.getGlobal() != null);
                result.add(map.build());
            }
        }
        if (args != null) {
            for (RequestHeaderEntity arg : args) {
                MapUtils map = new MapUtils();
                map.put("id", arg.getId());
                map.put("name", arg.getName());
                map.put("value", arg.getValue());
                map.put("note", arg.getNote() == null ? "" : arg.getNote());
                map.put("createTime", DataUtils.formatDate(arg.getCreateTime()));
                map.put("createUserName", arg.getCreateUserName());
                map.put("interfaceId", arg.getInterfaceId());
                map.put("projectId", arg.getProjectId());
                map.put("isGlobal", false);
                result.add(map.build());
            }
        }
        return new BaseResult(1, "ok", result);
    }

    @Override
    public List<RequestHeaderEntity> getGlobalRequestHeaders(String projectId) {
        return getBaseDao().executeCriteria(ResponseArgUtils.getGlobal(projectId));
    }
}
