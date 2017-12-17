package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ActionDao;
import me.zhouzhuo810.zzapidoc.android.entity.ActionEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActionService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 动作
 * Created by zz on 2017/11/10.
 */
@Service
public class ActionServiceImpl extends BaseServiceImpl<ActionEntity> implements ActionService {

    @Resource(name = "userServiceImpl")
    private UserService mUserService;

    @Override
    @Resource(name = "actionDaoImpl")
    public void setBaseDao(BaseDao<ActionEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public ActionDao getBaseDao() {
        return (ActionDao) baseDao;
    }

    @Override
    public BaseResult addAction(int type, String name, String widgetId, String title, String msg, String okText, String cancelText, String hintText, String defText, boolean showOrHide,
                                String items, String okApiId, int groupPosition, String okActId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！");
        }
        ActionEntity entity = new ActionEntity();
        entity.setType(type);
        entity.setName(name);
        entity.setWidgetId(widgetId);
        entity.setTitle(title);
        entity.setMsg(msg);
        entity.setOkText(okText);
        entity.setCancelText(cancelText);
        entity.setHintText(hintText);
        entity.setDefText(defText);
        entity.setOkApiGroupPosition(groupPosition);
        entity.setItems(items);
        entity.setShowOrHide(showOrHide);
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        entity.setOkApiId(okApiId);
        entity.setOkActId(okActId);
        try {
            save(entity);
            return new BaseResult(1, "添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败！");
        }
    }

    @Override
    public BaseResult deleteAction(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！");
        }
        ActionEntity entity = get(id);
        if (entity == null) {
            return new BaseResult(0, "动作不存在或已被删除！");
        }
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        entity.setModifyTime(new Date());
        entity.setDeleteFlag(BaseEntity.DELETE_FLAG_YES);
        try {
            update(entity);
            return new BaseResult(1, "删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "删除失败！");
        }
    }

    @Override
    public BaseResult updateAction(String actionId, int type, String name, String widgetId, String title, String msg, String okText, String cancelText, String hintText, String defText, boolean showOrHide,
                                   String items, String okApiId, int groupPosition, String okActId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！");
        }
        ActionEntity entity = get(actionId);
        if (entity == null) {
            return new BaseResult(0, "动作不存在或已被删除！");
        }
        entity.setType(type);
        entity.setName(name);
        entity.setWidgetId(widgetId);
        entity.setTitle(title);
        entity.setMsg(msg);
        entity.setOkApiGroupPosition(groupPosition);
        entity.setOkText(okText);
        entity.setCancelText(cancelText);
        entity.setHintText(hintText);
        entity.setDefText(defText);
        entity.setItems(items);
        entity.setShowOrHide(showOrHide);
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        entity.setModifyTime(new Date());
        entity.setOkApiId(okApiId);
        entity.setOkActId(okActId);
        try {
            update(entity);
            return new BaseResult(1, "更新成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "更新失败！");
        }
    }

    @Override
    public BaseResult getAllActions(String widgetId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！");
        }
        if (widgetId != null) {
            List<ActionEntity> actions = executeCriteria(new Criterion[]{
                    Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                    Restrictions.eq("widgetId", widgetId)
            });
            if (actions != null && actions.size() > 0) {
                List<Map<String, Object>> result = new ArrayList<>();
                for (ActionEntity action : actions) {
                    MapUtils map = new MapUtils();
                    map.put("id", action.getId());
                    map.put("name", action.getName());
                    map.put("type", action.getType());
                    map.put("title", action.getTitle());
                    map.put("createTime", DataUtils.formatDate(action.getCreateTime()));
                    map.put("createPerson", action.getCreateUserName());
                    map.put("okText", action.getOkText());
                    map.put("canText", action.getCancelText());
                    map.put("defText", action.getDefText());
                    map.put("hintText", action.getHintText());
                    map.put("msg", action.getMsg());
                    map.put("items", action.getItems());
                    result.add(map.build());
                }
                return new BaseResult(1, "ok", result);
            } else {
                return new BaseResult(1, "ok");
            }
        } else {
            return new BaseResult(0, "请选择控件！");
        }
    }

}

