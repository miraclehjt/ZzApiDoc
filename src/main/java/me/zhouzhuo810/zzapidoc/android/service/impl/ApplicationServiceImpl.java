package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ApplicationDao;
import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.ApplicationEntity;
import me.zhouzhuo810.zzapidoc.android.entity.WidgetEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.android.service.ApplicationService;
import me.zhouzhuo810.zzapidoc.android.service.WidgetService;
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
import me.zhouzhuo810.zzapidoc.project.entity.ProjectEntity;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceService;
import me.zhouzhuo810.zzapidoc.project.service.ProjectService;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.apache.log4j.Logger;
import org.aspectj.util.FileUtil;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Resource(name = "widgetServiceImpl")
    WidgetService mWidgetService;

    @Resource(name = "projectServiceImpl")
    ProjectService mProjectService;

    @Resource(name = "interfaceServiceImpl")
    InterfaceService mInterfaceService;


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
    public BaseResult addApplication(String appName, String versionName, String packageName, MultipartFile logo,
                                     String colorMain, int minSDK, int compileSDK, int targetSDK, int versionCode,
                                     boolean multiDex, boolean minifyEnabled, String apiId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法");
        }
        ApplicationEntity entity = new ApplicationEntity();
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
        entity.setPackageName(packageName);
        entity.setVersionCode(versionCode);
        entity.setVersionName(versionName);
        entity.setTargetSDK(targetSDK);
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
            map.put("minSDK", applicationEntity.getMinSDK());
            map.put("compileSDK", applicationEntity.getCompileSDK());
            map.put("targetSDK", applicationEntity.getTargetSDK());
            map.put("versionCode", applicationEntity.getVersionCode());
            map.put("versionName", applicationEntity.getVersionName());
            map.put("logo", applicationEntity.getLogo());
            map.put("createTime", DataUtils.formatDate(applicationEntity.getCreateTime()));
            map.put("colorMain", applicationEntity.getColorMain());
            map.put("packageName", applicationEntity.getPackageName());
            map.put("multiDex", applicationEntity.getMultiDex());
            map.put("minifyEnabled", applicationEntity.getMinifyEnabled());
            map.put("apiId", applicationEntity.getApiId());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);

    }

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

                createSettingGradleFile(appDirPath);
                createGradleProperties(appDirPath);
                createBuildGralde(appDirPath);
                copyGradleWrapper(realPath, appDirPath);
                generateApp(realPath, appDirPath, app);

                /*压缩文件*/
                String zipName = System.currentTimeMillis() + ".zip";
                String zipPath = mPath + File.separator + zipName;
                ZipUtils.doCompress(appDirPath, zipPath);

                /*压缩完毕，删除源文件*/
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

    private void generateApp(String rootPath, String appDirPath, ApplicationEntity app) throws IOException {
        StringBuilder sbStrings = new StringBuilder();
        sbStrings.append("<resources>\n" +
                "    <string name=\"app_name\">" + app.getAppName() + "</string>\n" +
                "    <string name=\"ok_text\">确定</string>\n"+
                "    <string name=\"cancel_text\">取消</string>\n"+
                "    <string name=\"submitting_text\">提交中...</string>\n"+
                "    <string name=\"loading_text\">加载中...</string>\n"+
                "    <string name=\"no_data_text\">暂无数据</string>");
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
        generateJavaAndLayoutAndAndroidManifest(app, appDirPath, packageName, sbStrings);
        File resDir = new File(appDirPath + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "res");
        if (!resDir.exists()) {
            resDir.mkdirs();
        }

        generateJavaAndRes(rootPath, appDirPath, app.getLogo(), app);

        String valuesPath = appDirPath + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "res"
                + File.separator + "values";
        FileUtils.saveFileToServer(sbStrings.toString(),valuesPath, "strings.xml");

    }

    private void generateJavaAndRes(String rootPath, String appDirPath, String logoPath, ApplicationEntity app) throws IOException {

        /*app/build.gradle*/
        FileUtils.saveFileToServer("apply plugin: 'com.android.application'\n" +
                "\n" +
                "android {\n" +
                "    compileSdkVersion 25\n" +
                "    buildToolsVersion \"25.0.3\"\n" +
                "    defaultConfig {\n" +
                "        applicationId \"" + app.getPackageName() + "\"\n" +
                "        minSdkVersion " + app.getMinSDK() + "\n" +
                "        targetSdkVersion " + app.getTargetSDK() + "\n" +
                "        versionCode " + app.getVersionCode() + "\n" +
                "        versionName \"" + app.getVersionName() + "\"\n" +
                "    }\n" +
                "\n" +
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
                "    buildTypes {\n" +
                "        release {\n" +
                "            minifyEnabled false\n" +
                "            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "dependencies {\n" +
                "    compile fileTree(dir: 'libs', include: ['*.jar'])\n" +
                "    //bugly\n" +
                "    compile 'com.tencent.bugly:crashreport:latest.release'\n" +
                "    //zzandframe\n" +
                "    compile 'com.github.zhouzhuo810:ZzAndFrame:1.0.7'\n" +
                "    //xutils\n" +
                "    compile 'org.xutils:xutils:3.1.26'\n" +
                "    //Glide\n" +
                "    compile 'com.github.bumptech.glide:glide:3.7.0'\n" +
                "    //RxPermissions\n" +
                "    compile 'com.tbruyelle.rxpermissions:rxpermissions:0.9.4@aar'\n" +
                "    //PhotoView\n" +
                "    compile 'com.github.chrisbanes:PhotoView:2.0.0'\n" +
                "    //Logger\n" +
                "    compile 'com.orhanobut:logger:2.1.1'\n" +
                "    //okgo\n" +
                "    compile 'com.lzy.net:okgo:3.0.4'\n" +
                "    //AndroidPickerView\n" +
                "    compile 'com.contrarywind:Android-PickerView:3.2.5'\n" +
                "    compile 'com.github.yalantis:ucrop:2.2.1'\n" +
                "\n" +
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
            FileUtil.copyFile(new File(logoPath), new File(mipmapPath + File.separator + new File(logoPath).getName()));
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

        FileUtils.saveFileToServer(style, valuesPath, "styles.xml");
        /*colors*/
        String color = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n" +
                "    <color name=\"colorMain\">#" + app.getColorMain() + "</color>\n" +
                "    <color name=\"colorPrimary\">#" + app.getColorMain() + "</color>\n" +
                "    <color name=\"colorPrimaryDark\">#" + app.getColorMain() + "</color>\n" +
                "    <color name=\"colorAccent\">#" + app.getColorMain() + "</color>\n" +
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
                "    <color name=\"colorTabPress\">#" + app.getColorMain() + "</color>\n" +
                "    <color name=\"colorExit\">#d44a4a</color>\n" +
                "    <color name=\"colorExitPress\">#dc5353</color>\n" +
                "</resources>\n";
        FileUtils.saveFileToServer(color, valuesPath, "colors.xml");
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
        FileUtil.copyDir(new File(rootPath + File.separator + "res" + File.separator + "drawable"), new File(drawablePath));
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

    }

    private void generateJavaAndLayoutAndAndroidManifest(ApplicationEntity app, String appDirPath, String packageName, StringBuilder sbStrings) throws IOException {
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
            logoName = name.substring(0, name.indexOf("."));
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
                "    <!--bugly end-->\n" +
                "\n" +
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

        /*查找splashactivity*/
        List<ActivityEntity> activityEntities = mActivityService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("applicationId", app.getId()),
                Restrictions.eq("type", ActivityEntity.TYPE_SPLASH)
        });
        String layoutPath = filePath
                + File.separator + "res"
                + File.separator + "layout";

        String packagePath = packageName.replace(".", File.separator);
        String javaPath = appDirPath
                + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "java"
                + File.separator + packagePath;
        if (activityEntities != null && activityEntities.size() > 0) {
            ActivityEntity activityEntity = activityEntities.get(0);
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
            FileUtils.saveFileToServer("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<FrameLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                    "    android:layout_width=\"match_parent\"\n" +
                    "    android:layout_height=\"match_parent\"\n" +
                    "    android:orientation=\"vertical\">\n" +
                    "\n" +
                    "    <ImageView\n" +
                    "        android:id=\"@+id/iv_pic\"\n" +
                    "        android:layout_width=\"match_parent\"\n" +
                    "        android:layout_height=\"match_parent\"\n" +
                    "        android:src=\"@mipmap/ic_launcher\" />\n" +
                    "\n" +
                    "    <TextView\n" +
                    "        android:id=\"@+id/tv_jump\"\n" +
                    "        android:layout_width=\"wrap_content\"\n" +
                    "        android:layout_height=\"wrap_content\"\n" +
                    "        android:layout_gravity=\"right|top\"\n" +
                    "        android:layout_marginRight=\"30px\"\n" +
                    "        android:layout_marginTop=\"30px\"\n" +
                    "        android:background=\"@drawable/btn_main_selector\"\n" +
                    "        android:paddingBottom=\"6px\"\n" +
                    "        android:paddingLeft=\"14px\"\n" +
                    "        android:paddingRight=\"14px\"\n" +
                    "        android:paddingTop=\"6px\"\n" +
                    "        android:textColor=\"@color/colorWhite\"\n" +
                    "        android:textSize=\"36px\"\n" +
                    "        android:visibility=\"gone\" />\n" +
                    "</FrameLayout>", layoutPath, "activity_splash.xml");
            /*java*/
            FileUtils.saveFileToServer("package " + packageName + ".ui.act;\n" +
                    "\n" +
                    "import android.content.Intent;\n" +
                    "import android.os.Bundle;\n" +
                    "import android.support.annotation.Nullable;\n" +
                    "import android.view.View;\n" +
                    "import android.widget.ImageView;\n" +
                    "import android.widget.TextView;\n" +
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
                    "}\n", javaPath+File.separator+"ui"+File.separator+"act", activityEntity.getName() + ".java");
        }

        /*查找其他act*/
        List<ActivityEntity> activityEntities1 = mActivityService.executeCriteria(new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("applicationId", app.getId()),
                Restrictions.ne("type", ActivityEntity.TYPE_SPLASH)
        });
        if (activityEntities1 != null) {
            for (ActivityEntity activityEntity : activityEntities1) {
                sbManifest.append("        <activity\n" +
                        "            android:name=\".ui.act." + activityEntity.getName() + "\"\n" +
                        "            android:configChanges=\"orientation|keyboardHidden|layoutDirection|screenSize|screenLayout\"\n" +
                        "            android:screenOrientation=\"portrait\"\n" +
                        "            android:windowSoftInputMode=\"stateAlwaysHidden\" />\n");
                switch (activityEntity.getType()) {
                    case ActivityEntity.TYPE_LV_ACT:
                        generateLvActLayout(layoutPath, activityEntity, app, sbStrings);
                        generateLvActJava(javaPath, activityEntity, app);
                        break;
                    case ActivityEntity.TYPE_RV_ACT:

                        break;
                    case ActivityEntity.TYPE_TAB_ACT:

                        break;
                    case ActivityEntity.TYPE_SETTING:

                        break;
                    case ActivityEntity.TYPE_SUBMIT:
                        generateSubmitActJavaAndLayout(layoutPath, javaPath, activityEntity, app, sbStrings);
                        break;
                    case ActivityEntity.TYPE_DETAILS:

                        break;
                }

            }
        }
        sbManifest.append("        <provider\n" +
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

        FileUtils.saveFileToServer(sbManifest.toString(), filePath, "AndroidManifest.xml");
    }

    private void generateSubmitActJavaAndLayout(String layoutPath, String javaPath, ActivityEntity activityEntity, ApplicationEntity app, StringBuilder sbStrings) throws IOException {
        List<WidgetEntity> widgetEntities = mWidgetService.executeCriteria(
                new Criterion[]{
                        Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                        Restrictions.eq("relativeId", activityEntity.getId())
                }
        );
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
        final String realLayoutName = layoutName.toLowerCase();
        layoutName = layoutName.replace("activity_", "").replace("__", "_").toLowerCase();
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
        sbJava.append("package "+app.getPackageName()+".ui.act;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import android.support.annotation.Nullable;\n" +
                "import android.widget.Button;\n" +
                "import android.widget.EditText;\n" +
                "import android.widget.ImageView;\n" +
                "import android.widget.TextView;\n" +
                "\n" +
                "import "+app.getPackageName()+".R;\n" +
                "import zhouzhuo810.me.zzandframe.ui.act.BaseActivity;\n" +
                "import zhouzhuo810.me.zzandframe.ui.widget.TitleBar;\n" +
                "\n" +
                "/**\n" +
                " *\n" +
                " * Created by admin on 2017/8/27.\n" +
                " */\n" +
                "public class "+activityEntity.getName()+" extends BaseActivity {\n");
        if (widgetEntities != null && widgetEntities.size() > 0) {
            /*变量声明*/
            StringBuilder sbDef = new StringBuilder();
            StringBuilder sbInit = new StringBuilder();
            StringBuilder sbEvent = new StringBuilder();
            StringBuilder sbSubmit = new StringBuilder();
            for (int i = 0; i < widgetEntities.size(); i++) {
                WidgetEntity widgetEntity = widgetEntities.get(i);
                switch (widgetEntity.getType()) {
                    case WidgetEntity.TYPE_EDIT_ITEM:
                        sbDef.append("\n    private EditText et_"+widgetEntity.getResId()+";");
                        sbDef.append("\n    private ImageView iv_clear_"+widgetEntity.getResId()+";");
                        sbInit.append("\n        et_"+widgetEntity.getResId()+" = (EditText) findViewById(R.id.et_"+widgetEntity.getResId()+");");
                        sbInit.append("\n        iv_clear_"+widgetEntity.getResId()+" = (ImageView) findViewById(R.id.iv_clear_"+widgetEntity.getResId()+");");
                        sbEvent.append("\n        setEditListener(et_"+widgetEntity.getResId()+", iv_clear_"+widgetEntity.getResId()+");");
                        sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_text\">" + widgetEntity.getTitle() + "</string>\n");
                        sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_hint_text\">请输入" + widgetEntity.getTitle() + "</string>\n");
                        sbSubmit.append("\n        String "+widgetEntity.getResId()+" = et_"+widgetEntity.getResId()+".getText().toString().trim();");
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
                    case WidgetEntity.TYPE_TITLE_BAR:
                        sbDef.append("\n    private TitleBar title_bar;");
                        sbInit.append("\n        title_bar = (TitleBar) findViewById(R.id.title_bar);");
                        sbEvent.append("\n        title_bar.setOnTitleClickListener(new TitleBar.OnTitleClick() {\n" +
                                "            @Override\n" +
                                "            public void onLeftClick(ImageView imageView, TextView textView) {\n" +
                                "                closeAct();\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onTitleClick(TextView textView) {\n" +
                                "\n" +
                                "            }\n" +
                                "\n" +
                                "            @Override\n" +
                                "            public void onRightClick(ImageView imageView, TextView textView) {\n" +
                                "\n" +
                                "            }\n" +
                                "        });");
                        sbStrings.append("    <string name=\"" + layoutName + "_text\">" + activityEntity.getTitle() + "</string>\n");
                        sbLayout.append("\n    <zhouzhuo810.me.zzandframe.ui.widget.TitleBar\n" +
                                "        android:id=\"@+id/title_bar\"\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"@dimen/title_height\"\n" +
                                "        android:background=\"@color/colorPrimary\"\n" +
                                "        app:leftImg=\"@mipmap/ic_launcher\"\n" +
                                "        app:showLeftImg=\"" + widgetEntity.getShowLeftTitleImg() + "\"\n" +
                                "        app:showLeftLayout=\"" + widgetEntity.getShowLeftTitleLayout() + "\"\n" +
                                "        app:showLeftText=\"" + widgetEntity.getShowLeftTitleText() + "\"\n" +
                                "        app:textColorAll=\"@color/colorWhite\"\n" +
                                "        app:textSizeTitle=\"@dimen/title_text_size\"\n" +
                                "        app:titleText=\"@string/" + layoutName + "_text\" />");
                        break;
                    case WidgetEntity.TYPE_BTN_ITEM:
                        sbDef.append("\n    private Button btn_"+widgetEntity.getResId()+";");
                        sbInit.append("\n        btn_"+widgetEntity.getResId()+" = (Button) findViewById(R.id.btn_"+widgetEntity.getResId()+");");
                        sbEvent.append("\n        btnLogin.setOnClickListener(new View.OnClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onClick(View v) {\n" +
                                "                doSubmit();\n" +
                                "            }\n" +
                                "        });");
                        sbStrings.append("    <string name=\"" + widgetEntity.getResId() + "_text\">" + activityEntity.getTitle() + "</string>\n");
                        sbLayout.append("\n    <Button\n" +
                                "        android:id=\"@+id/btn_"+widgetEntity.getResId()+"\"\n" +
                                "        android:layout_width=\"match_parent\"\n" +
                                "        android:layout_height=\"120px\"\n" +
                                "        android:layout_marginBottom=\"50px\"\n" +
                                "        android:layout_marginLeft=\"40px\"\n" +
                                "        android:layout_marginRight=\"40px\"\n" +
                                "        android:layout_marginTop=\"40px\"\n" +
                                "        android:background=\"@drawable/btn_save_selector\"\n" +
                                "        android:text=\"@string/"+widgetEntity.getResId()+"_text\"\n" +
                                "        android:textColor=\"#fff\"\n" +
                                "        android:textSize=\"@dimen/submit_btn_text_size\" />");
                        break;
                }
            }
            sbJava.append(sbDef.toString())
                    .append("\n\n    @Override\n" +
                            "    public int getLayoutId() {\n" +
                            "        return R.layout."+realLayoutName+";\n" +
                            "    }\n")
                    .append("    @Override\n" +
                            "    public void initView() {\n")
                    .append(sbInit.toString())
                    .append("    }\n" +
                            "\n" +
                            "    @Override\n" +
                            "    public void initData() {\n" +
                            "\n" +
                            "    }\n")
                    .append("\n" +
                            "    @Override\n" +
                            "    public void initEvent() {\n")
                    .append(sbEvent.toString())
                    .append("\n    }\n" +
                            "\n" +
                            "    private void doSubmit() {\n")
                    .append(sbSubmit.toString())
                    .append("\n    }\n");
        } else {
            sbJava.append("\n\n    @Override\n" +
                    "    public int getLayoutId() {\n" +
                    "        return R.layout.activity_login;\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void initView() {\n" +
                    "        \n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void initData() {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void initEvent() {\n" +
                    "        \n" +
                    "    }");
        }
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
        FileUtils.saveFileToServer(sbLayout.toString(), layoutPath, realLayoutName+".xml");
        FileUtils.saveFileToServer(sbJava.toString(), javaPath+File.separator+"ui"+File.separator+"act", activityEntity.getName()+".java");


    }


    private void generateLvActJava(String javaPath, ActivityEntity activityEntity, ApplicationEntity app) throws IOException {
        String name = activityEntity.getName();
        String packageName = app.getPackageName();
        String javaCode = "package " + packageName + ".ui.act;\n" +
                "\n" +
                "import android.content.Intent;\n" +
                "import android.os.Bundle;\n" +
                "import android.support.annotation.Nullable;\n" +
                "import android.support.v4.widget.SwipeRefreshLayout;\n" +
                "import android.view.View;\n" +
                "import android.widget.AdapterView;\n" +
                "import android.widget.ListView;\n" +
                "import android.widget.RelativeLayout;\n" +
                "import android.widget.TextView;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.Arrays;\n" +
                "import java.util.List;\n" +
                "\n" +
                "import " + packageName + ".R;\n" +
                "import " + packageName + ".common.api.Api;\n" +
                "import " + packageName + ".common.base.BaseActivity;\n" +
                "import " + packageName + ".common.rx.RxHelper;\n" +
                "import " + packageName + ".common.utils.ToastUtils;\n" +
                "import rx.Subscriber;\n" +
                "\n" +
                "/**\n" +
                " * Created by zhouzhuo810 on 2017/8/11.\n" +
                " */\n" +
                "public class " + name + " extends BaseActivity {\n" +
                "\n" +
                "    private RelativeLayout rlBack;\n" +
                "    private RelativeLayout rlRight;\n" +
                "    private SwipeRefreshLayout refresh;\n" +
                "    private ListView lv;\n" +
                "    private TextView tvNoData;\n" +
                "  //  private List<GetAllMyActivityResult.DataBean> list;\n" +
                "  //  private ActivityListAdapter adapter;\n" +
                "    private boolean choose;\n" +
                "\n" +
                "    @Override\n" +
                "    public int getLayoutId() {\n" +
                "        return R.layout.activity_activity_manage;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean defaultBack() {\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void initView() {\n" +
                "        rlBack = (RelativeLayout) findViewById(R.id.rl_back);\n" +
                "        rlRight = (RelativeLayout) findViewById(R.id.rl_right);\n" +
                "        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);\n" +
                "        lv = (ListView) findViewById(R.id.lv);\n" +
                "        tvNoData = (TextView) findViewById(R.id.tv_no_data);\n" +
                "\n" +
                "    //    list = new ArrayList<>();\n" +
                "    //    adapter = new ActivityListAdapter(this, list, R.layout.list_item_interface_group, true);\n" +
                "    //    lv.setAdapter(adapter);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void initData() {\n" +
                "        choose = getIntent().getBooleanExtra(\"choose\", false);\n" +
                "    }\n" +
                "\n" +
                "    private void getData() {\n" +
                "\n" +
                "     /*   Api.getApi0()\n" +
                "                .getAllMyActivity(appId, getUserId())\n" +
                "                .compose(RxHelper.<GetAllMyActivityResult>io_main())\n" +
                "                .subscribe(new Subscriber<GetAllMyActivityResult>() {\n" +
                "                    @Override\n" +
                "                    public void onCompleted() {\n" +
                "\n" +
                "                    }\n" +
                "\n" +
                "                    @Override\n" +
                "                    public void onError(Throwable e) {\n" +
                "                        stopRefresh(refresh);\n" +
                "                    }\n" +
                "\n" +
                "                    @Override\n" +
                "                    public void onNext(GetAllMyActivityResult getAllInterfaceGroupResult) {\n" +
                "                        stopRefresh(refresh);\n" +
                "                        if (getAllInterfaceGroupResult.getCode() == 1) {\n" +
                "                            list = getAllInterfaceGroupResult.getData();\n" +
                "                            adapter.setmDatas(list);\n" +
                "                            adapter.notifyDataSetChanged();\n" +
                "                            if (list == null || list.size() == 0) {\n" +
                "                                tvNoData.setVisibility(View.VISIBLE);\n" +
                "                            } else {\n" +
                "                                tvNoData.setVisibility(View.GONE);\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }\n" +
                "                });*/\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void initEvent() {\n" +
                "        rlBack.setOnClickListener(new View.OnClickListener() {\n" +
                "            @Override\n" +
                "            public void onClick(View v) {\n" +
                "                closeAct();\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        rlRight.setOnClickListener(new View.OnClickListener() {\n" +
                "            @Override\n" +
                "            public void onClick(View v) {\n" +
                "            /*    Intent intent = new Intent(" + name + ".this, AddActivity.class);\n" +
                "                intent.putExtra(\"appId\", appId);\n" +
                "                startActWithIntent(intent);*/\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "\n" +
                "        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {\n" +
                "            @Override\n" +
                "            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {\n" +
                "                if (choose) {\n" +
                "                    Intent intent = new Intent();\n" +
                "                    //intent.putExtra(\"id\", adapter.getmDatas().get(position).getId());\n" +
                "                    //intent.putExtra(\"name\", adapter.getmDatas().get(position).getName());\n" +
                "                    setResult(RESULT_OK, intent);\n" +
                "                    closeAct();\n" +
                "                } else {\n" +
                "            /*        Intent intent = new Intent(" + name + ".this, WidgetManageActivity.class);\n" +
                "                    intent.putExtra(\"appId\", appId);\n" +
                "                    intent.putExtra(\"groupId\", adapter.getmDatas().get(position).getId());\n" +
                "                    startActWithIntent(intent);*/\n" +
                "                }\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {\n" +
                "            @Override\n" +
                "            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {\n" +
                "                showListDialog(Arrays.asList(\"删除\"), true, null, new OnItemClick() {\n" +
                "                    @Override\n" +
                "                    public void onItemClick(int pos, String content) {\n" +
                "                        switch (pos) {\n" +
                "                            case 0:\n" +
                "                                //deleteActivity(adapter.getmDatas().get(position).getId());\n" +
                "                                break;\n" +
                "                        }\n" +
                "                    }\n" +
                "                });\n" +
                "                return true;\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {\n" +
                "            @Override\n" +
                "            public void onRefresh() {\n" +
                "                getData();\n" +
                "            }\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    private void deleteActivity(String id) {\n" +
                "        showPd(getString(R.string.submiting_text), false);\n" +
                "     /*   Api.getApi0()\n" +
                "                .deleteActivity(id, getUserId())\n" +
                "                .compose(RxHelper.<DeleteActivityResult>io_main())\n" +
                "                .subscribe(new Subscriber<DeleteActivityResult>() {\n" +
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
                "                    public void onNext(DeleteActivityResult deleteInterfaceGroupResult) {\n" +
                "                        hidePd();\n" +
                "                        ToastUtils.showCustomBgToast(deleteInterfaceGroupResult.getMsg());\n" +
                "                        if (deleteInterfaceGroupResult.getCode() == 1) {\n" +
                "                            startRefresh(refresh);\n" +
                "                            getData();\n" +
                "                        }\n" +
                "                    }\n" +
                "                });*/\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void resume() {\n" +
                "        startRefresh(refresh);\n" +
                "        getData();\n" +
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
                "}\n";
        FileUtils.saveFileToServer(javaCode, javaPath+File.separator+"ui"+File.separator+"act", name + ".java");
    }

    private void generateLvActLayout(String layoutPath, ActivityEntity activityEntity, ApplicationEntity app, StringBuilder sbStrings) throws IOException {
        String name = activityEntity.getName();
        String layoutName = "";
        boolean isNotFirst = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                if (isNotFirst) {
                    layoutName += "_";
                }
                isNotFirst = true;
            }
            layoutName += c;
        }
        layoutName = layoutName.toLowerCase();
        sbStrings.append("    <string name=\"" + layoutName + "_text\">" + activityEntity.getTitle() + "</string>\n");
        FileUtils.saveFileToServer("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\"\n" +
                "    android:background=\"@color/colorGrayBg\"\n" +
                "    android:orientation=\"vertical\">\n" +
                "\n" +
                "    <zhouzhuo810.me.zzandframe.ui.widget.TitleBar\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"120px\"\n" +
                "        android:background=\"@color/colorMain\"\n" +
                "        app:leftImg=\"@drawable/back\"\n" +
                "        app:rightText=\"@string/add_text\"\n" +
                "        app:showLeftImg=\"true\"\n" +
                "        app:showLeftLayout=\"true\"\n" +
                "        app:showRightImg=\"false\"\n" +
                "        app:showRightLayout=\"true\"\n" +
                "        app:showRightText=\"true\"\n" +
                "        app:textColorAll=\"@color/colorWhite\"\n" +
                "        app:titleText=\"@string/" + layoutName + "_text\" />\n" +
                "\n" +
                "    <RelativeLayout\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"match_parent\">\n" +
                "\n" +
                "        <android.support.v4.widget.SwipeRefreshLayout\n" +
                "            android:id=\"@+id/refresh\"\n" +
                "            android:layout_width=\"match_parent\"\n" +
                "            android:layout_height=\"match_parent\">\n" +
                "\n" +
                "            <ListView\n" +
                "                android:id=\"@+id/lv\"\n" +
                "                android:layout_width=\"match_parent\"\n" +
                "                android:layout_height=\"wrap_content\"\n" +
                "                android:divider=\"@null\"\n" +
                "                android:listSelector=\"@null\" />\n" +
                "\n" +
                "        </android.support.v4.widget.SwipeRefreshLayout>\n" +
                "\n" +
                "        <TextView\n" +
                "            android:id=\"@+id/tv_no_data\"\n" +
                "            android:layout_width=\"wrap_content\"\n" +
                "            android:layout_height=\"wrap_content\"\n" +
                "            android:layout_centerInParent=\"true\"\n" +
                "            android:text=\"@string/no_data_text\"\n" +
                "            android:textColor=\"#999\"\n" +
                "            android:textSize=\"30px\"\n" +
                "            android:visibility=\"gone\" />\n" +
                "    </RelativeLayout>\n" +
                "\n" +
                "</LinearLayout>", layoutPath, layoutName + ".xml");
    }

    private void copyGradleWrapper(String rootPath, String appDirPath) throws IOException {
        FileUtil.copyDir(new File(rootPath + File.separator + "res" + File.separator + "wrapper")
                , new File(appDirPath + File.separator + "gradle" + File.separator + "wrapper"));
    }

    private void createBuildGralde(String appDirPath) throws IOException {
        FileUtils.saveFileToServer("buildscript {\n" +
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
                "        maven { url \"https://jitpack.io \n" +
                "\n" +
                "\" }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "task clean(type: Delete) {\n" +
                "    delete rootProject.buildDir\n" +
                "}\n", appDirPath, "build.gradle");
    }

    private void createGradleProperties(String appDirPath) throws IOException {
        FileUtils.saveFileToServer("org.gradle.jvmargs=-Xmx1536m\n", appDirPath, "gradle.properties");
    }

    private void createSettingGradleFile(String appDirPath) throws IOException {
        FileUtils.saveFileToServer("include ':app'\n", appDirPath, "setting.gradle");
    }

}
