package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ActionDao;
import me.zhouzhuo810.zzapidoc.android.entity.ActionEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActionService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
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
    public BaseResult addAction(int type, String name, String widgetId, String title, String msg, String okText, String cancelText, String hintText, String defText, boolean showOrHide, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不能为空！");
        }


        return null;
    }

    @Override
    public BaseResult deleteAction(String id, String userId) {
        return null;
    }

    @Override
    public BaseResult updateAction(String actionId, int type, String name, String widgetId, String title, String msg, String okText, String cancelText, String hintText, String defText, boolean showOrHide, String userId) {
        return null;
    }

    @Override
    public BaseResult getAllActions(String userId) {
        return null;
    }
}
