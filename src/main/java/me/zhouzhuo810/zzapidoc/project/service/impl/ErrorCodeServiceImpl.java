package me.zhouzhuo810.zzapidoc.project.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.ErrorCodeDao;
import me.zhouzhuo810.zzapidoc.project.entity.DictionaryEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ErrorCodeEntity;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceGroupEntity;
import me.zhouzhuo810.zzapidoc.project.entity.RequestHeaderEntity;
import me.zhouzhuo810.zzapidoc.project.service.ErrorCodeService;
import me.zhouzhuo810.zzapidoc.project.utils.DictionaryUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by zz on 2017/12/29.
 */
@Service
public class ErrorCodeServiceImpl extends BaseServiceImpl<ErrorCodeEntity> implements ErrorCodeService {

    @Resource(name = "userServiceImpl")
    UserService mUserService;

    @Override
    @Resource(name = "errorCodeDaoImpl")
    public void setBaseDao(BaseDao<ErrorCodeEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public ErrorCodeDao getBaseDao() {
        return (ErrorCodeDao) this.baseDao;
    }

    @Override
    public BaseResult addErrorCode(int code, String note, String interfaceId, String groupId, String projectId, boolean isGlobal, boolean isGroup, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法", new HashMap<String, String>());
        }
        ErrorCodeEntity entity = new ErrorCodeEntity();
        entity.setCode(code);
        entity.setNote(note);
        entity.setInterfaceId(interfaceId);
        entity.setGroupId(groupId);
        entity.setProjectId(projectId);
        entity.setGlobal(isGlobal);
        entity.setGroup(isGroup);
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        try {
            getBaseDao().save(entity);
            String id = entity.getId();
            MapUtils map = new MapUtils();
            map.put("id", id);
            return new BaseResult(1, "ok", map.build());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败", new HashMap<String, String>());
        }
    }

    @Override
    public BaseResult getAllErrorCode(boolean global, boolean group, String projectId, String groupId, String interfaceId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        List<ErrorCodeEntity> list = null;
        if (global) {
            list = getBaseDao().executeCriteria(new Criterion[]{
                    Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                    Restrictions.eq("projectId", projectId),
                    Restrictions.eq("isGlobal", true)
            }, Order.asc("createTime"));
        } else if (group) {
            list = getBaseDao().executeCriteria(new Criterion[]{
                    Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                    Restrictions.eq("groupId", groupId),
                    Restrictions.eq("isGroup", true)
            }, Order.asc("createTime"));
        } else {
            list = getBaseDao().executeCriteria(new Criterion[]{
                    Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                    Restrictions.eq("interfaceId", interfaceId)
            }, Order.asc("createTime"));
        }
        if (list == null) {
            return new BaseResult(0, "暂无数据");
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (ErrorCodeEntity entity : list) {
            MapUtils map = new MapUtils();
            map.put("id", entity.getId());
            map.put("code", entity.getCode());
            map.put("note", entity.getNote());
            map.put("isGlobal", entity.getGlobal() == null ? false : entity.getGlobal());
            map.put("isGroup", entity.getGroup() == null ? false : entity.getGroup());
            map.put("projectId", entity.getProjectId());
            map.put("interfaceId", entity.getInterfaceId());
            map.put("groupId", entity.getGroupId());
            map.put("createTime", DataUtils.formatDate(entity.getCreateTime()));
            map.put("createMan", entity.getCreateUserName());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }

    @Override
    public BaseResult deleteErrorCode(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ErrorCodeEntity entity = getBaseDao().get(id);
        if (entity == null) {
            return new BaseResult(0, "错误码不存在或已被删除！");
        }
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        entity.setModifyTime(new Date());
        entity.setDeleteFlag(BaseEntity.DELETE_FLAG_YES);
        try {
            getBaseDao().update(entity);
            return new BaseResult(1, "删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "删除失败");
        }
    }
    @Override
    public BaseResult deleteErrorCodeWeb(String ids, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！", new HashMap<String, String>());
        }
        if (ids != null && ids.length() > 0) {
            if (ids.contains(",")) {
                String[] id = ids.split(",");
                for (String s : id) {
                    ErrorCodeEntity entity = getBaseDao().get(s);
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
                ErrorCodeEntity entity = getBaseDao().get(ids);
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
    public BaseResult updateErrorCode(String codeId, int code, String note, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ErrorCodeEntity entity = getBaseDao().get(codeId);
        if (entity == null) {
            return new BaseResult(0, "错误码不存在或已被删除！");
        }
        entity.setCode(code);
        entity.setNote(note);
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        entity.setModifyTime(new Date());
        try {
            getBaseDao().update(entity);
            return new BaseResult(1, "修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "修改失败");
        }
    }
}
