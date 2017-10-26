package me.zhouzhuo810.zzapidoc.version.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import me.zhouzhuo810.zzapidoc.version.dao.VersionDao;
import me.zhouzhuo810.zzapidoc.version.entity.VersionEntity;
import me.zhouzhuo810.zzapidoc.version.service.VersionService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zz on 2017/10/26.
 */
@Service
public class VersionServiceImpl extends BaseServiceImpl<VersionEntity> implements VersionService {

    @Resource(name = "userServiceImpl")
    private UserService mUserService;

    @Override
    @Resource(name = "versionDaoImpl")
    public void setBaseDao(BaseDao<VersionEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public VersionDao getBaseDao() {
        return (VersionDao) baseDao;
    }

    @Override
    public BaseResult addVersion(String projectId, String versionName, int versionCode, String versionDesc, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        VersionEntity entity = new VersionEntity();
        entity.setProjectId(projectId);
        entity.setVersionName(versionName);
        entity.setVersionCode(versionCode);
        entity.setVersionDesc(versionDesc);
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
    public BaseResult updateVersion(String versionId, String projectId, String versionName, int versionCode, String versionDesc, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        VersionEntity entity = get(versionId);
        if (entity == null) {
            return new BaseResult(0, "版本不存在或已被删除！");
        }
        entity.setProjectId(projectId);
        entity.setVersionName(versionName);
        entity.setVersionCode(versionCode);
        entity.setVersionDesc(versionDesc);
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
    public BaseResult deleteVersion(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        VersionEntity entity = get(id);
        if (entity == null) {
            return new BaseResult(0, "版本不存在或已被删除！");
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
    public BaseResult getAllVersion(String userId, String projectId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        List<VersionEntity> versions = executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("projectId", projectId)
        });
        if (versions == null) {
            return new BaseResult(0, "暂无数据！");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        MapUtils map;
        for (VersionEntity entity : versions) {
            map = new MapUtils();
            map.put("id", entity.getId());
            map.put("projectId", entity.getProjectId());
            map.put("name", entity.getVersionName());
            map.put("code", entity.getVersionCode());
            map.put("note", entity.getVersionDesc());
            map.put("userName", entity.getCreateUserName());
            map.put("createTime", DataUtils.formatDate(entity.getCreateTime()));
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }
}
