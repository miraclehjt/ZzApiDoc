package me.zhouzhuo810.zzapidoc.android.service;

import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.WidgetEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by admin on 2017/8/17.
 */
public interface WidgetService extends BaseService<WidgetEntity> {

    BaseResult addWidget(String name, String title, int type, String defValue,
                         String hint, String leftTitleText, String rightTitleText,
                         String leftTitleImg, String rightTitleImg, boolean showLeftTitleImg,
                         boolean showRightTitleImg, boolean showLeftTitleText, boolean showRightTitleText,
                         boolean showLeftTitleLayout, boolean showRightTitleLayout,
                         String targetActId, String relativeId, String appId, String userId);

    BaseResult getAllMyWidget(String userId);


    BaseResult deleteWidget(String id, String userId);

}
