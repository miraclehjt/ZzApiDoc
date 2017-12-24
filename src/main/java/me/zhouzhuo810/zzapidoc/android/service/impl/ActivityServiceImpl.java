package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ActivityDao;
import me.zhouzhuo810.zzapidoc.android.dao.ActivityDao;
import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.ApplicationEntity;
import me.zhouzhuo810.zzapidoc.android.entity.WidgetEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.android.service.ApplicationService;
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
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/17.
 */
@Service
public class ActivityServiceImpl extends BaseServiceImpl<ActivityEntity> implements ActivityService {

    @Resource(name = "userServiceImpl")
    private UserService mUserService;

    @Resource(name = "widgetServiceImpl")
    private WidgetService mWidgetService;

    @Resource(name = "applicationServiceImpl")
    private ApplicationService mApplicationService;


    @Override
    @Resource(name = "activityDaoImpl")
    public void setBaseDao(BaseDao<ActivityEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public ActivityDao getBaseDao() {
        return (ActivityDao) baseDao;
    }


    @Override
    public BaseResult addActivity(String name, String title, boolean isFirst,
                                  MultipartFile splashImg, int splashSecond, int type, String appId, String targetActId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ActivityEntity entity = new ActivityEntity();
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        entity.setName(name);
        entity.setFirst(isFirst);
        entity.setTargetActId(targetActId);
        entity.setTitle(title);
        entity.setSplashSecond(splashSecond == 0 ? 5 : splashSecond);
        if (splashImg != null) {
            try {
                String path = FileUtils.saveFile(splashImg.getBytes(), "image", splashImg.getOriginalFilename());
                entity.setSplashImg(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        entity.setApplicationId(appId);
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
    public BaseResult deleteActivity(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ActivityEntity entity = getBaseDao().get(id);
        if (entity == null) {
            return new BaseResult(0, "Activity不存在");
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
    public BaseResult previewUI(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法", new HashMap<String, String>());
        }
        ActivityEntity entity = getBaseDao().get(id);
        if (entity == null) {
            return new BaseResult(0, "Activity不存在", new HashMap<String, String>());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "\t<meta charset=\"UTF-8\">\n" +
                "\t<title>预览</title>\n" +
                "\t<style>\n" +
                "\t* {\n" +
                "\t\tmargin: 0 auto;\n" +
                "\t\ttext-align: center;\n" +
                "\t}\n" +
                "\t</style>\n" +
                "</head>\n" +
                "\n" +
                "<body>");

        generateChildUI(entity, sb);

        sb.append("</body>\n" +
                "</html>");

        MapUtils map = new MapUtils();
        map.put("content", sb.toString());
        return new BaseResult(1, "ok", map.build());
    }

    private void generateChildUI(ActivityEntity entity, StringBuilder sb) {
        String applicationId = entity.getApplicationId();
        ApplicationEntity applicationEntity = mApplicationService.get(applicationId);
        switch (entity.getType()) {
            case ActivityEntity.TYPE_EMPTY_ACT:
                generateWidgetUI(entity.getId(), applicationEntity, "0", sb);
                break;
        }
    }

    private void generateWidgetUI(String relativeId, ApplicationEntity app, String pid, StringBuilder sb) {
        List<WidgetEntity> widgetEntities = mWidgetService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("pid", pid),
                Restrictions.eq("relativeId", relativeId)
        });

        if (widgetEntities != null && widgetEntities.size() > 0) {
            for (WidgetEntity widgetEntity : widgetEntities) {
                switch (widgetEntity.getType()) {
                    case WidgetEntity.TYPE_TITLE_BAR:
                        sb.append("<div class=\"title-bar\" style=\"width:100%;height:100px;line-height:100px;background:" + app.getColorMain() + ";font-size:40px;color:#fff;\">\n" +
                                "\t" + widgetEntity.getTitle() + "\n" +
                                "</div>");
                        break;
                    case WidgetEntity.TYPE_UNDERLINE_EDIT_ITEM:
                        sb.append("\n<input type=\"text\" style=\"font-size:20px;color:#00f;width:94%;margin-top:20px;margin-bottom:20px;line-height:80px;height:80px;\" >\n");
                        break;
                    case WidgetEntity.TYPE_TITLE_EDIT_ITEM:
                        sb.append("\n<div>\n" +
                                "\t<label style=\"width:10%;font-size:26px;color:#000;margin-right:30px;\">" + widgetEntity.getTitle() + "</label>\n" +
                                "\t<input type=\"text\" style=\"font-size:20px;color:#00f;width:84%;margin-top:20px;margin-bottom:20px;line-height:80px;height:80px;\" >\n" +
                                "</div>\n");
                        break;
                    case WidgetEntity.TYPE_SETTING_ITEM:
                        sb.append("\n<hr>\n" +
                                "<div style=\"width:100%;height:90px;line-height:90px;text-align:right;\">\n" +
                                "\t<label style=\"font-size:26px;color:#000;margin-right:1550px;\">用户名</label>\n" +
                                "\t<img src=\"../../../res/drawable/more.png\" style=\"width:50px;height:50px;margin-right:100px;\" >\n" +
                                "</div>\n" +
                                "<hr>\n");
                        break;
                    case WidgetEntity.TYPE_SUBMIT_BTN_ITEM:
                        sb.append("\n<button style=\"width:94%;height:90px;background:" + app.getColorMain() + ";font-size:30px;color:#fff;margin-top:20px;margin-bottom:20px;\">提交</button>\n");
                        break;
                    case WidgetEntity.TYPE_EXIT_BTN_ITEM:
                        sb.append("\n<button style=\"width:94%;height:90px;background:#d44a4a;font-size:30px;color:#fff;margin-top:20px;margin-bottom:20px;\">退出</button>\n");
                        break;
                }
                generateWidgetUI(relativeId, app, widgetEntity.getId(), sb);
            }
        }
    }


    @Override
    public BaseResult getAllMyActivity(String appId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        List<ActivityEntity> applicationEntities = getBaseDao().executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("applicationId", appId),
                Restrictions.eq("createUserID", user.getId())
        });
        if (applicationEntities == null) {
            return new BaseResult(1, "ok");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ActivityEntity applicationEntity : applicationEntities) {
            MapUtils map = new MapUtils();
            map.put("id", applicationEntity.getId());
            map.put("name", applicationEntity.getName());
            map.put("type", applicationEntity.getType());
            map.put("splashImg", applicationEntity.getSplashImg());
            map.put("splashSecond", applicationEntity.getSplashSecond() == null ? 5 : applicationEntity.getSplashSecond());
            map.put("title", applicationEntity.getTitle());
            map.put("appId", applicationEntity.getApplicationId());
            map.put("targetActId", applicationEntity.getTargetActId());
            map.put("targetActName", applicationEntity.getTargetActName());
            map.put("createTime", DataUtils.formatDate(applicationEntity.getCreateTime()));
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);

    }

}
