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

    BaseResult addWidget(String name, String title, String resId, int type, String defValue,
                         String hint, String leftTitleText, String rightTitleText,
                         MultipartFile leftTitleImg, MultipartFile rightTitleImg, boolean showLeftTitleImg,
                         boolean showRightTitleImg, boolean showLeftTitleText, boolean showRightTitleText,
                         boolean showLeftTitleLayout, boolean showRightTitleLayout,
                         String pid, String background, int width, int height, double weight, int marginLeft,
                         int marginRight, int marginTop, int marginBottom, int paddingLeft, int paddingRight,
                         int paddingTop, int paddingBottom, String gravity,
                         String orientation, String relativeId, String appId, String textColor, int textSize, String userId);

    BaseResult getAllMyWidget(String relativeId, String pid, String userId);


    BaseResult deleteWidget(String id, String userId);

    BaseResult updateWidget(String widgetId, String name, String title, String resId, int type,
                            String defValue, String hint, String leftTitleText, String rightTitleText,
                            MultipartFile leftTitleImg, MultipartFile rightTitleImg, boolean showLeftTitleImg,
                            boolean showRightTitleImg, boolean showLeftTitleText, boolean showRightTitleText,
                            boolean showLeftTitleLayout, boolean showRightTitleLayout, String pid, String background,
                            int width, int height, double weight, int marginLeft, int marginRight, int marginTop, int marginBottom,
                            int paddingLeft, int paddingRight, int paddingTop, int paddingBottom, String gravity, String orientation,
                            String relativeId, String appId, String textColor, int textSize, String userId);

    BaseResult getWidgetDetail(String widgetId, String userId);
}
