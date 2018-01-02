package me.zhouzhuo810.zzapidoc.android.service;

import me.zhouzhuo810.zzapidoc.android.entity.ItemEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;

/**
 * Created by zz on 2017/11/10.
 */
public interface ItemService extends BaseService<ItemEntity> {

    BaseResult addItem(int type, String name, String resId, String widgetId, String userId);

    BaseResult deleteItem(String id, String userId);

    BaseResult updateItem(String itemId, int type, String name, String resId,
                          String widgetId, String userId);

    BaseResult getAllItems(String widgetId, String userId);
}
