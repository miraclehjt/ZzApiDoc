package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ApplicationDao;
import me.zhouzhuo810.zzapidoc.android.entity.*;
import me.zhouzhuo810.zzapidoc.android.service.*;
import me.zhouzhuo810.zzapidoc.android.utils.ZipUtils;
import me.zhouzhuo810.zzapidoc.android.widget.apicreator.ApiTool;
import me.zhouzhuo810.zzapidoc.cache.entity.CacheEntity;
import me.zhouzhuo810.zzapidoc.cache.service.CacheService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.FileUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ProjectEntity;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceService;
import me.zhouzhuo810.zzapidoc.project.service.ProjectService;
import me.zhouzhuo810.zzapidoc.project.service.RequestArgService;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.apache.log4j.Logger;
import org.aspectj.util.FileUtil;
import org.gradle.tooling.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONStringer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;

/**
 * Created by admin on 2017/8/17.
 */
@Service
public class ApplicationServiceImpl extends BaseServiceImpl<ApplicationEntity> implements ApplicationService {

    private static Logger LOGGER = Logger.getLogger(ApplicationServiceImpl.class.getSimpleName());

    @Resource(name = "userServiceImpl")
    private UserService mUserService;

    @Resource(name = "cacheServiceImpl")
    CacheService mCacheService;

    @Resource(name = "activityServiceImpl")
    ActivityService mActivityService;

    @Resource(name = "fragmentServiceImpl")
    FragmentService mFragmentService;

    @Resource(name = "widgetServiceImpl")
    WidgetService mWidgetService;

    @Resource(name = "projectServiceImpl")
    ProjectService mProjectService;

    @Resource(name = "interfaceServiceImpl")
    InterfaceService mInterfaceService;

    @Resource(name = "actionServiceImpl")
    ActionService mActionService;

    @Resource(name = "itemServiceImpl")
    ItemService mItemService;

    @Resource(name = "requestArgServiceImpl")
    RequestArgService mRequestArgService;


    @Override
    @Resource(name = "applicationDaoImpl")
    public void setBaseDao(BaseDao<ApplicationEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public ApplicationDao getBaseDao() {
        return (ApplicationDao) baseDao;
    }

    @Override
    public BaseResult addApplication(String chName, String appName, String versionName, String packageName, MultipartFile logo,
                                     String colorMain, int minSDK, int compileSDK, int targetSDK, int versionCode, boolean enableQrCode,
                                     boolean multiDex, boolean minifyEnabled, String apiId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ApplicationEntity entity = new ApplicationEntity();
        entity.setChName(chName);
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        entity.setApiId(apiId);
        entity.setAppName(appName);
        entity.setMinifyEnabled(minifyEnabled);
        entity.setColorMain(colorMain == null ? "#438cff" : "#" + colorMain);
        if (logo != null) {
            try {
                String path = FileUtils.saveFile(logo.getBytes(), "image", logo.getOriginalFilename());
                entity.setLogo(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        entity.setEnableQrCode(enableQrCode);
        entity.setPackageName(packageName);
        entity.setVersionCode(versionCode);
        entity.setVersionName(versionName);
        entity.setTargetSDK(targetSDK);
        entity.setMultiDex(multiDex);
        entity.setCompileSDK(compileSDK);
        entity.setMinSDK(minSDK);
        try {
            getBaseDao().save(entity);
            return new BaseResult(1, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败");
        }
    }

    @Override
    public BaseResult deleteApplication(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ApplicationEntity entity = getBaseDao().get(id);
        if (entity == null) {
            return new BaseResult(0, "应用不存在");
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
    public BaseResult getAllMyApplication(String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        List<ApplicationEntity> applicationEntities = getBaseDao().executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("createUserID", user.getId())
        });
        if (applicationEntities == null) {
            return new BaseResult(1, "ok");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ApplicationEntity applicationEntity : applicationEntities) {
            MapUtils map = new MapUtils();
            map.put("id", applicationEntity.getId());
            map.put("appName", applicationEntity.getAppName());
            map.put("chName", applicationEntity.getChName());
            map.put("minSDK", applicationEntity.getMinSDK());
            map.put("compileSDK", applicationEntity.getCompileSDK());
            map.put("targetSDK", applicationEntity.getTargetSDK());
            map.put("versionCode", applicationEntity.getVersionCode());
            map.put("versionName", applicationEntity.getVersionName());
            map.put("logo", applicationEntity.getLogo());
            map.put("enableQrCode", applicationEntity.getEnableQrCode());
            map.put("createTime", DataUtils.formatDate(applicationEntity.getCreateTime()));
            map.put("colorMain", applicationEntity.getColorMain());
            map.put("packageName", applicationEntity.getPackageName());
            map.put("multiDex", applicationEntity.getMultiDex() == null ? false : applicationEntity.getMultiDex());
            map.put("minifyEnabled", applicationEntity.getMinifyEnabled() == null ? false : applicationEntity.getMinifyEnabled());
            map.put("apiId", applicationEntity.getApiId());
            map.put("actCount", applicationEntity.getActCount() == null ? 0 : applicationEntity.getActCount());
            map.put("fgmCount", applicationEntity.getFgmCount() == null ? 0 : applicationEntity.getFgmCount());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);

    }


    /***********************************下载 APK 开始**************************************/

    @Override
    public ResponseEntity<byte[]> downloadApk(String appId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return null;
        }
        ApplicationEntity app = getBaseDao().get(appId);
        if (app == null) {
            return null;
        }

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String realPath = request.getRealPath("");
            if (realPath != null) {
                String mPath = realPath + File.separator + "APP";
                File dir = new File(mPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                CacheEntity cacheEntity = new CacheEntity();
                cacheEntity.setCachePath(mPath);
                try {
                    List<CacheEntity> cacheEntities = mCacheService.executeCriteria(new Criterion[]{
                            Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                            Restrictions.eq("cachePath", mPath)});
                    if (cacheEntities == null || cacheEntities.size() == 0) {
                        mCacheService.save(cacheEntity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("APP ERROR", e);
                }

                /*Api*/
                String appName = app.getAppName();
                String appDirPath = mPath + File.separator + appName;
                File appDir = new File(appDirPath);
                if (!appDir.exists()) {
                    /*如果不存在，创建app目录*/
                    appDir.mkdirs();
                } else {
                    /*如果存在，删除该目录里的所有文件*/
                    FileUtils.deleteFiles(appDirPath);
                }

                if (app.getApiId() != null && app.getApiId().length() > 0) {
                    ProjectEntity project = mProjectService.get(app.getApiId());
                    if (project != null) {
                        String packageName = app.getPackageName();
                        String packagePath = packageName.replace(".", File.separator);
                        String javaDir = appDirPath
                                + File.separator + "app"
                                + File.separator + "src"
                                + File.separator + "main"
                                + File.separator + "java"
                                + File.separator + packagePath
                                + File.separator + "common"
                                + File.separator + "api";
                        ApiTool.createApi(mInterfaceService.convertToJson(project), app.getPackageName(), javaDir);
                    }
                }

                copyLibs(realPath, appDirPath, app);
                createSettingGradleFile(appDirPath);
                createGradleProperties(appDirPath);
                createBuildGralde(appDirPath);
                copyGradleWrapper(realPath, appDirPath);
                generateApp(realPath, appDirPath, app);

                String apkName = app.getAppName() + "_" + app.getVersionName() + ".apk";

                final String apkPath = appDirPath
                        + File.separator + "app"
                        + File.separator + "build"
                        + File.separator + "outputs"
                        + File.separator + "apk";

                boolean success = buildLauncher(appDirPath, apkPath, apkName, realPath);
                if (success) {
                    /*压缩完毕，删除源文件*/
                    FileUtil.deleteContents(new File(appDirPath));
                } else {
                    /*压缩完毕，删除源文件*/
                    FileUtil.deleteContents(new File(appDirPath));
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", apkName);
                return new ResponseEntity<byte[]>(org.apache.commons.io.FileUtils.readFileToByteArray(new File(realPath + File.separator + "apk"
                        + File.separator + apkName)), headers, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void copyLibs(String rootPath, String appDirPath, ApplicationEntity app) throws IOException {

        String libPath = appDirPath + File.separator + "app" + File.separator + "libs";
        if (app.getEnableQrCode() != null) {
            if (app.getEnableQrCode()) {
                FileUtil.copyDir(new File(rootPath + File.separator + "res" + File.separator + "libs" + File.separator + "zbar")
                        , new File(libPath));
            }
        }
    }

    /***********************************下载 APK 结束**************************************/


    /***********************************下载 项目文件 开始**************************************/

    @Override
    public ResponseEntity<byte[]> downloadApplication(String appId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return null;
        }
        ApplicationEntity app = getBaseDao().get(appId);
        if (app == null) {
            return null;
        }

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String realPath = request.getRealPath("");
            if (realPath != null) {
                String mPath = realPath + File.separator + "APP";
                File dir = new File(mPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                CacheEntity cacheEntity = new CacheEntity();
                cacheEntity.setCachePath(mPath);
                try {
                    List<CacheEntity> cacheEntities = mCacheService.executeCriteria(new Criterion[]{
                            Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                            Restrictions.eq("cachePath", mPath)});
                    if (cacheEntities == null || cacheEntities.size() == 0) {
                        mCacheService.save(cacheEntity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("APP ERROR", e);
                }

                /*Api*/
                String appName = app.getAppName();
                String appDirPath = mPath + File.separator + appName;
                File appDir = new File(appDirPath);
                if (!appDir.exists()) {
                    /*如果不存在，创建app目录*/
                    appDir.mkdirs();
                } else {
                    /*如果存在，删除该目录里的所有文件*/
                    FileUtils.deleteFiles(appDirPath);
                }

                if (app.getApiId() != null && app.getApiId().length() > 0) {
                    ProjectEntity project = mProjectService.get(app.getApiId());
                    if (project != null) {
                        String packageName = app.getPackageName();
                        String packagePath = packageName.replace(".", File.separator);
                        String javaDir = appDirPath
                                + File.separator + "app"
                                + File.separator + "src"
                                + File.separator + "main"
                                + File.separator + "java"
                                + File.separator + packagePath
                                + File.separator + "common"
                                + File.separator + "api";
                        ApiTool.createApi(mInterfaceService.convertToJson(project), app.getPackageName(), javaDir);
                    }
                }

                copyLibs(realPath, appDirPath, app);
                createSettingGradleFile(appDirPath);
                createGradleProperties(appDirPath);
                createBuildGralde(appDirPath);
                copyGradleWrapper(realPath, appDirPath);
                generateApp(realPath, appDirPath, app);

                /*压缩文件*/
                String zipName = System.currentTimeMillis() + ".zip";
                String zipPath = mPath + File.separator + zipName;
                ZipUtils.doCompress(appDirPath, zipPath);

                //压缩完毕，删除源文件
                FileUtil.deleteContents(new File(appDirPath));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", zipName);
                return new ResponseEntity<byte[]>(org.apache.commons.io.FileUtils.readFileToByteArray(new File(zipPath)), headers, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<byte[]> downloadAppJson(String appId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return null;
        }
        ApplicationEntity app = get(appId);
        if (app == null) {
            return null;
        }

        String json = convertToJson(app);
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            URL resource = classLoader.getResource("../../empty_file.txt");
            if (resource != null) {
                String path = resource.getPath();
                if (path != null) {
                    String mPath = new File(path).getParent() + File.separator + "JSON";
                    CacheEntity cacheEntity = new CacheEntity();
                    cacheEntity.setCachePath(mPath);
                    try {
                        List<CacheEntity> cacheEntities = mCacheService.executeCriteria(new Criterion[]{
                                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                                Restrictions.eq("cachePath", mPath)});
                        if (cacheEntities == null || cacheEntities.size() == 0) {
                            mCacheService.save(cacheEntity);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String fileName = me.zhouzhuo810.zzapidoc.common.utils.FileUtils.saveFileToPathWithRandomName(json, mPath);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    headers.setContentDispositionFormData("attachment", fileName);
                    return new ResponseEntity<byte[]>(org.apache.commons.io.FileUtils.readFileToByteArray(new File(mPath + File.separator + fileName)), headers, HttpStatus.CREATED);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BaseResult updateApplication(String appId, String chName, String appName, String versionName, String packageName, MultipartFile logo, String colorMain, int minSDK, int compileSDK, int targetSDK, int versionCode, boolean enableQrCode, boolean multiDex, boolean minifyEnabled, String apiId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ApplicationEntity entity = getBaseDao().get(appId);
        if (entity == null) {
            return new BaseResult(0, "应用不存在或已被删除");
        }
        entity.setChName(chName);
        entity.setModifyUserID(user.getId());
        entity.setModifyUserName(user.getName());
        entity.setModifyTime(new Date());
        entity.setApiId(apiId);
        entity.setAppName(appName);
        entity.setMinifyEnabled(minifyEnabled);
        entity.setColorMain(colorMain == null ? "#438cff" : "#" + colorMain);
        if (logo != null) {
            try {
                String path = FileUtils.saveFile(logo.getBytes(), "image", logo.getOriginalFilename());
                entity.setLogo(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        entity.setEnableQrCode(enableQrCode);
        entity.setPackageName(packageName);
        entity.setVersionCode(versionCode);
        entity.setVersionName(versionName);
        entity.setTargetSDK(targetSDK);
        entity.setMultiDex(multiDex);
        entity.setCompileSDK(compileSDK);
        entity.setMinSDK(minSDK);
        try {
            getBaseDao().update(entity);
            return new BaseResult(1, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "修改失败");
        }
    }

    @Override
    public BaseResult getApplicationDetail(String userId, String appId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法", new HashMap<String,String>());
        }
        ApplicationEntity entity = get(appId);
        if (entity == null) {
            return new BaseResult(0, "应用不存在或已被删除", new HashMap<String,String>());
        }
        MapUtils map = new MapUtils();
        map.put("id", entity.getId());
        map.put("chName", entity.getChName());
        map.put("appName", entity.getAppName());
        map.put("versionName", entity.getVersionName());
        map.put("packageName", entity.getPackageName());
        map.put("colorMain", entity.getColorMain());
        map.put("minSDK", entity.getMinSDK());
        map.put("compileSDK", entity.getCompileSDK());
        map.put("targetSDK", entity.getTargetSDK());
        map.put("versionCode", entity.getVersionCode());
        map.put("enableQrCode", entity.getEnableQrCode() == null ? false : entity.getEnableQrCode());
        map.put("multiDex", entity.getMultiDex() == null ? false : entity.getMultiDex());
        map.put("minifyEnabled", entity.getMinifyEnabled() == null ? false : entity.getMinifyEnabled());
        map.put("apiId", entity.getApiId());
        map.put("apiName", entity.getApiName());
        map.put("modifyTime", DataUtils.formatDate(entity.getModifyTime()));
        return new BaseResult(1, "ok", map.build());
    }

    private String convertToJson(ApplicationEntity app) {
        JSONStringer stringer = new JSONStringer();
        stringer.object();

        //app start
        stringer.key("appId").value(app.getId())
                .key("targetSDK").value(app.getTargetSDK())
                .key("apiId").value(app.getApiId())
                .key("appName").value(app.getAppName())
                .key("chName").value(app.getChName())
                .key("colorMain").value(app.getColorMain())
                .key("compileSDK").value(app.getCompileSDK())
                .key("minSDK").value(app.getMinSDK())
                .key("minifyEnable").value(app.getMinifyEnabled())
                .key("qrCodeEnable").value(app.getEnableQrCode())
                .key("multiDexEnable").value(app.getMultiDex())
                .key("packageName").value(app.getPackageName())
                .key("versionCode").value(app.getVersionCode())
                .key("versionName").value(app.getVersionName())
                .key("logo").value(app.getLogo());
        //acts start
        stringer.key("activities").array();
        List<ActivityEntity> acts = mActivityService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("applicationId", app.getId())
        });
        if (acts != null) {
            for (ActivityEntity act : acts) {
                //act start
                stringer.object();
                stringer
                        .key("acdId").value(act.getId())
                        .key("name").value(act.getName())
                        .key("isFirst").value(act.getFirst())
                        .key("splashImg").value(act.getSplashImg())
                        .key("title").value(act.getTitle())
                        .key("appId").value(act.getApplicationId())
                        .key("splashSecond").value(act.getSplashSecond())
                        .key("targetActId").value(act.getTargetActId())
                        .key("targetActName").value(act.getTargetActName())
                        .key("type").value(act.getType());
                //widgets start
                stringer.key("widgets").array();
                widgetToJson(stringer, act.getId(), "0");
                stringer.endArray();
                //widgets end

                //fgms start
                stringer.key("fragments").array();
                List<FragmentEntity> fgms = mFragmentService.executeCriteria(new Criterion[]{
                        Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                        Restrictions.eq("activityId", act.getId())
                });
                if (fgms != null) {
                    for (FragmentEntity fgm : fgms) {
                        //fgm start
                        stringer.object()
                                .key("id").value(fgm.getId())
                                .key("position").value(fgm.getPosition())
                                .key("title").value(fgm.getTitle())
                                .key("type").value(fgm.getType())
                                .key("name").value(fgm.getName())
                                .key("actId").value(fgm.getActivityId())
                                .key("appId").value(fgm.getApplicationId());

                        //widgets start
                        stringer.key("widgets").array();
                        widgetToJson(stringer, fgm.getId(), "0");
                        stringer.endArray();
                        //widgets end

                        //fgm end
                        stringer.endObject();
                    }
                }
                //fgms end
                stringer.endArray();
                //act end
                stringer.endObject();
            }
        }


        //acts end
        stringer.endArray();
        //app end
        stringer.endObject();
        return stringer.toString();
    }

    private void widgetToJson(JSONStringer stringer, String relativeId, String pid) {
        List<WidgetEntity> widgets = mWidgetService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("pid", pid),
                Restrictions.eq("relativeId", relativeId)
        }, Order.asc("createTime"));
        if (widgets != null) {
            for (WidgetEntity widget : widgets) {
                //widget start
                stringer.object()
                        .key("id").value(widget.getId())
                        .key("appId").value(widget.getApplicationId())
                        .key("getRelativeId").value(widget.getRelativeId())
                        .key("pid").value(widget.getPid())
                        .key("hint").value(widget.getHint())
                        .key("background").value(widget.getBackground())
                        .key("defValue").value(widget.getDefValue())
                        .key("gravity").value(widget.getGravity())
                        .key("height").value(widget.getHeight())
                        .key("width").value(widget.getWidth())
                        .key("leftImg").value(widget.getLeftTitleImg())
                        .key("leftText").value(widget.getLeftTitleText())
                        .key("rightImg").value(widget.getRightTitleImg())
                        .key("rightText").value(widget.getRightTitleText())
                        .key("title").value(widget.getTitle())
                        .key("marginLeft").value(widget.getMarginLeft())
                        .key("marginTop").value(widget.getMarginTop())
                        .key("marginRight").value(widget.getMarginRight())
                        .key("marginBottom").value(widget.getMarginBottom())
                        .key("paddingLeft").value(widget.getPaddingLeft())
                        .key("paddingRight").value(widget.getPaddingRight())
                        .key("paddingTop").value(widget.getPaddingTop())
                        .key("paddingBottom").value(widget.getPaddingBottom())
                        .key("orientation").value(widget.getOrientation())
                        .key("name").value(widget.getName())
                        .key("resId").value(widget.getResId())
                        .key("textColor").value(widget.getTextColor())
                        .key("textSize").value(widget.getTextSize())
                        .key("weight").value(widget.getWeight())
                        .key("showLeftImg").value(widget.getShowLeftTitleImg())
                        .key("showRightImg").value(widget.getShowRightTitleImg())
                        .key("showLeftText").value(widget.getShowLeftTitleText())
                        .key("showRightText").value(widget.getShowRightTitleText())
                        .key("showLeftLayout").value(widget.getShowLeftTitleLayout())
                        .key("showRightLayout").value(widget.getShowRightTitleLayout());

                //actions start
                stringer.key("actions").array();
                actionToJson(stringer, widget.getId(), "0");
                //actions end
                stringer.endArray();

                //children start
                stringer.key("children").array();
                widgetToJson(stringer, relativeId, widget.getId());
                //children end
                stringer.endArray();
                //widget start
                stringer.endObject();
            }
        }
    }

    private void actionToJson(JSONStringer stringer, String widgetId, String pid) {
        List<ActionEntity> actions = mActionService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("pid", pid),
                Restrictions.eq("widgetId", widgetId)
        });
        if (actions != null) {
            for (ActionEntity action : actions) {
                stringer.object()
                        .key("id").value(action.getId())
                        .key("cancelText").value(action.getCancelText())
                        .key("defText").value(action.getDefText())
                        .key("hintText").value(action.getHintText())
                        .key("name").value(action.getName())
                        .key("msg").value(action.getMsg())
                        .key("items").value(action.getItems())
                        .key("okActId").value(action.getOkActId())
                        .key("okApiId").value(action.getOkApiId())
                        .key("okGroupPos").value(action.getOkApiGroupPosition())
                        .key("pid").value(action.getPid())
                        .key("isHide").value(action.getShowOrHide())
                        .key("title").value(action.getTitle())
                        .key("type").value(action.getType())
                        .key("widgetId").value(action.getWidgetId());
                stringer.key("children")
                        .array();
                actionToJson(stringer, widgetId, action.getId());
                stringer.endArray();
                stringer.endObject();

            }
        }
    }

    public boolean buildLauncher(final String projectPath, final String apkPath, final String fileName, final String realPath) {
        ProjectConnection connection = GradleConnector.newConnector().
                forProjectDirectory(new File(projectPath)).connect();
        String buildResult = "";
        try {
            BuildLauncher build = connection.newBuild();
//            build.forTasks("assembleDebug");
            build.forTasks("assembleRelease");
            ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
            PrintStream cacheStream = new PrintStream(baoStream);
            //PrintStream oldStream = System.out;
            System.setOut(cacheStream);//不打印到控制台

            build.setStandardOutput(System.out);
            build.setStandardError(System.err);
            build.run(new ResultHandler<Void>() {
                @Override
                public void onComplete(Void aVoid) {
                    File targetFile = new File(realPath + File.separator + "apk" + File.separator + fileName);
                    if (targetFile.exists()) {
                        targetFile.delete();
                    }
                    try {
                        FileUtil.copyDir(new File(apkPath), new File(realPath + File.separator + "apk"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(GradleConnectionException e) {
                    e.printStackTrace();
                    File targetFile = new File(realPath + File.separator + "apk" + File.separator + fileName);
                    if (targetFile.exists()) {
                        targetFile.delete();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        return buildResult.contains("BUILD SUCCESSFUL");
    }


    private void generateApp(String rootPath, String appDirPath, ApplicationEntity app) throws IOException {

        /*proguard file*/
        FileUtils.saveFileToPathWithName("# Add project specific ProGuard rules here.\n" +
                "# By default, the flags in this file are appended to flags specified\n" +
                "# in /Users/zhouzhuo810/Library/Android/sdk/tools/proguard/proguard-android.txt\n" +
                "# You can edit the include path and order by changing the proguardFiles\n" +
                "# directive in build.gradle.\n" +
                "#\n" +
                "# For more details, see\n" +
                "#   http://developer.android.com/guide/developing/tools/proguard.html\n" +
                "\n" +
                "# Add any project specific keep options here:\n" +
                "\n" +
                "# If your project uses WebView with JS, uncomment the following\n" +
                "# and specify the fully qualified class name to the JavaScript interface\n" +
                "# class:\n" +
                "#-keepclassmembers class fqcn.of.javascript.interface.for.webview {\n" +
                "#   public *;\n" +
                "#}\n" +
                "\n" +
                "# Uncomment this to preserve the line number information for\n" +
                "# debugging stack traces.\n" +
                "#-keepattributes SourceFile,LineNumberTable\n" +
                "\n" +
                "# If you keep the line number information, uncomment this to\n" +
                "# hide the original source file name.\n" +
                "#-renamesourcefileattribute SourceFile\n" +
                "\n" +
                "-dontpreverify\n" +
                "\n" +
                "-ignorewarnings\n" +
                "\n" +
                "##---------------Begin: proguard configuration for Gson  ----------\n" +
                "\n" +
                "-keepattributes Signature\n" +
                "\n" +
                "-keepattributes *Annotation*\n" +
                "\n" +
                "-keep class sun.misc.Unsafe { *; }\n" +
                "\n" +
                "-dontwarn com.google.**\n" +
                "-keep class com.google.gson.** {*;}\n" +
                "#\n" +
                "#-keep class com.google.gson.examples.android.model.** { *; }\n" +
                "\n" +
                "##---------------End: proguard configuration for Gson  ----------\n" +
                "\n" +
                "\n" +
                "##---------------Begin: proguard configuration for Retrofit  ----------\n" +
                "\n" +
                "-dontwarn retrofit2.**\n" +
                "-dontwarn retrofit.**\n" +
                "-keep class retrofit2.** { *; }\n" +
                "-keep class retrofit.** { *; }\n" +
                "\n" +
                "-keepattributes Signature\n" +
                "-keepattributes Exceptions\n" +
                "\n" +
                "##---------------End: proguard configuration for Retrofit  ----------\n" +
                "\n" +
                "##---------------Begin: proguard configuration for SELF  ----------\n" +
                "\n" +
                "-keep class " + app.getPackageName() + ".common.api.entity.** {*;}\n" +
                "-keep class " + app.getPackageName() + ".ui.widget.** {*;}\n" +
                "\n" +
                "##---------------End: proguard configuration for SELF  ----------\n" +
                "\n" +
                "\n" +
                "##---------------Begin: proguard configuration for bugly  ----------\n" +
                "\n" +
                "-dontwarn com.tencent.bugly.**\n" +
                "-keep public class com.tencent.bugly.**{*;}\n" +
                "\n" +
                "##---------------End: proguard configuration for bugly  ----------\n" +
                "\n" +
                "##---------------BEGIN: proguard configuration for u-crop  ----------\n" +
                "\n" +
                "-dontwarn com.yalantis.ucrop**\n" +
                "-keep class com.yalantis.ucrop** { *; }\n" +
                "-keep interface com.yalantis.ucrop** { *; }\n" +
                "\n" +
                "##---------------End: proguard configuration for u-crop  ----------\n" +
                "\n" +
                "##---------------BEGIN: proguard configuration for rxjava  ----------\n" +
                "\n" +
                "-dontwarn rx.**\n" +
                "-keep public class rx.** { *; }\n" +
                "-dontwarn rx.android.**\n" +
                "-keep public class rx.android.** { *; }\n" +
                "\n" +
                "##---------------End: proguard configuration for rxjava  ----------\n" +
                "##---------------BEGIN: proguard configuration for BaseRecyclerViewAdapterHelper  ----------\n" +
                "\n" +
                "-keep class com.chad.library.adapter.** {\n" +
                "*;\n" +
                "}\n" +
                "-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter\n" +
                "-keep public class * extends com.chad.library.adapter.base.BaseViewHolder\n" +
                "-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {\n" +
                "     <init>(...);\n" +
                "}\n" +
                "\n" +
                "##---------------End: proguard configuration for BaseRecyclerViewAdapterHelper  ----------\n" +
                "\n", appDirPath + File.separator + "app", "proguard-rules.pro");

        /*sign file*/
        FileUtil.copyFile(new File(rootPath
                + File.separator + "res"
                + File.separator + "sign"
                + File.separator + "test.jks"
        ), new File(appDirPath + File.separator + "app" + File.separator + "test.jks"));

        /*local.properties*/
        FileUtil.copyFile(new File(rootPath
                + File.separator + "res"
                + File.separator + "local"
                + File.separator + "local.properties"
        ), new File(appDirPath + File.separator + "local.properties"));

        /*git ignore file*/
        FileUtil.copyFile(new File(rootPath
                + File.separator + "res"
                + File.separator + "git"
                + File.separator + ".gitignore"
        ), new File(appDirPath + File.separator + ".gitignore"));


        /*qrcode*/
        if (app.getEnableQrCode() != null) {
            if (app.getEnableQrCode()) {
                FileUtil.copyDir(new File(rootPath
                        + File.separator + "res"
                        + File.separator + "java"
                        + File.separator + "zbar"
                ), new File(appDirPath + File.separator + "app" + File.separator + "src" + File.separator + "main" + File.separator + "java"
                        + File.separator + "com" + File.separator + "obsessive" + File.separator + "zbar"));
                FileUtils.saveFileToPathWithName("package com.obsessive.zbar;\n" +
                        "\n" +
                        "import android.content.Intent;\n" +
                        "import android.content.res.AssetFileDescriptor;\n" +
                        "import android.graphics.Rect;\n" +
                        "import android.hardware.Camera;\n" +
                        "import android.hardware.Camera.AutoFocusCallback;\n" +
                        "import android.hardware.Camera.PreviewCallback;\n" +
                        "import android.hardware.Camera.Size;\n" +
                        "import android.media.AudioManager;\n" +
                        "import android.media.MediaPlayer;\n" +
                        "import android.os.Bundle;\n" +
                        "import android.os.Handler;\n" +
                        "import android.os.Vibrator;\n" +
                        "import android.text.TextUtils;\n" +
                        "import android.view.animation.Animation;\n" +
                        "import android.view.animation.LinearInterpolator;\n" +
                        "import android.view.animation.TranslateAnimation;\n" +
                        "import android.widget.FrameLayout;\n" +
                        "import android.widget.ImageView;\n" +
                        "import android.widget.RelativeLayout;\n" +
                        "import android.widget.TextView;\n" +
                        "import android.widget.Toast;\n" +
                        "\n" +
                        "\n" +
                        "import net.sourceforge.zbar.Config;\n" +
                        "import net.sourceforge.zbar.Image;\n" +
                        "import net.sourceforge.zbar.ImageScanner;\n" +
                        "import net.sourceforge.zbar.Symbol;\n" +
                        "import net.sourceforge.zbar.SymbolSet;\n" +
                        "\n" +
                        "import java.io.IOException;\n" +
                        "import java.lang.reflect.Field;\n" +
                        "\n" +
                        "import " + app.getPackageName() + ".R;\n" +
                        "import " + app.getPackageName() + ".common.cons.Cons;\n" +
                        "import zhouzhuo810.me.zzandframe.common.utils.StrUtils;\n" +
                        "import zhouzhuo810.me.zzandframe.common.utils.ToastUtils;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.act.BaseActivity;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.widget.MarkView;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.widget.TitleBar;\n" +
                        "\n" +
                        "public class CaptureActivity extends BaseActivity {\n" +
                        "\n" +
                        "    private TitleBar titleBar;\n" +
                        "\n" +
                        "    private Camera mCamera;\n" +
                        "    private CameraPreview mPreview;\n" +
                        "    private Handler autoFocusHandler;\n" +
                        "    private CameraManager mCameraManager;\n" +
                        "\n" +
                        "    private Rect mCropRect = null;\n" +
                        "    private boolean barcodeScanned = false;\n" +
                        "    private boolean previewing = true;\n" +
                        "    private ImageScanner mImageScanner = null;\n" +
                        "\n" +
                        "    private MediaPlayer mediaPlayer;\n" +
                        "    private boolean playBeep;\n" +
                        "    private static final float BEEP_VOLUME = 0.50f;\n" +
                        "    private boolean vibrate;\n" +
                        "    private RelativeLayout mContainer = null;\n" +
                        "    private RelativeLayout mCropLayout = null;\n" +
                        "\n" +
                        "    static {\n" +
                        "        System.loadLibrary(\"iconv\");\n" +
                        "    }\n" +
                        "\n" +
                        "    private static final long VIBRATE_DURATION = 200L;\n" +
                        "\n" +
                        "    private FrameLayout scanPreview;\n" +
                        "\n" +
                        "    private boolean isFlashOpen = false;\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public int getLayoutId() {\n" +
                        "        return R.layout.activity_qr_scan;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public boolean defaultBack() {\n" +
                        "        return false;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void initView() {\n" +
                        "\n" +
                        "        titleBar = (TitleBar) findViewById(R.id.capture_title_bar);\n" +
                        "\n" +
                        "        mContainer = (RelativeLayout) findViewById(R.id.root);\n" +
                        "        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);\n" +
                        "\n" +
                        "        scanPreview = (FrameLayout) findViewById(R.id.capture_preview);\n" +
                        "\n" +
                        "        mImageScanner = new ImageScanner();\n" +
                        "        mImageScanner.setConfig(0, Config.X_DENSITY, 3);\n" +
                        "        mImageScanner.setConfig(0, Config.Y_DENSITY, 3);\n" +
                        "\n" +
                        "        autoFocusHandler = new Handler();\n" +
                        "        mCameraManager = new CameraManager(this);\n" +
                        "\n" +
                        "        try {\n" +
                        "            mCameraManager.openDriver();\n" +
                        "        } catch (IOException | RuntimeException e) {\n" +
                        "            ToastUtils.showCustomBgToast(getString(R.string.camera_permissin_text));\n" +
                        "            closeAct();\n" +
                        "            e.printStackTrace();\n" +
                        "        }\n" +
                        "\n" +
                        "        mCamera = mCameraManager.getCamera();\n" +
                        "        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);\n" +
                        "        scanPreview.addView(mPreview);\n" +
                        "\n" +
                        "        ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);\n" +
                        "        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,\n" +
                        "                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);\n" +
                        "        mAnimation.setDuration(1500);\n" +
                        "        mAnimation.setRepeatCount(-1);\n" +
                        "        mAnimation.setRepeatMode(Animation.REVERSE);\n" +
                        "        mAnimation.setInterpolator(new LinearInterpolator());\n" +
                        "        mQrLineView.setAnimation(mAnimation);\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "\n" +
                        "    private void initBeepSound() {\n" +
                        "        if (playBeep && mediaPlayer == null) {\n" +
                        "            setVolumeControlStream(AudioManager.STREAM_MUSIC);\n" +
                        "            mediaPlayer = new MediaPlayer();\n" +
                        "            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);\n" +
                        "            mediaPlayer.setOnCompletionListener(beepListener);\n" +
                        "\n" +
                        "            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);\n" +
                        "            try {\n" +
                        "                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());\n" +
                        "                file.close();\n" +
                        "                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);\n" +
                        "                mediaPlayer.prepare();\n" +
                        "            } catch (IOException e) {\n" +
                        "                mediaPlayer = null;\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {\n" +
                        "        public void onCompletion(MediaPlayer mediaPlayer) {\n" +
                        "            mediaPlayer.seekTo(0);\n" +
                        "        }\n" +
                        "    };\n" +
                        "\n" +
                        "    private void playBeepSoundAndVibrate() {\n" +
                        "        if (playBeep && mediaPlayer != null) {\n" +
                        "            mediaPlayer.start();\n" +
                        "        }\n" +
                        "        if (vibrate) {\n" +
                        "            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);\n" +
                        "            vibrator.vibrate(VIBRATE_DURATION);\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    protected void onResume() {\n" +
                        "        super.onResume();\n" +
                        "        playBeep = true;\n" +
                        "        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);\n" +
                        "        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {\n" +
                        "            playBeep = false;\n" +
                        "        }\n" +
                        "        initBeepSound();\n" +
                        "        vibrate = true;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void initData() {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void initEvent() {\n" +
                        "        titleBar.setOnTitleClickListener(new TitleBar.OnTitleClick() {\n" +
                        "            @Override\n" +
                        "            public void onLeftClick(ImageView imageView, MarkView markView, TextView textView) {\n" +
                        "                closeAct();\n" +
                        "            }\n" +
                        "\n" +
                        "            @Override\n" +
                        "            public void onTitleClick(TextView textView) {\n" +
                        "\n" +
                        "            }\n" +
                        "\n" +
                        "            @Override\n" +
                        "            public void onRightClick(ImageView imageView, MarkView markView, TextView textView) {\n" +
                        "                if (isFlashOpen) {\n" +
                        "                    closeFlashlight();\n" +
                        "                } else {\n" +
                        "                    openFlashlight();\n" +
                        "                }\n" +
                        "            }\n" +
                        "        });\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void resume() {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void pause() {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void destroy() {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void saveState(Bundle bundle) {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void restoreState(Bundle bundle) {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "\n" +
                        "    /*处理结果*/\n" +
                        "    public void handleDecode(String result) {\n" +
                        "//\t\tToast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();\n" +
                        "        playBeepSoundAndVibrate();\n" +
                        "\n" +
                        "        //FIXME\n" +
                        "        if (StrUtils.isEmpty(result)) {\n" +
                        "            Toast.makeText(CaptureActivity.this, getString(R.string.scan_fail), Toast.LENGTH_SHORT).show();\n" +
                        "            closeAct();\n" +
                        "        } else {\n" +
                        "            Intent intent = new Intent();\n" +
                        "            intent.putExtra(Cons.SCAN_RESULT, result);\n" +
                        "            setResult(RESULT_OK, intent);\n" +
                        "            closeAct();\n" +
                        "        }\n" +
                        "        // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描\n" +
                        "//\t\thandler.sendEmptyMessage(R.id.restart_preview);\n" +
                        "    }\n" +
                        "\n" +
                        "\n" +
                        "    public void onPause() {\n" +
                        "        super.onPause();\n" +
                        "        releaseCamera();\n" +
                        "    }\n" +
                        "\n" +
                        "    private void releaseCamera() {\n" +
                        "        if (mCamera != null) {\n" +
                        "            previewing = false;\n" +
                        "            mCamera.setPreviewCallback(null);\n" +
                        "            mCamera.release();\n" +
                        "            mCamera = null;\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    private Runnable doAutoFocus = new Runnable() {\n" +
                        "        public void run() {\n" +
                        "            if (previewing)\n" +
                        "                mCamera.autoFocus(autoFocusCB);\n" +
                        "        }\n" +
                        "    };\n" +
                        "\n" +
                        "    PreviewCallback previewCb = new PreviewCallback() {\n" +
                        "        public void onPreviewFrame(byte[] data, Camera camera) {\n" +
                        "            Size size = camera.getParameters().getPreviewSize();\n" +
                        "\n" +
                        "            // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据\n" +
                        "            if (data == null) {\n" +
                        "                closeAct();\n" +
                        "                return;\n" +
                        "            }\n" +
                        "            byte[] rotatedData = new byte[data.length];\n" +
                        "            for (int y = 0; y < size.height; y++) {\n" +
                        "                for (int x = 0; x < size.width; x++)\n" +
                        "                    rotatedData[x * size.height + size.height - y - 1] = data[x\n" +
                        "                            + y * size.width];\n" +
                        "            }\n" +
                        "\n" +
                        "            // 宽高也要调整\n" +
                        "            int tmp = size.width;\n" +
                        "            size.width = size.height;\n" +
                        "            size.height = tmp;\n" +
                        "\n" +
                        "            initCrop();\n" +
                        "\n" +
                        "            Image barcode = new Image(size.width, size.height, \"Y800\");\n" +
                        "            barcode.setData(rotatedData);\n" +
                        "            barcode.setCrop(mCropRect.left, mCropRect.top, mCropRect.width(),\n" +
                        "                    mCropRect.height());\n" +
                        "\n" +
                        "            int result = mImageScanner.scanImage(barcode);\n" +
                        "            String resultStr = null;\n" +
                        "\n" +
                        "            if (result != 0) {\n" +
                        "                SymbolSet syms = mImageScanner.getResults();\n" +
                        "                for (Symbol sym : syms) {\n" +
                        "                    resultStr = sym.getData();\n" +
                        "                }\n" +
                        "            }\n" +
                        "\n" +
                        "            if (!TextUtils.isEmpty(resultStr)) {\n" +
                        "                previewing = false;\n" +
                        "                mCamera.setPreviewCallback(null);\n" +
                        "                mCamera.stopPreview();\n" +
                        "\n" +
                        "//\t\t\t\tToastUtils.showCustomBgToast(resultStr);\n" +
                        "                //TODO 处理结果\n" +
                        "                handleDecode(resultStr);\n" +
                        "\n" +
                        "                barcodeScanned = true;\n" +
                        "            }\n" +
                        "        }\n" +
                        "    };\n" +
                        "\n" +
                        "    // Mimic continuous auto-focusing\n" +
                        "    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {\n" +
                        "        public void onAutoFocus(boolean success, Camera camera) {\n" +
                        "            autoFocusHandler.postDelayed(doAutoFocus, 1000);\n" +
                        "        }\n" +
                        "    };\n" +
                        "\n" +
                        "    /**\n" +
                        "     * 初始化截取的矩形区域\n" +
                        "     */\n" +
                        "    private void initCrop() {\n" +
                        "        int cameraWidth = mCameraManager.getCameraResolution().y;\n" +
                        "        int cameraHeight = mCameraManager.getCameraResolution().x;\n" +
                        "\n" +
                        "        /** 获取布局中扫描框的位置信息 */\n" +
                        "        int[] location = new int[2];\n" +
                        "        mCropLayout.getLocationInWindow(location);\n" +
                        "\n" +
                        "        int cropLeft = location[0];\n" +
                        "        int cropTop = location[1] - getStatusBarHeight();\n" +
                        "\n" +
                        "        int cropWidth = mCropLayout.getWidth();\n" +
                        "        int cropHeight = mCropLayout.getHeight();\n" +
                        "\n" +
                        "        /** 获取布局容器的宽高 */\n" +
                        "        int containerWidth = mContainer.getWidth();\n" +
                        "        int containerHeight = mContainer.getHeight();\n" +
                        "\n" +
                        "        /** 计算最终截取的矩形的左上角顶点x坐标 */\n" +
                        "        int x = cropLeft * cameraWidth / containerWidth;\n" +
                        "        /** 计算最终截取的矩形的左上角顶点y坐标 */\n" +
                        "        int y = cropTop * cameraHeight / containerHeight;\n" +
                        "\n" +
                        "        /** 计算最终截取的矩形的宽度 */\n" +
                        "        int width = cropWidth * cameraWidth / containerWidth;\n" +
                        "        /** 计算最终截取的矩形的高度 */\n" +
                        "        int height = cropHeight * cameraHeight / containerHeight;\n" +
                        "\n" +
                        "        /** 生成最终的截取的矩形 */\n" +
                        "        mCropRect = new Rect(x, y, width + x, height + y);\n" +
                        "    }\n" +
                        "\n" +
                        "    private int getStatusBarHeight() {\n" +
                        "        try {\n" +
                        "            Class<?> c = Class.forName(\"com.android.internal.R$dimen\");\n" +
                        "            Object obj = c.newInstance();\n" +
                        "            Field field = c.getField(\"status_bar_height\");\n" +
                        "            int x = Integer.parseInt(field.get(obj).toString());\n" +
                        "            return getResources().getDimensionPixelSize(x);\n" +
                        "        } catch (Exception e) {\n" +
                        "            e.printStackTrace();\n" +
                        "        }\n" +
                        "        return 0;\n" +
                        "    }\n" +
                        "\n" +
                        "    private void openFlashlight() {\n" +
                        "        if (mCamera != null) {\n" +
                        "            mCamera.startPreview();\n" +
                        "            Camera.Parameters parameters = mCamera.getParameters();\n" +
                        "            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);\n" +
                        "            mCamera.setParameters(parameters);\n" +
                        "            isFlashOpen = true;\n" +
                        "            titleBar.getIvRight().setImageResource(R.drawable.flash_close);\n" +
                        "        } else {\n" +
                        "            ToastUtils.showCustomBgToast(getString(R.string.open_flashlight_fail));\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    private void closeFlashlight() {\n" +
                        "        if (mCamera != null) {\n" +
                        "            Camera.Parameters parameters = mCamera.getParameters();\n" +
                        "            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);\n" +
                        "            mCamera.setParameters(parameters);\n" +
                        "            isFlashOpen = false;\n" +
                        "            titleBar.getIvRight().setImageResource(R.drawable.flash_open);\n" +
                        "        } else {\n" +
                        "            ToastUtils.showCustomBgToast(getString(R.string.close_flashlight_fail));\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    protected void onStop() {\n" +
                        "        super.onStop();\n" +
                        "        if (mCamera != null) {\n" +
                        "            Camera.Parameters parameters = mCamera.getParameters();\n" +
                        "            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);\n" +
                        "            mCamera.setParameters(parameters);\n" +
                        "            isFlashOpen = false;\n" +
                        "            titleBar.getIvRight().setImageResource(R.drawable.flash_open);\n" +
                        "        }\n" +
                        "\n" +
                        "    }\n" +
                        "}\n", appDirPath + File.separator + "app" + File.separator + "src" + File.separator + "main" + File.separator + "java"
                        + File.separator + "com" + File.separator + "obsessive" + File.separator + "zbar", "CaptureActivity.java");
            }
        }

        StringBuilder sbArrays = new StringBuilder();
        sbArrays.append("<resources>\n");
        StringBuilder sbStrings = new StringBuilder();
        sbStrings.append("<resources>\n" +
                "    <string name=\"app_name\">" + app.getChName() + "</string>\n" +
                "    <string name=\"ok_text\">确定</string>\n" +
                "    <string name=\"cancel_text\">取消</string>\n" +
                "    <string name=\"delete_text\">删除</string>\n" +
                "    <string name=\"revise_text\">修改</string>\n" +
                "    <string name=\"search_text\">搜索</string>\n" +
                "    <string name=\"no_net_text\">网络异常</string>\n" +
                "    <string name=\"submitting_text\">提交中...</string>\n" +
                "    <string name=\"add_text\">新增</string>\n" +
                "    <string name=\"loading_text\">加载中...</string>\n" +
                "    <string name=\"no_data_text\">暂无数据</string>\n");
        if (app.getEnableQrCode() != null) {
            if (app.getEnableQrCode()) {
                sbStrings.append("    <string name=\"scan_tips\">将二维码图片对准扫描框即可自动扫描</string>\n" +
                        "    <string name=\"camera_permissin_text\">请在设置中打开本应用摄像头权限</string>\n" +
                        "    <string name=\"scan_fail\">扫描失败，请重试</string>\n" +
                        "    <string name=\"open_flashlight_fail\">闪光灯打开失败~</string>\n" +
                        "    <string name=\"close_flashlight_fail\">闪光灯关闭失败~</string>\n");
            }
        }
        String packageName = app.getPackageName();
        String packagePath = packageName.replace(".", File.separator);
        File javaDir = new File(appDirPath
                + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "java"
                + File.separator + packagePath
        );
        if (!javaDir.exists()) {
            javaDir.mkdirs();
        }
        /*cons*/
        FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ".common.cons;\n" +
                "\n" +
                "/**\n" +
                " * 常量类.\n" +
                " * Created by zz on 2017/12/6.\n" +
                " */\n" +
                "\n" +
                "public class Cons {\n" +
                "    public static final String SCAN_RESULT = \"scan_result\";\n" +
                "}\n", javaDir + File.separator + "common" + File.separator + "cons", "Cons.java");

        generateJavaAndLayoutAndAndroidManifest(rootPath, app, appDirPath, packageName, sbStrings, sbArrays);
        sbArrays.append("\n</resources>");
        sbStrings.append("\n</resources>");
        File resDir = new File(appDirPath + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "res");
        if (!resDir.exists()) {
            resDir.mkdirs();
        }

        generateJavaAndRes(rootPath, appDirPath, app.getLogo(), app);

        /*values*/
        String valuesPath = appDirPath + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "res"
                + File.separator + "values";
        FileUtils.saveFileToPathWithName(sbStrings.toString(), valuesPath, "strings.xml");
        FileUtils.saveFileToPathWithName(sbArrays.toString(), valuesPath, "arrays.xml");
        /*raw*/
        if (app.getEnableQrCode() != null) {
            if (app.getEnableQrCode()) {
                String rawPath = appDirPath + File.separator + "app"
                        + File.separator + "src"
                        + File.separator + "main"
                        + File.separator + "res"
                        + File.separator + "raw";
                FileUtil.copyDir(new File(rootPath + File.separator + "res" + File.separator + "raw"), new File(rawPath));
            }
        }
    }

    private void generateJavaAndRes(String rootPath, String appDirPath, String logoPath, ApplicationEntity app) throws IOException {

        /*app/build.gradle*/
        FileUtils.saveFileToPathWithName("apply plugin: 'com.android.application'\n" +
                "\n" +
                "android {\n" +
                "    compileSdkVersion 27\n" +
                "    buildToolsVersion \"27.0.3\"\n" +
                "    defaultConfig {\n" +
                "        multiDexEnabled " + (app.getMultiDex() == null ? false : app.getMultiDex()) + "\n" +
                "        applicationId \"" + app.getPackageName() + "\"\n" +
                "        minSdkVersion " + app.getMinSDK() + "\n" +
                "        targetSdkVersion " + app.getTargetSDK() + "\n" +
                "        versionCode " + app.getVersionCode() + "\n" +
                "        versionName \"" + app.getVersionName() + "\"\n" +
                "        ndk { \n" +
                "              abiFilters \"armeabi\",\"armeabi-v7a\",\"x86\",\"mips\",\"arm64-v8a\",\"mips64\",\"x86_64\"\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    aaptOptions {\n" +
                "        cruncherEnabled false\n" +
                "        useNewCruncher false\n" +
                "    }\n\n" +
                "    signingConfigs {\n" +
                "        debugConfig {\n" +
                "            storeFile file(\"test.jks\")\n" +
                "            storePassword \"123456\"\n" +
                "            keyAlias \"test\"\n" +
                "            keyPassword \"123456\"\n" +
                "        }\n" +
                "        releaseConfig {\n" +
                "            storeFile file(\"test.jks\")\n" +
                "            storePassword \"123456\"\n" +
                "            keyAlias \"test\"\n" +
                "            keyPassword \"123456\"\n" +
                "        }\n" +
                "    }\n" +
                "    applicationVariants.all {variant ->\n" +
                "        variant.outputs.each {output ->\n" +
                "            def outputFile = output.outputFile\n" +
                "            def fileName\n" +
                "            if (outputFile != null && outputFile.name.endsWith('.apk')) {\n" +
                "                if (variant.buildType.name.equals('release')) {\n" +
                "                    fileName = \"" + app.getAppName() + "_${defaultConfig.versionName}.apk\"\n" +
                "                } else if (variant.buildType.name.equals('debug')) {\n" +
                "                    fileName = \"" + app.getAppName() + "_${defaultConfig.versionName}_debug.apk\"\n" +
                "                }\n" +
                "                output.outputFile = new File(outputFile.parent, fileName)\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    sourceSets.main {\n" +
                "        jniLibs.srcDirs = ['libs']  // <-- Set your folder here!\n" +
                "    }\n"
                +
                "    buildTypes {\n" +
                "        debug {\n" +
                "            signingConfig signingConfigs.debugConfig\n" +
                "        }\n" +
                "        release {\n" +
                "            minifyEnabled " + (app.getMinifyEnabled() ? false : app.getMinifyEnabled()) + "\n" +
                "            signingConfig signingConfigs.releaseConfig\n" +
                "            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'\n" +
                "        }\n" +
                "    }" +
                "}\n" +
                "\n" +
                "dependencies {\n" +
                "    compile fileTree(dir: 'libs', include: ['*.jar'])\n" +
                "    //bugly\n" +
                "    compile 'com.tencent.bugly:crashreport:latest.release'\n" +
                "    //zzandframe\n" +
                "    compile 'com.github.zhouzhuo810:ZzAndFrame:1.2.6'\n" +
                "    //xutils\n" +
                "    compile 'org.xutils:xutils:3.3.38'\n" +
                "    //RxPermissions\n" +
                "    compile 'com.tbruyelle.rxpermissions:rxpermissions:0.9.4@aar'\n" +
                "    //Logger\n" +
                "    compile 'com.orhanobut:logger:2.1.1'\n" +
                "    //okgo\n" +
                "    compile 'com.lzy.net:okgo:3.0.4'\n" +
                "    //AndroidPickerView\n" +
                "    compile 'com.contrarywind:Android-PickerView:3.2.5'\n" +
                "    //Ucrop\n" +
                "    compile 'com.github.yalantis:ucrop:2.2.1'\n" +
                "    //SwipeRecyclerView\n" +
                "    compile 'com.yanzhenjie:recyclerview-swipe:1.1.3'\n" +
                "    //BaseRecyclerViewAdapterHelper\n" +
                "    //compile 'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.30'\n" +
                "    //multidex\n" +
                (app.getMultiDex() == null ? "\n" : (app.getMultiDex() ?
                        "    compile 'com.android.support:multidex:1.0.1'\n" : "\n"))
                +
                "}\n", appDirPath + File.separator + "app", "build.gradle");

        /*拷贝桌面LOGO*/
        if (logoPath != null && logoPath.length() > 0) {
            String mipmapPath = appDirPath
                    + File.separator + "app"
                    + File.separator + "src"
                    + File.separator + "main"
                    + File.separator + "res"
                    + File.separator + "mipmap-hdpi";
            File mipmapDir = new File(mipmapPath);
            if (!mipmapDir.exists()) {
                mipmapDir.mkdirs();
            }
            try {
                FileUtil.copyFile(new File(logoPath), new File(mipmapPath + File.separator + new File(logoPath).getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*style*/
        String style = "<resources>\n" +
                "\n" +
                "    <!-- Base application theme. -->\n" +
                "    <style name=\"AppTheme\" parent=\"Theme.AppCompat.Light.NoActionBar\">\n" +
                "        <!-- Customize your theme here. -->\n" +
                "        <item name=\"colorPrimary\">@color/colorPrimary</item>\n" +
                "        <item name=\"colorPrimaryDark\">@color/colorPrimaryDark</item>\n" +
                "        <item name=\"colorAccent\">@color/colorAccent</item>\n" +
                "    </style>\n" +
                "\n" +
                "    <style name=\"transparentWindow\" parent=\"@android:style/Theme.Dialog\">\n" +
                "        <item name=\"android:windowBackground\">@drawable/dialog_custom_bg</item>\n" +
                "    </style>\n" +
                "\n" +
                "</resources>\n";
        String valuesPath = appDirPath
                + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "res"
                + File.separator + "values";

        FileUtils.saveFileToPathWithName(style, valuesPath, "styles.xml");
        /*colors*/
        String color = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n" +
                "    <color name=\"colorMain\">" + app.getColorMain() + "</color>\n" +
                "    <color name=\"colorPrimary\">" + app.getColorMain() + "</color>\n" +
                "    <color name=\"colorPrimaryDark\">" + app.getColorMain() + "</color>\n" +
                "    <color name=\"colorAccent\">" + app.getColorMain() + "</color>\n" +
                "    <color name=\"colorPress\">#1d61ce</color>\n" +
                "    <color name=\"colorGrayBg\">#ecf4f8</color>\n" +
                "    <color name=\"colorWhite\">#fff</color>\n" +
                "    <color name=\"colorBlack\">#000</color>\n" +
                "    <color name=\"colorTransparent\">#00000000</color>\n" +
                "    <color name=\"colorGrayD\">#ddd</color>\n" +
                "    <color name=\"colorGrayB\">#bbb</color>\n" +
                "    <color name=\"colorLine\">#ccc</color>\n" +
                "    <color name=\"colorText\">#222</color>\n" +
                "    <color name=\"colorTabNormal\">#aac3d2</color>\n" +
                "    <color name=\"colorTabPress\">" + app.getColorMain() + "</color>\n" +
                "    <color name=\"colorExit\">#d44a4a</color>\n" +
                "    <color name=\"colorExitPress\">#dc5353</color>\n" +
                "</resources>\n";
        FileUtils.saveFileToPathWithName(color, valuesPath, "colors.xml");
        /*anim*/
        String animPath = appDirPath
                + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "res"
                + File.separator + "anim";
        FileUtil.copyDir(new File(rootPath + File.separator + "res" + File.separator + "anim"), new File(animPath));
        /*drawable*/
        String drawablePath = appDirPath
                + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "res"
                + File.separator + "drawable";
        String drawableHPath = appDirPath
                + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "res"
                + File.separator + "drawable-hdpi";
        FileUtil.copyDir(new File(rootPath + File.separator + "res" + File.separator + "drawable" + File.separator + "common"), new File(drawablePath));
        if (app.getEnableQrCode() != null) {
            if (app.getEnableQrCode()) {
                FileUtil.copyDir(new File(rootPath + File.separator + "res" + File.separator + "drawable" + File.separator + "zbar"), new File(drawableHPath));
            }
        }
        /*xml*/
        String xmlPath = appDirPath
                + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "res"
                + File.separator + "xml";
        FileUtil.copyDir(new File(rootPath + File.separator + "res" + File.separator + "xml"), new File(xmlPath));
        /*values*/
        FileUtil.copyDir(new File(rootPath + File.separator + "res" + File.separator + "values"), new File(valuesPath));
        String layoutPath = appDirPath
                + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "res"
                + File.separator + "layout";
        /*layouts*/
        FileUtil.copyDir(new File(rootPath + File.separator + "res" + File.separator + "layout" + File.separator + "common"), new File(layoutPath));
        if (app.getEnableQrCode() != null) {
            if (app.getEnableQrCode()) {
                FileUtil.copyFile(new File(rootPath + File.separator + "res" + File.separator + "layout" + File.separator + "activity_qr_scan.xml"),
                        new File(layoutPath + File.separator + "activity_qr_scan.xml"));
            }
        }
    }

    private void generateJavaAndLayoutAndAndroidManifest(String rootPath, ApplicationEntity app, String appDirPath, String packageName, StringBuilder sbStrings, StringBuilder sbArrays) throws IOException {
        String filePath = appDirPath
                + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main";
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String logoName = "ic_launcher";
        if (app.getLogo() != null && app.getLogo().length() > 0) {
            String name = new File(app.getLogo()).getName();
            logoName = name.substring(0, name.lastIndexOf("."));
        }

        StringBuilder sbManifest = new StringBuilder();
        sbManifest.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    package=\"" + packageName + "\">\n" +
                "\n" +
                "    <uses-permission android:name=\"android.permission.INTERNET\" />\n" +
                "    <uses-permission android:name=\"android.permission.WRITE_EXTERNAL_STORAGE\" />\n" +
                "    <uses-permission android:name=\"android.permission.MOUNT_UNMOUNT_FILESYSTEMS\" />\n" +
                "    <uses-permission android:name=\"android.permission.READ_PHONE_STATE\" />\n" +
                "    <uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\" />\n" +
                "    <uses-permission android:name=\"android.permission.CALL_PHONE\" />\n" +
                "    <uses-permission android:name=\"android.permission.CAMERA\" />\n" +
                "    <uses-permission android:name=\"android.permission.ACCESS_COARSE_LOCATION\" />\n" +
                "    <uses-permission android:name=\"android.permission.ACCESS_FINE_LOCATION\" />\n" +
                "    <uses-permission android:name=\"android.permission.ACCESS_LOCATION_EXTRA_COMMANDS\" />\n" +
                "    <!-- bugly start-->\n" +
                "    <uses-permission android:name=\"android.permission.READ_LOGS\" />\n" +
                "    <!--bugly end-->\n");
        if (app.getEnableQrCode() != null) {
            if (app.getEnableQrCode()) {
                sbManifest.append("    <!--二维码 -->\n" +
                        "    <uses-permission android:name=\"android.permission.VIBRATE\" />\n" +
                        "    <uses-permission android:name=\"android.permission.FLASHLIGHT\" />\n" +
                        "\n" +
                        "    <uses-feature android:name=\"android.hardware.camera\" />\n" +
                        "    <uses-feature android:name=\"android.hardware.camera.autofocus\" />\n\n");
            }
        }
        sbManifest.append(
                "    <application\n" +
                        "        android:name=\".MyApplication\"\n" +
                        "        android:allowBackup=\"true\"\n" +
                        "        android:icon=\"@mipmap/" + logoName + "\"\n" +
                        "        android:label=\"@string/app_name\"\n" +
                        "        android:roundIcon=\"@mipmap/" + logoName + "\"\n" +
                        "        android:supportsRtl=\"true\"\n" +
                        "        android:theme=\"@style/AppTheme\">\n" +
                        "\n" +
                        "        <meta-data\n" +
                        "            android:name=\"design_width\"\n" +
                        "            android:value=\"1080\" />\n" +
                        "        <meta-data\n" +
                        "            android:name=\"design_height\"\n" +
                        "            android:value=\"1920\" />\n");
        sbManifest.append("        <activity android:name=\"zhouzhuo810.me.zzandframe.ui.act.ImagePreviewActivity\"/>\n" +
                "        <activity android:name=\"zhouzhuo810.me.zzandframe.ui.act.MultiImagePreviewActivity\"/>\n"
        );
        if (app.getEnableQrCode() != null) {
            if (app.getEnableQrCode()) {
                sbManifest.append("        <activity\n" +
                        "            android:name=\"com.obsessive.zbar.CaptureActivity\"\n" +
                        "            android:configChanges=\"orientation|keyboardHidden|layoutDirection|screenSize|screenLayout\"\n" +
                        "            android:screenOrientation=\"portrait\"\n" +
                        "            android:windowSoftInputMode=\"stateAlwaysHidden\" />\n");
            }
        }
        /*查找splashactivity*/
        List<ActivityEntity> activityEntities = mActivityService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("applicationId", app.getId()),
                Restrictions.eq("type", ActivityEntity.TYPE_SPLASH)
        });
        String layoutPath = filePath
                + File.separator + "res"
                + File.separator + "layout";

        String drawablePath = filePath
                + File.separator + "res"
                + File.separator + "drawable-hdpi";

        String packagePath = packageName.replace(".", File.separator);
        String javaPath = appDirPath
                + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "java"
                + File.separator + packagePath;
        /*MyApplication*/
        FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ";\n" +
                "\n" +
                "import android.content.Context;\n" +
                "\n" +
                "import zhouzhuo810.me.zzandframe.ui.app.BaseApplication;\n" +
                "import zhouzhuo810.me.zzandframe.common.utils.ToastUtils;\n" +
                ((app.getMultiDex() == null ? false : app.getMultiDex()) ? "import android.support.multidex.MultiDex;\n" : "") +
                "\n" +
                "/**\n" +
                " * Created by admin on 2017/8/27.\n" +
                " */\n" +
                "public class MyApplication extends BaseApplication {\n" +
                "\n" +
                "    private static MyApplication INSTANCE;\n" +
                "\n" +
                "    @Override\n" +
                "    public void onCreate() {\n" +
                "        super.onCreate();\n" +
                "        INSTANCE = this;\n" +
                "        ToastUtils.init(this, R.color.colorMain, R.color.colorWhite);\n" +
                "    }\n" +
                "\n" +
                "    public static MyApplication getContext() {\n" +
                "        return INSTANCE;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    protected void attachBaseContext(Context base) {\n" +
                "        super.attachBaseContext(base);\n" +
                ((app.getMultiDex() == null ? false : app.getMultiDex()) ? "        MultiDex.install(this);\n" : "") +
                "        \n" +
                "    }\n" +
                "}\n", javaPath, "MyApplication.java");

        /*custom widgets*/
        generateWidgets(rootPath, javaPath, app);

        if (activityEntities != null && activityEntities.size() > 0) {
            ActivityEntity activityEntity = activityEntities.get(0);
            String splashImgName = null;
            if (activityEntity.getSplashImg() != null && activityEntity.getSplashImg().length() > 0) {
                File img = new File(activityEntity.getSplashImg());
                if (img.exists()) {
                    String name = img.getName();
                    String suffix = name.substring(name.lastIndexOf("."));
                    splashImgName = "splash_img";
                    FileUtil.copyFile(img, new File(drawablePath + File.separator + splashImgName + suffix));
                }
            }
            sbManifest.append("        <activity\n" +
                    "            android:name=\".ui.act." + activityEntity.getName() + "\"\n" +
                    "            android:configChanges=\"orientation|keyboardHidden|layoutDirection|screenSize|screenLayout\"\n" +
                    "            android:screenOrientation=\"portrait\"\n" +
                    "            android:windowSoftInputMode=\"stateAlwaysHidden\">\n" +
                    "            <intent-filter>\n" +
                    "                <action android:name=\"android.intent.action.MAIN\" />\n" +
                    "\n" +
                    "                <category android:name=\"android.intent.category.LAUNCHER\" />\n" +
                    "            </intent-filter>\n" +
                    "        </activity>\n");
            /*layout*/
            FileUtils.saveFileToPathWithName("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<FrameLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                    "    android:layout_width=\"match_parent\"\n" +
                    "    android:layout_height=\"match_parent\"\n" +
                    "    android:orientation=\"vertical\">\n" +
                    "\n" +
                    "    <ImageView\n" +
                    "        android:id=\"@+id/iv_pic\"\n" +
                    "        android:layout_width=\"match_parent\"\n" +
                    "        android:layout_height=\"match_parent\"\n" +
                    "        android:src=\"" + (splashImgName == null ? "@mipmap/ic_launcher" : "@drawable/" + splashImgName) + "\" />\n" +
                    "\n" +
                    "    <TextView\n" +
                    "        android:id=\"@+id/tv_jump\"\n" +
                    "        android:layout_width=\"wrap_content\"\n" +
                    "        android:layout_height=\"wrap_content\"\n" +
                    "        android:layout_gravity=\"right|top\"\n" +
                    "        android:layout_marginRight=\"50px\"\n" +
                    "        android:layout_marginTop=\"50px\"\n" +
                    "        android:background=\"@drawable/btn_main_selector\"\n" +
                    "        android:paddingBottom=\"8px\"\n" +
                    "        android:paddingLeft=\"18px\"\n" +
                    "        android:paddingRight=\"18px\"\n" +
                    "        android:paddingTop=\"8px\"\n" +
                    "        android:textColor=\"@color/colorWhite\"\n" +
                    "        android:textSize=\"36px\"\n" +
                    "        android:visibility=\"gone\" />\n" +
                    "</FrameLayout>", layoutPath, "activity_splash.xml");
            /*java*/
            FileUtils.saveFileToPathWithName("package " + packageName + ".ui.act;\n" +
                    "\n" +
                    "import android.content.Intent;\n" +
                    "import android.os.Bundle;\n" +
                    "import android.support.annotation.Nullable;\n" +
                    "import android.view.View;\n" +
                    "import android.widget.ImageView;\n" +
                    "import android.widget.TextView;\n" +
                    "import android.view.WindowManager;\n" +
                    "\n" +
                    "import java.util.concurrent.TimeUnit;\n" +
                    "\n" +
                    "import " + packageName + ".R;\n" +
                    "import rx.Observable;\n" +
                    "import rx.Subscriber;\n" +
                    "import rx.Subscription;\n" +
                    "import rx.android.schedulers.AndroidSchedulers;\n" +
                    "import rx.schedulers.Schedulers;\n" +
                    "import zhouzhuo810.me.zzandframe.ui.act.BaseActivity;\n" +
                    "\n" +
                    "/**\n" +
                    " * 启动activity\n" +
                    " * Created by zhouzhuo810 on 2017/8/25.\n" +
                    " */\n" +
                    "public class SplashActivity extends BaseActivity {\n" +
                    "\n" +
                    "    private ImageView ivPic;\n" +
                    "    private TextView tvJump;\n" +
                    "    private Subscription subscribe;\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public int getLayoutId() {\n" +
                    "        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏\n" +
                    "        return R.layout.activity_splash;\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public boolean defaultBack() {\n" +
                    "        return false;\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void initView() {\n" +
                    "        ivPic = (ImageView) findViewById(R.id.iv_pic);\n" +
                    "        tvJump = (TextView) findViewById(R.id.tv_jump);\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void initData() {\n" +
                    "        tvJump.setVisibility(View.VISIBLE);\n" +
                    "        final int duration = " + activityEntity.getSplashSecond() + ";\n" +
                    "        tvJump.setText(duration + \"s 跳过\");\n" +
                    "        subscribe = Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())\n" +
                    "                .take(duration)\n" +
                    "                .observeOn(AndroidSchedulers.mainThread())\n" +
                    "                .subscribe(new Subscriber<Long>() {\n" +
                    "                    @Override\n" +
                    "                    public void onCompleted() {\n" +
                    "                    }\n" +
                    "\n" +
                    "                    @Override\n" +
                    "                    public void onError(Throwable e) {\n" +
                    "                    }\n" +
                    "\n" +
                    "                    @Override\n" +
                    "                    public void onNext(Long aLong) {\n" +
                    "                        if ((duration - 1 - aLong) == 0) {\n" +
                    "                            Intent intent = new Intent(SplashActivity.this, " + activityEntity.getTargetActName() + ".class);\n" +
                    "                            startActWithIntent(intent);\n" +
                    "                            closeAct();\n" +
                    "                        } else {\n" +
                    "                            tvJump.setText((duration - 1 - aLong) + \"s 跳过\");\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                });\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void initEvent() {\n" +
                    "        tvJump.setOnClickListener(new View.OnClickListener() {\n" +
                    "            @Override\n" +
                    "            public void onClick(View v) {\n" +
                    "                subscribe.unsubscribe();\n" +
                    "                Intent intent = new Intent(SplashActivity.this, " + activityEntity.getTargetActName() + ".class);\n" +
                    "                startActWithIntent(intent);\n" +
                    "                closeAct();\n" +
                    "            }\n" +
                    "        });\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void resume() {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void pause() {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void destroy() {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void saveState(Bundle bundle) {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void restoreState(@Nullable Bundle bundle) {\n" +
                    "\n" +
                    "    }\n" +
                    "}\n", javaPath + File.separator + "ui" + File.separator + "act", activityEntity.getName() + ".java");
        }

        /*查找其他act*/
        List<ActivityEntity> activityEntities1 = mActivityService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("applicationId", app.getId()),
                Restrictions.ne("type", ActivityEntity.TYPE_SPLASH)
        });
        if (activityEntities1 != null) {
            //empty entity for rv
            String entityPath = javaPath + File.separator + "common" + File.separator + "api" + File.separator + "entity";
            String adapterPath = javaPath + File.separator + "ui" + File.separator + "adapter";
            FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ".common.api.entity;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by admin on 2018/1/2.\n" +
                    " */\n" +
                    "\n" +
                    "public class RvTestEntity {\n" +
                    "\n" +
                    "}\n", entityPath, "RvTestEntity.java");
            for (ActivityEntity activityEntity : activityEntities1) {
                if (activityEntity.getFirst()) {
                    sbManifest.append("        <activity\n" +
                            "            android:name=\".ui.act." + activityEntity.getName() + "\"\n" +
                            "            android:configChanges=\"orientation|keyboardHidden|layoutDirection|screenSize|screenLayout\"\n" +
                            "            android:screenOrientation=\"" + ((activityEntity.getLandscape() == null) ? "portrait" : (activityEntity.getLandscape() ? "portrait" : "portrait")) + "\"\n" +
                            "            android:windowSoftInputMode=\"stateAlwaysHidden\">\n" +
                            "            <intent-filter>\n" +
                            "                <action android:name=\"android.intent.action.MAIN\" />\n" +
                            "\n" +
                            "                <category android:name=\"android.intent.category.LAUNCHER\" />\n" +
                            "            </intent-filter>\n" +
                            "        </activity>");
                } else {
                    sbManifest.append("        <activity\n" +
                            "            android:name=\".ui.act." + activityEntity.getName() + "\"\n" +
                            "            android:configChanges=\"orientation|keyboardHidden|layoutDirection|screenSize|screenLayout\"\n" +
                            "            android:screenOrientation=\"" + ((activityEntity.getLandscape() == null) ? "portrait" : (activityEntity.getLandscape() ? "portrait" : "portrait")) + "\"\n" +
                            "            android:windowSoftInputMode=\"stateAlwaysHidden\" />\n");
                }
                switch (activityEntity.getType()) {
                    case ActivityEntity.TYPE_EMPTY_ACT:
                        generateEmptyActJavaAndLayout(entityPath, adapterPath, logoName, layoutPath, javaPath, activityEntity, app, sbStrings);
                        break;
                    case ActivityEntity.TYPE_GUIDE:
                        generateGuideActJavaAndLayout(drawablePath, logoName, layoutPath, javaPath, activityEntity, app, sbStrings);
                        break;
                    case ActivityEntity.TYPE_BOTTOM_FRAGMENT:
                        generateBottomFgmActJavaAndLayout(entityPath, adapterPath, logoName, layoutPath, javaPath, activityEntity, app, sbStrings, sbArrays);
                        break;
                    case ActivityEntity.TYPE_TOP_FRAGMENT:
                        generateTopFgmActJavaAndLayout(entityPath, adapterPath, logoName, layoutPath, javaPath, activityEntity, app, sbStrings, sbArrays);
                        break;
                }
            }
        }
        sbManifest.append("\n        <provider\n" +
                "            android:name=\"android.support.v4.content.FileProvider\"\n" +
                "            android:authorities=\"" + packageName + ".provider\"\n" +
                "            android:exported=\"false\"\n" +
                "            android:grantUriPermissions=\"true\">\n" +
                "            <meta-data\n" +
                "                android:name=\"android.support.FILE_PROVIDER_PATHS\"\n" +
                "                android:resource=\"@xml/provider_paths\" />\n" +
                "        </provider>\n" +
                "    </application>\n" +
                "\n" +
                "</manifest>");

        FileUtils.saveFileToPathWithName(sbManifest.toString(), filePath, "AndroidManifest.xml");
    }

    private void generateGuideActJavaAndLayout(String drawablePath, String logoName, String layoutPath, String javaPath, ActivityEntity activityEntity, ApplicationEntity app, StringBuilder sbStrings) throws IOException {
        String layoutName = "";
        boolean isNotFirst = false;
        for (int i = 0; i < activityEntity.getName().length(); i++) {
            char c = activityEntity.getName().charAt(i);
            if (c >= 'A' && c <= 'Z') {
                if (isNotFirst) {
                    layoutName += "_";
                }
                isNotFirst = true;
            }
            layoutName += c;
        }

        layoutName = layoutName.replace("activity_", "").replace("_activity", "").replace("activity", "").replace("__", "_").toLowerCase();
        final String realLayoutName = "activity_" + layoutName.toLowerCase();

        String imgOne = null;
        if (activityEntity.getGuideImgOne() != null && activityEntity.getGuideImgOne().length() > 0) {
            File img = new File(activityEntity.getGuideImgOne());
            if (img.exists()) {
                String name = img.getName();
                String suffix = name.substring(name.lastIndexOf("."));
                imgOne = "guide_img_one";
                FileUtil.copyFile(img, new File(drawablePath + File.separator + imgOne + suffix));
            }
        }
        String imgTwo = null;
        if (activityEntity.getGuideImgTwo() != null && activityEntity.getGuideImgTwo().length() > 0) {
            File img = new File(activityEntity.getGuideImgTwo());
            if (img.exists()) {
                String name = img.getName();
                String suffix = name.substring(name.lastIndexOf("."));
                imgTwo = "guide_img_two";
                FileUtil.copyFile(img, new File(drawablePath + File.separator + imgTwo + suffix));
            }
        }
        String imgThree = null;
        if (activityEntity.getGuideImgThree() != null && activityEntity.getGuideImgThree().length() > 0) {
            File img = new File(activityEntity.getGuideImgThree());
            if (img.exists()) {
                String name = img.getName();
                String suffix = name.substring(name.lastIndexOf("."));
                imgThree = "guide_img_three";
                FileUtil.copyFile(img, new File(drawablePath + File.separator + imgThree + suffix));
            }
        }
        String imgFour = null;
        if (activityEntity.getGuideImgFour() != null && activityEntity.getGuideImgFour().length() > 0) {
            File img = new File(activityEntity.getGuideImgFour());
            if (img.exists()) {
                String name = img.getName();
                String suffix = name.substring(name.lastIndexOf("."));
                imgFour = "guide_img_four";
                FileUtil.copyFile(img, new File(drawablePath + File.separator + imgFour + suffix));
            }
        }
        String imgFive = null;
        if (activityEntity.getGuideImgFive() != null && activityEntity.getGuideImgFive().length() > 0) {
            File img = new File(activityEntity.getGuideImgFive());
            if (img.exists()) {
                String name = img.getName();
                String suffix = name.substring(name.lastIndexOf("."));
                imgFive = "guide_img_five";
                FileUtil.copyFile(img, new File(drawablePath + File.separator + imgFive + suffix));
            }
        }

        sbStrings.append("    <string name=\"enter_text\">立即体验</string>\n");
        StringBuilder sbLayout = new StringBuilder();
        sbLayout.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<FrameLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\"\n" +
                "    android:orientation=\"vertical\">\n" +
                "\n" +
                "    <android.support.v4.view.ViewPager\n" +
                "        android:id=\"@+id/view_pager\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"match_parent\" />\n" +
                "\n" +
                "    <zhouzhuo810.me.zzandframe.ui.widget.zzpagerindicator.ZzPagerIndicator\n" +
                "        android:id=\"@+id/indicator\"\n" +
                "        android:layout_width=\"wrap_content\"\n" +
                "        android:layout_height=\"wrap_content\"\n" +
                "        android:layout_gravity=\"bottom|center_horizontal\"\n" +
                "        android:layout_marginBottom=\"100px\"\n" +
                "        app:zz_indicator_type=\"round_point\"\n" +
                "        app:zz_is_need_scale_in_px=\"true\"\n" +
                "        app:zz_point_spacing=\"15px\"\n" +
                "        app:zz_select_point_color=\"@color/colorMain\"\n" +
                "        app:zz_select_point_size=\"15px\"\n" +
                "        app:zz_unselect_point_color=\"@color/colorGrayD\"\n" +
                "        app:zz_unselect_point_size=\"15px\" />\n" +
                "\n" +
                "    <Button\n" +
                "        android:id=\"@+id/btn_ok\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"120px\"\n" +
                "        android:layout_gravity=\"bottom|center_horizontal\"\n" +
                "        android:layout_marginBottom=\"160px\"\n" +
                "        android:layout_marginLeft=\"130px\"\n" +
                "        android:layout_marginRight=\"130px\"\n" +
                "        android:layout_marginTop=\"40px\"\n" +
                "        android:background=\"@drawable/btn_save_selector\"\n" +
                "        android:gravity=\"center\"\n" +
                "        android:text=\"@string/enter_text\"\n" +
                "        android:textColor=\"@color/colorWhite\"\n" +
                "        android:textSize=\"@dimen/title_text_size\"\n" +
                "        android:visibility=\"gone\" />\n" +
                "</FrameLayout>\n");
        StringBuilder sbJava = new StringBuilder();

        sbJava.append("package " + app.getPackageName() + ".ui.act;\n" +
                "\n" +
                "import android.annotation.SuppressLint;\n" +
                "import android.content.Intent;\n" +
                "import android.os.Bundle;\n" +
                "import android.support.annotation.Nullable;\n" +
                "import android.support.v4.view.ViewPager;\n" +
                "import android.view.View;\n" +
                "import android.view.WindowManager;\n" +
                "import android.widget.Button;\n" +
                "import android.widget.ImageView;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "\n" +
                "import " + app.getPackageName() + ".R;\n" +
                "import zhouzhuo810.me.zzandframe.ui.act.BaseActivity;\n" +
                "import zhouzhuo810.me.zzandframe.ui.widget.zzpagerindicator.ZzPagerIndicator;\n" +
                "import zhouzhuo810.me.zzandframe.ui.widget.zzpagerindicator.adapter.ZzBasePagerAdapter;\n" +
                "\n" +
                "public class " + activityEntity.getName() + " extends BaseActivity {\n" +
                "\n" +
                "    private ViewPager viewPager;\n" +
                "    private ZzPagerIndicator indicator;\n" +
                "    private Button btnOk;\n" +
                "    private List<ImageView> imgs;\n" +
                "\n" +
                "    @Override\n" +
                "    public int getLayoutId() {\n");
        if (activityEntity.getFullScreen() != null && activityEntity.getFullScreen()) {
            sbJava.append("        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏\n");
        }
        sbJava.append("        return R.layout." + realLayoutName + ";\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void initView() {\n" +
                "        viewPager = (ViewPager) findViewById(R.id.view_pager);\n" +
                "        indicator = (ZzPagerIndicator) findViewById(R.id.indicator);\n" +
                "        btnOk = (Button) findViewById(R.id.btn_ok);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void initData() {\n" +
                "\n" +
                "        imgs = new ArrayList<>();\n" +
                "        List<Integer> ids = new ArrayList<>();\n"
        );
        if (activityEntity.getGuideImgCount() != null) {
            for (Integer i = 0; i < activityEntity.getGuideImgCount(); i++) {
                switch (i) {
                    case 0:
                        sbJava.append("            ids.add(R.drawable.").append(imgOne).append(");\n");
                        break;
                    case 1:
                        sbJava.append("            ids.add(R.drawable.").append(imgTwo).append(");\n");
                        break;
                    case 2:
                        sbJava.append("            ids.add(R.drawable.").append(imgThree).append(");\n");
                        break;
                    case 3:
                        sbJava.append("            ids.add(R.drawable.").append(imgFour).append(");\n");
                        break;
                    case 4:
                        sbJava.append("            ids.add(R.drawable.").append(imgFive).append(");\n");
                        break;
                }
            }
        }
        sbJava.append("        for (int i = 0; i < " + (activityEntity.getGuideImgCount() == null ? 0 : activityEntity.getGuideImgCount()) + "; i++) {\n" +
                "            ImageView iv = new ImageView(this);\n" +
                "            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);\n" +
                "            imgs.add(iv);\n" +
                "        }\n" +
                "        viewPager.setOffscreenPageLimit("+ (activityEntity.getGuideImgCount() == null ? 0 : activityEntity.getGuideImgCount())+");\n"+
                "        viewPager.setPageTransformer(false, new ZoomOutPageTransformer());\n" +
                "        viewPager.setAdapter(new ZzBasePagerAdapter<ImageView, Integer>(this, imgs, ids) {\n" +
                "            @Override\n" +
                "            public void bindData(ImageView imageView, Integer integer) {\n" +
                "                imageView.setImageResource(integer);\n" +
                "            }\n" +
                "\n" +
                "            @Override\n" +
                "            public String getTabText(Integer integer, int i) {\n" +
                "                return i + \"\";\n" +
                "            }\n" +
                "\n" +
                "            @Override\n" +
                "            public int getSelectedIcon(int i) {\n" +
                "                return 0;\n" +
                "            }\n" +
                "\n" +
                "            @Override\n" +
                "            public int getUnselectedIcon(int i) {\n" +
                "                return 0;\n" +
                "            }\n" +
                "        });\n" +
                "        indicator.setViewPager(viewPager);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void initEvent() {\n" +
                "        btnOk.setOnClickListener(new View.OnClickListener() {\n" +
                "            @Override\n" +
                "            public void onClick(View v) {\n");
        if (activityEntity.getTargetActId() != null) {
            /*点击确定按钮跳转*/
            sbJava.append("                Intent intent = new Intent(GuideActivity.this, " + activityEntity.getTargetActName() + ".class);\n" +
                    "                startActWithIntent(intent);\n" +
                    "                closeAct();\n");
        }
        sbJava.append("            }\n" +
                "        });\n" +
                "\n" +
                "        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {\n" +
                "            @Override\n" +
                "            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {\n" +
                "\n" +
                "            }\n" +
                "\n" +
                "            @Override\n" +
                "            public void onPageSelected(int position) {\n" +
                "                if (position == imgs.size() - 1) {\n" +
                "                    btnOk.setVisibility(View.VISIBLE);\n" +
                "                } else {\n" +
                "                    btnOk.setVisibility(View.GONE);\n" +
                "                }\n" +
                "            }\n" +
                "\n" +
                "            @Override\n" +
                "            public void onPageScrollStateChanged(int state) {\n" +
                "\n" +
                "            }\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {\n" +
                "        private static final float MIN_SCALE = 0.85f;\n" +
                "        private static final float MIN_ALPHA = 0.5f;\n" +
                "\n" +
                "        @SuppressLint(\"NewApi\")\n" +
                "        public void transformPage(View view, float position) {\n" +
                "            int pageWidth = view.getWidth();\n" +
                "            int pageHeight = view.getHeight();\n" +
                "\n" +
                "            if (position < -1) { // [-Infinity,-1)\n" +
                "                // This page is way off-screen to the left.\n" +
                "                view.setAlpha(0);\n" +
                "\n" +
                "            } else if (position <= 1) //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0\n" +
                "            { // [-1,1]\n" +
                "                // Modify the default slide transition to shrink the page as well\n" +
                "                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));\n" +
                "                float vertMargin = pageHeight * (1 - scaleFactor) / 2;\n" +
                "                float horzMargin = pageWidth * (1 - scaleFactor) / 2;\n" +
                "                if (position < 0) {\n" +
                "                    view.setTranslationX(horzMargin - vertMargin / 2);\n" +
                "                } else {\n" +
                "                    view.setTranslationX(-horzMargin + vertMargin / 2);\n" +
                "                }\n" +
                "\n" +
                "                // Scale the page down (between MIN_SCALE and 1)\n" +
                "                view.setScaleX(scaleFactor);\n" +
                "                view.setScaleY(scaleFactor);\n" +
                "\n" +
                "                // Fade the page relative to its size.\n" +
                "                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)\n" +
                "                        / (1 - MIN_SCALE) * (1 - MIN_ALPHA));\n" +
                "\n" +
                "            } else { // (1,+Infinity]\n" +
                "                // This page is way off-screen to the right.\n" +
                "                view.setAlpha(0);\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean defaultBack() {\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void resume() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void pause() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void destroy() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void saveState(Bundle bundle) {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void restoreState(@Nullable Bundle bundle) {\n" +
                "\n" +
                "    }\n" +
                "}");
        // TODO: 2018/1/2 添加布局和java


        FileUtils.saveFileToPathWithName(sbLayout.toString(), layoutPath, realLayoutName + ".xml");
        FileUtils.saveFileToPathWithName(sbJava.toString(), javaPath + File.separator + "ui" + File.separator + "act", activityEntity.getName() + ".java");
    }

    private void generateTopFgmActJavaAndLayout(String entityPath, String adapterPath, String logoName, String layoutPath, String javaPath, ActivityEntity activityEntity, ApplicationEntity app, StringBuilder sbStrings, StringBuilder sbArrays) throws IOException {
        String layoutName = "";
        boolean isNotFirst = false;
        for (int i = 0; i < activityEntity.getName().length(); i++) {
            char c = activityEntity.getName().charAt(i);
            if (c >= 'A' && c <= 'Z') {
                if (isNotFirst) {
                    layoutName += "_";
                }
                isNotFirst = true;
            }
            layoutName += c;
        }
        layoutName = layoutName.replace("activity_", "").replace("_activity", "").replace("__", "_").toLowerCase();
        final String realLayoutName = "activity_" + layoutName.toLowerCase();

        /*layout*/
        StringBuilder sbLayout = new StringBuilder();
        if (!sbStrings.toString().contains("\"" + layoutName + "_title_text\"")) {
            sbStrings.append("    <string name=\"" + layoutName + "_title_text\">" + activityEntity.getTitle() + "</string>\n");
        }
        sbLayout.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\"\n" +
                "    android:orientation=\"vertical\">\n" +
                "\n" +
                "    <zhouzhuo810.me.zzandframe.ui.widget.TitleBar\n" +
                "        android:id=\"@+id/title_bar\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"@dimen/title_height\"\n" +
                "        android:background=\"@color/colorPrimary\"\n" +
                "        app:ttb_leftImg=\"@drawable/back\"\n" +
                "        app:ttb_showLeftImg=\"true\"\n" +
                "        app:ttb_showLeftLayout=\"false\"\n" +
                "        app:ttb_showLeftText=\"false\"\n" +
                "        app:ttb_textColorAll=\"@color/colorWhite\"\n" +
                "        app:ttb_textSizeTitle=\"@dimen/title_text_size\"\n" +
                "        app:ttb_titleText=\"@string/" + layoutName + "_title_text\" />\n" +
                "\n" +
                "    <zhouzhuo810.me.zzandframe.ui.widget.zzpagerindicator.ZzPagerIndicator\n" +
                "        android:id=\"@+id/indicator\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"wrap_content\"\n" +
                "        android:paddingBottom=\"10px\"\n" +
                "        android:paddingTop=\"10px\"\n" +
                "        app:zz_indicator_type=\"tab_with_icon_and_text\"\n" +
                "        app:zz_is_need_scale_in_px=\"true\"\n" +
                "        app:zz_select_tab_text_color=\"@color/colorPrimary\"\n" +
                "        app:zz_select_tab_text_size=\"@dimen/tab_text_size\"\n" +
                "        app:zz_should_tab_expand=\"true\"\n" +
                "        app:zz_tab_icon_size=\"@dimen/tab_img_size\"\n" +
                "        app:zz_underline_color=\"@color/colorPrimary\"\n" +
                "        app:zz_underline_height=\"4px\"\n" +
                "        app:zz_unselect_tab_text_color=\"@color/colorBlack\"\n" +
                "        app:zz_unselect_tab_text_size=\"@dimen/tab_text_size\" />\n" +
                "\n" +
                "    <android.support.v4.view.ViewPager\n" +
                "        android:id=\"@+id/view_pager\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"0dp\"\n" +
                "        android:layout_weight=\"1\">\n" +
                "\n" +
                "    </android.support.v4.view.ViewPager>\n" +
                "\n" +
                "</LinearLayout>");

        /*java*/
        StringBuilder sbJava = new StringBuilder();
        StringBuilder sbImp = new StringBuilder();
        sbImp.append(
                "\nimport android.os.Bundle;\n" +
                        "import android.support.annotation.Nullable;\n" +
                        "import android.content.Intent;\n" +
                        "import android.view.View;\n" +
                        "import android.view.ViewGroup;\n" +
                        "import android.widget.Button;\n" +
                        "import android.widget.CheckBox;\n" +
                        "import android.widget.EditText;\n" +
                        "import android.widget.ImageView;\n" +
                        "import android.widget.TextView;\n" +
                        "import android.widget.LinearLayout;\n" +
                        "import java.util.ArrayList;\n" +
                        "import java.util.List;\n" +
                        "\n" +
                        "import " + app.getPackageName() + ".R;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.act.BaseActivity;\n" +
                        "import " + app.getPackageName() + ".common.api.Api;\n" +
                        "import " + app.getPackageName() + ".common.api.entity.*;\n" +
                        "import rx.Subscriber;\n" +
                        "import zhouzhuo810.me.zzandframe.common.rx.RxHelper;\n" +
                        "import zhouzhuo810.me.zzandframe.common.utils.ToastUtils;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.widget.MarkView;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.widget.TitleBar;\n"
        );

        StringBuilder sbDef = new StringBuilder();
        StringBuilder sbInit = new StringBuilder();
        StringBuilder sbData = new StringBuilder();
        StringBuilder sbEvent = new StringBuilder();
        StringBuilder sbEditInfo = new StringBuilder();
        StringBuilder sbMethods = new StringBuilder();

        sbInit.append("        titleBar = (TitleBar) findViewById(R.id.title_bar);\n" +
                "        indicator = (ZzPagerIndicator) findViewById(R.id.indicator);\n" +
                "        viewPager = (ViewPager) findViewById(R.id.view_pager);\n");

        String arrayName = "tab_names_" + layoutName;
        sbArrays.append("\n    <string-array name=\"" + arrayName + "\">");
        fillTopFragment(entityPath, adapterPath, arrayName, logoName, app, activityEntity, layoutPath, javaPath, sbData, sbStrings, sbImp, sbJava, sbDef, sbInit, sbEvent, sbMethods, sbArrays);

        sbJava.append("package " + app.getPackageName() + ".ui.act;\n" +
                "\n")
                .append(sbImp.toString())
                .append("\n" +
                        "/**\n" +
                        " * " + activityEntity.getTitle() + "\n" +
                        " * Created by admin on 2017/8/27.\n" +
                        " */\n" +
                        "public class " + activityEntity.getName() + " extends BaseActivity {\n")
                .append(sbDef.toString())
                .append("\n\n    @Override\n" +
                        "    public int getLayoutId() {\n" +
                        "        return R.layout." + realLayoutName + ";\n" +
                        "    }\n")
                .append("    @Override\n" +
                        "    public void initView() {\n")
                .append(sbInit.toString())
                .append("\n    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void initData() {\n")
                .append(sbData.toString())
                .append("\n    }\n")
                .append("\n\n" +
                        "    @Override\n" +
                        "    public void initEvent() {\n")
                .append(sbEvent.toString())
                .append("\n    }\n");

        sbJava.append(sbMethods.toString());

        sbJava.append("\n" +
                "    @Override\n" +
                "    public void resume() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "       public void pause() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void destroy() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void saveState(Bundle bundle) {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void restoreState(@Nullable Bundle bundle) {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean defaultBack() {\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "}\n");

        FileUtils.saveFileToPathWithName(sbLayout.toString(), layoutPath, realLayoutName + ".xml");
        FileUtils.saveFileToPathWithName(sbJava.toString(), javaPath + File.separator + "ui" + File.separator + "act", activityEntity.getName() + ".java");

    }


    private void fillTopFragment(String entityPath, String adapterPath, String arrayName, String logoName, ApplicationEntity app, ActivityEntity activityEntity, String layoutPath, String javaPath,
                                 StringBuilder sbData, StringBuilder sbStrings,
                                 StringBuilder sbImp, StringBuilder sbJava, StringBuilder sbDef, StringBuilder sbInit, StringBuilder sbEvent,
                                 StringBuilder sbMethods, StringBuilder sbArrays) throws IOException {
        sbDef.append("\n" +
                "    private TitleBar titleBar;\n" +
                "    private ZzPagerIndicator indicator;\n" +
                "    private ViewPager viewPager;\n" +
                "    List<Fragment> fragments;\n");
        sbImp.append("import android.support.v4.app.Fragment;\n" +
                "import android.support.v4.view.ViewPager;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n\n" +
                "import zhouzhuo810.me.zzandframe.ui.widget.zzpagerindicator.ZzPagerIndicator;\n" +
                "import zhouzhuo810.me.zzandframe.ui.widget.zzpagerindicator.adapter.ZzFragmentPagerAdapter;\n")
                .append("import zhouzhuo810.me.zzandframe.ui.widget.TabBar;\n");
        sbData.append("\n        fragments = new ArrayList<>();");
        List<FragmentEntity> fragments = mFragmentService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("activityId", activityEntity.getId()),
                Restrictions.eq("pid", "0")
        }, Order.asc("position"));
        if (fragments != null && fragments.size() > 0) {
            StringBuilder sbPic = new StringBuilder();
            for (int i = 0; i < fragments.size(); i++) {
                sbPic.append("\n            R.mipmap." + logoName + ",");
            }
            sbPic.deleteCharAt(sbPic.length() - 1);
            sbDef.append("\n    private int[] pressIcons = {")
                    .append(sbPic.toString())
                    .append("\n    };\n")
                    .append("\n    private int[] normalIcons = {")
                    .append(sbPic.toString())
                    .append("\n    };\n");
            StringBuilder sbArray2 = new StringBuilder();
            for (int i1 = 0; i1 < fragments.size(); i1++) {
                FragmentEntity fragment = fragments.get(i1);
                sbImp.append("\nimport " + app.getPackageName() + ".ui.fgm." + fragment.getName() + ";");
                sbArrays.append("\n        <item>" + fragment.getTitle() + "</item>");

                String layoutName = "";
                boolean isNotFirst = false;
                for (int i = 0; i < fragment.getName().length(); i++) {
                    char c = fragment.getName().charAt(i);
                    if (c >= 'A' && c <= 'Z') {
                        if (isNotFirst) {
                            layoutName += "_";
                        }
                        isNotFirst = true;
                    }
                    layoutName += c;
                }
                layoutName = layoutName.replace("fragment_", "").replace("_fragment", "").replace("__", "_").toLowerCase();
                final String realLayoutName = "fragment_" + layoutName.toLowerCase();

                sbData.append("\n        fragments.add(new " + fragment.getName() + "());");

                StringBuilder sbLayout1 = new StringBuilder();
                StringBuilder sbImp1 = new StringBuilder();
                StringBuilder sbJava1 = new StringBuilder();
                StringBuilder sbDef1 = new StringBuilder();
                StringBuilder sbInit1 = new StringBuilder();
                StringBuilder sbData1 = new StringBuilder();
                StringBuilder sbEvent1 = new StringBuilder();
                StringBuilder sbEditInfo1 = new StringBuilder();
                StringBuilder sbMethods1 = new StringBuilder();

                sbLayout1.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                        "    android:layout_width=\"match_parent\"\n" +
                        "    android:layout_height=\"match_parent\"\n" +
                        "    android:background=\"@color/colorGrayBg\"\n" +
                        "    android:orientation=\"vertical\">\n");

                sbImp1.append(
                        "\nimport android.os.Bundle;\n" +
                                "import android.support.annotation.Nullable;\n" +
                                "import android.content.Intent;\n" +
                                "import android.view.View;\n" +
                                "import android.view.ViewGroup;\n" +
                                "import android.widget.Button;\n" +
                                "import android.widget.CheckBox;\n" +
                                "import android.widget.EditText;\n" +
                                "import android.widget.ImageView;\n" +
                                "import android.widget.TextView;\n" +
                                "import android.widget.LinearLayout;\n" +
                                "\n" +
                                "import " + app.getPackageName() + ".R;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.fgm.BaseFragment;\n" +
                                "import " + app.getPackageName() + ".common.api.Api;\n" +
                                "import " + app.getPackageName() + ".common.api.entity.*;\n" +
                                "import rx.Subscriber;\n" +
                                "import zhouzhuo810.me.zzandframe.common.rx.RxHelper;\n" +
                                "import zhouzhuo810.me.zzandframe.common.utils.ToastUtils;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.widget.MarkView;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.widget.TitleBar;\n"
                );

                fillFgmWidget(adapterPath, logoName, app, activityEntity, fragment, layoutName, layoutPath, javaPath, "0", sbStrings, sbLayout1, sbImp1, sbJava1, sbDef1, sbInit1, sbData1, sbEvent1, sbEditInfo1, sbMethods1);

                if (fragment.getChildCount() > 0) {
                    String arrayName1 = "child_tab_names_" + layoutName;
                    sbArray2.append("\n    <string-array name=\"" + arrayName1 + "\">");
                    fillChildTopFragment(entityPath, adapterPath, layoutName, sbLayout1, arrayName1, logoName, app, activityEntity, layoutPath, javaPath, sbData1, sbStrings, sbImp1, sbJava1, sbDef1, sbInit1, sbEvent1, sbMethods1, sbArray2, fragment.getId());
                    sbArray2.append("\n    </string-array>");
                }

                sbJava1.append("package " + app.getPackageName() + ".ui.fgm;\n" +
                        "\n")
                        .append(sbImp1.toString())
                        .append("\n" +
                                "/**\n" +
                                " *\n" +
                                " * Created by admin on 2017/8/27.\n" +
                                " */\n" +
                                "public class " + fragment.getName() + " extends BaseFragment {\n")
                        .append(sbDef1.toString())
                        .append("\n\n    @Override\n" +
                                "    public int getLayoutId() {\n" +
                                "        return R.layout." + realLayoutName + ";\n" +
                                "    }\n")
                        .append("    @Override\n" +
                                "    public void initView() {\n")
                        .append(sbInit1.toString())
                        .append("\n    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public void initData() {\n" +
                                sbData1.toString() +
                                "\n    }\n")
                        .append("\n" +
                                "    @Override\n" +
                                "    public void initEvent() {\n")
                        .append(sbEvent1.toString())
                        .append("\n    }\n");

                sbJava1.append(sbMethods1.toString());

                sbJava1.append("\n" +
                        "    @Override\n" +
                        "    public void resume() {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "       public void destroyView() {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void saveState(Bundle bundle) {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void restoreState(@Nullable Bundle bundle) {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "}\n");
                sbLayout1.append("\n" +
                        "</LinearLayout>");


                FileUtils.saveFileToPathWithName(sbLayout1.toString(), layoutPath, realLayoutName + ".xml");
                FileUtils.saveFileToPathWithName(sbJava1.toString(), javaPath + File.separator + "ui" + File.separator + "fgm", fragment.getName() + ".java");

            }
            sbArrays.append("\n    </string-array>");
            sbArrays.append("\n").append(sbArray2.toString());
            sbData.append("\n        String[] titles = getResources().getStringArray(R.array." + arrayName + ");\n" +
                    "        viewPager.setAdapter(new ZzFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles,\n" +
                    "                pressIcons, normalIcons));\n" +
                    "\n" +
                    "        indicator.setViewPager(viewPager);\n");
        }
    }

    private void fillChildTopFragment(String entityPath, String adapterPath, String layoutName1, StringBuilder sbLayout, String arrayName, String logoName, ApplicationEntity app, ActivityEntity activityEntity, String layoutPath, String javaPath,
                                      StringBuilder sbData, StringBuilder sbStrings,
                                      StringBuilder sbImp, StringBuilder sbJava, StringBuilder sbDef, StringBuilder sbInit, StringBuilder sbEvent,
                                      StringBuilder sbMethods, StringBuilder sbArrays, String pid) throws IOException {
        if (!sbStrings.toString().contains("\"" + layoutName1 + "_title_text\"")) {
            sbStrings.append("    <string name=\"" + layoutName1 + "_title_text\">" + activityEntity.getTitle() + "</string>\n");
        }
        sbLayout.append(
                "\n" +
                        "    <zhouzhuo810.me.zzandframe.ui.widget.zzpagerindicator.ZzPagerIndicator\n" +
                        "        android:id=\"@+id/indicator\"\n" +
                        "        android:layout_width=\"match_parent\"\n" +
                        "        android:layout_height=\"wrap_content\"\n" +
                        "        android:paddingBottom=\"10px\"\n" +
                        "        android:paddingTop=\"10px\"\n" +
                        "        app:zz_indicator_type=\"tab_with_icon_and_text\"\n" +
                        "        app:zz_is_need_scale_in_px=\"true\"\n" +
                        "        app:zz_select_tab_text_color=\"@color/colorPrimary\"\n" +
                        "        app:zz_select_tab_text_size=\"@dimen/tab_text_size\"\n" +
                        "        app:zz_should_tab_expand=\"true\"\n" +
                        "        app:zz_tab_icon_size=\"@dimen/tab_img_size\"\n" +
                        "        app:zz_underline_color=\"@color/colorPrimary\"\n" +
                        "        app:zz_underline_height=\"4px\"\n" +
                        "        app:zz_unselect_tab_text_color=\"@color/colorBlack\"\n" +
                        "        app:zz_unselect_tab_text_size=\"@dimen/tab_text_size\" />\n" +
                        "\n" +
                        "    <android.support.v4.view.ViewPager\n" +
                        "        android:id=\"@+id/view_pager\"\n" +
                        "        android:layout_width=\"match_parent\"\n" +
                        "        android:layout_height=\"0dp\"\n" +
                        "        android:layout_weight=\"1\">\n" +
                        "\n" +
                        "    </android.support.v4.view.ViewPager>\n"
        );

        sbDef.append("\n" +
                "    private ZzPagerIndicator indicator;\n" +
                "    private ViewPager viewPager;\n" +
                "    List<Fragment> fragments;\n");
        sbImp.append("import android.support.v4.app.Fragment;\n" +
                "import android.support.v4.view.ViewPager;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n\n" +
                "import zhouzhuo810.me.zzandframe.ui.widget.zzpagerindicator.ZzPagerIndicator;\n" +
                "import zhouzhuo810.me.zzandframe.ui.widget.zzpagerindicator.adapter.ZzFragmentPagerAdapter;\n")
                .append("import zhouzhuo810.me.zzandframe.ui.widget.TabBar;\n");
        sbInit.append("\n        indicator = (ZzPagerIndicator) rootView.findViewById(R.id.indicator);\n" +
                "        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);\n");
        sbData.append("\n        fragments = new ArrayList<>();");
        List<FragmentEntity> fragments = mFragmentService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("activityId", activityEntity.getId()),
                Restrictions.eq("pid", pid)
        }, Order.asc("position"));
        if (fragments != null && fragments.size() > 0) {
            StringBuilder sbPic = new StringBuilder();
            for (int i = 0; i < fragments.size(); i++) {
                sbPic.append("\n            R.mipmap." + logoName + ",");
            }
            sbPic.deleteCharAt(sbPic.length() - 1);
            sbDef.append("\n    private int[] pressIcons = {")
                    .append(sbPic.toString())
                    .append("\n    };\n")
                    .append("\n    private int[] normalIcons = {")
                    .append(sbPic.toString())
                    .append("\n    };\n");

            for (int i1 = 0; i1 < fragments.size(); i1++) {
                FragmentEntity fragment = fragments.get(i1);
                sbImp.append("\nimport " + app.getPackageName() + ".ui.fgm." + fragment.getName() + ";");
                sbArrays.append("\n        <item>" + fragment.getTitle() + "</item>");

                String layoutName = "";
                boolean isNotFirst = false;
                for (int i = 0; i < fragment.getName().length(); i++) {
                    char c = fragment.getName().charAt(i);
                    if (c >= 'A' && c <= 'Z') {
                        if (isNotFirst) {
                            layoutName += "_";
                        }
                        isNotFirst = true;
                    }
                    layoutName += c;
                }
                layoutName = layoutName.replace("fragment_", "").replace("_fragment", "").replace("__", "_").toLowerCase();
                final String realLayoutName = "fragment_" + layoutName.toLowerCase();

                sbData.append("\n        fragments.add(new " + fragment.getName() + "());");

                StringBuilder sbLayout1 = new StringBuilder();
                StringBuilder sbImp1 = new StringBuilder();
                StringBuilder sbJava1 = new StringBuilder();
                StringBuilder sbDef1 = new StringBuilder();
                StringBuilder sbInit1 = new StringBuilder();
                StringBuilder sbData1 = new StringBuilder();
                StringBuilder sbEvent1 = new StringBuilder();
                StringBuilder sbEditInfo1 = new StringBuilder();
                StringBuilder sbMethods1 = new StringBuilder();

                sbLayout1.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                        "    android:layout_width=\"match_parent\"\n" +
                        "    android:layout_height=\"match_parent\"\n" +
                        "    android:background=\"@color/colorGrayBg\"\n" +
                        "    android:orientation=\"vertical\">\n");

                sbImp1.append(
                        "\nimport android.os.Bundle;\n" +
                                "import android.support.annotation.Nullable;\n" +
                                "import android.content.Intent;\n" +
                                "import android.view.View;\n" +
                                "import android.view.ViewGroup;\n" +
                                "import android.widget.Button;\n" +
                                "import android.widget.CheckBox;\n" +
                                "import android.widget.EditText;\n" +
                                "import android.widget.ImageView;\n" +
                                "import android.widget.TextView;\n" +
                                "import android.widget.LinearLayout;\n" +
                                "\n" +
                                "import " + app.getPackageName() + ".R;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.fgm.BaseFragment;\n" +
                                "import " + app.getPackageName() + ".common.api.Api;\n" +
                                "import " + app.getPackageName() + ".common.api.entity.*;\n" +
                                "import rx.Subscriber;\n" +
                                "import zhouzhuo810.me.zzandframe.common.rx.RxHelper;\n" +
                                "import zhouzhuo810.me.zzandframe.common.utils.ToastUtils;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.widget.MarkView;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.widget.TitleBar;\n"
                );

                fillFgmWidget(adapterPath, logoName, app, activityEntity, fragment, layoutName, layoutPath, javaPath, "0", sbStrings, sbLayout1, sbImp1, sbJava1, sbDef1, sbInit1, sbData1, sbEvent1, sbEditInfo1, sbMethods1);

                sbJava1.append("package " + app.getPackageName() + ".ui.fgm;\n" +
                        "\n")
                        .append(sbImp1.toString())
                        .append("\n" +
                                "/**\n" +
                                " *\n" +
                                " * Created by admin on 2017/8/27.\n" +
                                " */\n" +
                                "public class " + fragment.getName() + " extends BaseFragment {\n")
                        .append(sbDef1.toString())
                        .append("\n\n    @Override\n" +
                                "    public int getLayoutId() {\n" +
                                "        return R.layout." + realLayoutName + ";\n" +
                                "    }\n")
                        .append("    @Override\n" +
                                "    public void initView() {\n")
                        .append(sbInit1.toString())
                        .append("\n    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public void initData() {\n" +
                                sbData1.toString() +
                                "\n    }\n")
                        .append("\n" +
                                "    @Override\n" +
                                "    public void initEvent() {\n")
                        .append(sbEvent1.toString())
                        .append("\n    }\n");

                sbJava1.append(sbMethods1.toString());

                sbJava1.append("\n" +
                        "    @Override\n" +
                        "    public void resume() {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "       public void destroyView() {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void saveState(Bundle bundle) {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void restoreState(@Nullable Bundle bundle) {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "}\n");
                sbLayout1.append("\n" +
                        "</LinearLayout>");


                FileUtils.saveFileToPathWithName(sbLayout1.toString(), layoutPath, realLayoutName + ".xml");
                FileUtils.saveFileToPathWithName(sbJava1.toString(), javaPath + File.separator + "ui" + File.separator + "fgm", fragment.getName() + ".java");

            }

            sbData.append("\n        String[] titles = getResources().getStringArray(R.array." + arrayName + ");\n" +
                    "        viewPager.setAdapter(new ZzFragmentPagerAdapter(getChildFragmentManager(), fragments, titles,\n" +
                    "                pressIcons, normalIcons));\n" +
                    "\n" +
                    "        indicator.setViewPager(viewPager);\n");
        }
    }

    private void generateBottomFgmActJavaAndLayout(String entityPath, String adapterPath, String logoName, String layoutPath, String javaPath, ActivityEntity activityEntity, ApplicationEntity app, StringBuilder sbStrings, StringBuilder sbArrays) throws IOException {
        String layoutName = "";
        boolean isNotFirst = false;
        for (int i = 0; i < activityEntity.getName().length(); i++) {
            char c = activityEntity.getName().charAt(i);
            if (c >= 'A' && c <= 'Z') {
                if (isNotFirst) {
                    layoutName += "_";
                }
                isNotFirst = true;
            }
            layoutName += c;
        }
        layoutName = layoutName.replace("activity_", "").replace("_activity", "").replace("__", "_").toLowerCase();
        final String realLayoutName = "activity_" + layoutName.toLowerCase();

        /*layout*/
        StringBuilder sbLayout = new StringBuilder();
        sbLayout.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\"\n" +
                "    android:orientation=\"vertical\">\n" +
                "\n" +
                "    <FrameLayout\n" +
                "        android:id=\"@+id/fgm_container\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"0dp\"\n" +
                "        android:layout_weight=\"1\">\n" +
                "\n" +
                "    </FrameLayout>\n" +
                "\n");

        /*java*/
        StringBuilder sbJava = new StringBuilder();
        StringBuilder sbImp = new StringBuilder();
        sbImp.append(
                "\nimport android.os.Bundle;\n" +
                        "import android.support.annotation.Nullable;\n" +
                        "import android.content.Intent;\n" +
                        "import android.view.View;\n" +
                        "import android.view.ViewGroup;\n" +
                        "import android.widget.Button;\n" +
                        "import android.widget.CheckBox;\n" +
                        "import android.widget.EditText;\n" +
                        "import android.widget.ImageView;\n" +
                        "import android.widget.TextView;\n" +
                        "import android.widget.LinearLayout;\n" +
                        "import java.util.ArrayList;\n" +
                        "import java.util.List;\n" +
                        "\n" +
                        "import " + app.getPackageName() + ".R;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.act.BaseActivity;\n" +
                        "import " + app.getPackageName() + ".common.api.Api;\n" +
                        "import " + app.getPackageName() + ".common.api.entity.*;\n" +
                        "import rx.Subscriber;\n" +
                        "import zhouzhuo810.me.zzandframe.common.rx.RxHelper;\n" +
                        "import zhouzhuo810.me.zzandframe.common.utils.ToastUtils;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.widget.MarkView;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.widget.TitleBar;\n");

        StringBuilder sbDef = new StringBuilder();
        StringBuilder sbInit = new StringBuilder();
        StringBuilder sbEvent = new StringBuilder();
        StringBuilder sbEditInfo = new StringBuilder();
        StringBuilder sbMethods = new StringBuilder();
        StringBuilder sbAttach = new StringBuilder();

        sbInit.append("        tabBar = (TabBar) findViewById(R.id.tab_bar);\n" +
                "        tabBar.setPressIconRes(pressIcons)\n" +
                "                .setNormalIconRes(normalIcons)\n" +
                "                .update();\n");
        sbEvent.append("        tabBar.setOnTabBarClickListener(new TabBar.OnTabBarClick() {\n" +
                "            @Override\n" +
                "            public void onTabClick(ImageView imageView, TextView textView, int position, boolean changed) {\n" +
                "                if (changed) {\n" +
                "                    select(position);\n" +
                "                }\n" +
                "            }\n" +
                "        });\n");
        sbAttach.append("\n" +
                "    @Override\n" +
                "    public void onAttachFragment(Fragment fragment) {\n" +
                "        super.onAttachFragment(fragment);");
        String arrayName = "tan_names_" + layoutName;
        sbArrays.append("\n    <string-array name=\"" + arrayName + "\">");
        fillBottomFragment(entityPath, adapterPath, arrayName, logoName, app, activityEntity, layoutPath, javaPath, sbAttach, sbStrings, sbLayout, sbImp, sbJava, sbDef, sbInit, sbEvent, sbMethods, sbArrays);
        sbAttach.append("\n    }\n");

        sbJava.append("package " + app.getPackageName() + ".ui.act;\n" +
                "\n")
                .append(sbImp.toString())
                .append("\n" +
                        "/**\n" +
                        " *\n" +
                        " * Created by admin on 2017/8/27.\n" +
                        " */\n" +
                        "public class " + activityEntity.getName() + " extends BaseActivity {\n")
                .append(sbDef.toString())
                .append("\n\n    @Override\n" +
                        "    public int getLayoutId() {\n" +
                        "        return R.layout." + realLayoutName + ";\n" +
                        "    }\n")
                .append("    @Override\n" +
                        "    public void initView() {\n")
                .append(sbInit.toString())
                .append("\n    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void initData() {\n" +
                        "        select(0);\n" +
                        "    }\n")
                .append("\n" +
                        "    @Override\n" +
                        "    public void initEvent() {\n")
                .append(sbEvent.toString())
                .append("\n    }\n");
        sbJava.append(sbMethods.toString());

        sbJava.append("\n" +
                "    @Override\n" +
                "    public void resume() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "       public void pause() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void destroy() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void saveState(Bundle bundle) {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void restoreState(@Nullable Bundle bundle) {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean defaultBack() {\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "}\n");
        sbLayout.append("\n" +
                "</LinearLayout>");


        FileUtils.saveFileToPathWithName(sbLayout.toString(), layoutPath, realLayoutName + ".xml");
        FileUtils.saveFileToPathWithName(sbJava.toString(), javaPath + File.separator + "ui" + File.separator + "act", activityEntity.getName() + ".java");

    }

    private void fillBottomFragment(String entityPath, String adapterPath, String arrayName, String logoName, ApplicationEntity app, ActivityEntity activityEntity, String layoutPath, String javaPath, StringBuilder sbAttach, StringBuilder sbStrings,
                                    StringBuilder sbLayout, StringBuilder sbImp, StringBuilder sbJava, StringBuilder sbDef, StringBuilder sbInit, StringBuilder sbEvent,
                                    StringBuilder sbMethods, StringBuilder sbArrays) throws IOException {
        sbDef.append("\n    private TabBar tabBar;\n\n");
        sbImp.append("import android.support.v4.app.Fragment;\n" +
                "import android.support.v4.app.FragmentManager;\n" +
                "import android.support.v4.app.FragmentTransaction;\n")
                .append("import zhouzhuo810.me.zzandframe.ui.widget.TabBar;\n");
        List<FragmentEntity> fragments = mFragmentService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("activityId", activityEntity.getId()),
                Restrictions.eq("pid", "0")
        }, Order.asc("position"));
        if (fragments != null && fragments.size() > 0) {
            String tabCount = "FIVE";
            switch (fragments.size()) {
                case 1:
                    tabCount = "ONE";
                    break;
                case 2:
                    tabCount = "TWO";
                    break;
                case 3:
                    tabCount = "THREE";
                    break;
                case 4:
                    tabCount = "FOUR";
                    break;
                case 5:
                    tabCount = "FIVE";
                    break;
                default:
                    tabCount = "FIVE";
                    break;
            }
            sbLayout.append("    <zhouzhuo810.me.zzandframe.ui.widget.TabBar\n" +
                    "        android:id=\"@+id/tab_bar\"\n" +
                    "        android:layout_width=\"match_parent\"\n" +
                    "        android:layout_height=\"wrap_content\"\n" +
                    "        android:background=\"@color/colorWhite\"\n" +
                    "        app:tb_tabNames=\"@array/" + arrayName + "\"\n" +
                    "        app:tb_textColorNormal=\"@color/colorGrayB\"\n" +
                    "        app:tb_textColorPress=\"@color/colorPrimary\"\n" +
                    "        app:tb_tabCount=\"" + tabCount + "\"\n" +
                    "        app:tb_textSize=\"@dimen/tab_text_size\" />\n");
            StringBuilder sbPic = new StringBuilder();
            for (int i = 0; i < fragments.size(); i++) {
                sbPic.append("\n            R.mipmap." + logoName + ",");
            }
            sbPic.deleteCharAt(sbPic.length() - 1);
            sbDef.append("\n    private int[] pressIcons = {")
                    .append(sbPic.toString())
                    .append("\n    };\n")
                    .append("\n    private int[] normalIcons = {")
                    .append(sbPic.toString())
                    .append("\n    };\n");

            StringBuilder sbSelect = new StringBuilder();
            StringBuilder sbHide = new StringBuilder();
            StringBuilder sbArray2 = new StringBuilder();
            for (int i1 = 0; i1 < fragments.size(); i1++) {
                FragmentEntity fragment = fragments.get(i1);
                sbImp.append("\nimport " + app.getPackageName() + ".ui.fgm." + fragment.getName() + ";");
                sbArrays.append("\n        <item>" + fragment.getTitle() + "</item>");

                String layoutName = "";
                boolean isNotFirst = false;
                for (int i = 0; i < fragment.getName().length(); i++) {
                    char c = fragment.getName().charAt(i);
                    if (c >= 'A' && c <= 'Z') {
                        if (isNotFirst) {
                            layoutName += "_";
                        }
                        isNotFirst = true;
                    }
                    layoutName += c;
                }
                layoutName = layoutName.replace("fragment_", "").replace("_fragment", "").replace("__", "_").toLowerCase();
                final String realLayoutName = "fragment_" + layoutName.toLowerCase();
                sbDef.append("\n    private " + fragment.getName() + " " + realLayoutName + ";");
                sbAttach.append("\n        if (" + realLayoutName + " == null && fragment instanceof " + fragment.getName() + ") {\n" +
                        "            " + realLayoutName + " = (" + fragment.getName() + ") fragment;\n" +
                        "        }");
                sbSelect.append("            case " + i1 + ":\n" +
                        "                if (" + realLayoutName + " == null) {\n" +
                        "                    " + realLayoutName + " = new " + fragment.getName() + "();\n" +
                        "                    ft.add(R.id.fgm_container, " + realLayoutName + ");\n" +
                        "                } else {\n" +
                        "                    ft.attach(" + realLayoutName + ");\n" +
                        "                }\n" +
                        "                break;\n");
                sbHide.append("        if (" + realLayoutName + " != null) {\n" +
                        "            ft.detach(" + realLayoutName + ");\n" +
                        "        }\n");

                StringBuilder sbLayout1 = new StringBuilder();
                StringBuilder sbImp1 = new StringBuilder();
                StringBuilder sbJava1 = new StringBuilder();
                StringBuilder sbDef1 = new StringBuilder();
                StringBuilder sbInit1 = new StringBuilder();
                StringBuilder sbData1 = new StringBuilder();
                StringBuilder sbEvent1 = new StringBuilder();
                StringBuilder sbEditInfo1 = new StringBuilder();
                StringBuilder sbMethods1 = new StringBuilder();

                sbLayout1.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                        "    android:layout_width=\"match_parent\"\n" +
                        "    android:layout_height=\"match_parent\"\n" +
                        "    android:background=\"@color/colorGrayBg\"\n" +
                        "    android:orientation=\"vertical\">\n");

                sbImp1.append(
                        "\nimport android.os.Bundle;\n" +
                                "import android.support.annotation.Nullable;\n" +
                                "import android.content.Intent;\n" +
                                "import android.view.View;\n" +
                                "import android.view.ViewGroup;\n" +
                                "import android.widget.Button;\n" +
                                "import android.widget.CheckBox;\n" +
                                "import android.widget.EditText;\n" +
                                "import android.widget.ImageView;\n" +
                                "import android.widget.TextView;\n" +
                                "import android.widget.LinearLayout;\n" +
                                "import java.util.ArrayList;\n" +
                                "import java.util.List;\n" +
                                "\n" +
                                "import " + app.getPackageName() + ".R;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.fgm.BaseFragment;\n" +
                                "import " + app.getPackageName() + ".common.api.Api;\n" +
                                "import " + app.getPackageName() + ".common.api.entity.*;\n" +
                                "import rx.Subscriber;\n" +
                                "import zhouzhuo810.me.zzandframe.common.rx.RxHelper;\n" +
                                "import zhouzhuo810.me.zzandframe.common.utils.ToastUtils;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.widget.MarkView;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.widget.TitleBar;\n");

                fillFgmWidget(adapterPath, logoName, app, activityEntity, fragment, layoutName, layoutPath, javaPath, "0", sbStrings, sbLayout1, sbImp1, sbJava1, sbDef1, sbInit1, sbData1, sbEvent1, sbEditInfo1, sbMethods1);

                // TODO: 2017/12/29 添加子Fragment
                if (fragment.getChildCount() > 0) {
                    String arrayName1 = "child_tab_names_" + layoutName;
                    sbArray2.append("\n    <string-array name=\"" + arrayName1 + "\">");
                    fillChildTopFragment(entityPath, adapterPath, layoutName, sbLayout1, arrayName1, logoName, app, activityEntity, layoutPath, javaPath, sbData1, sbStrings, sbImp1, sbJava1, sbDef1, sbInit1, sbEvent1, sbMethods1, sbArray2, fragment.getId());
                    sbArray2.append("\n    </string-array>");
                }

                sbJava1.append("package " + app.getPackageName() + ".ui.fgm;\n" +
                        "\n")
                        .append(sbImp1.toString())
                        .append("\n" +
                                "/**\n" +
                                " *\n" +
                                " * Created by admin on 2017/8/27.\n" +
                                " */\n" +
                                "public class " + fragment.getName() + " extends BaseFragment {\n")
                        .append(sbDef1.toString())
                        .append("\n\n    @Override\n" +
                                "    public int getLayoutId() {\n" +
                                "        return R.layout." + realLayoutName + ";\n" +
                                "    }\n")
                        .append("    @Override\n" +
                                "    public void initView() {\n")
                        .append(sbInit1.toString())
                        .append("\n    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public void initData() {\n" +
                                sbData1.toString() +
                                "\n    }\n")
                        .append("\n" +
                                "    @Override\n" +
                                "    public void initEvent() {\n")
                        .append(sbEvent1.toString())
                        .append("\n    }\n");

                sbJava1.append(sbMethods1.toString());

                sbJava1.append("\n" +
                        "    @Override\n" +
                        "    public void resume() {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "       public void destroyView() {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void saveState(Bundle bundle) {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void restoreState(@Nullable Bundle bundle) {\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "}\n");
                sbLayout1.append("\n" +
                        "</LinearLayout>");


                FileUtils.saveFileToPathWithName(sbLayout1.toString(), layoutPath, realLayoutName + ".xml");
                FileUtils.saveFileToPathWithName(sbJava1.toString(), javaPath + File.separator + "ui" + File.separator + "fgm", fragment.getName() + ".java");

            }
            sbArrays.append("\n    </string-array>");
            sbArrays.append("\n").append(sbArray2.toString());
            sbMethods
                    .append("\n" +
                            "    private void select(int position) {\n" +
                            "        FragmentManager fm = getSupportFragmentManager();\n" +
                            "        FragmentTransaction ft = fm.beginTransaction();\n" +
                            "        hideFragment(ft);\n" +
                            "        switch (position) {\n")
                    .append(sbSelect.toString())
                    .append("        }\n" +
                            "        ft.commit();\n" +
                            "    }\n")
                    .append("\n" +
                            "    private void hideFragment(FragmentTransaction ft) {\n")
                    .append(sbHide.toString())
                    .append("    }\n");
        }
    }

    private void generateWidgets(String rootPath, String javaPath, ApplicationEntity app) throws IOException {
        /*scrollableListView*/
        String scrollLvPath = javaPath + File.separator + "ui" + File.separator + "widget";
        FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ".ui.widget;\n" +
                "\n" +
                "import android.content.Context;\n" +
                "import android.util.AttributeSet;\n" +
                "import android.widget.ListView;\n" +
                "\n" +
                "/**\n" +
                " * Created by zhouzhuo810 on 2016/6/6.\n" +
                " */\n" +
                "public class ScrollableListView extends ListView {\n" +
                "    public ScrollableListView(Context context) {\n" +
                "        super(context);\n" +
                "    }\n" +
                "\n" +
                "    public ScrollableListView(Context context, AttributeSet attrs) {\n" +
                "        super(context, attrs);\n" +
                "    }\n" +
                "\n" +
                "    public ScrollableListView(Context context, AttributeSet attrs, int defStyleAttr) {\n" +
                "        super(context, attrs, defStyleAttr);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "\n" +
                "/**   只重写该方法，达到使ListView适应ScrollView的效果   */\n" +
                "\n" +
                "    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {\n" +
                "\n" +
                "        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,\n" +
                "\n" +
                "                MeasureSpec.AT_MOST);\n" +
                "\n" +
                "        super.onMeasure(widthMeasureSpec, expandSpec);\n" +
                "\n" +
                "    }\n" +
                "}\n", scrollLvPath, "ScrollableListView.java");
        /*sidebar*/
        String sideBarPath = javaPath + File.separator + "ui" + File.separator + "widget" + File.separator + "sidebar";
        FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ".ui.widget.sidebar;\n" +
                "\n" +
                "/**\n" +
                " * Java汉字转换为拼音\n" +
                " */\n" +
                "public class CharacterParser {\n" +
                "\n" +
                "    private static int[] pyvalue = new int[]{-20319, -20317, -20304, -20295,\n" +
                "            -20292, -20283, -20265, -20257, -20242, -20230, -20051, -20036,\n" +
                "            -20032, -20026, -20002, -19990, -19986, -19982, -19976, -19805,\n" +
                "            -19784, -19775, -19774, -19763, -19756, -19751, -19746, -19741,\n" +
                "            -19739, -19728, -19725, -19715, -19540, -19531, -19525, -19515,\n" +
                "            -19500, -19484, -19479, -19467, -19289, -19288, -19281, -19275,\n" +
                "            -19270, -19263, -19261, -19249, -19243, -19242, -19238, -19235,\n" +
                "            -19227, -19224, -19218, -19212, -19038, -19023, -19018, -19006,\n" +
                "            -19003, -18996, -18977, -18961, -18952, -18783, -18774, -18773,\n" +
                "            -18763, -18756, -18741, -18735, -18731, -18722, -18710, -18697,\n" +
                "            -18696, -18526, -18518, -18501, -18490, -18478, -18463, -18448,\n" +
                "            -18447, -18446, -18239, -18237, -18231, -18220, -18211, -18201,\n" +
                "            -18184, -18183, -18181, -18012, -17997, -17988, -17970, -17964,\n" +
                "            -17961, -17950, -17947, -17931, -17928, -17922, -17759, -17752,\n" +
                "            -17733, -17730, -17721, -17703, -17701, -17697, -17692, -17683,\n" +
                "            -17676, -17496, -17487, -17482, -17468, -17454, -17433, -17427,\n" +
                "            -17417, -17202, -17185, -16983, -16970, -16942, -16915, -16733,\n" +
                "            -16708, -16706, -16689, -16664, -16657, -16647, -16474, -16470,\n" +
                "            -16465, -16459, -16452, -16448, -16433, -16429, -16427, -16423,\n" +
                "            -16419, -16412, -16407, -16403, -16401, -16393, -16220, -16216,\n" +
                "            -16212, -16205, -16202, -16187, -16180, -16171, -16169, -16158,\n" +
                "            -16155, -15959, -15958, -15944, -15933, -15920, -15915, -15903,\n" +
                "            -15889, -15878, -15707, -15701, -15681, -15667, -15661, -15659,\n" +
                "            -15652, -15640, -15631, -15625, -15454, -15448, -15436, -15435,\n" +
                "            -15419, -15416, -15408, -15394, -15385, -15377, -15375, -15369,\n" +
                "            -15363, -15362, -15183, -15180, -15165, -15158, -15153, -15150,\n" +
                "            -15149, -15144, -15143, -15141, -15140, -15139, -15128, -15121,\n" +
                "            -15119, -15117, -15110, -15109, -14941, -14937, -14933, -14930,\n" +
                "            -14929, -14928, -14926, -14922, -14921, -14914, -14908, -14902,\n" +
                "            -14894, -14889, -14882, -14873, -14871, -14857, -14678, -14674,\n" +
                "            -14670, -14668, -14663, -14654, -14645, -14630, -14594, -14429,\n" +
                "            -14407, -14399, -14384, -14379, -14368, -14355, -14353, -14345,\n" +
                "            -14170, -14159, -14151, -14149, -14145, -14140, -14137, -14135,\n" +
                "            -14125, -14123, -14122, -14112, -14109, -14099, -14097, -14094,\n" +
                "            -14092, -14090, -14087, -14083, -13917, -13914, -13910, -13907,\n" +
                "            -13906, -13905, -13896, -13894, -13878, -13870, -13859, -13847,\n" +
                "            -13831, -13658, -13611, -13601, -13406, -13404, -13400, -13398,\n" +
                "            -13395, -13391, -13387, -13383, -13367, -13359, -13356, -13343,\n" +
                "            -13340, -13329, -13326, -13318, -13147, -13138, -13120, -13107,\n" +
                "            -13096, -13095, -13091, -13076, -13068, -13063, -13060, -12888,\n" +
                "            -12875, -12871, -12860, -12858, -12852, -12849, -12838, -12831,\n" +
                "            -12829, -12812, -12802, -12607, -12597, -12594, -12585, -12556,\n" +
                "            -12359, -12346, -12320, -12300, -12120, -12099, -12089, -12074,\n" +
                "            -12067, -12058, -12039, -11867, -11861, -11847, -11831, -11798,\n" +
                "            -11781, -11604, -11589, -11536, -11358, -11340, -11339, -11324,\n" +
                "            -11303, -11097, -11077, -11067, -11055, -11052, -11045, -11041,\n" +
                "            -11038, -11024, -11020, -11019, -11018, -11014, -10838, -10832,\n" +
                "            -10815, -10800, -10790, -10780, -10764, -10587, -10544, -10533,\n" +
                "            -10519, -10331, -10329, -10328, -10322, -10315, -10309, -10307,\n" +
                "            -10296, -10281, -10274, -10270, -10262, -10260, -10256, -10254};\n" +
                "    public static String[] pystr = new String[]{\"a\", \"ai\", \"an\", \"ang\", \"ao\",\n" +
                "            \"ba\", \"bai\", \"ban\", \"bang\", \"bao\", \"bei\", \"ben\", \"beng\", \"bi\",\n" +
                "            \"bian\", \"biao\", \"bie\", \"bin\", \"bing\", \"bo\", \"bu\", \"ca\", \"cai\",\n" +
                "            \"can\", \"cang\", \"cao\", \"ce\", \"ceng\", \"cha\", \"chai\", \"chan\", \"chang\",\n" +
                "            \"chao\", \"che\", \"chen\", \"cheng\", \"chi\", \"chong\", \"chou\", \"chu\",\n" +
                "            \"chuai\", \"chuan\", \"chuang\", \"chui\", \"chun\", \"chuo\", \"ci\", \"cong\",\n" +
                "            \"cou\", \"cu\", \"cuan\", \"cui\", \"cun\", \"cuo\", \"da\", \"dai\", \"dan\",\n" +
                "            \"dang\", \"dao\", \"de\", \"deng\", \"di\", \"dian\", \"diao\", \"die\", \"ding\",\n" +
                "            \"diu\", \"dong\", \"dou\", \"du\", \"duan\", \"dui\", \"dun\", \"duo\", \"e\", \"en\",\n" +
                "            \"er\", \"fa\", \"fan\", \"fang\", \"fei\", \"fen\", \"feng\", \"fo\", \"fou\", \"fu\",\n" +
                "            \"ga\", \"gai\", \"gan\", \"gang\", \"gao\", \"ge\", \"gei\", \"gen\", \"geng\",\n" +
                "            \"gong\", \"gou\", \"gu\", \"gua\", \"guai\", \"guan\", \"guang\", \"gui\", \"gun\",\n" +
                "            \"guo\", \"ha\", \"hai\", \"han\", \"hang\", \"hao\", \"he\", \"hei\", \"hen\",\n" +
                "            \"heng\", \"hong\", \"hou\", \"hu\", \"hua\", \"huai\", \"huan\", \"huang\", \"hui\",\n" +
                "            \"hun\", \"huo\", \"ji\", \"jia\", \"jian\", \"jiang\", \"jiao\", \"jie\", \"jin\",\n" +
                "            \"jing\", \"jiong\", \"jiu\", \"ju\", \"juan\", \"jue\", \"jun\", \"ka\", \"kai\",\n" +
                "            \"kan\", \"kang\", \"kao\", \"ke\", \"ken\", \"keng\", \"kong\", \"kou\", \"ku\",\n" +
                "            \"kua\", \"kuai\", \"kuan\", \"kuang\", \"kui\", \"kun\", \"kuo\", \"la\", \"lai\",\n" +
                "            \"lan\", \"lang\", \"lao\", \"le\", \"lei\", \"leng\", \"li\", \"lia\", \"lian\",\n" +
                "            \"liang\", \"liao\", \"lie\", \"lin\", \"ling\", \"liu\", \"long\", \"lou\", \"lu\",\n" +
                "            \"lv\", \"luan\", \"lue\", \"lun\", \"luo\", \"ma\", \"mai\", \"man\", \"mang\",\n" +
                "            \"mao\", \"me\", \"mei\", \"men\", \"meng\", \"mi\", \"mian\", \"miao\", \"mie\",\n" +
                "            \"min\", \"ming\", \"miu\", \"mo\", \"mou\", \"mu\", \"na\", \"nai\", \"nan\",\n" +
                "            \"nang\", \"nao\", \"ne\", \"nei\", \"nen\", \"neng\", \"ni\", \"nian\", \"niang\",\n" +
                "            \"niao\", \"nie\", \"nin\", \"ning\", \"niu\", \"nong\", \"nu\", \"nv\", \"nuan\",\n" +
                "            \"nue\", \"nuo\", \"o\", \"ou\", \"pa\", \"pai\", \"pan\", \"pang\", \"pao\", \"pei\",\n" +
                "            \"pen\", \"peng\", \"pi\", \"pian\", \"piao\", \"pie\", \"pin\", \"ping\", \"po\",\n" +
                "            \"pu\", \"qi\", \"qia\", \"qian\", \"qiang\", \"qiao\", \"qie\", \"qin\", \"qing\",\n" +
                "            \"qiong\", \"qiu\", \"qu\", \"quan\", \"que\", \"qun\", \"ran\", \"rang\", \"rao\",\n" +
                "            \"re\", \"ren\", \"reng\", \"ri\", \"rong\", \"rou\", \"ru\", \"ruan\", \"rui\",\n" +
                "            \"run\", \"ruo\", \"sa\", \"sai\", \"san\", \"sang\", \"sao\", \"se\", \"sen\",\n" +
                "            \"seng\", \"sha\", \"shai\", \"shan\", \"shang\", \"shao\", \"she\", \"shen\",\n" +
                "            \"sheng\", \"shi\", \"shou\", \"shu\", \"shua\", \"shuai\", \"shuan\", \"shuang\",\n" +
                "            \"shui\", \"shun\", \"shuo\", \"si\", \"song\", \"sou\", \"su\", \"suan\", \"sui\",\n" +
                "            \"sun\", \"suo\", \"ta\", \"tai\", \"tan\", \"tang\", \"tao\", \"te\", \"teng\",\n" +
                "            \"ti\", \"tian\", \"tiao\", \"tie\", \"ting\", \"tong\", \"tou\", \"tu\", \"tuan\",\n" +
                "            \"tui\", \"tun\", \"tuo\", \"wa\", \"wai\", \"wan\", \"wang\", \"wei\", \"wen\",\n" +
                "            \"weng\", \"wo\", \"wu\", \"xi\", \"xia\", \"xian\", \"xiang\", \"xiao\", \"xie\",\n" +
                "            \"xin\", \"xing\", \"xiong\", \"xiu\", \"xu\", \"xuan\", \"xue\", \"xun\", \"ya\",\n" +
                "            \"yan\", \"yang\", \"yao\", \"ye\", \"yi\", \"yin\", \"ying\", \"yo\", \"yong\",\n" +
                "            \"you\", \"yu\", \"yuan\", \"yue\", \"yun\", \"za\", \"zai\", \"zan\", \"zang\",\n" +
                "            \"zao\", \"ze\", \"zei\", \"zen\", \"zeng\", \"zha\", \"zhai\", \"zhan\", \"zhang\",\n" +
                "            \"zhao\", \"zhe\", \"zhen\", \"zheng\", \"zhi\", \"zhong\", \"zhou\", \"zhu\",\n" +
                "            \"zhua\", \"zhuai\", \"zhuan\", \"zhuang\", \"zhui\", \"zhun\", \"zhuo\", \"zi\",\n" +
                "            \"zong\", \"zou\", \"zu\", \"zuan\", \"zui\", \"zun\", \"zuo\"};\n" +
                "    private StringBuilder buffer;\n" +
                "    private String resource;\n" +
                "    private static CharacterParser characterParser = new CharacterParser();\n" +
                "\n" +
                "    public static CharacterParser getInstance() {\n" +
                "        return characterParser;\n" +
                "    }\n" +
                "\n" +
                "    public String getResource() {\n" +
                "        return resource;\n" +
                "    }\n" +
                "\n" +
                "    public void setResource(String resource) {\n" +
                "        this.resource = resource;\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 汉字转成ASCII码 * * @param chs * @return\n" +
                "     */\n" +
                "    private int getChsAscii(String chs) {\n" +
                "        int asc = 0;\n" +
                "        try {\n" +
                "            byte[] bytes = chs.getBytes(\"gb2312\");\n" +
                "            if (bytes == null || bytes.length > 2 || bytes.length <= 0) {\n" +
                "                throw new RuntimeException(\"illegal resource string\");\n" +
                "            }\n" +
                "            if (bytes.length == 1) {\n" +
                "                asc = bytes[0];\n" +
                "            }\n" +
                "            if (bytes.length == 2) {\n" +
                "                int hightByte = 256 + bytes[0];\n" +
                "                int lowByte = 256 + bytes[1];\n" +
                "                asc = (256 * hightByte + lowByte) - 256 * 256;\n" +
                "            }\n" +
                "        } catch (Exception e) {\n" +
                "            System.out\n" +
                "                    .println(\"ERROR:ChineseSpelling.class-getChsAscii(String chs)\"\n" +
                "                            + e);\n" +
                "        }\n" +
                "        return asc;\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 单字解析 * * @param str * @return\n" +
                "     */\n" +
                "    public String convert(String str) {\n" +
                "        String result = null;\n" +
                "        int ascii = getChsAscii(str);\n" +
                "        if (ascii > 0 && ascii < 160) {\n" +
                "            result = String.valueOf((char) ascii);\n" +
                "        } else {\n" +
                "            for (int i = (pyvalue.length - 1); i >= 0; i--) {\n" +
                "                if (pyvalue[i] <= ascii) {\n" +
                "                    result = pystr[i];\n" +
                "                    break;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return result;\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 词组解析 * * @param chs * @return\n" +
                "     */\n" +
                "    public String getSelling(String chs) {\n" +
                "        String key, value;\n" +
                "        buffer = new StringBuilder();\n" +
                "\n" +
                "        if (chs!=null && chs.length()>0){\n" +
                "            for (int i = 0; i < chs.length(); i++) {\n" +
                "                key = chs.substring(i, i + 1);\n" +
                "                if (key.getBytes().length >= 2) {\n" +
                "                    value = (String) convert(key);\n" +
                "                    if (value == null) {\n" +
                "                        value = chs;\n" +
                "                    }\n" +
                "                } else {\n" +
                "                    value = key;\n" +
                "                }\n" +
                "                buffer.append(value);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        return buffer.toString();\n" +
                "    }\n" +
                "\n" +
                "    public String getSpelling() {\n" +
                "        return this.getSelling(this.getResource());\n" +
                "    }\n" +
                "\n" +
                "}\n", sideBarPath, "CharacterParser.java");
        FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ".ui.widget.sidebar;\n" +
                "\n" +
                "import java.util.Comparator;\n" +
                "\n" +
                "/**\n" +
                " * @author\n" +
                " */\n" +
                "public class PinyinComparator implements Comparator<SortModel> {\n" +
                "\n" +
                "    @Override\n" +
                "    public int compare(SortModel lhs, SortModel rhs) {\n" +
                "        if (lhs.getSortLetters().equals(\"@\")\n" +
                "                || rhs.getSortLetters().equals(\"#\")) {\n" +
                "            if (lhs.isNumber() && rhs.isNumber()) {\n" +
                "                if (lhs.isMac() && rhs.isMac()) {\n" +
                "                    int a = 0;\n" +
                "                    int b = 0;\n" +
                "                    try {\n" +
                "                        a = Integer.parseInt(lhs.getFullName());\n" +
                "                    } catch (Exception e) {\n" +
                "                         e.printStackTrace();\n" +
                "                        a=0;\n" +
                "                    }\n" +
                "                    try {\n" +
                "                        b = Integer.parseInt(rhs.getFullName());\n" +
                "                    } catch (Exception e) {\n" +
                "                         e.printStackTrace();\n" +
                "                        b=0;\n" +
                "                    }\n" +
                "//                    Log.e(\"ttt\", \"isMac \" + a + \", \" + b);\n" +
                "                    if (a > b)\n" +
                "                        return 1;\n" +
                "                    else if (a == b) {\n" +
                "                        return 0;\n" +
                "                    } else {\n" +
                "                        return -1;\n" +
                "                    }\n" +
                "                }\n" +
                "                int c = lhs.getFullName().compareTo(rhs.getFullName());\n" +
                "                if (c > 0) {\n" +
                "                    return 1;\n" +
                "                } else if (c == 0) {\n" +
                "                    return  0;\n" +
                "                } else {\n" +
                "                    return -1;\n" +
                "                }\n" +
                "            } else {\n" +
                "                return -1;\n" +
                "            }\n" +
                "        } else if (lhs.getSortLetters().equals(\"#\")\n" +
                "                || rhs.getSortLetters().equals(\"@\")) {\n" +
                "            return 1;\n" +
                "        } else {\n" +
                "            int c = lhs.getSortLetters().compareTo(rhs.getSortLetters());\n" +
                "            if (c > 0) {\n" +
                "                return 1;\n" +
                "            } else if (c == 0) {\n" +
                "                return  0;\n" +
                "            } else {\n" +
                "                return -1;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}\n", sideBarPath, "PinyinComparator.java");

        FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ".ui.widget.sidebar;\n" +
                "\n" +
                "import android.content.Context;\n" +
                "import android.graphics.Bitmap;\n" +
                "import android.graphics.Canvas;\n" +
                "import android.graphics.Color;\n" +
                "import android.graphics.Paint;\n" +
                "import android.util.AttributeSet;\n" +
                "import android.view.MotionEvent;\n" +
                "import android.view.View;\n" +
                "\n" +
                "import " + app.getPackageName() + ".R;\n" +
                "\n" +
                "\n" +
                "/**\n" +
                " * Created by zz on 2016/5/12.\n" +
                " */\n" +
                "public class SideBar extends View {\n" +
                "\n" +
                "    public static final String TAG = SideBar.class.getSimpleName();\n" +
                "\n" +
                "    private String[] letters;\n" +
                "\n" +
                "    private OnLetterTouchListener letterTouchListener;\n" +
                "\n" +
                "    private float itemHeight = -1;\n" +
                "\n" +
                "    private Paint paint;\n" +
                "\n" +
                "    // 26个字母\n" +
                "    public static String[] b = {\"A\", \"B\", \"C\", \"D\", \"E\", \"F\", \"G\", \"H\", \"I\",\n" +
                "            \"J\", \"K\", \"L\", \"M\", \"N\", \"O\", \"P\", \"Q\", \"R\", \"S\", \"T\", \"U\", \"V\",\n" +
                "            \"W\", \"X\", \"Y\", \"Z\", \"#\"};\n" +
                "\n" +
                "    private int position = -1;\n" +
                "\n" +
                "    public interface OnLetterTouchListener {\n" +
                "        void onLetterTouch(String letter, int position);\n" +
                "\n" +
                "        void onActionUp();\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    public SideBar(Context context) {\n" +
                "        super(context);\n" +
                "\n" +
                "        init(context);\n" +
                "    }\n" +
                "\n" +
                "    public SideBar(Context context, AttributeSet attrs) {\n" +
                "        super(context, attrs);\n" +
                "\n" +
                "        init(context);\n" +
                "    }\n" +
                "\n" +
                "    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {\n" +
                "        super(context, attrs, defStyleAttr);\n" +
                "\n" +
                "        init(context);\n" +
                "    }\n" +
                "\n" +
                "    private void init(Context context) {\n" +
                "        paint = new Paint();\n" +
                "        paint.setColor(getResources().getColor(R.color.colorGrayB));\n" +
                "        paint.setFlags(Paint.ANTI_ALIAS_FLAG);\n" +
                "\n" +
                "        setShowString(b);\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    @Override\n" +
                "    protected void onDraw(Canvas canvas) {\n" +
                "        super.onDraw(canvas);\n" +
                "\n" +
                "        if (letters == null) {\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        if (itemHeight == -1) {\n" +
                "            itemHeight = getHeight() / letters.length;\n" +
                "            paint.setTextSize(itemHeight - 4);\n" +
                "        }\n" +
                "        float widthCenter = getMeasuredWidth() / 2.0f;\n" +
                "        for (int i = 0; i < letters.length; i++) {\n" +
                "            if (position == i) {\n" +
                "                paint.setColor(getResources().getColor(R.color.colorPrimary));\n" +
                "            } else {\n" +
                "                paint.setColor(getResources().getColor(R.color.colorGrayB));\n" +
                "            }\n" +
                "            canvas.drawText(letters[i], widthCenter - paint.measureText(letters[i]) / 2, itemHeight * i + itemHeight, paint);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean onTouchEvent(MotionEvent event) {\n" +
                "        super.onTouchEvent(event);\n" +
                "        if (letterTouchListener == null || letters == null) {\n" +
                "            return false;\n" +
                "        }\n" +
                "\n" +
                "        switch (event.getAction()) {\n" +
                "            case MotionEvent.ACTION_DOWN:\n" +
                "            case MotionEvent.ACTION_MOVE:\n" +
                "                position = (int) (event.getY() / itemHeight);\n" +
                "                if (position >= 0 && position < letters.length) {\n" +
                "                    invalidate();\n" +
                "                    letterTouchListener.onLetterTouch(letters[position], position);\n" +
                "                }\n" +
                "                return true;\n" +
                "            case MotionEvent.ACTION_OUTSIDE:\n" +
                "            case MotionEvent.ACTION_UP:\n" +
                "                position = -1;\n" +
                "                invalidate();\n" +
                "                letterTouchListener.onActionUp();\n" +
                "                return true;\n" +
                "        }\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    public void setShowString(String[] letters) {\n" +
                "        this.letters = letters;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    public void setLetterTouchListener(OnLetterTouchListener letterTouchListener) {\n" +
                "        this.letterTouchListener = letterTouchListener;\n" +
                "    }\n" +
                "}\n", sideBarPath, "SideBar.java");
        FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ".ui.widget.sidebar;\n" +
                "\n" +
                "public class SortModel {\n" +
                "\n" +
                "    private boolean isMac;\n" +
                "    private boolean isNumber;\n" +
                "    private String fullName;\n" +
                "    private String name;//显示的数据\n" +
                "    private String sortLetters;//显示数据拼音的首字母\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "\n" +
                "    public void setName(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    public String getSortLetters() {\n" +
                "        return sortLetters;\n" +
                "    }\n" +
                "\n" +
                "    public void setSortLetters(String sortLetters) {\n" +
                "        this.sortLetters = sortLetters;\n" +
                "    }\n" +
                "\n" +
                "    public boolean isMac() {\n" +
                "        return isMac;\n" +
                "    }\n" +
                "\n" +
                "    public void setMac(boolean mac) {\n" +
                "        isMac = mac;\n" +
                "    }\n" +
                "\n" +
                "    public boolean isNumber() {\n" +
                "        return isNumber;\n" +
                "    }\n" +
                "\n" +
                "    public void setNumber(boolean number) {\n" +
                "        isNumber = number;\n" +
                "    }\n" +
                "\n" +
                "    public String getFullName() {\n" +
                "        return fullName;\n" +
                "    }\n" +
                "\n" +
                "    public void setFullName(String fullName) {\n" +
                "        this.fullName = fullName;\n" +
                "    }\n" +
                "}\n", sideBarPath, "SortModel.java");

    }

    private void generateEmptyActJavaAndLayout(String entityPath, String adapterPath, String logoName, String layoutPath, String javaPath, ActivityEntity activityEntity, ApplicationEntity app, StringBuilder sbStrings) throws IOException {
        String layoutName = "";
        boolean isNotFirst = false;
        for (int i = 0; i < activityEntity.getName().length(); i++) {
            char c = activityEntity.getName().charAt(i);
            if (c >= 'A' && c <= 'Z') {
                if (isNotFirst) {
                    layoutName += "_";
                }
                isNotFirst = true;
            }
            layoutName += c;
        }

        layoutName = layoutName.replace("activity_", "").replace("_activity", "").replace("__", "_").toLowerCase();
        final String realLayoutName = "activity_" + layoutName.toLowerCase();

        /*layout*/
        StringBuilder sbLayout = new StringBuilder();
        sbLayout.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\"\n" +
                "    android:orientation=\"vertical\">\n");

        /*java*/
        StringBuilder sbJava = new StringBuilder();
        StringBuilder sbImp = new StringBuilder();
        sbImp.append(
                "\nimport android.os.Bundle;\n" +
                        "import android.support.annotation.Nullable;\n" +
                        "import android.content.Intent;\n" +
                        "import android.view.View;\n" +
                        "import android.view.ViewGroup;\n" +
                        "import android.view.WindowManager;\n" +
                        "import android.widget.Button;\n" +
                        "import android.widget.CheckBox;\n" +
                        "import android.widget.EditText;\n" +
                        "import android.widget.ImageView;\n" +
                        "import android.widget.TextView;\n" +
                        "import android.widget.LinearLayout;\n" +
                        "import java.util.ArrayList;\n" +
                        "import java.util.List;\n" +
                        "\n" +
                        "import " + app.getPackageName() + ".R;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.act.BaseActivity;\n" +
                        "import " + app.getPackageName() + ".common.api.Api;\n" +
                        "import " + app.getPackageName() + ".common.api.entity.*;\n" +
                        "import rx.Subscriber;\n" +
                        "import zhouzhuo810.me.zzandframe.common.rx.RxHelper;\n" +
                        "import zhouzhuo810.me.zzandframe.common.utils.ToastUtils;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.widget.MarkView;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.widget.TitleBar;\n");


        StringBuilder sbDef = new StringBuilder();
        StringBuilder sbInit = new StringBuilder();
        StringBuilder sbEvent = new StringBuilder();
        StringBuilder sbEditInfo = new StringBuilder();
        StringBuilder sbMethods = new StringBuilder();
        StringBuilder sbData = new StringBuilder();

        fillWidget(adapterPath, logoName, app, activityEntity, layoutName, layoutPath, javaPath, "0", sbStrings, sbLayout, sbImp, sbJava, sbDef, sbInit, sbData, sbEvent, sbEditInfo, sbMethods);

        sbJava.append("package " + app.getPackageName() + ".ui.act;\n" +
                "\n")
                .append(sbImp.toString())
                .append("\n" +
                        "/**\n" +
                        " *\n" +
                        " * Created by admin on 2017/8/27.\n" +
                        " */\n" +
                        "public class " + activityEntity.getName() + " extends BaseActivity {\n")
                .append(sbDef.toString())
                .append("\n\n    @Override\n" +
                        "    public int getLayoutId() {\n");
        if (activityEntity.getFullScreen() != null && activityEntity.getFullScreen()) {
            sbJava.append("        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏\n");
        }
        sbJava.append("        return R.layout." + realLayoutName + ";\n" +
                "    }\n")
                .append("    @Override\n" +
                        "    public void initView() {\n")
                .append(sbInit.toString())
                .append("\n    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void initData() {\n" +
                        sbData.toString() +
                        "\n    }\n")
                .append("\n" +
                        "    @Override\n" +
                        "    public void initEvent() {\n")
                .append(sbEvent.toString())
                .append("\n    }\n");

        sbJava.append(sbMethods.toString());

        sbJava.append("\n" +
                "    @Override\n" +
                "    public void resume() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "       public void pause() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void destroy() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void saveState(Bundle bundle) {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void restoreState(@Nullable Bundle bundle) {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean defaultBack() {\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "}\n");
        sbLayout.append("\n" +
                "</LinearLayout>");


        FileUtils.saveFileToPathWithName(sbLayout.toString(), layoutPath, realLayoutName + ".xml");
        FileUtils.saveFileToPathWithName(sbJava.toString(), javaPath + File.separator + "ui" + File.separator + "act", activityEntity.getName() + ".java");


    }


    private void fillFgmWidget(String adapterPath, String logoName, ApplicationEntity app, ActivityEntity activityEntity, FragmentEntity fragmentEntity, String layoutName, String layoutPath, String javaPath,
                               String pid, StringBuilder sbStrings, StringBuilder sbLayout, StringBuilder sbImp, StringBuilder sbJava,
                               StringBuilder sbDef, StringBuilder sbInit, StringBuilder sbData, StringBuilder sbEvent, StringBuilder sbEditInfo, StringBuilder sbMethods) throws IOException {

        List<WidgetEntity> widgetEntities = mWidgetService.executeCriteria(
                new Criterion[]{
                        Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                        Restrictions.eq("pid", pid),
                        Restrictions.eq("relativeId", fragmentEntity.getId())
                }, Order.asc("createTime")
        );

        if (widgetEntities != null && widgetEntities.size() > 0) {
            for (int i = 0; i < widgetEntities.size(); i++) {
                WidgetEntity widgetEntity = widgetEntities.get(i);
                int width = widgetEntity.getWidth();
                int height = widgetEntity.getHeight();
                String widthString = "wrap_content";
                switch (width) {
                    case -2:
                        widthString = "wrap_content";
                        break;
                    case -1:
                        widthString = "match_parent";
                        break;
                    default:
                        widthString = width + "px";
                        break;
                }
                String heightString = "wrap_content";
                switch (height) {
                    case -2:
                        heightString = "wrap_content";
                        break;
                    case -1:
                        heightString = "match_parent";
                        break;
                    default:
                        heightString = height + "px";
                        break;
                }
                String actions = genearteActions(widgetEntity.getId(), "0", true, app.getPackageName(), "", sbStrings, sbImp, sbData);
                switch (widgetEntity.getType()) {
                    case WidgetEntity.TYPE_TITLE_BAR:
                        sbDef.append("\n    private TitleBar titleBar;");
                        sbInit.append("\n        titleBar = (TitleBar) rootView.findViewById(R.id.title_bar);");
                        sbEvent.append("\n        titleBar.setOnTitleClickListener(new TitleBar.OnTitleClick() {\n" +
                                "            @Override\n" +
                                "            public void onLeftClick(ImageView imageView, MarkView markView, TextView textView) {\n" +
                                "                getBaseAct().closeAct();\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onTitleClick(TextView textView) {\n" +
                                "\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onRightClick(ImageView imageView, MarkView markView, TextView textView) {\n" +
                                "\n" + actions +
                                "\n            }\n" +
                                "        });\n");
                        if (!sbStrings.toString().contains("\"" + layoutName + "_text\"")) {
                            sbStrings.append("    <string name=\"" + layoutName + "_text\">" + fragmentEntity.getTitle() + "</string>\n");
                        }
                        sbLayout.append("\n    <zhouzhuo810.me.zzandframe.ui.widget.TitleBar\n" +
                                "        android:id=\"@+id/title_bar\"\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"@dimen/title_height\"\n" +
                                "        android:background=\"@color/colorPrimary\"\n" +
                                "        app:ttb_showLeftLayout=\"" + widgetEntity.getShowLeftTitleLayout() + "\"\n" +
                                "        app:ttb_showRightLayout=\"" + widgetEntity.getShowRightTitleLayout() + "\"\n" +
                                "        app:ttb_showLeftText=\"" + widgetEntity.getShowLeftTitleText() + "\"\n" +
                                "        app:ttb_leftImg=\"@drawable/back\"\n" +
                                "        app:ttb_textColorAll=\"@color/colorWhite\"\n" +
                                "        app:ttb_textSizeTitle=\"@dimen/title_text_size\"\n" +
                                "        app:ttb_titleText=\"@string/" + layoutName + "_text\" />");
                        break;
                    case WidgetEntity.TYPE_SETTING_ITEM:
                        String varName = generateVarName("ll", widgetEntity.getResId());
                        sbDef.append("\n    private LinearLayout " + varName + ";");
                        sbInit.append("\n        " + varName + " = (LinearLayout) rootView.findViewById(R.id.ll_" + widgetEntity.getResId() + ");");
                        sbStrings.append("\n    <string name=\"si_" + widgetEntity.getResId() + "_text\">" + widgetEntity.getTitle() + "</string>");
                        sbLayout.append("\n            <LinearLayout\n" +
                                "                android:id=\"@+id/ll_" + widgetEntity.getResId() + "\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                (widgetEntity.getMarginTop() == 0 ? "" : "                android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "                android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "                android:layout_height=\"@dimen/setting_item_height\"\n" +
                                "                android:background=\"@drawable/setting_item_bg_selector\"\n" +
                                "                android:clickable=\"true\"\n" +
                                "                android:gravity=\"center_vertical\"\n" +
                                "                android:orientation=\"horizontal\">\n" +
                                "\n" +
                                "                <ImageView\n" +
                                "                    android:layout_width=\"70px\"\n" +
                                "                    android:layout_height=\"70px\"\n" +
                                "                    android:layout_marginLeft=\"40px\"\n" +
                                "                    android:visibility=\"" + (widgetEntity.getShowLeftTitleImg() ? "visible" : "gone") + "\"\n" +
                                "                    android:src=\"@mipmap/" + logoName + "\" />\n" +
                                "\n" +
                                "                <TextView\n" +
                                "                    android:layout_width=\"0dp\"\n" +
                                "                    android:layout_height=\"wrap_content\"\n" +
                                "                    android:layout_weight=\"1\"\n" +
                                "                    android:layout_marginLeft=\"40px\"\n" +
                                "                    android:text=\"@string/si_" + widgetEntity.getResId() + "_text\"\n" +
                                "                    android:textColor=\"@color/colorText\"\n" +
                                "                    android:textSize=\"40px\" />\n" +
                                "\n" +
                                "                <ImageView\n" +
                                "                    android:layout_width=\"40px\"\n" +
                                "                    android:layout_height=\"40px\"\n" +
                                "                    android:layout_marginRight=\"40px\"\n" +
                                "                    android:src=\"@drawable/more\" />\n" +
                                "            </LinearLayout>\n" +
                                "\n" +
                                "            <View\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"1px\"\n" +
                                "                android:layout_marginLeft=\"40px\" />\n");
                        sbEvent.append("\n        " + varName + ".setOnClickListener(new View.OnClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onClick(View v) {\n" +
                                "                \n" + actions +
                                "\n            }\n" +
                                "        });");
                        break;
                    case WidgetEntity.TYPE_INFO_ITEM:
                        String varNameInfo = generateVarName("tv", widgetEntity.getResId());
                        sbDef.append("\n    private TextView " + varNameInfo + ";");
                        sbInit.append("\n        " + varNameInfo + " = (TextView) rootView.findViewById(R.id.tv_" + widgetEntity.getResId() + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        sbLayout.append("\n    <LinearLayout\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"wrap_content\"\n" +
                                "        android:background=\"@color/colorWhite\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:gravity=\"center_vertical\"\n" +
                                "        android:minHeight=\"130px\"\n" +
                                "        android:orientation=\"horizontal\">\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:layout_width=\"280px\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_marginLeft=\"40px\"\n" +
                                "            android:gravity=\"center|left\"\n" +
                                "            android:text=\"@string/" + widgetEntity.getResId() + "_text\"\n" +
                                "            android:textColor=\"#415868\"\n" +
                                "            android:textSize=\"44px\" />\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:id=\"@+id/tv_" + widgetEntity.getResId() + "\"\n" +
                                "            android:layout_width=\"0dp\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_marginLeft=\"30px\"\n" +
                                "            android:layout_marginRight=\"30px\"\n" +
                                "            android:layout_weight=\"1\"\n" +
                                "            android:background=\"@null\"\n" +
                                "            android:gravity=\"right|center_vertical\"\n" +
                                "            android:textColor=\"@color/colorBlack\"\n" +
                                "            android:textSize=\"44px\" />\n" +
                                "    </LinearLayout>\n" +
                                "\n" +
                                "    <View\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"1px\"\n" +
                                "        android:layout_marginLeft=\"30px\"\n" +
                                "        android:background=\"@color/colorGrayBg\" />");
                        break;
                    case WidgetEntity.TYPE_TITLE_EDIT_ITEM:
                        String varNameEt = generateVarName("et", widgetEntity.getResId());
                        String varNameIvClear = generateVarName("ivClear", widgetEntity.getResId());
                        sbDef.append("\n    private EditText " + varNameEt + ";");
                        sbDef.append("\n    private ImageView " + varNameIvClear + ";");
                        sbInit.append("\n        " + varNameEt + " = (EditText) rootView.findViewById(R.id.et_" + widgetEntity.getResId() + ");");
                        sbInit.append("\n        " + varNameIvClear + " = (ImageView) rootView.findViewById(R.id.iv_clear_" + widgetEntity.getResId() + ");");
                        sbEvent.append("\n        setEditListener(" + varNameEt + ", " + varNameIvClear + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_hint_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_hint_text\">请输入" + widgetEntity.getTitle() + "</string>\n");
                        }
                        sbEditInfo.append("\n        String " + widgetEntity.getResId() + " = et_" + widgetEntity.getResId() + ".getText().toString().trim();");
                        sbLayout.append("\n    <LinearLayout\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"wrap_content\"\n" +
                                "        android:background=\"@color/colorWhite\"\n" +
                                "        android:gravity=\"center_vertical\"\n" +
                                "        android:minHeight=\"130px\"\n" +
                                "        android:orientation=\"horizontal\">\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:layout_width=\"280px\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_marginLeft=\"40px\"\n" +
                                "            android:gravity=\"center|left\"\n" +
                                "            android:text=\"@string/" + widgetEntity.getResId() + "_text\"\n" +
                                "            android:textColor=\"#415868\"\n" +
                                "            android:textSize=\"44px\" />\n" +
                                "\n" +
                                "        <EditText\n" +
                                "            android:id=\"@+id/et_" + widgetEntity.getResId() + "\"\n" +
                                "            android:layout_width=\"0dp\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_marginLeft=\"30px\"\n" +
                                "            android:layout_marginRight=\"30px\"\n" +
                                "            android:layout_weight=\"1\"\n" +
                                "            android:background=\"@null\"\n" +
                                "            android:gravity=\"right|center_vertical\"\n" +
                                "            android:hint=\"@string/" + widgetEntity.getResId() + "_hint_text\"\n" +
                                "            android:textColor=\"@color/colorBlack\"\n" +
                                "            android:textSize=\"44px\" />\n" +
                                "\n" +
                                "        <ImageView\n" +
                                "            android:id=\"@+id/iv_clear_" + widgetEntity.getResId() + "\"\n" +
                                "            android:layout_width=\"60px\"\n" +
                                "            android:layout_height=\"60px\"\n" +
                                "            android:layout_marginRight=\"30px\"\n" +
                                "            android:src=\"@drawable/clear\"\n" +
                                "            android:visibility=\"gone\" />\n" +
                                "    </LinearLayout>\n" +
                                "\n" +
                                "    <View\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"1px\"\n" +
                                "        android:layout_marginLeft=\"30px\"\n" +
                                "        android:background=\"@color/colorGrayBg\" />");
                        break;
                    case WidgetEntity.TYPE_UNDERLINE_EDIT_ITEM:
                        String uderLineEtName = generateVarName("et", widgetEntity.getResId());
                        String uderLineIvName = generateVarName("ivClear", widgetEntity.getResId());
                        sbDef.append("\n    private EditText " + uderLineEtName + ";");
                        sbDef.append("\n    private ImageView " + uderLineIvName + ";");
                        sbInit.append("\n        " + uderLineEtName + " = (EditText) findViewById(R.id.et_" + widgetEntity.getResId() + ");");
                        sbInit.append("\n        " + uderLineIvName + " = (ImageView) findViewById(R.id.iv_clear_" + widgetEntity.getResId() + ");");
                        sbEvent.append("\n        setEditListener(" + uderLineEtName + ", " + uderLineIvName + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_hint_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_hint_text\">请输入" + widgetEntity.getTitle() + "</string>\n");
                        }
                        sbEditInfo.append("\n        String " + widgetEntity.getResId() + " = " + uderLineEtName + ".getText().toString().trim();");
                        sbLayout.append("\n" +
                                "        <RelativeLayout\n" +
                                "            android:layout_width=\"match_parent\"\n" +
                                "            android:layout_height=\"120px\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "            android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "            android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "            android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                ">\n" +
                                "            <EditText\n" +
                                "                android:id=\"@+id/et_" + widgetEntity.getResId() + "\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"match_parent\"\n" +
                                "                android:background=\"@drawable/et_bg_line\"\n" +
                                "                android:hint=\"@string/" + widgetEntity.getResId() + "_hint_text\"\n" +
                                "                android:paddingLeft=\"20px\"\n" +
                                "                android:paddingRight=\"120px\"\n" +
                                "                android:textColor=\"@color/colorBlack\"\n" +
                                "                android:textColorHint=\"@color/colorMain\"\n" +
                                "                android:textSize=\"40px\" />\n" +
                                "\n" +
                                "            <ImageView\n" +
                                "                android:id=\"@+id/iv_clear_" + widgetEntity.getResId() + "\"\n" +
                                "                android:layout_width=\"60px\"\n" +
                                "                android:layout_height=\"60px\"\n" +
                                "                android:layout_alignParentRight=\"true\"\n" +
                                "                android:layout_centerVertical=\"true\"\n" +
                                "                android:layout_marginRight=\"30px\"\n" +
                                "                android:src=\"@drawable/clear\"\n" +
                                "                android:visibility=\"gone\" />\n" +
                                "        </RelativeLayout>\n");
                        break;
                    case WidgetEntity.TYPE_SUBMIT_BTN_ITEM:
                        String submitBtnName = generateVarName("btn", widgetEntity.getResId());
                        sbDef.append("\n    private Button " + submitBtnName + ";");
                        sbInit.append("\n        btn_" + submitBtnName + " = (Button) rootView.findViewById(R.id.btn_" + widgetEntity.getResId() + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_btn_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_btn_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        sbLayout.append("\n    <Button\n" +
                                "        android:id=\"@+id/btn_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"120px\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:background=\"@drawable/btn_save_selector\"\n" +
                                "        android:text=\"@string/" + widgetEntity.getResId() + "_btn_text\"\n" +
                                "        android:textColor=\"#fff\"\n" +
                                "        android:textSize=\"@dimen/submit_btn_text_size\" />");
                        sbEvent.append("        " + submitBtnName + ".setOnClickListener(new View.OnClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onClick(View v) {\n" + actions + "\n"
                        );
                        sbEvent.append("            }\n" +
                                "        });");
                        break;
                    case WidgetEntity.TYPE_EXIT_BTN_ITEM:
                        String exitBtnName = generateVarName("btn", widgetEntity.getResId());
                        sbDef.append("\n    private Button " + exitBtnName + ";");
                        sbInit.append("\n        " + exitBtnName + " = (Button) rootView.findViewById(R.id.btn_" + widgetEntity.getResId() + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        sbLayout.append("\n    <Button\n" +
                                "        android:id=\"@+id/btn_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"120px\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:background=\"@drawable/btn_exit_selector\"\n" +
                                "        android:text=\"@string/" + widgetEntity.getResId() + "_text\"\n" +
                                "        android:textColor=\"#fff\"\n" +
                                "        android:textSize=\"@dimen/submit_btn_text_size\" />");
                        sbEvent.append("\n        " + exitBtnName + ".setOnClickListener(new View.OnClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onClick(View v) {\n" + actions + "\n"
                        );
                        sbEvent.append("            }\n" +
                                "        });");
                        break;
                    case WidgetEntity.TYPE_RV:
                        //fgm rv
                        String adapterName = generateVarName("", widgetEntity.getResId()) + "RvAdapter";
                        sbImp.append("\nimport android.support.v4.widget.SwipeRefreshLayout;")
                                .append("\nimport android.support.v7.widget.RecyclerView;")
                                .append("\nimport android.support.v7.widget.LinearLayoutManager;")
                                .append("\nimport zhouzhuo810.me.zzandframe.ui.adapter.RvAutoBaseAdapter;")
                                .append("\nimport android.support.v4.widget.SwipeRefreshLayout;")
                                .append("\nimport " + app.getPackageName() + ".ui.adapter." + adapterName + ";");
                        sbDef.append("\n    private SwipeRefreshLayout refresh;")
                                .append("\n    private RecyclerView rv;")
                                .append("\n    private " + adapterName + " adapter;")
                                .append("\n    private TextView tvNoData;");
                        sbInit.append("\n        refresh = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);")
                                .append("\n        rv = (RecyclerView) rootView.findViewById(R.id.rv);")
                                .append("\n        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager." + widgetEntity.getOrientation().toUpperCase() + ", false));")
                                .append("\n        tvNoData = (TextView) rootView.findViewById(R.id.tv_no_data);");
                        sbEvent.append("\n        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {\n" +
                                "            @Override\n" +
                                "            public void onRefresh() {\n" +
                                "                getData();\n" +
                                "            }\n" +
                                "        });\n");
                        sbEvent.append("\n        adapter.setOnItemLongClickListener(new RvAutoBaseAdapter.OnItemLongClickListener() {\n" +
                                "            @Override\n" +
                                "            public boolean onItemLongClick(View view, int position) {\n" +
                                "\n" +
                                "                return false;\n" +
                                "            }\n" +
                                "        });\n");
                        sbMethods.append("\n" +
                                "    private void getData() {\n" +
                                "\n" +
                                "        stopRefresh(refresh);\n" +
                                "    }\n");
                        sbData.append("        List<RvTestEntity> list = new ArrayList<>();\n" +
                                "        for (int i = 0; i < 10; i++) {\n" +
                                "            list.add(new RvTestEntity());\n" +
                                "        }\n" +
                                "        adapter = new " + adapterName + "(getActivity(), list);\n" +
                                "        rv.setAdapter(adapter);\n");
                        sbData.append("\n        startRefresh(refresh);\n" +
                                "        getData();\n");
                        sbLayout.append("\n    <RelativeLayout\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"match_parent\">\n" +
                                "\n" +
                                "        <android.support.v4.widget.SwipeRefreshLayout\n" +
                                "            android:id=\"@+id/refresh\"\n" +
                                "            android:layout_width=\"match_parent\"\n" +
                                "            android:layout_height=\"match_parent\">\n" +
                                "\n" +
                                "            <android.support.v7.widget.RecyclerView\n" +
                                "                android:id=\"@+id/rv\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"match_parent\" />\n" +
                                "        </android.support.v4.widget.SwipeRefreshLayout>\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:id=\"@+id/tv_no_data\"\n" +
                                "            android:layout_width=\"wrap_content\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_centerInParent=\"true\"\n" +
                                "            android:text=\"@string/no_data_text\"\n" +
                                "            android:textColor=\"@color/colorGrayB\"\n" +
                                "            android:textSize=\"40px\"\n" +
                                "            android:visibility=\"gone\" />\n" +
                                "    </RelativeLayout>\n");
                        generateItem(app, adapterPath, adapterName, logoName, widgetEntity.getId(), layoutPath, sbStrings);
                        break;
                    case WidgetEntity.TYPE_LV:
                        String adapterNameLv = generateVarName("", widgetEntity.getResId()) + "LvAdapter";
                        sbImp.append("\nimport android.support.v4.widget.SwipeRefreshLayout;")
                                .append("\nimport android.widget.ListView;")
                                .append("\nimport android.support.v7.widget.RecyclerView;")
                                .append("\nimport zhouzhuo810.me.zzandframe.ui.widget.ZzLvRefreshLayout;")
                                .append("\nimport " + app.getPackageName() + ".ui.adapter." + adapterNameLv + ";");
                        sbDef.append("\n    private ZzLvRefreshLayout refresh")
                                .append("\n    private ListView lv;")
                                .append("\n    private TextView tvNoData;");
                        sbInit.append("\n        refresh = (ZzLvRefreshLayout) rootView.findViewById(R.id.refresh);")
                                .append("\n        rv = (RecyclerView) rootView.findViewById(R.id.rv);")
                                .append("\n        tvNoData = (TextView) rootView.findViewById(R.id.tv_no_data);");
                        sbEvent.append("\n        titleBar.setOnTitleClickListener(new TitleBar.OnTitleClick() {\n" +
                                "            @Override\n" +
                                "            public void onLeftClick(ImageView imageView, MarkView markView, TextView textView) {\n" +
                                "                getBaseAct().closeAct();\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onTitleClick(TextView textView) {\n" +
                                "\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onRightClick(ImageView imageView, MarkView markView, TextView textView) {\n" +
                                "\n" +
                                "            }\n" +
                                "        });\n")
                                .append("        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {\n" +
                                        "            @Override\n" +
                                        "            public void onRefresh() {\n" +
                                        "                getData();\n" +
                                        "            }\n" +
                                        "        });");
                        sbMethods.append("\n" +
                                "    private void getData() {\n" +
                                "\n" +
                                "        stopRefresh(refresh);\n" +
                                "    }\n");
                        sbData.append("        List<RvTestEntity> list = new ArrayList<>();\n" +
                                "        for (int i = 0; i < 10; i++) {\n" +
                                "            list.add(new RvTestEntity());\n" +
                                "        }\n" +
                                "        " + adapterNameLv + " adapter = new " + adapterNameLv + "(getActivity(), list);\n" +
                                "        rv.setAdapter(adapter);\n");
                        sbData.append("\n        startRefresh(refresh);\n" +
                                "        getData();\n");
                        sbLayout.append("\n    <RelativeLayout\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"match_parent\">\n" +
                                "\n" +
                                "        <zhouzhuo810.me.zzandframe.ui.widget.ZzLvRefreshLayout\n" +
                                "            android:id=\"@+id/refresh\"\n" +
                                "            android:layout_width=\"match_parent\"\n" +
                                "            android:layout_height=\"wrap_content\">\n" +
                                "\n" +
                                "            <ListView\n" +
                                "                android:id=\"@+id/lv\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"match_parent\"\n" +
                                "                android:divider=\"@null\"\n" +
                                "                android:dividerHeight=\"0dp\"\n" +
                                "                android:listSelector=\"@color/colorTransparent\" />\n" +
                                "        </zhouzhuo810.me.zzandframe.ui.widget.ZzLvRefreshLayout>\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:id=\"@+id/tv_no_data\"\n" +
                                "            android:layout_width=\"wrap_content\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_centerInParent=\"true\"\n" +
                                "            android:text=\"@string/no_data_text\"\n" +
                                "            android:textColor=\"@color/colorGrayB\"\n" +
                                "            android:textSize=\"40px\"\n" +
                                "            android:visibility=\"gone\" />\n" +
                                "    </RelativeLayout>\n");
                        generateItem(app, adapterPath, adapterNameLv, logoName, widgetEntity.getId(), layoutPath, sbStrings);
                        break;
                    case WidgetEntity.TYPE_SCROLLABLE_LV:
                        String adapterNameSlv = generateVarName("", widgetEntity.getResId()) + "ScrollLvAdapter";
                        String scrollLvName = generateVarName("lv", widgetEntity.getResId());
                        sbImp.append("\nimport " + app.getPackageName() + ".ui.widget.ScrollableListView;");
                        sbDef.append("\n    private ScrollableListView " + scrollLvName + ";");
                        sbInit.append("\n        " + scrollLvName + " = (ScrollableListView) rootView.findViewById(R.id.lv_" + widgetEntity.getResId() + ");");
                        sbLayout.append("\n            <" + app.getPackageName() + ".ui.widget.ScrollableListView\n" +
                                "                android:id=\"@+id/lv_" + widgetEntity.getResId() + "\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"wrap_content\"\n" +
                                "                android:divider=\"@null\"\n" +
                                "                android:dividerHeight=\"0dp\"\n" +
                                "                android:listSelector=\"@color/colorTransparent\" />\n");
                        generateItem(app, adapterPath, adapterNameSlv, logoName, widgetEntity.getId(), layoutPath, sbStrings);
                        break;
                    case WidgetEntity.TYPE_EDIT_TEXT:
                        String etName = generateVarName("et", widgetEntity.getResId());
                        sbDef.append("\n    private EditText " + etName + ";");
                        sbInit.append("\n        " + etName + " = (EditText) rootView.findViewById(R.id.et_" + widgetEntity.getResId() + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_hint_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_hint_text\">请输入" + widgetEntity.getTitle() + "</string>\n");
                        }
                        sbLayout.append("\n    <EditText\n" +
                                "        android:id=\"@+id/et_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                (widgetEntity.getWeight() > 0 ? "        android:layout_weight=\"" + widgetEntity.getWeight() + "\"\n" : "") +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:background=\"@drawable/et_round_white_bg\"\n" +
                                "        android:hint=\"@string/" + widgetEntity.getResId() + "_hint_text\"\n" +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:gravity=\"" + widgetEntity.getGravity() + "\"\n" +
                                "        android:textColor=\"@color/colorBlack\"\n" +
                                "        android:textColorHint=\"@color/colorGrayB\"\n" +
                                "        android:textSize=\"40px\" />\n");
                        break;
                    case WidgetEntity.TYPE_LETTER_RV:
                        String testClazz = "RvTestResult";
                        String realClazz = "";
                        String entityPath = javaPath
                                + File.separator + "common"
                                + File.separator + "api"
                                + File.separator + "entity";
                        FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ".common.api.entity;\n" +
                                "\n" +
                                "import java.util.List;\n" +
                                "import " + app.getPackageName() + ".ui.widget.sidebar.SortModel;\n" +
                                "import zhouzhuo810.me.zzandframe.common.rule.SearchAble;\n" +
                                "\n" +
                                "/**\n" +
                                " * Created by zz on 2017/9/7.\n" +
                                " */\n" +
                                "public class " + testClazz + " {\n" +
                                "    private int code;\n" +
                                "    private String msg;\n" +
                                "    private List<DataEntity> data;\n" +
                                "\n" +
                                "    public int getCode() {\n" +
                                "        return code;\n" +
                                "    }\n" +
                                "\n" +
                                "    public void setCode(int code) {\n" +
                                "        this.code = code;\n" +
                                "    }\n" +
                                "\n" +
                                "    public String getMsg() {\n" +
                                "        return msg;\n" +
                                "    }\n" +
                                "\n" +
                                "    public void setMsg(String msg) {\n" +
                                "        this.msg = msg;\n" +
                                "    }\n" +
                                "\n" +
                                "    public List<DataEntity> getData() {\n" +
                                "        return data;\n" +
                                "    }\n" +
                                "\n" +
                                "    public void setData(List<DataEntity> data) {\n" +
                                "        this.data = data;\n" +
                                "    }\n" +
                                "\n" +
                                "    public static class DataEntity extends SortModel implements SearchAble {\n" +
                                "\n" +
                                "        @Override\n" +
                                "        public String toSearch() {\n" +
                                "            return \"\";\n" +
                                "        }\n" +
                                "    }\n" +
                                "}\n", entityPath, testClazz + ".java");
                        StringBuilder sbAdapter = new StringBuilder();
                        sbImp.append("import " + app.getPackageName() + ".ui.adapter." + activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter;\n");
                        sbAdapter.append("package " + app.getPackageName() + ".ui.adapter;\n" +
                                "\n" +
                                "import android.content.Context;\n" +
                                "import android.widget.SectionIndexer;\n" +
                                "\n" +
                                "import java.util.List;\n" +
                                "import java.util.ArrayList;\n" +
                                "\n" +
                                "import " + app.getPackageName() + ".R;\n" +
                                "import " + app.getPackageName() + ".common.api.entity." + testClazz + ";\n" +
                                (realClazz.length() > 0 ? "import " + app.getPackageName() + ".common.api.entity." + realClazz + ";\n" : "") +
                                "import zhouzhuo810.me.zzandframe.common.rule.ISearch;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.adapter.RvAutoBaseAdapter;\n" +
                                "\n" +
                                "/**\n" +
                                " * Created by zz on 2017/8/28.\n" +
                                " */\n" +
                                "public class " + activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter extends RvAutoBaseAdapter<" + testClazz + ".DataEntity> implements ISearch<" + testClazz + ".DataEntity>,SectionIndexer {\n" +
                                "\n" +
                                "    public " + activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter(Context context, List<" + testClazz + ".DataEntity> data) {\n" +
                                "        super(context, data);\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    protected int getLayoutId(int type) {\n" +
                                "        return R.layout.list_item_" + layoutName + ";\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    protected void fillData(ViewHolder viewHolder, " + testClazz + ".DataEntity dataEntity, int position) {\n" +
                                "        \n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public void startSearch(String s) {\n" +
                                "        List<" + testClazz + ".DataEntity> msgs = new ArrayList<>();\n" +
                                "        for (" + testClazz + ".DataEntity mData : data) {\n" +
                                "            if (mData.toSearch().contains(s)) {\n" +
                                "                msgs.add(mData);\n" +
                                "            }\n" +
                                "        }\n" +
                                "        updateAll(msgs);\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public void cancelSearch(List<" + testClazz + ".DataEntity> list) {\n" +
                                "        updateAll(list);\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public Object[] getSections() {\n" +
                                "        return new Object[0];\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public int getPositionForSection(int sectionIndex) {\n" +
                                "        for (int i = 0; i < getItemCount(); i++) {\n" +
                                "            String sortStr = data.get(i).getSortLetters();\n" +
                                "            char firstChar = sortStr.toUpperCase().charAt(0);\n" +
                                "            if (firstChar == sectionIndex) {\n" +
                                "                return i;\n" +
                                "            }\n" +
                                "        }\n" +
                                "\n" +
                                "        return -1;\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public int getSectionForPosition(int position) {\n" +
                                "        if (data == null)\n" +
                                "            return -1;\n" +
                                "        if (position >= data.size()) {\n" +
                                "            return -1;\n" +
                                "        }\n" +
                                "        return data.get(position).getSortLetters() == null ? -1 : data.get(position).getSortLetters().charAt(0);\n" +
                                "    }\n" +
                                "}\n");
                        FileUtils.saveFileToPathWithName(sbAdapter.toString(), javaPath + File.separator + "ui" + File.separator + "adapter", activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter.java");
                        /*list_item*/
                        FileUtils.saveFileToPathWithName("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                                "<com.zhy.autolayout.AutoLinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                                "    android:layout_width=\"match_parent\"\n" +
                                "    android:layout_height=\"match_parent\"\n" +
                                "    android:orientation=\"vertical\">\n" +
                                "\n" +
                                "</com.zhy.autolayout.AutoLinearLayout>", layoutPath, "list_item_" + layoutName + ".xml");

                        sbImp.append("import " + app.getPackageName() + ".ui.widget.sidebar.CharacterParser;\n" +
                                "import " + app.getPackageName() + ".ui.widget.sidebar.PinyinComparator;\n" +
                                "import " + app.getPackageName() + ".ui.widget.sidebar.SideBar;\n" +
                                "import android.Manifest;\n" +
                                "import android.graphics.Color;\n" +
                                "import android.text.Editable;\n" +
                                "import android.text.TextWatcher;\n" +
                                "import android.view.LayoutInflater;\n" +
                                "import com.tbruyelle.rxpermissions.RxPermissions;\n" +
                                "import android.support.v4.widget.SwipeRefreshLayout;\n" +
                                "import android.support.v7.widget.LinearLayoutManager;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenu;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;\n" +
                                "import com.zhy.autolayout.utils.AutoUtils;\n" +
                                "\n" +
                                "import java.util.ArrayList;\n" +
                                "import java.util.Collections;\n" +
                                "import java.util.List;\n" +
                                "import rx.functions.Action1;\n");
                        sbDef.append("\n    private View header;\n" +
                                "    private SwipeRefreshLayout refreshLayout;\n" +
                                "    private SwipeMenuRecyclerView lv;\n" +
                                "    private List<" + testClazz + ".DataEntity> list;\n" +
                                "    private SideBar sideBar;\n" +
                                "    private TextView tv_toast;\n" +
                                "\n" +
                                "    /**\n" +
                                "     * 汉字转换成拼音的类\n" +
                                "     */\n" +
                                "    private CharacterParser characterParser;\n" +
                                "\n" +
                                "    /**\n" +
                                "     * 根据拼音来排列ListView里面的数据类\n" +
                                "     */\n" +
                                "    private PinyinComparator pinyinComparator;\n" +
                                "    private " + activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter adapter;\n" +
                                "    private RxPermissions rxPermissions;\n" +
                                "    private EditText et_search;\n" +
                                "    private TextView tv_footer;");
                        sbInit.append("\n        rxPermissions = new RxPermissions(getActivity());\n" +
                                "        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);\n" +
                                "        lv = (SwipeMenuRecyclerView) rootView.findViewById(R.id.lv);\n" +
                                "\n" +
                                "        lv.setLayoutManager(new LinearLayoutManager(getActivity()));\n" +
                                "\n" +
                                "        sideBar = (SideBar) rootView.findViewById(R.id.side_bar);\n" +
                                "        tv_toast = (TextView) rootView.findViewById(R.id.tv_toast);\n" +
                                "\n" +
                                "        header = LayoutInflater.from(getActivity()).inflate(R.layout.item_header_search, lv, false);\n" +
                                "        AutoUtils.auto(header);\n" +
                                "        et_search = (EditText) header.findViewById(R.id.et_search);\n" +
                                "        lv.addHeaderView(header);\n" +
                                "\n" +
                                "        View footer = LayoutInflater.from(getActivity()).inflate(R.layout.item_footer, lv, false);\n" +
                                "        AutoUtils.auto(footer);\n" +
                                "        tv_footer = (TextView) footer.findViewById(R.id.tv_footer);\n" +
                                "        tv_footer.setText(0 + getString(R.string." + layoutName + "_unit_text));\n" +
                                "        lv.addFooterView(footer);\n" +
                                "\n" +
                                "        list = new ArrayList<>();\n" +
                                "        //实例化汉字转拼音类\n" +
                                "        characterParser = CharacterParser.getInstance();\n" +
                                "\n" +
                                "        pinyinComparator = new PinyinComparator();\n" +
                                "\n" +
                                "        lv.setSwipeMenuCreator(new SwipeMenuCreator() {\n" +
                                "            @Override\n" +
                                "            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {\n" +
                                "                SwipeMenuItem callItem = new SwipeMenuItem(getActivity());\n" +
                                "                callItem.setImage(R.drawable.delete)\n" +
                                "                        .setWidth(AutoUtils.getPercentWidthSize(250))\n" +
                                "                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)\n" +
                                "                        .setBackgroundColor(Color.rgb(224, 232, 238));\n" +
                                "                swipeRightMenu.addMenuItem(callItem);\n" +
                                "\n" +
                                "            }\n" +
                                "        });\n" +
                                "\n" +
                                "        lv.setSwipeItemClickListener(new SwipeItemClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onItemClick(View itemView, int position) {\n" +
                                "            }\n" +
                                "        });\n" +
                                "\n" +
                                "        lv.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onItemClick(SwipeMenuBridge menuBridge) {\n" +
                                "                switch (menuBridge.getPosition()) {\n" +
                                "                    case 0:\n" +
                                "                        //String id = list.get(menuBridge.getAdapterPosition()).getId();\n" +
                                "                        //delete(id);\n" +
                                "                        break;\n" +
                                "                }\n" +
                                "            }\n" +
                                "        });\n" +
                                "        adapter = new " + activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter(getActivity(), list);\n" +
                                "        lv.setAdapter(adapter);\n"

                        );
                        sbEvent.append("\n       refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {\n" +
                                "            @Override\n" +
                                "            public void onRefresh() {\n" +
                                "                getData();\n" +
                                "            }\n" +
                                "        });\n" +
                                "\n" +
                                "        //设置右侧触摸监听\n" +
                                "        sideBar.setLetterTouchListener(new SideBar.OnLetterTouchListener() {\n" +
                                "            @Override\n" +
                                "            public void onLetterTouch(String letter, int position) {\n" +
                                "                tv_toast.setVisibility(View.VISIBLE);\n" +
                                "                tv_toast.setText(letter);\n" +
                                "                //该字母首次出现的位置\n" +
                                "                if (position != -1 && adapter.getPositionForSection(letter.charAt(0)) != -1) {\n" +
                                "                    ((LinearLayoutManager) lv.getLayoutManager()).scrollToPositionWithOffset(adapter.getPositionForSection(letter.charAt(0)) + 1, 0);\n" +
                                "                }\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onActionUp() {\n" +
                                "                tv_toast.setVisibility(View.INVISIBLE);\n" +
                                "            }\n" +
                                "        });\n" +
                                "\n" +
                                "\n" +
                                "        et_search.addTextChangedListener(new TextWatcher() {\n" +
                                "            @Override\n" +
                                "            public void beforeTextChanged(CharSequence s, int start, int count, int after) {\n" +
                                "\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onTextChanged(CharSequence s, int start, int before, int count) {\n" +
                                "\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void afterTextChanged(Editable s) {\n" +
                                "                if (s.length() == 0) {\n" +
                                "                    adapter.cancelSearch(list);\n" +
                                "                } else {\n" +
                                "                    adapter.startSearch(s.toString());\n" +
                                "                }\n" +
                                "                tv_footer.setText(adapter.getItemCount() + getString(R.string." + layoutName + "_unit_text));\n" +
                                "            }\n" +
                                "        });");
                        if (!sbStrings.toString().contains("\"" + layoutName + "_unit_text\"")) {
                            sbStrings.append("    <string name=\"" + layoutName + "_unit_text\">个" + fragmentEntity.getTitle() + "</string>\n");
                        }

                        sbLayout.append("\n" +
                                "    <FrameLayout\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"match_parent\">\n" +
                                "\n" +
                                "        <android.support.v4.widget.SwipeRefreshLayout\n" +
                                "            android:id=\"@+id/refresh\"\n" +
                                "            android:layout_width=\"match_parent\"\n" +
                                "            android:layout_height=\"match_parent\">\n" +
                                "\n" +
                                "            <com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView\n" +
                                "                android:id=\"@+id/lv\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"match_parent\"\n" +
                                "                android:divider=\"@null\"\n" +
                                "                android:dividerHeight=\"0dp\"\n" +
                                "                android:listSelector=\"@color/colorTransparent\" />\n" +
                                "\n" +
                                "        </android.support.v4.widget.SwipeRefreshLayout>\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:id=\"@+id/tv_toast\"\n" +
                                "            android:layout_width=\"260px\"\n" +
                                "            android:layout_height=\"200px\"\n" +
                                "            android:layout_gravity=\"center\"\n" +
                                "            android:background=\"@drawable/sort_lv_bg\"\n" +
                                "            android:gravity=\"center\"\n" +
                                "            android:textColor=\"@android:color/white\"\n" +
                                "            android:textSize=\"56px\"\n" +
                                "            android:visibility=\"invisible\" />\n" +
                                "\n" +
                                "        <" + app.getPackageName() + ".ui.widget.sidebar.SideBar\n" +
                                "            android:id=\"@+id/side_bar\"\n" +
                                "            android:layout_width=\"70px\"\n" +
                                "            android:layout_height=\"match_parent\"\n" +
                                "            android:layout_gravity=\"right|center_vertical\"\n" +
                                "            android:layout_marginBottom=\"90px\"\n" +
                                "            android:layout_marginTop=\"90px\" />\n" +
                                "    </FrameLayout>\n" +
                                "\n");
                        sbMethods.append("\n    private void getData() {\n" +
                                "        getBaseAct().stopRefresh(refreshLayout);\n" +
                                "    }\n");

                        break;
                    case WidgetEntity.TYPE_SCROLL_VIEW:
                        sbLayout.append("\n    <ScrollView\n" +
                                (widgetEntity.getWeight() > 0 ? "        android:layout_weight=\"" + widgetEntity.getWeight() + "\"\n" : "") +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"" + heightString + "\">\n" +
                                "        <LinearLayout\n" +
                                "            android:layout_width=\"match_parent\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:orientation=\"vertical\">");
                        fillFgmWidget(adapterPath, logoName, app, activityEntity, fragmentEntity, layoutName, layoutPath, javaPath, widgetEntity.getId(), sbStrings, sbLayout, sbImp, sbJava, sbDef, sbInit, sbData, sbEvent, sbEditInfo, sbMethods);
                        sbLayout.append("        </LinearLayout>\n" +
                                "    </ScrollView>");
                        break;
                    case WidgetEntity.TYPE_LINEAR_LAYOUT:
                        sbLayout.append("\n    <LinearLayout\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:background=\"" + (widgetEntity.getBackground().length() == 0 ? "@color/colorTransparent" : widgetEntity.getBackground()) + "\"\n" +
                                (widgetEntity.getWeight() > 0 ? "        android:layout_weight=\"" + widgetEntity.getWeight() + "\"\n" : "") +
                                "        android:gravity=\"" + (widgetEntity.getGravity() == null ? "center" : widgetEntity.getGravity()) + "\"\n" +
                                "        android:orientation=\"" + (widgetEntity.getOrientation() == null ? "horizontal" : widgetEntity.getOrientation()) + "\">");
                        fillFgmWidget(adapterPath, logoName, app, activityEntity, fragmentEntity, layoutName, layoutPath, javaPath, widgetEntity.getId(), sbStrings, sbLayout, sbImp, sbJava, sbDef, sbInit, sbData, sbEvent, sbEditInfo, sbMethods);
                        sbLayout.append("\n   </LinearLayout>");
                        break;
                    case WidgetEntity.TYPE_RELATIVE_LAYOUT:
                        sbLayout.append("\n    <RelativeLayout\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:background=\"" + (widgetEntity.getBackground().length() == 0 ? "@color/colorTransparent" : widgetEntity.getBackground()) + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\">");
                        fillFgmWidget(adapterPath, logoName, app, activityEntity, fragmentEntity, layoutName, layoutPath, javaPath, widgetEntity.getId(), sbStrings, sbLayout, sbImp, sbJava, sbDef, sbInit, sbData, sbEvent, sbEditInfo, sbMethods);
                        sbLayout.append("    </RelativeLayout>\n");
                        break;
                    case WidgetEntity.TYPE_IMAGE_VIEW:
                        String ivName = generateVarName("iv", widgetEntity.getResId());
                        sbDef.append("\n    private ImageView " + ivName + ";");
                        sbInit.append("\n        " + ivName + " = (ImageView) rootView.findViewById(R.id.iv_" + widgetEntity.getResId() + ");");
                        sbLayout.append("\n    <ImageView\n" +
                                "        android:id=\"@+id/iv_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:src=\"@mipmap/" + logoName + "\" />");
                        sbEvent.append("\n        " + ivName + ".setOnClickListener(new View.OnClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onClick(View v) {\n" +
                                actions + "\n"
                                + "            }\n" +
                                "        });\n");
                        break;
                    case WidgetEntity.TYPE_TEXT_VIEW:
                        String tvName = generateVarName("tv", widgetEntity.getResId());
                        sbDef.append("\n    private TextView " + tvName + ";");
                        sbInit.append("\n        " + tvName + " = (TextView) rootView.findViewById(R.id.tv_" + widgetEntity.getResId() + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_tv_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_tv_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_tv_hint_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_tv_hint_text\">" + widgetEntity.getHint() + "</string>\n");
                        }
                        sbLayout.append("\n    <TextView\n" +
                                "        android:id=\"@+id/tv_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                "        android:hint=\"@string/" + widgetEntity.getResId() + "_tv_hint_text\"\n" +
                                "        android:text=\"@string/" + widgetEntity.getResId() + "_tv_text\"\n" +
                                "        android:textColor=\"" + widgetEntity.getTextColor() + "\"\n" +
                                (widgetEntity.getWeight() > 0 ? "        android:layout_weight=\"" + widgetEntity.getWeight() + "\"\n" : "") +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:gravity=\"" + (widgetEntity.getGravity() == null ? "center" : widgetEntity.getGravity()) + "\"\n" +
                                "        android:textSize=\"" + widgetEntity.getTextSize() + "px\" />");
                        break;
                    case WidgetEntity.TYPE_CHECK_BOX:
                        String cbName = generateVarName("cb", widgetEntity.getResId());
                        sbDef.append("\n    private CheckBox " + cbName + ";");
                        sbInit.append("\n        " + cbName + " = (CheckBox) rootView.findViewById(R.id.cb_" + widgetEntity.getResId() + ");");
                        sbLayout.append("\n    <CheckBox\n" +
                                "        android:id=\"@+id/cb_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"99px\"\n" +
                                "        android:layout_height=\"75px\"\n" +
                                "        android:background=\"@drawable/cb_selector\"\n" +
                                "        android:button=\"@null\"\n" +
                                "        app:layout_auto_baseheight=\"width\" />");
                        break;
                }
            }
        }
    }

    private void generateItem(ApplicationEntity app, String adapterPath, String adapterName, String logoName, String widgetId, String layoutPath, StringBuilder sbStrings) throws IOException {
        List<ItemEntity> itemEntities = mItemService.executeCriteria(
                new Criterion[]{
                        Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                        Restrictions.eq("widgetId", widgetId)
                }, Order.asc("createTime")
        );
        if (itemEntities != null && itemEntities.size() > 0) {
            for (ItemEntity itemEntity : itemEntities) {
                String layoutName = itemEntity.getResId() + ".xml";
                FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ".ui.adapter;\n" +
                        "\n" +
                        "import android.content.Context;\n" +
                        "\n" +
                        "import java.util.List;\n" +
                        "\n" +
                        "import " + app.getPackageName() + ".R;\n" +
                        "import " + app.getPackageName() + ".common.api.entity.RvTestEntity;\n" +
                        "import zhouzhuo810.me.zzandframe.ui.adapter.RvAutoBaseAdapter;\n" +
                        "\n" +
                        "/**\n" +
                        " * Created by admin on 2018/1/2.\n" +
                        " */\n" +
                        "\n" +
                        "public class " + adapterName + " extends RvAutoBaseAdapter<RvTestEntity> {\n" +
                        "\n" +
                        "    public " + adapterName + "(Context context, List<RvTestEntity> data) {\n" +
                        "        super(context, data);\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    protected int getLayoutId(int i) {\n" +
                        "        return R.layout." + itemEntity.getResId() + ";\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    protected void fillData(ViewHolder viewHolder, RvTestEntity rvTestEntity, int i) {\n" +
                        "\n" +
                        "    }\n" +
                        "}\n", adapterPath, adapterName + ".java");
                StringBuilder sbLayout = new StringBuilder();
                sbLayout.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<com.zhy.autolayout.AutoLinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "    android:layout_width=\"match_parent\"\n" +
                        "    android:layout_height=\"wrap_content\"\n" +
                        "    android:background=\"@drawable/setting_item_bg_selector\"\n" +
                        "    android:orientation=\"vertical\">\n");
                generateItemWidgets(logoName, itemEntity.getWidgetId(), sbLayout, sbStrings);
                sbLayout.append("\n    <View\n" +
                        "        android:layout_width=\"match_parent\"\n" +
                        "        android:layout_height=\"1px\"\n" +
                        "        android:background=\"@color/colorGrayBg\" />\n" +
                        "</com.zhy.autolayout.AutoLinearLayout>");
                FileUtils.saveFileToPathWithName(sbLayout.toString(), layoutPath, layoutName);
            }
        }
    }

    private void generateItemWidgets(String logoName, String widgetId, StringBuilder sbLayout, StringBuilder sbStrings) {
        List<WidgetEntity> widgetEntities = mWidgetService.executeCriteria(
                new Criterion[]{
                        Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                        Restrictions.eq("pid", widgetId)
                }, Order.asc("createTime")
        );

        if (widgetEntities != null && widgetEntities.size() > 0) {
            for (WidgetEntity widgetEntity : widgetEntities) {
                int width = widgetEntity.getWidth();
                int height = widgetEntity.getHeight();
                String widthString = "wrap_content";
                switch (width) {
                    case -2:
                        widthString = "wrap_content";
                        break;
                    case -1:
                        widthString = "match_parent";
                        break;
                    default:
                        widthString = width + "px";
                        break;
                }
                String heightString = "wrap_content";
                switch (height) {
                    case -2:
                        heightString = "wrap_content";
                        break;
                    case -1:
                        heightString = "match_parent";
                        break;
                    default:
                        heightString = height + "px";
                        break;
                }
                switch (widgetEntity.getType()) {
                    case WidgetEntity.TYPE_SETTING_ITEM:
                        String siName = generateVarName("si", widgetEntity.getResId());
                        sbStrings.append("\n    <string name=\"si_" + widgetEntity.getResId() + "_text\">" + widgetEntity.getTitle() + "</string>");
                        sbLayout.append("\n            <com.zhy.autolayout.AutoLinearLayout\n" +
                                "                android:id=\"@+id/ll_" + widgetEntity.getResId() + "\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "                android:layout_height=\"@dimen/setting_item_height\"\n" +
                                "                android:background=\"@drawable/setting_item_bg_selector\"\n" +
                                "                android:clickable=\"true\"\n" +
                                "                android:gravity=\"center_vertical\"\n" +
                                "                android:orientation=\"horizontal\">\n" +
                                "\n" +
                                "                <ImageView\n" +
                                "                    android:layout_width=\"70px\"\n" +
                                "                    android:layout_height=\"70px\"\n" +
                                "                    android:layout_marginLeft=\"40px\"\n" +
                                "                    android:visibility=\"" + (widgetEntity.getShowLeftTitleImg() ? "visible" : "gone") + "\"\n" +
                                "                    android:src=\"@mipmap/" + logoName + "\" />\n" +
                                "\n" +
                                "                <TextView\n" +
                                "                    android:layout_width=\"0dp\"\n" +
                                "                    android:layout_height=\"wrap_content\"\n" +
                                "                    android:layout_weight=\"1\"\n" +
                                "                    android:layout_marginLeft=\"40px\"\n" +
                                "                    android:text=\"@string/si_" + widgetEntity.getResId() + "_text\"\n" +
                                "                    android:textColor=\"@color/colorText\"\n" +
                                "                    android:textSize=\"40px\" />\n" +
                                "\n" +
                                "                <ImageView\n" +
                                "                    android:layout_width=\"40px\"\n" +
                                "                    android:layout_height=\"40px\"\n" +
                                "                    android:layout_marginRight=\"40px\"\n" +
                                "                    android:src=\"@drawable/more\" />\n" +
                                "            </com.zhy.autolayout.AutoLinearLayout>\n" +
                                "\n" +
                                "            <View\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"1px\"\n" +
                                "                android:layout_marginLeft=\"40px\" />\n");
                        break;
                    case WidgetEntity.TYPE_SUBMIT_BTN_ITEM:
                        String submitBtnName = generateVarName("btn", widgetEntity.getResId());
                        sbLayout.append("\n    <Button\n" +
                                "        android:id=\"@+id/btn_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"120px\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:background=\"@drawable/btn_save_selector\"\n" +
                                "        android:text=\"@string/" + widgetEntity.getResId() + "_btn_text\"\n" +
                                "        android:textColor=\"#fff\"\n" +
                                "        android:textSize=\"@dimen/submit_btn_text_size\" />");
                        break;
                    case WidgetEntity.TYPE_EDIT_TEXT:
                        String etName = generateVarName("et", widgetEntity.getResId());
                        sbLayout.append("\n    <EditText\n" +
                                "        android:id=\"@+id/et_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                (widgetEntity.getWeight() > 0 ? "        android:layout_weight=\"" + widgetEntity.getWeight() + "\"\n" : "") +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:background=\"@drawable/et_round_white_bg\"\n" +
                                "        android:hint=\"@string/search_text\"\n" +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:gravity=\"" + widgetEntity.getGravity() + "\"\n" +
                                "        android:textColor=\"@color/colorBlack\"\n" +
                                "        android:textColorHint=\"@color/colorGrayB\"\n" +
                                "        android:textSize=\"40px\" />\n");
                        break;
                    case WidgetEntity.TYPE_LINEAR_LAYOUT:
                        sbLayout.append("\n    <com.zhy.autolayout.AutoLinearLayout\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:background=\"" + (widgetEntity.getBackground().length() == 0 ? "@color/colorTransparent" : widgetEntity.getBackground()) + "\"\n" +
                                (widgetEntity.getWeight() > 0 ? "        android:layout_weight=\"" + widgetEntity.getWeight() + "\"\n" : "") +
                                "        android:gravity=\"" + (widgetEntity.getGravity() == null ? "center" : widgetEntity.getGravity()) + "\"\n" +
                                "        android:orientation=\"" + (widgetEntity.getOrientation() == null ? "horizontal" : widgetEntity.getOrientation()) + "\">");
                        generateItemWidgets(logoName, widgetEntity.getId(), sbLayout, sbStrings);
                        sbLayout.append("\n   </com.zhy.autolayout.AutoLinearLayout>");
                        break;
                    case WidgetEntity.TYPE_RELATIVE_LAYOUT:
                        sbLayout.append("\n    <com.zhy.autolayout.AutoRelativeLayout\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:background=\"" + (widgetEntity.getBackground().length() == 0 ? "@color/colorTransparent" : widgetEntity.getBackground()) + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\">");
                        generateItemWidgets(logoName, widgetEntity.getId(), sbLayout, sbStrings);
                        sbLayout.append("    </com.zhy.autolayout.AutoRelativeLayout>\n");
                        break;
                    case WidgetEntity.TYPE_IMAGE_VIEW:
                        String ivName = generateVarName("iv", widgetEntity.getResId());
                        sbLayout.append("\n    <ImageView\n" +
                                "        android:id=\"@+id/iv_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:src=\"@mipmap/" + logoName + "\" />");
                        break;
                    case WidgetEntity.TYPE_TEXT_VIEW:
                        String tvName = generateVarName("tv", widgetEntity.getResId());
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_tv_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_tv_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_tv_hint_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_tv_hint_text\">" + widgetEntity.getHint() + "</string>\n");
                        }
                        sbLayout.append("\n    <TextView\n" +
                                "        android:id=\"@+id/tv_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                "        android:hint=\"@string/" + widgetEntity.getResId() + "_tv_hint_text\"\n" +
                                "        android:text=\"@string/" + widgetEntity.getResId() + "_tv_text\"\n" +
                                (widgetEntity.getWeight() > 0 ? "        android:layout_weight=\"" + widgetEntity.getWeight() + "\"\n" : "") +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:textColor=\"" + widgetEntity.getTextColor() + "\"\n" +
                                "        android:textSize=\"" + widgetEntity.getTextSize() + "px\" />");
                        break;
                    case WidgetEntity.TYPE_CHECK_BOX:
                        String cbName = generateVarName("cb", widgetEntity.getResId());
                        sbLayout.append("\n    <CheckBox\n" +
                                "        android:id=\"@+id/cb_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"99px\"\n" +
                                "        android:layout_height=\"75px\"\n" +
                                "        android:background=\"@drawable/cb_selector\"\n" +
                                "        android:button=\"@null\"\n" +
                                "        app:layout_auto_baseheight=\"width\" />");
                        break;

                }
            }
        }
    }


    private void fillWidget(String adapterPath, String logoName, ApplicationEntity app, ActivityEntity activityEntity, String layoutName, String layoutPath, String javaPath,
                            String pid, StringBuilder sbStrings, StringBuilder sbLayout, StringBuilder sbImp, StringBuilder sbJava,
                            StringBuilder sbDef, StringBuilder sbInit, StringBuilder sbData, StringBuilder sbEvent, StringBuilder sbEditInfo, StringBuilder sbMethods) throws IOException {

        List<WidgetEntity> widgetEntities = mWidgetService.executeCriteria(
                new Criterion[]{
                        Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                        Restrictions.eq("pid", pid),
                        Restrictions.eq("relativeId", activityEntity.getId())
                }, Order.asc("createTime")
        );

        if (widgetEntities != null && widgetEntities.size() > 0) {
            for (int i = 0; i < widgetEntities.size(); i++) {
                WidgetEntity widgetEntity = widgetEntities.get(i);
                int width = widgetEntity.getWidth();
                int height = widgetEntity.getHeight();
                String widthString = "wrap_content";
                switch (width) {
                    case -2:
                        widthString = "wrap_content";
                        break;
                    case -1:
                        widthString = "match_parent";
                        break;
                    default:
                        widthString = width + "px";
                        break;
                }
                String heightString = "wrap_content";
                switch (height) {
                    case -2:
                        heightString = "wrap_content";
                        break;
                    case -1:
                        heightString = "match_parent";
                        break;
                    default:
                        heightString = height + "px";
                        break;
                }
                String actions = genearteActions(widgetEntity.getId(), "0", false, app.getPackageName(), activityEntity.getName(), sbStrings, sbImp, sbData);
                switch (widgetEntity.getType()) {
                    case WidgetEntity.TYPE_TITLE_BAR:
                        // TODO: 2017/12/16 添加动作
                        sbDef.append("\n    private TitleBar titleBar;");
                        sbInit.append("\n        titleBar = (TitleBar) findViewById(R.id.title_bar);");
                        sbEvent.append("\n        titleBar.setOnTitleClickListener(new TitleBar.OnTitleClick() {\n" +
                                "            @Override\n" +
                                "            public void onLeftClick(ImageView imageView, MarkView markView, TextView textView) {\n" +
                                "                closeAct();\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onTitleClick(TextView textView) {\n" +
                                "\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onRightClick(ImageView imageView, MarkView markView, TextView textView) {\n" +
                                "\n" + actions +
                                "\n            }\n" +
                                "        });");
                        if (!sbStrings.toString().contains("\"" + layoutName + "_text\"")) {
                            sbStrings.append("    <string name=\"" + layoutName + "_text\">" + activityEntity.getTitle() + "</string>\n");
                        }
                        sbLayout.append("\n    <zhouzhuo810.me.zzandframe.ui.widget.TitleBar\n" +
                                "        android:id=\"@+id/title_bar\"\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"@dimen/title_height\"\n" +
                                "        android:background=\"@color/colorPrimary\"\n" +
                                "        app:ttb_leftImg=\"@drawable/back\"\n" +
                                "        app:ttb_showLeftImg=\"" + widgetEntity.getShowLeftTitleImg() + "\"\n" +
                                "        app:ttb_showLeftLayout=\"" + widgetEntity.getShowLeftTitleLayout() + "\"\n" +
                                "        app:ttb_showLeftText=\"" + widgetEntity.getShowLeftTitleText() + "\"\n" +
                                "        app:ttb_textColorAll=\"@color/colorWhite\"\n" +
                                "        app:ttb_textSizeTitle=\"@dimen/title_text_size\"\n" +
                                "        app:ttb_titleText=\"@string/" + layoutName + "_text\" />");
                        break;
                    case WidgetEntity.TYPE_SETTING_ITEM:
                        String siName = generateVarName("si", widgetEntity.getResId());
                        sbDef.append("\n    private LinearLayout " + siName + ";");
                        sbInit.append("\n        " + siName + " = (LinearLayout) findViewById(R.id.ll_" + widgetEntity.getResId() + ");");
                        sbStrings.append("\n    <string name=\"si_" + widgetEntity.getResId() + "_text\">" + widgetEntity.getTitle() + "</string>");
                        sbEvent.append("\n        " + siName + ".setOnClickListener(new View.OnClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onClick(View v) {\n" +
                                "                \n" + actions +
                                "\n            }\n" +
                                "        });");
                        sbLayout.append("\n            <LinearLayout\n" +
                                "                android:id=\"@+id/ll_" + widgetEntity.getResId() + "\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "                android:layout_height=\"@dimen/setting_item_height\"\n" +
                                "                android:background=\"@drawable/setting_item_bg_selector\"\n" +
                                "                android:clickable=\"true\"\n" +
                                "                android:gravity=\"center_vertical\"\n" +
                                "                android:orientation=\"horizontal\">\n" +
                                "\n" +
                                "                <ImageView\n" +
                                "                    android:layout_width=\"70px\"\n" +
                                "                    android:layout_height=\"70px\"\n" +
                                "                    android:layout_marginLeft=\"40px\"\n" +
                                "                    android:visibility=\"" + (widgetEntity.getShowLeftTitleImg() ? "visible" : "gone") + "\"\n" +
                                "                    android:src=\"@mipmap/" + logoName + "\" />\n" +
                                "\n" +
                                "                <TextView\n" +
                                "                    android:layout_width=\"0dp\"\n" +
                                "                    android:layout_height=\"wrap_content\"\n" +
                                "                    android:layout_weight=\"1\"\n" +
                                "                    android:layout_marginLeft=\"40px\"\n" +
                                "                    android:text=\"@string/si_" + widgetEntity.getResId() + "_text\"\n" +
                                "                    android:textColor=\"@color/colorText\"\n" +
                                "                    android:textSize=\"40px\" />\n" +
                                "\n" +
                                "                <ImageView\n" +
                                "                    android:layout_width=\"40px\"\n" +
                                "                    android:layout_height=\"40px\"\n" +
                                "                    android:layout_marginRight=\"40px\"\n" +
                                "                    android:src=\"@drawable/more\" />\n" +
                                "            </LinearLayout>\n" +
                                "\n" +
                                "            <View\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"1px\"\n" +
                                "                android:layout_marginLeft=\"40px\" />\n");
                        sbEvent.append("\n        " + siName + ".setOnClickListener(new View.OnClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onClick(View v) {\n" +
                                "                \n" + actions +
                                "\n            }\n" +
                                "        });");
                        break;
                    case WidgetEntity.TYPE_INFO_ITEM:
                        String varNameInfo = generateVarName("tv", widgetEntity.getResId());
                        sbDef.append("\n    private TextView " + varNameInfo + ";");
                        sbInit.append("\n        " + varNameInfo + " = (TextView) findViewById(R.id.tv_" + widgetEntity.getResId() + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        sbLayout.append("\n    <LinearLayout\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"wrap_content\"\n" +
                                "        android:background=\"@color/colorWhite\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:gravity=\"center_vertical\"\n" +
                                "        android:minHeight=\"130px\"\n" +
                                "        android:orientation=\"horizontal\">\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:layout_width=\"280px\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_marginLeft=\"40px\"\n" +
                                "            android:gravity=\"center|left\"\n" +
                                "            android:text=\"@string/" + widgetEntity.getResId() + "_text\"\n" +
                                "            android:textColor=\"#415868\"\n" +
                                "            android:textSize=\"44px\" />\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:id=\"@+id/tv_" + widgetEntity.getResId() + "\"\n" +
                                "            android:layout_width=\"0dp\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_marginLeft=\"30px\"\n" +
                                "            android:layout_marginRight=\"30px\"\n" +
                                "            android:layout_weight=\"1\"\n" +
                                "            android:background=\"@null\"\n" +
                                "            android:gravity=\"right|center_vertical\"\n" +
                                "            android:textColor=\"@color/colorBlack\"\n" +
                                "            android:textSize=\"44px\" />\n" +
                                "    </LinearLayout>\n" +
                                "\n" +
                                "    <View\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"1px\"\n" +
                                "        android:layout_marginLeft=\"30px\"\n" +
                                "        android:background=\"@color/colorGrayBg\" />");
                        break;
                    case WidgetEntity.TYPE_TITLE_EDIT_ITEM:
                        String titleEtName = generateVarName("et", widgetEntity.getResId());
                        String titleIvName = generateVarName("ivClear", widgetEntity.getResId());
                        sbDef.append("\n    private EditText " + titleEtName + ";");
                        sbDef.append("\n    private ImageView " + titleIvName + ";");
                        sbInit.append("\n        " + titleEtName + " = (EditText) findViewById(R.id.et_" + widgetEntity.getResId() + ");");
                        sbInit.append("\n        " + titleIvName + " = (ImageView) findViewById(R.id.iv_clear_" + widgetEntity.getResId() + ");");
                        sbEvent.append("\n        setEditListener(" + titleEtName + ", " + titleIvName + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_et_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_et_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_et_hint_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_et_hint_text\">请输入" + widgetEntity.getTitle() + "</string>\n");
                        }
                        sbEditInfo.append("\n        String " + widgetEntity.getResId() + " = et_" + widgetEntity.getResId() + ".getText().toString().trim();");
                        sbLayout.append("\n    <LinearLayout\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"wrap_content\"\n" +
                                "        android:background=\"@color/colorWhite\"\n" +
                                "        android:gravity=\"center_vertical\"\n" +
                                "        android:minHeight=\"130px\"\n" +
                                "        android:orientation=\"horizontal\">\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:layout_width=\"280px\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_marginLeft=\"40px\"\n" +
                                "            android:gravity=\"center|left\"\n" +
                                "            android:text=\"@string/" + widgetEntity.getResId() + "_et_text\"\n" +
                                "            android:textColor=\"#415868\"\n" +
                                "            android:textSize=\"44px\" />\n" +
                                "\n" +
                                "        <EditText\n" +
                                "            android:id=\"@+id/et_" + widgetEntity.getResId() + "\"\n" +
                                "            android:layout_width=\"0dp\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_marginLeft=\"30px\"\n" +
                                "            android:layout_marginRight=\"30px\"\n" +
                                "            android:layout_weight=\"1\"\n" +
                                "            android:background=\"@null\"\n" +
                                "            android:gravity=\"right|center_vertical\"\n" +
                                "            android:hint=\"@string/" + widgetEntity.getResId() + "_et_hint_text\"\n" +
                                "            android:textColor=\"@color/colorBlack\"\n" +
                                "            android:textSize=\"44px\" />\n" +
                                "\n" +
                                "        <ImageView\n" +
                                "            android:id=\"@+id/iv_clear_" + widgetEntity.getResId() + "\"\n" +
                                "            android:layout_width=\"60px\"\n" +
                                "            android:layout_height=\"60px\"\n" +
                                "            android:layout_marginRight=\"30px\"\n" +
                                "            android:src=\"@drawable/clear\"\n" +
                                "            android:visibility=\"gone\" />\n" +
                                "    </LinearLayout>\n" +
                                "\n" +
                                "    <View\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"1px\"\n" +
                                "        android:layout_marginLeft=\"30px\"\n" +
                                "        android:background=\"@color/colorGrayBg\" />");
                        break;
                    case WidgetEntity.TYPE_UNDERLINE_EDIT_ITEM:
                        String underEtName = generateVarName("et", widgetEntity.getResId());
                        String underIvName = generateVarName("ivClear", widgetEntity.getResId());
                        sbDef.append("\n    private EditText " + underEtName + ";");
                        sbDef.append("\n    private ImageView " + underIvName + ";");
                        sbInit.append("\n        " + underEtName + " = (EditText) findViewById(R.id.et_" + widgetEntity.getResId() + ");");
                        sbInit.append("\n        " + underIvName + " = (ImageView) findViewById(R.id.iv_clear_" + widgetEntity.getResId() + ");");
                        sbEvent.append("\n        setEditListener(" + underEtName + ", " + underIvName + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_et_hint_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_et_hint_text\">请输入" + widgetEntity.getTitle() + "</string>\n");
                        }
                        sbEditInfo.append("\n        String " + widgetEntity.getResId() + " = et_" + widgetEntity.getResId() + ".getText().toString().trim();");
                        sbLayout.append("\n" +
                                "        <RelativeLayout\n" +
                                "            android:layout_width=\"match_parent\"\n" +
                                "            android:layout_height=\"120px\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "            android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "            android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "            android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "            android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                ">\n" +
                                "            <EditText\n" +
                                "                android:id=\"@+id/et_" + widgetEntity.getResId() + "\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"match_parent\"\n" +
                                "                android:background=\"@drawable/et_bg_line\"\n" +
                                "                android:hint=\"@string/" + widgetEntity.getResId() + "_et_hint_text\"\n" +
                                "                android:paddingLeft=\"20px\"\n" +
                                "                android:paddingRight=\"120px\"\n" +
                                "                android:textColor=\"@color/colorBlack\"\n" +
                                "                android:textColorHint=\"@color/colorMain\"\n" +
                                "                android:textSize=\"40px\" />\n" +
                                "\n" +
                                "            <ImageView\n" +
                                "                android:id=\"@+id/iv_clear_" + widgetEntity.getResId() + "\"\n" +
                                "                android:layout_width=\"60px\"\n" +
                                "                android:layout_height=\"60px\"\n" +
                                "                android:layout_alignParentRight=\"true\"\n" +
                                "                android:layout_centerVertical=\"true\"\n" +
                                "                android:layout_marginRight=\"30px\"\n" +
                                "                android:src=\"@drawable/clear\"\n" +
                                "                android:visibility=\"gone\" />\n" +
                                "        </RelativeLayout>\n");
                        break;
                    case WidgetEntity.TYPE_SUBMIT_BTN_ITEM:
                        String submitBtnName = generateVarName("btn", widgetEntity.getResId());
                        sbDef.append("\n    private Button " + submitBtnName + ";");
                        sbInit.append("\n        " + submitBtnName + " = (Button) findViewById(R.id.btn_" + widgetEntity.getResId() + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_btn_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_btn_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        sbLayout.append("\n    <Button\n" +
                                "        android:id=\"@+id/btn_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"120px\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:background=\"@drawable/btn_save_selector\"\n" +
                                "        android:text=\"@string/" + widgetEntity.getResId() + "_btn_text\"\n" +
                                "        android:textColor=\"#fff\"\n" +
                                "        android:textSize=\"@dimen/submit_btn_text_size\" />");
                        sbEvent.append("\n        " + submitBtnName + ".setOnClickListener(new View.OnClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onClick(View v) {\n" +
                                actions + "\n"
                        );
                        sbEvent.append("            }\n" +
                                "        });");
                        break;
                    case WidgetEntity.TYPE_EXIT_BTN_ITEM:
                        String exitBtnName = generateVarName("btn", widgetEntity.getResId());
                        sbDef.append("\n    private Button " + exitBtnName + ";");
                        sbInit.append("\n        " + exitBtnName + " = (Button) findViewById(R.id.btn_" + widgetEntity.getResId() + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_btn_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_btn_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        sbLayout.append("\n    <Button\n" +
                                "        android:id=\"@+id/btn_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"120px\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:background=\"@drawable/btn_exit_selector\"\n" +
                                "        android:text=\"@string/" + widgetEntity.getResId() + "_btn_text\"\n" +
                                "        android:textColor=\"#fff\"\n" +
                                "        android:textSize=\"@dimen/submit_btn_text_size\" />");
                        sbEvent.append("\n        " + exitBtnName + ".setOnClickListener(new View.OnClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onClick(View v) {\n" +
                                actions + "\n");
                        sbEvent.append("            }\n" +
                                "        });");
                        break;
                    case WidgetEntity.TYPE_RV:
                        //act rv
                        String adapterName = generateVarName("", widgetEntity.getResId()) + "RvAdapter";
                        sbImp.append("\nimport android.support.v4.widget.SwipeRefreshLayout;")
                                .append("\nimport android.support.v7.widget.LinearLayoutManager;")
                                .append("\nimport android.support.v7.widget.RecyclerView;")
                                .append("\nimport zhouzhuo810.me.zzandframe.ui.adapter.RvAutoBaseAdapter;")
                                .append("\nimport android.support.v4.widget.SwipeRefreshLayout;")
                                .append("\nimport " + app.getPackageName() + ".ui.adapter." + adapterName + ";");
                        sbDef.append("\n    private SwipeRefreshLayout refresh;")
                                .append("\n    private RecyclerView rv;")
                                .append("\n    private " + adapterName + " adapter;")
                                .append("\n    private TextView tvNoData;");
                        sbInit.append("\n        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);")
                                .append("\n        rv = (RecyclerView) findViewById(R.id.rv);")
                                .append("\n        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager." + widgetEntity.getOrientation().toUpperCase() + ", false));")
                                .append("\n        tvNoData = (TextView) findViewById(R.id.tv_no_data);");
                        sbEvent.append("\n        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {\n" +
                                "            @Override\n" +
                                "            public void onRefresh() {\n" +
                                "                getData();\n" +
                                "            }\n" +
                                "        });\n");
                        sbEvent.append("\n        adapter.setOnItemLongClickListener(new RvAutoBaseAdapter.OnItemLongClickListener() {\n" +
                                "            @Override\n" +
                                "            public boolean onItemLongClick(View view, int position) {\n" +
                                "\n" +
                                "                return false;\n" +
                                "            }\n" +
                                "        });\n");
                        sbMethods.append("\n" +
                                "    private void getData() {\n" +
                                "\n" +
                                "        stopRefresh(refresh);\n" +
                                "    }\n");
                        sbData.append("        List<RvTestEntity> list = new ArrayList<>();\n" +
                                "        for (int i = 0; i < 10; i++) {\n" +
                                "            list.add(new RvTestEntity());\n" +
                                "        }\n" +
                                "        adapter = new " + adapterName + "(this, list);\n" +
                                "        rv.setAdapter(adapter);\n");
                        sbData.append("\n        startRefresh(refresh);\n" +
                                "        getData();\n");
                        sbLayout.append("\n    <RelativeLayout\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"match_parent\">\n" +
                                "\n" +
                                "        <android.support.v4.widget.SwipeRefreshLayout\n" +
                                "            android:id=\"@+id/refresh\"\n" +
                                "            android:layout_width=\"match_parent\"\n" +
                                "            android:layout_height=\"match_parent\">\n" +
                                "\n" +
                                "            <android.support.v7.widget.RecyclerView\n" +
                                "                android:id=\"@+id/rv\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"match_parent\" />\n" +
                                "        </android.support.v4.widget.SwipeRefreshLayout>\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:id=\"@+id/tv_no_data\"\n" +
                                "            android:layout_width=\"wrap_content\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_centerInParent=\"true\"\n" +
                                "            android:text=\"@string/no_data_text\"\n" +
                                "            android:textColor=\"@color/colorGrayB\"\n" +
                                "            android:textSize=\"40px\"\n" +
                                "            android:visibility=\"gone\" />\n" +
                                "    </RelativeLayout>\n");
                        FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ".ui.adapter;\n" +
                                "\n" +
                                "import android.content.Context;\n" +
                                "\n" +
                                "import java.util.List;\n" +
                                "\n" +
                                "import " + app.getPackageName() + ".R;\n" +
                                "import " + app.getPackageName() + ".common.api.entity.RvTestEntity;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.adapter.RvAutoBaseAdapter;\n" +
                                "\n" +
                                "/**\n" +
                                " * Created by admin on 2018/1/2.\n" +
                                " */\n" +
                                "\n" +
                                "public class " + adapterName + " extends RvAutoBaseAdapter<RvTestEntity> {\n" +
                                "\n" +
                                "    public " + adapterName + "(Context context, List<RvTestEntity> data) {\n" +
                                "        super(context, data);\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    protected int getLayoutId(int i) {\n" +
                                "        return R.layout.rv_item_project;\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    protected void fillData(ViewHolder viewHolder, RvTestEntity rvTestEntity, int i) {\n" +
                                "\n" +
                                "    }\n" +
                                "}\n", adapterPath, adapterName + ".java");
                        generateItem(app, adapterPath, adapterName, logoName, widgetEntity.getId(), layoutPath, sbStrings);
                        break;
                    case WidgetEntity.TYPE_LV:
                        String adapterNameLv = generateVarName("", widgetEntity.getResId()) + "LvAdapter";
                        sbImp.append("\nimport android.support.v4.widget.SwipeRefreshLayout;")
                                .append("\nimport android.widget.ListView;")
                                .append("\nimport android.support.v7.widget.RecyclerView;")
                                .append("\nimport zhouzhuo810.me.zzandframe.ui.widget.ZzLvRefreshLayout;");
                        sbDef.append("\n    private ZzLvRefreshLayout refresh")
                                .append("\n    private ListView lv;")
                                .append("\n    private TextView tvNoData;");
                        sbInit.append("\n        refresh = (ZzLvRefreshLayout) findViewById(R.id.refresh);")
                                .append("\n        rv = (RecyclerView) findViewById(R.id.rv);")
                                .append("\n        tvNoData = (TextView) findViewById(R.id.tv_no_data);");
                        sbEvent.append("\n        titleBar.setOnTitleClickListener(new TitleBar.OnTitleClick() {\n" +
                                "            @Override\n" +
                                "            public void onLeftClick(ImageView imageView, MarkView markView, TextView textView) {\n" +
                                "                closeAct();\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onTitleClick(TextView textView) {\n" +
                                "\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onRightClick(ImageView imageView, MarkView markView, TextView textView) {\n" +
                                "\n" +
                                "            }\n" +
                                "        });\n")
                                .append("        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {\n" +
                                        "            @Override\n" +
                                        "            public void onRefresh() {\n" +
                                        "                getData();\n" +
                                        "            }\n" +
                                        "        });");
                        sbMethods.append("\n" +
                                "    private void getData() {\n" +
                                "\n" +
                                "        stopRefresh(refresh);\n" +
                                "    }\n");
                        sbData.append("\n        startRefresh(refresh);\n" +
                                "        getData();\n");
                        sbLayout.append("\n    <RelativeLayout\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"match_parent\">\n" +
                                "\n" +
                                "        <zhouzhuo810.me.zzandframe.ui.widget.ZzLvRefreshLayout\n" +
                                "            android:id=\"@+id/refresh\"\n" +
                                "            android:layout_width=\"match_parent\"\n" +
                                "            android:layout_height=\"wrap_content\">\n" +
                                "\n" +
                                "            <ListView\n" +
                                "                android:id=\"@+id/lv\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"match_parent\"\n" +
                                "                android:divider=\"@null\"\n" +
                                "                android:dividerHeight=\"0dp\"\n" +
                                "                android:listSelector=\"@color/colorTransparent\" />\n" +
                                "        </zhouzhuo810.me.zzandframe.ui.widget.ZzLvRefreshLayout>\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:id=\"@+id/tv_no_data\"\n" +
                                "            android:layout_width=\"wrap_content\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:layout_centerInParent=\"true\"\n" +
                                "            android:text=\"@string/no_data_text\"\n" +
                                "            android:textColor=\"@color/colorGrayB\"\n" +
                                "            android:textSize=\"40px\"\n" +
                                "            android:visibility=\"gone\" />\n" +
                                "    </RelativeLayout>\n");
                        generateItem(app, adapterPath, adapterNameLv, logoName, widgetEntity.getId(), layoutPath, sbStrings);
                        break;
                    case WidgetEntity.TYPE_SCROLLABLE_LV:
                        String adapterNameSlv = generateVarName("", widgetEntity.getResId()) + "ScrollLvAdapter";
                        String srollLvName = generateVarName("lv", widgetEntity.getResId());
                        sbImp.append("\nimport " + app.getPackageName() + ".ui.widget.ScrollableListView;");
                        sbDef.append("\n    private ScrollableListView " + srollLvName + ";");
                        sbInit.append("\n        " + srollLvName + " = (ScrollableListView) findViewById(R.id.lv_" + widgetEntity.getResId() + ");");
                        sbLayout.append("\n            <" + app.getPackageName() + ".ui.widget.ScrollableListView\n" +
                                "                android:id=\"@+id/lv_" + widgetEntity.getResId() + "\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"wrap_content\"\n" +
                                "                android:divider=\"@null\"\n" +
                                "                android:dividerHeight=\"0dp\"\n" +
                                "                android:listSelector=\"@color/colorTransparent\" />\n");
                        generateItem(app, adapterPath, adapterNameSlv, logoName, widgetEntity.getId(), layoutPath, sbStrings);
                        break;
                    case WidgetEntity.TYPE_EDIT_TEXT:
                        String etName = generateVarName("et", widgetEntity.getResId());
                        sbDef.append("\n    private EditText " + etName + ";");
                        sbInit.append("\n        " + etName + " = (EditText) findViewById(R.id.et_" + widgetEntity.getResId() + ");");
                        sbLayout.append("\n    <EditText\n" +
                                "        android:id=\"@+id/et_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                (widgetEntity.getWeight() > 0 ? "        android:layout_weight=\"" + widgetEntity.getWeight() + "\"\n" : "") +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:background=\"@drawable/et_round_white_bg\"\n" +
                                "        android:hint=\"@string/search_text\"\n" +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:gravity=\"" + widgetEntity.getGravity() + "\"\n" +
                                "        android:textColor=\"@color/colorBlack\"\n" +
                                "        android:textColorHint=\"@color/colorGrayB\"\n" +
                                "        android:textSize=\"40px\" />\n");
                        break;
                    case WidgetEntity.TYPE_LETTER_RV:
                        //activity
                        String testClazz = "RvTestResult";
                        String realClazz = "";
                        String entityPath = javaPath
                                + File.separator + "common"
                                + File.separator + "api"
                                + File.separator + "entity";
                        FileUtils.saveFileToPathWithName("package " + app.getPackageName() + ".common.api.entity;\n" +
                                "\n" +
                                "import java.util.List;\n" +
                                "import " + app.getPackageName() + ".ui.widget.sidebar.SortModel;\n" +
                                "import zhouzhuo810.me.zzandframe.common.rule.SearchAble;\n" +
                                "\n" +
                                "/**\n" +
                                " * Created by zz on 2017/9/7.\n" +
                                " */\n" +
                                "public class " + testClazz + " {\n" +
                                "    private int code;\n" +
                                "    private String msg;\n" +
                                "    private List<DataEntity> data;\n" +
                                "\n" +
                                "    public int getCode() {\n" +
                                "        return code;\n" +
                                "    }\n" +
                                "\n" +
                                "    public void setCode(int code) {\n" +
                                "        this.code = code;\n" +
                                "    }\n" +
                                "\n" +
                                "    public String getMsg() {\n" +
                                "        return msg;\n" +
                                "    }\n" +
                                "\n" +
                                "    public void setMsg(String msg) {\n" +
                                "        this.msg = msg;\n" +
                                "    }\n" +
                                "\n" +
                                "    public List<DataEntity> getData() {\n" +
                                "        return data;\n" +
                                "    }\n" +
                                "\n" +
                                "    public void setData(List<DataEntity> data) {\n" +
                                "        this.data = data;\n" +
                                "    }\n" +
                                "\n" +
                                "    public static class DataEntity extends SortModel implements SearchAble {\n" +
                                "\n" +
                                "        @Override\n" +
                                "        public String toSearch() {\n" +
                                "            return \"\";\n" +
                                "        }\n" +
                                "    }\n" +
                                "}\n", entityPath, testClazz + ".java");
                        StringBuilder sbAdapter = new StringBuilder();
                        sbImp.append("import " + app.getPackageName() + ".ui.adapter." + activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter;\n");
                        sbAdapter.append("package " + app.getPackageName() + ".ui.adapter;\n" +
                                "\n" +
                                "import android.content.Context;\n" +
                                "import android.widget.SectionIndexer;\n" +
                                "\n" +
                                "import java.util.List;\n" +
                                "import java.util.ArrayList;\n" +
                                "\n" +
                                "import " + app.getPackageName() + ".R;\n" +
                                "import " + app.getPackageName() + ".common.api.entity." + testClazz + ";\n" +
                                (realClazz.length() > 0 ? "import " + app.getPackageName() + ".common.api.entity." + realClazz + ";\n" : "") +
                                "import zhouzhuo810.me.zzandframe.common.rule.ISearch;\n" +
                                "import zhouzhuo810.me.zzandframe.ui.adapter.RvAutoBaseAdapter;\n" +
                                "\n" +
                                "/**\n" +
                                " * Created by zz on 2017/8/28.\n" +
                                " */\n" +
                                "public class " + activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter extends RvAutoBaseAdapter<" + testClazz + ".DataEntity> implements ISearch<" + testClazz + ".DataEntity>,SectionIndexer {\n" +
                                "\n" +
                                "    public " + activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter(Context context, List<" + testClazz + ".DataEntity> data) {\n" +
                                "        super(context, data);\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    protected int getLayoutId(int type) {\n" +
                                "        return R.layout.list_item_" + layoutName + ";\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    protected void fillData(ViewHolder viewHolder, " + testClazz + ".DataEntity dataEntity, int position) {\n" +
                                "        \n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public void startSearch(String s) {\n" +
                                "        List<" + testClazz + ".DataEntity> msgs = new ArrayList<>();\n" +
                                "        for (" + testClazz + ".DataEntity mData : data) {\n" +
                                "            if (mData.toSearch().contains(s)) {\n" +
                                "                msgs.add(mData);\n" +
                                "            }\n" +
                                "        }\n" +
                                "        updateAll(msgs);\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public void cancelSearch(List<" + testClazz + ".DataEntity> list) {\n" +
                                "        updateAll(list);\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public Object[] getSections() {\n" +
                                "        return new Object[0];\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public int getPositionForSection(int sectionIndex) {\n" +
                                "        for (int i = 0; i < getItemCount(); i++) {\n" +
                                "            String sortStr = data.get(i).getSortLetters();\n" +
                                "            char firstChar = sortStr.toUpperCase().charAt(0);\n" +
                                "            if (firstChar == sectionIndex) {\n" +
                                "                return i;\n" +
                                "            }\n" +
                                "        }\n" +
                                "\n" +
                                "        return -1;\n" +
                                "    }\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public int getSectionForPosition(int position) {\n" +
                                "        if (data == null)\n" +
                                "            return -1;\n" +
                                "        if (position >= data.size()) {\n" +
                                "            return -1;\n" +
                                "        }\n" +
                                "        return data.get(position).getSortLetters() == null ? -1 : data.get(position).getSortLetters().charAt(0);\n" +
                                "    }\n" +
                                "}\n");
                        FileUtils.saveFileToPathWithName(sbAdapter.toString(), javaPath + File.separator + "ui" + File.separator + "adapter", activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter.java");
                        /*list_item*/
                        FileUtils.saveFileToPathWithName("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                                "<com.zhy.autolayout.AutoLinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                                "    android:layout_width=\"match_parent\"\n" +
                                "    android:layout_height=\"match_parent\"\n" +
                                "    android:orientation=\"vertical\">\n" +
                                "\n" +
                                "</com.zhy.autolayout.AutoLinearLayout>", layoutPath, "list_item_" + layoutName + ".xml");

                        sbImp.append("import " + app.getPackageName() + ".ui.widget.sidebar.CharacterParser;\n" +
                                "import " + app.getPackageName() + ".ui.widget.sidebar.PinyinComparator;\n" +
                                "import " + app.getPackageName() + ".ui.widget.sidebar.SideBar;\n" +
                                "import android.Manifest;\n" +
                                "import android.graphics.Color;\n" +
                                "import android.text.Editable;\n" +
                                "import android.text.TextWatcher;\n" +
                                "import android.view.LayoutInflater;\n" +
                                "import com.tbruyelle.rxpermissions.RxPermissions;\n" +
                                "import android.support.v4.widget.SwipeRefreshLayout;\n" +
                                "import android.support.v7.widget.LinearLayoutManager;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenu;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;\n" +
                                "import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;\n" +
                                "import com.zhy.autolayout.utils.AutoUtils;\n" +
                                "\n" +
                                "import java.util.ArrayList;\n" +
                                "import java.util.Collections;\n" +
                                "import java.util.List;\n" +
                                "import rx.functions.Action1;\n");
                        sbDef.append("\n    private View header;\n" +
                                "    private SwipeRefreshLayout refreshLayout;\n" +
                                "    private SwipeMenuRecyclerView lv;\n" +
                                "    private List<" + testClazz + ".DataEntity> list;\n" +
                                "    private SideBar sideBar;\n" +
                                "    private TextView tv_toast;\n" +
                                "\n" +
                                "    /**\n" +
                                "     * 汉字转换成拼音的类\n" +
                                "     */\n" +
                                "    private CharacterParser characterParser;\n" +
                                "\n" +
                                "    /**\n" +
                                "     * 根据拼音来排列ListView里面的数据类\n" +
                                "     */\n" +
                                "    private PinyinComparator pinyinComparator;\n" +
                                "    private " + activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter adapter;\n" +
                                "    private RxPermissions rxPermissions;\n" +
                                "    private EditText et_search;\n" +
                                "    private TextView tv_footer;");
                        sbInit.append("\n        rxPermissions = new RxPermissions(this);\n" +
                                "        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);\n" +
                                "        lv = (SwipeMenuRecyclerView) findViewById(R.id.lv);\n" +
                                "\n" +
                                "        lv.setLayoutManager(new LinearLayoutManager(this));\n" +
                                "\n" +
                                "        sideBar = (SideBar) findViewById(R.id.side_bar);\n" +
                                "        tv_toast = (TextView) findViewById(R.id.tv_toast);\n" +
                                "\n" +
                                "        header = LayoutInflater.from(" + activityEntity.getName() + ".this).inflate(R.layout.item_header_search, lv, false);\n" +
                                "        AutoUtils.auto(header);\n" +
                                "        et_search = (EditText) header.findViewById(R.id.et_search);\n" +
                                "        lv.addHeaderView(header);\n" +
                                "\n" +
                                "        View footer = LayoutInflater.from(" + activityEntity.getName() + ".this).inflate(R.layout.item_footer, lv, false);\n" +
                                "        AutoUtils.auto(footer);\n" +
                                "        tv_footer = (TextView) footer.findViewById(R.id.tv_footer);\n" +
                                "        tv_footer.setText(0 + getString(R.string." + layoutName + "_unit_text));\n" +
                                "        lv.addFooterView(footer);\n" +
                                "\n" +
                                "        list = new ArrayList<>();\n" +
                                "        //实例化汉字转拼音类\n" +
                                "        characterParser = CharacterParser.getInstance();\n" +
                                "\n" +
                                "        pinyinComparator = new PinyinComparator();\n" +
                                "\n" +
                                "        lv.setSwipeMenuCreator(new SwipeMenuCreator() {\n" +
                                "            @Override\n" +
                                "            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {\n" +
                                "                SwipeMenuItem callItem = new SwipeMenuItem(" + activityEntity.getName() + ".this);\n" +
                                "                callItem.setImage(R.drawable.delete)\n" +
                                "                        .setWidth(AutoUtils.getPercentWidthSize(250))\n" +
                                "                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)\n" +
                                "                        .setBackgroundColor(Color.rgb(224, 232, 238));\n" +
                                "                swipeRightMenu.addMenuItem(callItem);\n" +
                                "\n" +
                                "            }\n" +
                                "        });" +
                                "\n" +
                                "        lv.setSwipeItemClickListener(new SwipeItemClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onItemClick(View itemView, int position) {\n" +
                                "            }\n" +
                                "        });\n" +
                                "\n" +
                                "        lv.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onItemClick(SwipeMenuBridge menuBridge) {\n" +
                                "                switch (menuBridge.getPosition()) {\n" +
                                "                    case 0:\n" +
                                "                        //String id = list.get(menuBridge.getAdapterPosition()).getId();\n" +
                                "                        //delete(id);\n" +
                                "                        break;\n" +
                                "                }\n" +
                                "            }\n" +
                                "        });\n" +
                                "\n" +
                                "        adapter = new " + activityEntity.getName().substring(0, 1).toUpperCase() + activityEntity.getName().substring(1) + "ListAdapter(this, list);\n" +
                                "        lv.setAdapter(adapter);\n"
                        );
                        sbEvent.append("\n       refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {\n" +
                                "            @Override\n" +
                                "            public void onRefresh() {\n" +
                                "                getData();\n" +
                                "            }\n" +
                                "        });\n" +
                                "\n" +
                                "        //设置右侧触摸监听\n" +
                                "        sideBar.setLetterTouchListener(new SideBar.OnLetterTouchListener() {\n" +
                                "            @Override\n" +
                                "            public void onLetterTouch(String letter, int position) {\n" +
                                "                tv_toast.setVisibility(View.VISIBLE);\n" +
                                "                tv_toast.setText(letter);\n" +
                                "                //该字母首次出现的位置\n" +
                                "                if (position != -1 && adapter.getPositionForSection(letter.charAt(0)) != -1) {\n" +
                                "                    ((LinearLayoutManager) lv.getLayoutManager()).scrollToPositionWithOffset(adapter.getPositionForSection(letter.charAt(0)) + 1, 0);\n" +
                                "                }\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onActionUp() {\n" +
                                "                tv_toast.setVisibility(View.INVISIBLE);\n" +
                                "            }\n" +
                                "        });\n" +
                                "\n" +
                                "\n" +
                                "        et_search.addTextChangedListener(new TextWatcher() {\n" +
                                "            @Override\n" +
                                "            public void beforeTextChanged(CharSequence s, int start, int count, int after) {\n" +
                                "\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onTextChanged(CharSequence s, int start, int before, int count) {\n" +
                                "\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void afterTextChanged(Editable s) {\n" +
                                "                if (s.length() == 0) {\n" +
                                "                    adapter.cancelSearch(list);\n" +
                                "                } else {\n" +
                                "                    adapter.startSearch(s.toString());\n" +
                                "                }\n" +
                                "                tv_footer.setText(adapter.getItemCount() + getString(R.string." + layoutName + "_unit_text));\n" +
                                "            }\n" +
                                "        });");
                        if (!sbStrings.toString().contains("\"" + layoutName + "_unit_text\"")) {
                            sbStrings.append("    <string name=\"" + layoutName + "_unit_text\">个" + activityEntity.getTitle() + "</string>\n");
                        }
                        sbLayout.append("\n" +
                                "    <FrameLayout\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"match_parent\">\n" +
                                "\n" +
                                "        <android.support.v4.widget.SwipeRefreshLayout\n" +
                                "            android:id=\"@+id/refresh\"\n" +
                                "            android:layout_width=\"match_parent\"\n" +
                                "            android:layout_height=\"match_parent\">\n" +
                                "\n" +
                                "            <com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView\n" +
                                "                android:id=\"@+id/lv\"\n" +
                                "                android:layout_width=\"match_parent\"\n" +
                                "                android:layout_height=\"match_parent\"\n" +
                                "                android:divider=\"@null\"\n" +
                                "                android:dividerHeight=\"0dp\"\n" +
                                "                android:listSelector=\"@color/colorTransparent\" />\n" +
                                "\n" +
                                "        </android.support.v4.widget.SwipeRefreshLayout>\n" +
                                "\n" +
                                "        <TextView\n" +
                                "            android:id=\"@+id/tv_toast\"\n" +
                                "            android:layout_width=\"260px\"\n" +
                                "            android:layout_height=\"200px\"\n" +
                                "            android:layout_gravity=\"center\"\n" +
                                "            android:background=\"@drawable/sort_lv_bg\"\n" +
                                "            android:gravity=\"center\"\n" +
                                "            android:textColor=\"@android:color/white\"\n" +
                                "            android:textSize=\"56px\"\n" +
                                "            android:visibility=\"invisible\" />\n" +
                                "\n" +
                                "        <" + app.getPackageName() + ".ui.widget.sidebar.SideBar\n" +
                                "            android:id=\"@+id/side_bar\"\n" +
                                "            android:layout_width=\"70px\"\n" +
                                "            android:layout_height=\"match_parent\"\n" +
                                "            android:layout_gravity=\"right|center_vertical\"\n" +
                                "            android:layout_marginBottom=\"90px\"\n" +
                                "            android:layout_marginTop=\"90px\" />\n" +
                                "    </FrameLayout>\n" +
                                "\n");
                        sbMethods.append("\n    private void getData() {\n" +
                                "        stopRefresh(refreshLayout);\n" +
                                "    }\n");

                        break;
                    case WidgetEntity.TYPE_SCROLL_VIEW:
                        sbLayout.append("\n    <ScrollView\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"match_parent\">\n" +
                                "        <LinearLayout\n" +
                                "            android:layout_width=\"match_parent\"\n" +
                                "            android:layout_height=\"wrap_content\"\n" +
                                "            android:orientation=\"vertical\">");
                        fillWidget(adapterPath, logoName, app, activityEntity, layoutName, layoutPath, javaPath, widgetEntity.getId(), sbStrings, sbLayout, sbImp, sbJava, sbDef, sbInit, sbData, sbEvent, sbEditInfo, sbMethods);
                        sbLayout.append("        </LinearLayout>\n" +
                                "    </ScrollView>");
                        break;
                    case WidgetEntity.TYPE_LINEAR_LAYOUT:
                        sbLayout.append("\n    <LinearLayout\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:background=\"" + (widgetEntity.getBackground().length() == 0 ? "@color/colorTransparent" : widgetEntity.getBackground()) + "\"\n" +
                                (widgetEntity.getWeight() > 0 ? "        android:layout_weight=\"" + widgetEntity.getWeight() + "\"\n" : "") +
                                "        android:gravity=\"" + (widgetEntity.getGravity() == null ? "center" : widgetEntity.getGravity()) + "\"\n" +
                                "        android:orientation=\"" + (widgetEntity.getOrientation() == null ? "horizontal" : widgetEntity.getOrientation()) + "\">");
                        fillWidget(adapterPath, logoName, app, activityEntity, layoutName, layoutPath, javaPath, widgetEntity.getId(), sbStrings, sbLayout, sbImp, sbJava, sbDef, sbInit, sbData, sbEvent, sbEditInfo, sbMethods);
                        sbLayout.append("\n   </LinearLayout>");
                        break;
                    case WidgetEntity.TYPE_RELATIVE_LAYOUT:
                        sbLayout.append("\n    <RelativeLayout\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:background=\"" + (widgetEntity.getBackground().length() == 0 ? "@color/colorTransparent" : widgetEntity.getBackground()) + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\">");
                        fillWidget(adapterPath, logoName, app, activityEntity, layoutName, layoutPath, javaPath, widgetEntity.getId(), sbStrings, sbLayout, sbImp, sbJava, sbDef, sbInit, sbData, sbEvent, sbEditInfo, sbMethods);
                        sbLayout.append("    </RelativeLayout>\n");
                        break;
                    case WidgetEntity.TYPE_IMAGE_VIEW:
                        String ivName = generateVarName("iv", widgetEntity.getResId());
                        sbDef.append("\n    private ImageView " + ivName + ";");
                        sbInit.append("\n        " + ivName + " = (ImageView) findViewById(R.id.iv_" + widgetEntity.getResId() + ");");
                        sbLayout.append("\n    <ImageView\n" +
                                "        android:id=\"@+id/iv_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                "        android:src=\"@mipmap/" + logoName + "\" />");
                        ActivityEntity targetAct = null;
                        sbEvent.append("\n        " + ivName + ".setOnClickListener(new View.OnClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onClick(View v) {\n" +
                                actions + "\n");
                        sbEvent.append("            }\n" +
                                "        });\n");
                        break;
                    case WidgetEntity.TYPE_TEXT_VIEW:
                        String tvName = generateVarName("tv", widgetEntity.getResId());
                        sbDef.append("\n    private TextView " + tvName + ";");
                        sbInit.append("\n        " + tvName + " = (TextView) findViewById(R.id.tv_" + widgetEntity.getResId() + ");");
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_tv_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_tv_text\">" + widgetEntity.getTitle() + "</string>\n");
                        }
                        if (!sbStrings.toString().contains("\"" + widgetEntity.getResId() + "_tv_hint_text\"")) {
                            sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_tv_hint_text\">" + widgetEntity.getHint() + "</string>\n");
                        }
                        sbLayout.append("\n    <TextView\n" +
                                "        android:id=\"@+id/tv_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"" + widthString + "\"\n" +
                                "        android:layout_height=\"" + heightString + "\"\n" +
                                "        android:hint=\"@string/" + widgetEntity.getResId() + "_tv_hint_text\"\n" +
                                "        android:text=\"@string/" + widgetEntity.getResId() + "_tv_text\"\n" +
                                (widgetEntity.getWeight() > 0 ? "        android:layout_weight=\"" + widgetEntity.getWeight() + "\"\n" : "") +
                                (widgetEntity.getMarginLeft() == 0 ? "" : "        android:layout_marginLeft=\"" + widgetEntity.getMarginLeft() + "px\"\n") +
                                (widgetEntity.getMarginRight() == 0 ? "" : "        android:layout_marginRight=\"" + widgetEntity.getMarginRight() + "px\"\n") +
                                (widgetEntity.getMarginTop() == 0 ? "" : "        android:layout_marginTop=\"" + widgetEntity.getMarginTop() + "px\"\n") +
                                (widgetEntity.getMarginBottom() == 0 ? "" : "        android:layout_marginBottom=\"" + widgetEntity.getMarginBottom() + "px\"\n") +
                                (widgetEntity.getPaddingLeft() == 0 ? "" : "        android:paddingLeft=\"" + widgetEntity.getPaddingLeft() + "px\"\n") +
                                (widgetEntity.getPaddingRight() == 0 ? "" : "        android:paddingRight=\"" + widgetEntity.getPaddingRight() + "px\"\n") +
                                (widgetEntity.getPaddingTop() == 0 ? "" : "        android:paddingTop=\"" + widgetEntity.getPaddingTop() + "px\"\n") +
                                (widgetEntity.getPaddingBottom() == 0 ? "" : "        android:paddingBottom=\"" + widgetEntity.getPaddingBottom() + "px\"\n") +
                                "        android:textColor=\"" + widgetEntity.getTextColor() + "\"\n" +
                                "        android:textSize=\"" + widgetEntity.getTextSize() + "px\" />");
                        break;
                    case WidgetEntity.TYPE_CHECK_BOX:
                        String cbName = generateVarName("cb", widgetEntity.getResId());
                        sbDef.append("\n    private CheckBox " + cbName + ";");
                        sbInit.append("\n        " + cbName + " = (CheckBox) findViewById(R.id.cb_" + widgetEntity.getResId() + ");");
                        sbLayout.append("\n    <CheckBox\n" +
                                "        android:id=\"@+id/cb_" + widgetEntity.getResId() + "\"\n" +
                                "        android:layout_width=\"99px\"\n" +
                                "        android:layout_height=\"75px\"\n" +
                                "        android:background=\"@drawable/cb_selector\"\n" +
                                "        android:button=\"@null\"\n" +
                                "        app:layout_auto_baseheight=\"width\" />");
                        break;

                }
            }
        }

    }

    private String genearteActions(String widgetId, String pid, boolean fgm, String packageName, String actName, StringBuilder sbStrings, StringBuilder sbImp, StringBuilder sbData) {
        List<ActionEntity> actions = mActionService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("pid", pid),
                Restrictions.eq("widgetId", widgetId)
        });
        StringBuilder sbActions = new StringBuilder();
        if (actions != null && actions.size() > 0) {
            for (ActionEntity action : actions) {

                switch (action.getType()) {
                    case ActionEntity.TYPE_DIALOG_PROGRESS:
                        if (fgm) {
                            //fragment
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                getBaseAct().hidePd();");
                            } else {
                                //show
                                sbActions.append("\n                getBaseAct().showPd(\"" + action.getMsg() + "\", false);");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                            }
                        } else {
                            //activity
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                hidePd();");
                            } else {
                                //show
                                sbActions.append("\n                showPd(\"" + action.getMsg() + "\", false);");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                            }
                        }
                        break;
                    case ActionEntity.TYPE_DIALOG_TWO_BTN:
                        if (fgm) {
                            //fragment
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                getBaseAct().hideTwoBtnDialog();();");
                            } else {
                                //show
                                sbActions.append("\n                getBaseAct().showTwoBtnDialog(\"" + action.getTitle() + "\", \"" + action.getMsg() + "\", false, new OnTwoBtnClick() {\n" +
                                        "                    @Override\n" +
                                        "                    public void onOk() {\n");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                sbActions.append(
                                        "\n                    }\n" +
                                                "\n" +
                                                "                    @Override\n" +
                                                "                    public void onCancel() {\n" +
                                                "\n" +
                                                "                    }\n" +
                                                "                });");
                            }
                        } else {
                            //activity
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                hideTwoBtnDialog();();");
                            } else {
                                //show
                                sbActions.append("\n                showTwoBtnDialog(\"" + action.getTitle() + "\", \"" + action.getMsg() + "\", false, new OnTwoBtnClick() {\n" +
                                        "                    @Override\n" +
                                        "                    public void onOk() {\n");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                sbActions.append(
                                        "\n                    }\n" +
                                                "\n" +
                                                "                    @Override\n" +
                                                "                    public void onCancel() {\n" +
                                                "\n" +
                                                "                    }\n" +
                                                "                });");
                            }
                        }
                        break;
                    case ActionEntity.TYPE_DIALOG_EDIT:
                        if (fgm) {
                            //fragment
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                getBaseAct().hideTwoBtnEditDialog();");
                            } else {
                                //show
                                sbActions.append("\n                getBaseAct().showTwoBtnEditDialog(\"" + action.getTitle() + "\", \"" + action.getHintText() + "\", \"" + action.getDefText() + "\", false, new OnTwoBtnEditClick() {\n" +
                                        "                    @Override\n" +
                                        "                    public void onOk(String s) {\n");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                sbActions.append(
                                        "\n                    }\n" +
                                                "\n" +
                                                "                    @Override\n" +
                                                "                    public void onCancel() {\n" +
                                                "\n" +
                                                "                    }\n" +
                                                "                });");
                            }
                        } else {
                            //activity
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                hideTwoBtnEditDialog();");
                            } else {
                                //show
                                sbActions.append("\n                showTwoBtnEditDialog(\"" + action.getTitle() + "\", \"" + action.getHintText() + "\", \"" + action.getDefText() + "\", false, new OnTwoBtnEditClick() {\n" +
                                        "                    @Override\n" +
                                        "                    public void onOk(String s) {\n");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                sbActions.append(
                                        "\n                    }\n" +
                                                "\n" +
                                                "                    @Override\n" +
                                                "                    public void onCancel() {\n" +
                                                "\n" +
                                                "                    }\n" +
                                                "                });");
                            }
                        }
                        break;
                    case ActionEntity.TYPE_DIALOG_UPDATE:
                        if (fgm) {
                            //fragment
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                getBaseAct().hideUpdateDialog();");
                            } else {
                                //show
                                sbActions.append("\n                getBaseAct().showUpdateDialog(\"" + action.getTitle() + "\", \"" + action.getMsg() + "\", false, new OnOneBtnClickListener() {\n" +
                                        "                    @Override\n" +
                                        "                    public void onProgress(TextView textView, ProgressBar progressBar) {\n" +
                                        "\n" +
                                        "                    }\n" +
                                        "\n" +
                                        "                    @Override\n" +
                                        "                    public void onOK() {\n");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                sbActions.append(
                                        "\n                    }\n" +
                                                "                });");
                            }
                        } else {
                            //activity
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                hideUpdateDialog();");
                            } else {
                                //show
                                sbActions.append("\n                showUpdateDialog(\"" + action.getTitle() + "\", \"" + action.getMsg() + "\", false, new OnOneBtnClickListener() {\n" +
                                        "                    @Override\n" +
                                        "                    public void onProgress(TextView textView, ProgressBar progressBar) {\n" +
                                        "\n" +
                                        "                    }\n" +
                                        "\n" +
                                        "                    @Override\n" +
                                        "                    public void onOK() {\n");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                sbActions.append(
                                        "\n                    }\n" +
                                                "                });");
                            }
                        }
                        break;
                    case ActionEntity.TYPE_DIALOG_LIST:
                        if (fgm) {
                            //fragment
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                getBaseAct().hideListDialog();");
                            } else {
                                //show
                                String items = action.getItems();
                                if (items != null) {
                                    sbActions.append("\n                List<String> items = new ArrayList<String>();\n");
                                    String[] split = items.split(",");
                                    for (String s : split) {
                                        sbActions.append("                    items.add(\"" + s + "\");\n");
                                    }
                                    sbActions.append("                getBaseAct().showListDialog(items, false, null, new OnItemClick() {\n" +
                                            "                    @Override\n" +
                                            "                    public void onItemClick(int i, String s) {\n");
                                    sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                    sbActions.append(
                                            "\n                    }\n" + "                });");
                                }
                            }
                        } else {
                            //activity
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                hideListDialog();");
                            } else {
                                //show
                                String items = action.getItems();
                                if (items != null) {
                                    sbActions.append("\n                List<String> items = new ArrayList<String>();\n");
                                    String[] split = items.split(",");
                                    for (String s : split) {
                                        sbActions.append("                    items.add(\"" + s + "\");\n");
                                    }
                                    sbActions.append("                showListDialog(items, false, null, new OnItemClick() {\n" +
                                            "                    @Override\n" +
                                            "                    public void onItemClick(int i, String s) {\n");
                                    sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                    sbActions.append(
                                            "\n                    }\n" +
                                                    "                });");
                                }
                            }
                        }
                        break;
                    case ActionEntity.TYPE_DIALOG_TWO_BTN_IOS:
                        if (fgm) {
                            //fragment
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                getBaseAct().hideTwoBtnDialog();");
                            } else {
                                //show
                                sbActions.append("\n                getBaseAct().showTwoBtnDialogIOSStyle(\"" + action.getTitle() + "\", \"" + action.getMsg() + "\", \"" + action.getCancelText() + "\", \"" + action.getOkText() + "\", 0xff000000, 0xff000000, false, new OnIOSTwoBtnEditClick() {\n" +
                                        "                    @Override\n" +
                                        "                    public void onLeftClick() {\n" +
                                        "                        \n" +
                                        "                    }\n" +
                                        "\n" +
                                        "                    @Override\n" +
                                        "                    public void onRightClick() {\n");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                sbActions.append(
                                        "\n                    }\n" +
                                                "                });");
                            }
                        } else {
                            //activity
                            if (action.getShowOrHide()) {
                                //hide
                                sbActions.append("\n                hideTwoBtnDialog();");
                            } else {
                                //show
                                sbActions.append("\n                showTwoBtnDialogIOSStyle(\"" + action.getTitle() + "\", \"" + action.getMsg() + "\", \"" + action.getCancelText() + "\", \"" + action.getOkText() + "\", 0xff000000, 0xff000000, false, new OnIOSTwoBtnEditClick() {\n" +
                                        "                    @Override\n" +
                                        "                    public void onLeftClick() {\n" +
                                        "                        \n" +
                                        "                    }\n" +
                                        "\n" +
                                        "                    @Override\n" +
                                        "                    public void onRightClick() {\n");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                sbActions.append(
                                        "\n                    }\n" +
                                                "                });");
                            }
                        }
                        break;
                    case ActionEntity.TYPE_CHOOSE_PIC:
                        if (fgm) {
                            //fragment
                            sbImp.append("\nimport android.os.Environment;");
                            sbActions.append("\n                getBaseAct().choosePhoto(Environment.getExternalStorageDirectory().getAbsolutePath(), true);");
                        } else {
                            //activity
                            sbImp.append("\nimport android.os.Environment;");
                            sbActions.append("\n                choosePhoto(Environment.getExternalStorageDirectory().getAbsolutePath(), true);");
                        }
                        break;
                    case ActionEntity.TYPE_USE_API:
                        if (fgm) {
                            //fragment
                            InterfaceEntity interfaceEntity = mInterfaceService.get(action.getOkApiId());
                            if (interfaceEntity != null) {
                                int requestParamsNo = interfaceEntity.getRequestParamsNo();
                                ActivityEntity targetAct = null;
                                if (action.getOkActId() != null && action.getOkActId().length() > 0) {
                                    targetAct = mActivityService.get(action.getOkActId());
                                }
                                String url = interfaceEntity.getPath();
                                String m = url.substring(url.lastIndexOf("/") + 1, url.length());
                                String beanClazz = m.substring(0, 1).toUpperCase() + m.substring(1, m.length()) + "Result";
                                if (m.length() > 0) {
                                    m = m.substring(0, 1).toLowerCase() + m.substring(1);
                                }
                                sbActions.append("\n        getBaseAct().showPd(getString(R.string.loading_text), false);\n" +
                                        "        Api.getApi" + action.getOkApiGroupPosition() + "()\n" +
                                        "                ." + m + "(");
                                if (requestParamsNo > 0) {
                                    for (int i1 = 0; i1 < requestParamsNo; i1++) {
                                        sbActions.append("\"\", ");
                                    }
                                    sbActions.deleteCharAt(sbActions.length() - 1);
                                    sbActions.deleteCharAt(sbActions.length() - 1);
                                }
                                sbActions.append(
                                        ")\n" +
                                                "                .compose(RxHelper.<" + beanClazz + ">io_main())\n" +
                                                "                .subscribe(new Subscriber<" + beanClazz + ">() {\n" +
                                                "                    @Override\n" +
                                                "                    public void onCompleted() {\n" +
                                                "\n" +
                                                "                    }\n" +
                                                "\n" +
                                                "                    @Override\n" +
                                                "                    public void onError(Throwable e) {\n" +
                                                "                        hidePd();\n" +
                                                "                        ToastUtils.showCustomBgToast(getString(R.string.no_net_text) + e.toString());\n" +
                                                "                    }\n" +
                                                "\n" +
                                                "                    @Override\n" +
                                                "                    public void onNext(" + beanClazz + " result) {\n" +
                                                "                        hidePd();\n" +
                                                "                    /*    ToastUtils.showCustomBgToast(result.getMsg());\n" +
                                                "                        if (result.getCode() == 1) {\n" +
                                                "                            Intent intent = new Intent(getActivity(), " + (targetAct == null ? actName : targetAct.getName()) + ".class);\n" +
                                                "                            startActWithIntent(intent);\n" +
                                                "                        }\n*/\n");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                sbActions.append("\n                    }\n" +
                                        "                });\n");
                            }
                        } else {
                            //activity
                            InterfaceEntity interfaceEntity = mInterfaceService.get(action.getOkApiId());
                            if (interfaceEntity != null) {
                                int requestParamsNo = interfaceEntity.getRequestParamsNo();
                                ActivityEntity targetAct = null;
                                if (action.getOkActId() != null && action.getOkActId().length() > 0) {
                                    targetAct = mActivityService.get(action.getOkActId());
                                }
                                String url = interfaceEntity.getPath();
                                String m = url.substring(url.lastIndexOf("/") + 1, url.length());
                                String beanClazz = m.substring(0, 1).toUpperCase() + m.substring(1, m.length()) + "Result";
                                if (m.length() > 0) {
                                    m = m.substring(0, 1).toLowerCase() + m.substring(1);
                                }
                                sbActions.append("\n        showPd(getString(R.string.loading_text), false);\n" +
                                        "        Api.getApi" + action.getOkApiGroupPosition() + "()\n" +
                                        "                ." + m + "(");
                                if (requestParamsNo > 0) {
                                    for (int i1 = 0; i1 < requestParamsNo; i1++) {
                                        sbActions.append("\"\", ");
                                    }
                                    sbActions.deleteCharAt(sbActions.length() - 1);
                                    sbActions.deleteCharAt(sbActions.length() - 1);
                                }
                                sbActions.append(
                                        ")\n" +
                                                "                .compose(RxHelper.<" + beanClazz + ">io_main())\n" +
                                                "                .subscribe(new Subscriber<" + beanClazz + ">() {\n" +
                                                "                    @Override\n" +
                                                "                    public void onCompleted() {\n" +
                                                "\n" +
                                                "                    }\n" +
                                                "\n" +
                                                "                    @Override\n" +
                                                "                    public void onError(Throwable e) {\n" +
                                                "                        hidePd();\n" +
                                                "                        ToastUtils.showCustomBgToast(getString(R.string.no_net_text) + e.toString());\n" +
                                                "                    }\n" +
                                                "\n" +
                                                "                    @Override\n" +
                                                "                    public void onNext(" + beanClazz + " result) {\n" +
                                                "                        hidePd();\n" +
                                                "                    /*    ToastUtils.showCustomBgToast(result.getMsg());\n" +
                                                "                        if (result.getCode() == 1) {\n" +
                                                "                            Intent intent = new Intent(" + actName + ".this, " + (targetAct == null ? actName : targetAct.getName()) + ".class);\n" +
                                                "                            startActWithIntent(intent);\n" +
                                                "                        }\n*/");
                                sbActions.append(genearteActions(widgetId, action.getId(), fgm, packageName, actName, sbStrings, sbImp, sbData));
                                sbActions.append("                    }\n" +
                                        "                });\n");
                            }
                        }
                        break;
                    case ActionEntity.TYPE_TARGET_ACT:
                        if (fgm) {
                            //fragment
                            String okActId = action.getOkActId();
                            ActivityEntity activityEntity = mActivityService.get(okActId);
                            if (activityEntity != null) {
                                sbActions.append("\n                Intent intent = new Intent(getActivity(), " + activityEntity.getName() + ".class);\n" +
                                        "                startActWithIntent(intent);");
                                sbImp.append("\nimport " + packageName + ".ui.act." + activityEntity.getName() + ";");
                            }
                        } else {
                            //activity
                            String okActId = action.getOkActId();
                            ActivityEntity activityEntity = mActivityService.get(okActId);
                            if (activityEntity != null) {
                                sbActions.append("\n                Intent intent = new Intent(" + actName + ".this, " + activityEntity.getName() + ".class);\n" +
                                        "                startActWithIntent(intent);");
                            }
                        }
                        break;
                    case ActionEntity.TYPE_CLOSE_ACT:
                        if (fgm) {
                            //fragment
                            sbActions.append("\n        getBaseAct().closeAct();");
                        } else {
                            //activity
                            sbActions.append("\n        closeAct();");
                        }
                        break;
                    case ActionEntity.TYPE_CLOSE_ALL_ACT:
                        if (fgm) {
                            //fragment
                            sbActions.append("\n        getBaseAct().closeAllAct();");
                        } else {
                            //activity
                            sbActions.append("\n        closeAllAct();");
                        }
                        break;
                }
            }
        }
        return sbActions.toString();
    }

    private void copyGradleWrapper(String rootPath, String appDirPath) throws IOException {
        FileUtil.copyDir(new File(rootPath + File.separator + "res" + File.separator + "wrapper")
                , new File(appDirPath + File.separator + "gradle" + File.separator + "wrapper"));
    }

    private void createBuildGralde(String appDirPath) throws IOException {
        FileUtils.saveFileToPathWithName("buildscript {\n" +
                "    repositories {\n" +
                "        jcenter()\n" +
                "    }\n" +
                "    dependencies {\n" +
                "        classpath 'com.android.tools.build:gradle:2.3.1'\n" +
                "\n" +
                "        // NOTE: Do not place your application dependencies here; they belong\n" +
                "        // in the individual module build.gradle files\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "allprojects {\n" +
                "    repositories {\n" +
                "        jcenter()\n" +
                "        maven {\n" +
                "            url \"https://jitpack.io\"\n" +
                "        }\n" +
                "        maven {\n" +
                "            url \"https://maven.google.com\"\n" +
                "        }\n"+
                "    }\n" +
                "}\n" +
                "\n" +
                "task clean(type: Delete) {\n" +
                "    delete rootProject.buildDir\n" +
                "}\n", appDirPath, "build.gradle");
    }

    private void createGradleProperties(String appDirPath) throws IOException {
        FileUtils.saveFileToPathWithName("# Project-wide Gradle settings.\n" +
                "\n" +
                "# IDE (e.g. Android Studio) users:\n" +
                "# Gradle settings configured through the IDE *will override*\n" +
                "# any settings specified in this file.\n" +
                "\n" +
                "# For more details on how to configure your build environment visit\n" +
                "# http://www.gradle.org/docs/current/userguide/build_environment.html\n" +
                "\n" +
                "# Specifies the JVM arguments used for the daemon process.\n" +
                "# The setting is particularly useful for tweaking memory settings.\n" +
                "org.gradle.jvmargs=-Xmx1536m\n" +
                "\n" +
                "# When configured, Gradle will run in incubating parallel mode.\n" +
                "# This option should only be used with decoupled projects. More details, visit\n" +
                "# http://www.gradle.org/docs/current/userguide/multi_project_builds.html#sec:decoupled_projects\n" +
                "# org.gradle.parallel=true\n", appDirPath, "gradle.properties");
    }

    private void createSettingGradleFile(String appDirPath) throws IOException {
        FileUtils.saveFileToPathWithName("include ':app'\n", appDirPath, "settings.gradle");
    }

    /***********************************下载 项目文件 结束**************************************/


    private String generateVarName(String prefix, String resId) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        if (resId.contains("_")) {
            String[] split = resId.split("_");
            for (String s : split) {
                if (s.length() > 0) {
                    sb.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase());
                }
            }
        } else {
            sb.append(resId.substring(0, 1).toUpperCase()).append(resId.substring(1).toLowerCase());
        }
        return sb.toString();
    }

}
