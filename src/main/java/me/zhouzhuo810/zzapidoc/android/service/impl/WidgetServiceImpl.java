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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

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
                                boolean showRightTitleLayout, String pid, String background, int width, int height,
                                double weight, int marginLeft, int marginRight, int marginTop, int marginBottom,
                                int paddingLeft, int paddingRight, int paddingTop, int paddingBottom, String gravity, String orientation, String relativeId,
                                String appId, String textColor, int textSize, String userId) {
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
        entity.setRelativeId(relativeId);
        entity.setPid(pid == null ? "0" : pid);
        entity.setBackground(background == null ? "@android:color/transparent" : background);
        entity.setWidth(width);
        entity.setHeight(height);
        entity.setTextColor(textColor == null ? "000" : textColor);
        entity.setTextSize(textSize);
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
    public BaseResult updateWidget(String widgetId, String name, String title, String resId, int type, String defValue, String hint, String leftTitleText, String rightTitleText, MultipartFile leftTitleImg, MultipartFile rightTitleImg, boolean showLeftTitleImg, boolean showRightTitleImg, boolean showLeftTitleText, boolean showRightTitleText, boolean showLeftTitleLayout, boolean showRightTitleLayout, String pid, String background, int width, int height, double weight, int marginLeft, int marginRight, int marginTop, int marginBottom, int paddingLeft, int paddingRight, int paddingTop, int paddingBottom, String gravity, String orientation, String relativeId, String appId, String textColor, int textSize, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        WidgetEntity entity = getBaseDao().get(widgetId);
        if (entity == null) {
            return new BaseResult(0, "控件不存在或已被删除");
        }
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        entity.setModifyTime(new Date());
        entity.setName(name);
        entity.setResId(resId);
        entity.setHint(hint);
        entity.setRelativeId(relativeId);
        entity.setPid(pid == null ? "0" : pid);
        entity.setBackground(background == null ? "@android:color/transparent" : background);
        entity.setWidth(width);
        entity.setHeight(height);
        entity.setTextColor(textColor == null ? "000" : textColor);
        entity.setTextSize(textSize);
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
            getBaseDao().update(entity);
            return new BaseResult(1, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "修改失败");
        }
    }

    @Override
    public BaseResult getWidgetDetail(String widgetId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法", new HashMap<String, String>());
        }
        WidgetEntity entity = get(widgetId);
        if (entity == null) {
            return new BaseResult(0, "控件不存在或已被删除", new HashMap<String, String>());
        }
        MapUtils map = new MapUtils();
        map.put("id", entity.getId());
        map.put("name", entity.getName());
        map.put("title", entity.getTitle());
        map.put("resId", entity.getResId());
        map.put("defValue", entity.getDefValue());
        map.put("type", entity.getType());
        map.put("hint", entity.getHint());
        map.put("leftTitleText", entity.getLeftTitleText());
        map.put("rightTitleText", entity.getRightTitleText());
        map.put("showLeftTitleImg", entity.getShowLeftTitleImg() == null ? false : entity.getShowLeftTitleImg());
        map.put("showRightTitleImg", entity.getShowRightTitleImg() == null ? false : entity.getShowRightTitleImg());
        map.put("showLeftTitleText", entity.getShowLeftTitleText() == null ? false : entity.getShowLeftTitleText());
        map.put("showRightTitleText", entity.getShowRightTitleText() == null ? false : entity.getShowRightTitleText());
        map.put("showLeftTitleLayout", entity.getShowLeftTitleLayout() == null ? false : entity.getShowLeftTitleLayout());
        map.put("showRightTitleLayout", entity.getShowRightTitleLayout() == null ? false : entity.getShowRightTitleLayout());
        map.put("pid", entity.getPid());
        map.put("background", entity.getBackground());
        map.put("width", entity.getWidth());
        map.put("height", entity.getHeight());
        map.put("weight", entity.getWeight() == null ? 0 : entity.getWeight());
        map.put("marginLeft", entity.getMarginLeft());
        map.put("marginRight", entity.getMarginRight());
        map.put("marginTop", entity.getMarginTop());
        map.put("marginBottom", entity.getMarginBottom());
        map.put("paddingLeft", entity.getPaddingLeft());
        map.put("paddingRight", entity.getPaddingRight());
        map.put("paddingTop", entity.getPaddingTop());
        map.put("paddingBottom", entity.getPaddingBottom());
        map.put("gravity", entity.getGravity());
        map.put("orientation", entity.getOrientation());
        map.put("relativeId", entity.getRelativeId());
        map.put("appId", entity.getApplicationId());
        map.put("textColor", entity.getTextColor());
        map.put("textSize", entity.getTextSize() == null ? 40 : entity.getTextSize());
        map.put("modifyTime", DataUtils.formatDate(entity.getModifyTime()));
        return new BaseResult(1, "ok", map.build());
    }

    @Override
    public BaseResult getAllMyWidget(String relativeId, String pid, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        List<WidgetEntity> widgetEntities = getBaseDao().executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("createUserID", user.getId()),
                Restrictions.eq("pid", pid),
                Restrictions.eq("relativeId", relativeId)
        }, Order.asc("createTime"));
        if (widgetEntities == null) {
            return new BaseResult(1, "ok");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (WidgetEntity widgetEntity : widgetEntities) {
            MapUtils map = new MapUtils();
            map.put("id", widgetEntity.getId());
            map.put("name", widgetEntity.getName());
            map.put("type", widgetEntity.getType());
            map.put("title", widgetEntity.getTitle());
            map.put("resId", widgetEntity.getResId());
            map.put("defValue", widgetEntity.getDefValue());
            map.put("hint", widgetEntity.getHint());
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
            map.put("createTime", DataUtils.formatDate(widgetEntity.getCreateTime()));
            map.put("createUserName", widgetEntity.getCreateUserName());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);

    }

}
