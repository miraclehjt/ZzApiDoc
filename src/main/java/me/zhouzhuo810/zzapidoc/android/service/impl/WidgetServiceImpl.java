package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ActivityDao;
import me.zhouzhuo810.zzapidoc.android.dao.WidgetDao;
import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.WidgetEntity;
import me.zhouzhuo810.zzapidoc.android.entity.WidgetEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.android.service.WidgetService;
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
public class WidgetServiceImpl extends BaseServiceImpl<WidgetEntity> implements WidgetService {

    @Resource(name = "userServiceImpl")
    private UserService mUserService;

    @Override
    @Resource(name = "widgetDaoImpl")
    public void setBaseDao(BaseDao<WidgetEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public WidgetDao getBaseDao() {
        return (WidgetDao) baseDao;
    }

    @Override
    public BaseResult addWidget(String name, String title, String resId, int type, String defValue, String hint,
                                String leftTitleText, String rightTitleText, MultipartFile leftTitleImg,
                                MultipartFile rightTitleImg, boolean showLeftTitleImg, boolean showRightTitleImg,
                                boolean showLeftTitleText, boolean showRightTitleText, boolean showLeftTitleLayout,
                                boolean showRightTitleLayout, boolean clickToClose, String pid, String background, int width, int height,
                                double weight, int marginLeft, int marginRight, int marginTop, int marginBottom,
                                int paddingLeft, int paddingRight, int paddingTop, int paddingBottom, String gravity,
                                String targetActId, String orientation, String relativeId, String targetApiId, String appId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        WidgetEntity entity = new WidgetEntity();
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        entity.setName(name);
        entity.setResId(resId);
        entity.setHint(hint);
        entity.setTargetApiId(targetApiId);
        entity.setRelativeId(relativeId);
        entity.setPid(pid==null?"0":pid);
        entity.setBackground(background==null?"@android:color/transparent":background);
        entity.setWidth(width);
        entity.setHeight(height);
        entity.setClickToClose(clickToClose);
        entity.setGravity(gravity);
        entity.setOrientation(orientation);
        entity.setWeight(weight);
        entity.setMarginLeft(marginLeft);
        entity.setMarginRight(marginRight);
        entity.setMarginTop(marginTop);
        entity.setMarginBottom(marginBottom);
        entity.setPaddingLeft(paddingLeft);
        entity.setPaddingRight(paddingRight);
        entity.setPaddingTop(paddingTop);
        entity.setPaddingBottom(paddingBottom);
        entity.setTitle(title);
        entity.setDefValue(defValue);
        entity.setApplicationId(appId);
        entity.setTargetActivityId(targetActId);
        if (leftTitleImg != null) {
            try {
                String path = FileUtils.saveFile(leftTitleImg.getBytes(), "image", leftTitleImg.getOriginalFilename());
                entity.setLeftTitleImg(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (rightTitleImg != null) {
            try {
                String path = FileUtils.saveFile(rightTitleImg.getBytes(), "image", rightTitleImg.getOriginalFilename());
                entity.setRightTitleImg(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        entity.setLeftTitleText(leftTitleText);
        entity.setRightTitleText(rightTitleText);
        entity.setShowLeftTitleImg(showLeftTitleImg);
        entity.setShowRightTitleImg(showRightTitleImg);
        entity.setShowLeftTitleText(showLeftTitleText);
        entity.setShowRightTitleText(showRightTitleText);
        entity.setShowLeftTitleLayout(showLeftTitleLayout);
        entity.setShowRightTitleLayout(showRightTitleLayout);
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
    public BaseResult deleteWidget(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        WidgetEntity entity = getBaseDao().get(id);
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
    public BaseResult getAllMyWidget(String relativeId, String pid, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        List<WidgetEntity> applicationEntities = getBaseDao().executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("createUserID", user.getId()),
                Restrictions.eq("pid", pid),
                Restrictions.eq("relativeId", relativeId)
        });
        if (applicationEntities == null) {
            return new BaseResult(1, "ok");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (WidgetEntity widgetEntity : applicationEntities) {
            MapUtils map = new MapUtils();
            map.put("id", widgetEntity.getId());
            map.put("name", widgetEntity.getName());
            map.put("type", widgetEntity.getType());
            map.put("title", widgetEntity.getTitle());
            map.put("resId", widgetEntity.getResId());
            map.put("defValue", widgetEntity.getDefValue());
            map.put("hint", widgetEntity.getHint());
            map.put("clickToClose", widgetEntity.getClickToClose());
            map.put("leftTitleImg", widgetEntity.getLeftTitleImg());
            map.put("rightTitleImg", widgetEntity.getRightTitleImg());
            map.put("leftTitleText", widgetEntity.getLeftTitleText());
            map.put("rightTitleText", widgetEntity.getRightTitleText());
            map.put("showLeftTitleImg", widgetEntity.getShowLeftTitleImg());
            map.put("showLeftTitleText", widgetEntity.getShowLeftTitleText());
            map.put("showRightTitleImg", widgetEntity.getShowRightTitleImg());
            map.put("showRightTitleText", widgetEntity.getShowRightTitleText());
            map.put("showLeftTitleLayout", widgetEntity.getShowLeftTitleLayout());
            map.put("showRightTitleLayout", widgetEntity.getShowRightTitleLayout());
            map.put("appId", widgetEntity.getApplicationId());
            map.put("targetActivityId", widgetEntity.getTargetActivityId());
            map.put("createTime", DataUtils.formatDate(widgetEntity.getCreateTime()));
            map.put("createUserName", widgetEntity.getCreateUserName());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);

    }

}
