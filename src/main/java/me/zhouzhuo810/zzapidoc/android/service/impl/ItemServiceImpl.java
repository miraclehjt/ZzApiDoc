package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ItemDao;
import me.zhouzhuo810.zzapidoc.android.entity.ItemEntity;
import me.zhouzhuo810.zzapidoc.android.service.ItemService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
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
 * Item
 * Created by zz on 2017/11/10.
 */
@Service
public class ItemServiceImpl extends BaseServiceImpl<ItemEntity> implements ItemService {

    @Resource(name = "userServiceImpl")
    private UserService mUserService;

    @Override
    @Resource(name = "itemDaoImpl")
    public void setBaseDao(BaseDao<ItemEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public ItemDao getBaseDao() {
        return (ItemDao) baseDao;
    }

    @Override
    public BaseResult addItem(int type, String name, String resId, String widgetId, String widgetPid, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！");
        }
        ItemEntity entity = new ItemEntity();
        entity.setType(type);
        entity.setName(name);
        entity.setResId(resId);
        entity.setWidgetId(widgetId);
        entity.setWidgetPid(widgetPid);
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        try {
            save(entity);
            return new BaseResult(1, "添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败！");
        }
    }

    @Override
    public BaseResult deleteItem(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！");
        }
        ItemEntity entity = get(id);
        if (entity == null) {
            return new BaseResult(0, "Item不存在或已被删除！");
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
    public BaseResult updateItem(String itemId, int type, String name, String resId, String widgetId, String widgetPid, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！");
        }
        ItemEntity entity = get(itemId);
        if (entity == null) {
            return new BaseResult(0, "Item不存在或已被删除！");
        }
        entity.setType(type);
        entity.setName(name);
        entity.setResId(resId);
        entity.setWidgetId(widgetId);
        entity.setWidgetPid(widgetPid);
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        entity.setModifyTime(new Date());
        try {
            update(entity);
            return new BaseResult(1, "更新成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "更新失败！");
        }
    }

    @Override
    public BaseResult getAllItems(String widgetId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！");
        }
        if (widgetId != null) {
            List<ItemEntity> items = executeCriteria(new Criterion[]{
                    Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                    Restrictions.eq("widgetId", widgetId)
            }, Order.asc("createTime"));
            if (items != null && items.size() > 0) {
                List<Map<String, Object>> result = new ArrayList<>();
                for (ItemEntity item : items) {
                    MapUtils map = new MapUtils();
                    map.put("id", item.getId());
                    map.put("type", item.getType());
                    map.put("name", item.getName());
                    map.put("resId", item.getResId());
                    map.put("widgetName", item.getWidgetName());
                    map.put("parentWidgetName", item.getParentWidgetName());
                    map.put("createTime", DataUtils.formatDate(item.getCreateTime()));
                    map.put("createPerson", item.getCreateUserName());
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

