package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ActivityDao;
import me.zhouzhuo810.zzapidoc.android.dao.ActivityDao;
import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.FileUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/17.
 */
@Service
public class ActivityServiceImpl extends BaseServiceImpl<ActivityEntity> implements ActivityService {

    @Resource(name = "userServiceImpl")
    private UserService mUserService;

    @Override
    @Resource(name = "activityDaoImpl")
    public void setBaseDao(BaseDao<ActivityEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public ActivityDao getBaseDao() {
        return (ActivityDao) baseDao;
    }


    @Override
    public BaseResult addActivity(String name, String title, boolean showTitle,
                                  MultipartFile splashImg, int type, String appId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ActivityEntity entity = new ActivityEntity();
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        entity.setName(name);
        entity.setTitle(title);
        entity.setShowTitleBar(showTitle);
        if (splashImg != null) {
            try {
                String path = FileUtils.saveFile(splashImg.getBytes(), "image", splashImg.getOriginalFilename());
                entity.setSplashImg(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        entity.setApplicationId(appId);
        entity.setType(type);
        try {
            getBaseDao().save(entity);
            return new BaseResult(1, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败");
        }
    }

    @Override
    public BaseResult deleteActivity(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ActivityEntity entity = getBaseDao().get(id);
        if (entity == null) {
            return new BaseResult(0, "应用不存在");
        }
        entity.setDeleteFlag(BaseEntity.DELETE_FLAG_YES);
        try {
            getBaseDao().update(entity);
            return new BaseResult(1, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "删除失败");
        }
    }

    @Override
    public BaseResult getAllMyActivity(String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        List<ActivityEntity> applicationEntities = getBaseDao().executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("createUserID", user.getId())
        });
        if (applicationEntities == null) {
            return new BaseResult(1, "ok");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ActivityEntity applicationEntity : applicationEntities) {
            MapUtils map = new MapUtils();
            map.put("id", applicationEntity.getId());
            map.put("name", applicationEntity.getName());
            map.put("type", applicationEntity.getType());
            map.put("splashImg", applicationEntity.getSplashImg());
            map.put("title", applicationEntity.getTitle());
            map.put("showTitle", applicationEntity.getShowTitleBar());
            map.put("appId", applicationEntity.getApplicationId());
            map.put("createTime", DataUtils.formatDate(applicationEntity.getCreateTime()));
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);

    }

}
