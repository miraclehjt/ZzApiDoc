package me.zhouzhuo810.zzapidoc.version.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import me.zhouzhuo810.zzapidoc.version.dao.VersionProjectDao;
import me.zhouzhuo810.zzapidoc.version.entity.VersionProjectEntity;
import me.zhouzhuo810.zzapidoc.version.service.VersionProjectService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zz on 2017/10/26.
 */
@Service
public class VersionProjectServiceImpl extends BaseServiceImpl<VersionProjectEntity> implements VersionProjectService {

    @Resource(name = "userServiceImpl")
    private UserService mUserService;


    @Override
    @Resource(name = "versionProjectDaoImpl")
    public void setBaseDao(BaseDao<VersionProjectEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public VersionProjectDao getBaseDao() {
        return (VersionProjectDao) baseDao;
    }

    @Override
    public BaseResult addVersionProject(String name, String note, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        VersionProjectEntity entity = new VersionProjectEntity();
        entity.setName(name);
        entity.setNote(note);
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        try {
            save(entity);
            return new BaseResult(1, "添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败！" + e.toString());
        }
    }

    @Override
    public BaseResult updateVersionProject(String projectId, String name, String note, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        VersionProjectEntity entity = get(projectId);
        if (entity == null) {
            return new BaseResult(0, "项目不存在或已被删除！");
        }
        entity.setName(name);
        entity.setNote(note);
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        try {
            update(entity);
            return new BaseResult(1, "修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "修改失败！" + e.toString());
        }
    }

    @Override
    public BaseResult deleteVersionProject(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        VersionProjectEntity entity = get(id);
        if (entity == null) {
            return new BaseResult(0, "项目不存在或已被删除！");
        }
        entity.setDeleteFlag(BaseEntity.DELETE_FLAG_YES);
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        try {
            update(entity);
            return new BaseResult(1, "删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "删除失败！" + e.toString());
        }
    }

    @Override
    public BaseResult getAllVersionProject(String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        List<VersionProjectEntity> versionProjectEntities = executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO)
        }, Order.desc("createTime"));
        if (versionProjectEntities == null) {
            return new BaseResult(0, "暂无数据！");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        MapUtils map;
        for (VersionProjectEntity entity : versionProjectEntities) {
            map = new MapUtils();
            map.put("id", entity.getId());
            map.put("name", entity.getName());
            map.put("note", entity.getNote());
            map.put("userName", entity.getCreateUserName());
            map.put("createTime", DataUtils.formatDate(entity.getCreateTime()));
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }
}
