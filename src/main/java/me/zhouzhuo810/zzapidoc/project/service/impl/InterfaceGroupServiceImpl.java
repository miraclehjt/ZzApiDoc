package me.zhouzhuo810.zzapidoc.project.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.InterfaceGroupDao;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceGroupEntity;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceGroupService;
import me.zhouzhuo810.zzapidoc.project.utils.InterfaceUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/22.
 */
@Service
public class InterfaceGroupServiceImpl extends BaseServiceImpl<InterfaceGroupEntity> implements InterfaceGroupService {

    @Resource(name = "userServiceImpl")
    UserService mUserService;


    @Override
    @Resource(name = "interfaceGroupDaoImpl")
    public void setBaseDao(BaseDao<InterfaceGroupEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public InterfaceGroupDao getBaseDao() {
        return (InterfaceGroupDao) baseDao;
    }


    @Override
    public BaseResult addInterfaceGroup(String name, String projectId, String userId) {
        UserEntity user = mUserService.get(userId);
        InterfaceGroupEntity entity = new InterfaceGroupEntity();
        entity.setName(name);
        entity.setProjectId(projectId);
        if (user != null) {
            entity.setCreateUserID(user.getId());
            entity.setCreateUserName(user.getName());
        }
        try {
            getBaseDao().save(entity);
            MapUtils map = new MapUtils();
            map.put("id", entity.getId());
            return new BaseResult(1, "添加成功！", map.build());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败！", new HashMap<String, String>());
        }
    }

    @Override
    public BaseResult updateInterfaceGroup(String interfaceGroupId, String name, String projectId, String userId) {
        UserEntity user = mUserService.get(userId);
        InterfaceGroupEntity entity = getBaseDao().get(interfaceGroupId);
        if (entity == null) {
            return new BaseResult(0, "该接口分组不存在或已被删除！");
        }
        entity.setName(name);
        entity.setProjectId(projectId);
        if (user != null) {
            entity.setModifyUserID(user.getId());
            entity.setModifyUserName(user.getName());
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
    public BaseResult deleteInterfaceGroup(String id, String userId) {
        InterfaceGroupEntity entity = getBaseDao().get(id);
        if (entity == null) {
            return new BaseResult(0, "该接口分组不存在或已被删除！");
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
    public BaseResult getAllInterfaceGroup(String projectId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！");
        }
        List<InterfaceGroupEntity> groups = getBaseDao().executeCriteria(InterfaceUtils.getInterfaceByProjectId(projectId));
        if (groups == null) {
            return new BaseResult(0, "暂无数据！");
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (InterfaceGroupEntity group : groups) {
            MapUtils map = new MapUtils();
            map.put("id", group.getId());
            map.put("name", group.getName());
            map.put("interfaceNo", group.getInterfaceNo());
            map.put("createTime", DataUtils.formatDate(group.getCreateTime()));
            map.put("createUserName", group.getCreateUserName());
            map.put("createUserId", group.getCreateUserID());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }
}
