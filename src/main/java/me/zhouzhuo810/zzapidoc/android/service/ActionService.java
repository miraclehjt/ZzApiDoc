package me.zhouzhuo810.zzapidoc.android.service;

import me.zhouzhuo810.zzapidoc.android.entity.ActionEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;

/**
 * Created by zz on 2017/11/10.
 */
public interface ActionService extends BaseService<ActionEntity> {

    BaseResult addAction(int type, String name,String widgetId, String title, String msg, String okText, String cancelText, String hintText, String defText, boolean showOrHide,
                         String okApiId, String okActId, String userId);

    BaseResult deleteAction(String id, String userId);

    BaseResult updateAction(String actionId, int type, String name,String widgetId, String title, String msg, String okText, String cancelText, String hintText, String defText, boolean showOrHide,
                            String okApiId, String okActId, String userId);

    BaseResult getAllActions(String widgetId, String userId);
}