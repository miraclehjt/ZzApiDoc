package me.zhouzhuo810.zzapidoc.android.action;

import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/8/17.
 */
@Controller
@RequestMapping(value = "v1/activity")
public class ActivityAction extends BaseController<ActivityEntity> {

    @Override
    @Resource(name = "activityServiceImpl")
    public void setBaseService(BaseService<ActivityEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public ActivityService getBaseService() {
        return (ActivityService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addActivity", method = RequestMethod.POST)
    public BaseResult addActivity(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "isFirst") boolean isFirst,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "appId", required = false) String appId,
            @RequestParam(value = "targetActId", required = false) String targetActId,
            @RequestParam(value = "isLandscape", required = false) boolean isLandscape,
            @RequestParam(value = "isFullScreen", required = false) boolean isFullScreen,
            @RequestParam(value = "splashSecond") int splashSecond,
            @RequestBody(required = false) MultipartFile splashImg,
            @RequestParam(value = "guideImgCount", required = false) int guideImgCount,
            @RequestBody(required = false) MultipartFile guideImgOne,
            @RequestBody(required = false) MultipartFile guideImgTwo,
            @RequestBody(required = false) MultipartFile guideImgThree,
            @RequestBody(required = false) MultipartFile guideImgFour,
            @RequestBody(required = false) MultipartFile guideImgFive,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().addActivity(name, title, isFirst, splashImg, splashSecond, type, appId, targetActId, isLandscape, isFullScreen,
                guideImgCount, guideImgOne, guideImgTwo, guideImgThree, guideImgFour, guideImgFive, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/updateActivity", method = RequestMethod.POST)
    public BaseResult updateActivity(
            @RequestParam(value = "actId") String actId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "isFirst") boolean isFirst,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "appId", required = false) String appId,
            @RequestParam(value = "targetActId", required = false) String targetActId,
            @RequestParam(value = "isLandscape", required = false) boolean isLandscape,
            @RequestParam(value = "isFullScreen", required = false) boolean isFullScreen,
            @RequestParam(value = "splashSecond") int splashSecond,
            @RequestBody(required = false) MultipartFile splashImg,
            @RequestParam(value = "guideImgCount", required = false) int guideImgCount,
            @RequestBody(required = false) MultipartFile guideImgOne,
            @RequestBody(required = false) MultipartFile guideImgTwo,
            @RequestBody(required = false) MultipartFile guideImgThree,
            @RequestBody(required = false) MultipartFile guideImgFour,
            @RequestBody(required = false) MultipartFile guideImgFive,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().updateActivity(actId, name, title, isFirst, splashImg, splashSecond, type, appId, targetActId, isLandscape, isFullScreen,
                guideImgCount, guideImgOne, guideImgTwo, guideImgThree, guideImgFour, guideImgFive, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getActivityDetail", method = RequestMethod.GET)
    public BaseResult getActivityDetail(
            @RequestParam(value = "actId") String actId,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().getActivityDetail(actId, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/getAllMyActivity", method = RequestMethod.GET)
    public BaseResult getAllMyActivity(
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().getAllMyActivity(appId, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/deleteActivity", method = RequestMethod.POST)
    public BaseResult deleteActivity(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "id") String id
    ) {
        return getBaseService().deleteActivity(id, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/previewUI", method = RequestMethod.POST)
    public BaseResult previewUI(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "id") String id
    ) {
        return getBaseService().previewUI(id, userId);
    }

}
