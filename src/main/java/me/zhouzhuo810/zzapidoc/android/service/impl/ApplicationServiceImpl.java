package me.zhouzhuo810.zzapidoc.android.service.impl;

import me.zhouzhuo810.zzapidoc.android.dao.ApplicationDao;
import me.zhouzhuo810.zzapidoc.android.entity.ActivityEntity;
import me.zhouzhuo810.zzapidoc.android.entity.ApplicationEntity;
import me.zhouzhuo810.zzapidoc.android.service.ActivityService;
import me.zhouzhuo810.zzapidoc.android.service.ApplicationService;
import me.zhouzhuo810.zzapidoc.android.utils.ZipUtils;
import me.zhouzhuo810.zzapidoc.cache.entity.CacheEntity;
import me.zhouzhuo810.zzapidoc.cache.service.CacheService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.FileUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
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
        generateJavaAndLayoutAndAndroidManifest(app, appDirPath, packageName);
        File resDir = new File(appDirPath + File.separator + "app"
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "res");
        if (!resDir.exists()) {
            resDir.mkdirs();
        }

        generateJavaAndRes(rootPath, appDirPath, app.getLogo(), app);

    }

    private void generateJavaAndRes(String rootPath, String appDirPath, String logoPath, ApplicationEntity app) throws IOException {

        /*app/build.gradle*/
        FileUtils.saveFileToServer("apply plugin: 'com.android.application'\n" +
                "\n" +
                "android {\n" +
                "    compileSdkVersion 25\n" +
                "    buildToolsVersion \"25.0.3\"\n" +
                "    defaultConfig {\n" +
                "        applicationId \""+app.getPackageName()+"\"\n" +
                "        minSdkVersion "+app.getMinSDK()+"\n" +
                "        targetSdkVersion "+app.getTargetSDK()+"\n" +
                "        versionCode "+app.getVersionCode()+"\n" +
                "        versionName \""+app.getVersionName()+"\"\n" +
                "    }\n" +
                "\n" +
                "    applicationVariants.all {variant ->\n" +
                "        variant.outputs.each {output ->\n" +
                "            def outputFile = output.outputFile\n" +
                "            def fileName\n" +
                "            if (outputFile != null && outputFile.name.endsWith('.apk')) {\n" +
                "                if (variant.buildType.name.equals('release')) {\n" +
                "                    fileName = \""+app.getAppName()+"_${defaultConfig.versionName}.apk\"\n" +
                "                } else if (variant.buildType.name.equals('debug')) {\n" +
                "                    fileName = \""+app.getAppName()+"_${defaultConfig.versionName}_debug.apk\"\n" +
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

    }

    private void generateJavaAndLayoutAndAndroidManifest(ApplicationEntity app, String appDirPath, String packageName) throws IOException {
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
                "        android:icon=\"@mipmap/"+logoName+"\"\n" +
                "        android:label=\"@string/app_name\"\n" +
                "        android:roundIcon=\"@mipmap/"+logoName+"\"\n" +
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
            String layoutPath = filePath
                    + File.separator + "res"
                    + File.separator + "layout";
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
            String packagePath = packageName.replace(".", File.separator);
            String javaPath = appDirPath
                    + File.separator + "app"
                    + File.separator + "src"
                    + File.separator + "main"
                    + File.separator + "java"
                    + File.separator + packagePath;
            FileUtils.saveFileToServer("package "+packageName+".ui.act;\n" +
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
                    "import "+packageName+".R;\n" +
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
                    "        final int duration = "+activityEntity.getSplashSecond()+";\n" +
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
                    "                            Intent intent = new Intent(SplashActivity.this, "+activityEntity.getTargetActName()+".class);\n" +
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
                    "                Intent intent = new Intent(SplashActivity.this, "+activityEntity.getTargetActName()+".class);\n" +
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
                    "}\n", javaPath, activityEntity.getName()+".java");
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


            }
        }
        sbManifest.append("        <provider\n" +
                "            android:name=\"android.support.v4.content.FileProvider\"\n" +
                "            android:authorities=\"me.zhouzhuo810.zzapidoc.provider\"\n" +
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
