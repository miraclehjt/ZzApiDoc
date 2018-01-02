package me.zhouzhuo810.zzapidoc.android.service;

import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by admin on 2017/8/17.
 */
public interface ActivityService extends BaseService<ActivityEntity> {
    BaseResult addActivity(String name, String title,
                           boolean isFirst, MultipartFile splashImg,
                           int splashSecond,
                           int type, String appId,
                           String targetActId,
                           boolean isLandscape,
                           boolean isFullScreen,
                           int guideImgCount,
                           MultipartFile guideImgOne,
                           MultipartFile guideImgTwo,
                           MultipartFile guideImgThree,
                           MultipartFile guideImgFour,
                           MultipartFile guideImgFive,
                           String userId);

    BaseResult deleteActivity(String id, String userId);

    BaseResult previewUI(String id, String userId);

    BaseResult getAllMyActivity(String appId, String userId);
}
