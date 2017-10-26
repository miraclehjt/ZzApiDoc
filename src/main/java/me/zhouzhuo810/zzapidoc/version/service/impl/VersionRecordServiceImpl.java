package me.zhouzhuo810.zzapidoc.version.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import me.zhouzhuo810.zzapidoc.version.dao.VersionRecordDao;
import me.zhouzhuo810.zzapidoc.version.entity.VersionProjectEntity;
import me.zhouzhuo810.zzapidoc.version.entity.VersionRecordEntity;
import me.zhouzhuo810.zzapidoc.version.service.VersionRecordService;
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
public class VersionRecordServiceImpl extends BaseServiceImpl<VersionRecordEntity> implements VersionRecordService {

    @Resource(name = "userServiceImpl")
    private UserService mUserService;

    @Override
    @Resource(name = "versionRecordDaoImpl")
    public void setBaseDao(BaseDao<VersionRecordEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public VersionRecordDao getBaseDao() {
        return (VersionRecordDao) baseDao;
    }

    @Override
    public BaseResult addVersionRecord(String versionId, String projectId, String note, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        VersionRecordEntity entity = new VersionRecordEntity();
        entity.setVersionId(versionId);
        entity.setProjectId(projectId);
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
    public BaseResult updateVersionRecord(String recordId, String versionId, String projectId, String note, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        VersionRecordEntity entity = get(recordId);
        if (entity==null) {
            return new BaseResult(0, "记录不存在或已被删除！");
        }
        entity.setVersionId(versionId);
        entity.setProjectId(projectId);
        entity.setNote(note);
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        try {
            save(entity);
            return new BaseResult(1, "修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "修改失败！" + e.toString());
        }
    }

    @Override
    public BaseResult deleteVersionRecord(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        VersionRecordEntity entity = get(id);
        if (entity==null) {
            return new BaseResult(0, "记录不存在或已被删除！");
        }
        entity.setDeleteFlag(BaseEntity.DELETE_FLAG_YES);
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        try {
            save(entity);
            return new BaseResult(1, "删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "删除失败！" + e.toString());
        }
    }

    @Override
    public BaseResult getAllVersionRecord(String userId, String versionId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        List<VersionRecordEntity> versionProjectEntities = executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("versionId", versionId)
        });
        if (versionProjectEntities == null) {
            return new BaseResult(0, "暂无数据！");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        MapUtils map;
        for (VersionRecordEntity entity : versionProjectEntities) {
            map = new MapUtils();
            map.put("id", entity.getId());
            map.put("note", entity.getNote());
            map.put("userName", entity.getCreateUserName());
            map.put("createTime", DataUtils.formatDate(entity.getCreateTime()));
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }
}
