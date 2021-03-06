package me.zhouzhuo810.zzapidoc.android.widget.apicreator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.zhouzhuo810.zzapidoc.android.widget.apicreator.bean.ArgEntity;
import me.zhouzhuo810.zzapidoc.common.utils.FileUtils;
import me.zhouzhuo810.zzapidoc.project.entity.ApiEntity;
import org.apache.tools.ant.util.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/8/27.
 */
public class ApiTool {

    public static void createApi(String json, String packageName, String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            Gson gson = new GsonBuilder().create();
            ApiEntity api = gson.fromJson(json, ApiEntity.class);

            if (api != null) {

                List<ApiEntity.ModulesBean> modules = api.getModules();
                if (modules != null) {
                    for (int i = 0; i < modules.size(); i++) {
                        ApiEntity.ModulesBean modulesBean = modules.get(i);
                        //全局参数
                        String requestArgs = modulesBean.getRequestArgs();
                        if (requestArgs != null && !requestArgs.equals("[]")) {
                            ArgEntity argEntity = gson.fromJson("{\"data\":" + requestArgs + "}", ArgEntity.class);
                            if (argEntity != null && argEntity.getData() != null) {
                                //有全局参数
                                List<ArgEntity.DataBean> data1 = argEntity.getData();
                                for (int i4 = 0; i4 < data1.size(); i4++) {
                                    ArgEntity.DataBean dataBean1 = data1.get(i4);
                                    String name1 = dataBean1.getName();
                                    String type1 = dataBean1.getType();

                                    //接口分组
                                    List<ApiEntity.ModulesBean.FoldersBean> folders = modulesBean.getFolders();
                                    if (folders != null) {
                                        //Api单例类
                                        StringBuilder sbApi = new StringBuilder();
                                        sbApi.append("package ").append(packageName).append(".common.api;");
                                        sbApi.append("\n");
                                        sbApi.append("\nimport java.io.File;");
                                        sbApi.append("\nimport java.net.CookieHandler;");
                                        sbApi.append("\nimport java.net.CookieManager;");
                                        sbApi.append("\nimport java.net.CookiePolicy;");
                                        sbApi.append("\nimport java.util.concurrent.TimeUnit;");
                                        sbApi.append("\n\n");
                                        sbApi.append("\nimport ").append(packageName).append(".MyApplication;\n");
                                        sbApi.append("\nimport okhttp3.Cache;");
                                        sbApi.append("\nimport okhttp3.OkHttpClient;");
                                        sbApi.append("\nimport okhttp3.logging.HttpLoggingInterceptor;");
                                        sbApi.append("\n\n");
                                        sbApi.append("\nimport retrofit2.Retrofit;");
                                        sbApi.append("\nimport retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;");
                                        sbApi.append("\nimport retrofit2.converter.gson.GsonConverterFactory;");
                                        sbApi.append("\n");
                                        sbApi.append("\nimport android.content.Context;");
                                        sbApi.append("\n");
                                        sbApi.append("\n/**");
                                        sbApi.append("\n * ").append("Api调用");
                                        sbApi.append("\n */");
                                        sbApi.append("\npublic class Api").append(" {");

                                        for (int i1 = 0; i1 < folders.size(); i1++) {
                                            ApiEntity.ModulesBean.FoldersBean foldersBean = folders.get(i1);
                                            String ip = foldersBean.getIp();
                                            StringBuilder sb = new StringBuilder();
                                            //包名导入
                                            sb.append("package ").append(packageName).append(".common.api;");
                                            sb.append("\n");
                                            sb.append("\nimport retrofit2.http.*;");
                                            sb.append("\nimport rx.Observable;");
                                            sb.append("\nimport ").append(packageName).append(".MyApplication;");
                                            sb.append("\nimport ").append(packageName).append(".common.api.entity.*;");
                                            sb.append("\n\n");
                                            sb.append("\n/**");
                                            sb.append("\n * ").append(foldersBean.getName());
                                            sb.append("\n */");
                                            sb.append("\npublic interface Api").append(i1).append(" {");
                                            sbApi.append("\n    private static final String SERVER_IP").append(i1).append(" = ").append("\"").append(ip == null ? "" : ip).append("\"").append(";");
                                            sbApi.append("\n    private static Api").append(i1).append(" api").append(i1).append(";");
                                            sbApi.append("\n    public static Api").append(i1).append(" getApi").append(i1).append("() {");
                                            sbApi.append("\n        if (api").append(i1).append(" == null) {");
                                            sbApi.append("\n            synchronized (Api.class) {");
                                            sbApi.append("\n                if (api").append(i1).append(" == null) {");
                                            sbApi.append("\n                    File cache = MyApplication.getContext().getCacheDir();");
                                            sbApi.append("\n                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();");
                                            sbApi.append("\n                    logging.setLevel(HttpLoggingInterceptor.Level.BASIC);");
                                            sbApi.append("\n                    HttpLoggingInterceptor logging1 = new HttpLoggingInterceptor();");
                                            sbApi.append("\n                    logging1.setLevel(HttpLoggingInterceptor.Level.BODY);");
                                            sbApi.append("\n                    OkHttpClient client = new OkHttpClient.Builder()");
                                            sbApi.append("\n                            .cache(new Cache(cache, 10 * 1024 * 1024))");
                                            sbApi.append("\n                            .readTimeout(20, TimeUnit.SECONDS)");
                                            sbApi.append("\n                            .writeTimeout(20, TimeUnit.SECONDS)");
                                            sbApi.append("\n                            .connectTimeout(20, TimeUnit.SECONDS)");
                                            sbApi.append("\n                            .addInterceptor(logging)");
                                            sbApi.append("\n                            .addInterceptor(logging1)");
                                            sbApi.append("\n                            .build();");
                                            sbApi.append("\n\n");
                                            sbApi.append("\n                    CookieHandler.setDefault(getCookieManager());");
                                            sbApi.append("\n                    Retrofit retrofit = new Retrofit.Builder()");
                                            sbApi.append("\n                            .client(client)");
                                            sbApi.append("\n                            .addConverterFactory(GsonConverterFactory.create())//添加json转换器");
                                            sbApi.append("\n                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加RxJava适配器");
                                            sbApi.append("\n                            .baseUrl(SERVER_IP").append(i1).append(")");
                                            sbApi.append("\n                            .build();");
                                            sbApi.append("\n                    api").append(i1).append(" = retrofit.create(Api").append(i1).append(".class);");
                                            sbApi.append("\n                }");
                                            sbApi.append("\n            }");
                                            sbApi.append("\n        }");
                                            sbApi.append("\n        return api").append(i1).append(";");
                                            sbApi.append("\n    }");
                                            sbApi.append("\n\n");


                                            //接口
                                            List<ApiEntity.ModulesBean.FoldersBean.ChildrenBean> children = foldersBean.getChildren();
                                            if (children != null) {
                                                for (ApiEntity.ModulesBean.FoldersBean.ChildrenBean childrenBean : children) {

                                                    //实体类内容
                                                    StringBuilder sbEntity = new StringBuilder();
                                                    sbEntity.append("package ").append(packageName).append(".common.api.entity;");
                                                    sbEntity.append("\n");
                                                    sbEntity.append("\nimport java.util.List;");
                                                    sbEntity.append("\n/**");
                                                    sbEntity.append("\n * ").append(childrenBean.getName());
                                                    sbEntity.append("\n */");

                                                    //接口地址
                                                    String url = childrenBean.getUrl();
                                                    System.out.println(url);

                                                    //方法描述
                                                    String desc = childrenBean.getDescription();
                                                    sb.append("\n   /*");
                                                    sb.append("\n    * ").append(desc);
                                                    sb.append("\n    */");

                                                    //方法名
                                                    String m = url.substring(url.lastIndexOf("/") + 1, url.length());
                                                    if (m.length() > 0) {
                                                        m = m.substring(0, 1).toLowerCase() + m.substring(1);
                                                    }

                                                    //请求方式GET或POST
                                                    String method = childrenBean.getRequestMethod();
                                                    if (method.equals("GET")) {
                                                        sb.append("\n   @GET").append("(\"").append(url).append("\")");
                                                    } else {
                                                        sb.append("\n   @FormUrlEncoded");
                                                        sb.append("\n   @POST").append("(\"").append(url).append("\")");
                                                    }
//                                        System.out.println(method);

                                                    String beanClazz = m.substring(0, 1).toUpperCase() + m.substring(1, m.length()) + "Result";


                                                    //接口返回数据
                                                    String responseData = childrenBean.getResponseArgs();
                                                    try {
                                                        sbEntity.append("\npublic class ").append(beanClazz).append(" {");
                                                        JSONArray root = new JSONArray(responseData);
                                                        generateJavaBean2(root, sbEntity);
                                                        sbEntity.append("\n}");
                                                        System.out.println(sbEntity.toString());
                                                        FileUtils.saveFileToPathWithName(sbEntity.toString(), path + File.separator + "entity", beanClazz + ".java");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        System.out.println(url + "接口的返回json实例解析异常");
                                                    }

                                                    //TODO 创建实体类
                                                    //接口请求参数
                                                    String requestArgs1 = childrenBean.getRequestArgs();
                                                    ArgEntity argEntity1 = gson.fromJson("{\"data\":" + requestArgs1 + "}", ArgEntity.class);
                                                    if (argEntity1 != null && argEntity1.getData() != null) {

                                                        sb.append("\n   Observable<").append(beanClazz).append("> ").append(m).append("(");
                                                        //有参数
                                                        List<ArgEntity.DataBean> data = argEntity1.getData();
                                                        //添加全局参数
                                                        if (method.equals("GET")) {
                                                            if (type1.equals("int")) {
                                                                sb.append("@Query(\"").append(name1).append("\") ").append("int ").append(name1).append(",");
                                                            } else if (type1.equals("float")) {
                                                                sb.append("@Query(\"").append(name1).append("\") ").append("float ").append(name1).append(",");
                                                            } else {
                                                                sb.append("@Query(\"").append(name1).append("\") ").append("String ").append(name1).append(",");
                                                            }
                                                        } else {
                                                            if (type1.equals("int")) {
                                                                sb.append("@Field(\"").append(name1).append("\") ").append("int ").append(name1).append(",");
                                                            } else if (type1.equals("float")) {
                                                                sb.append("@Field(\"").append(name1).append("\") ").append("float ").append(name1).append(",");
                                                            } else {
                                                                sb.append("@Field(\"").append(name1).append("\") ").append("String ").append(name1).append(",");
                                                            }
                                                        }
                                                        for (ArgEntity.DataBean aData : data) {
                                                            String name = aData.getName();
                                                            String type = aData.getType();
                                                            if (method.equals("GET")) {
                                                                if (type.equals("int")) {
                                                                    sb.append("@Query(\"").append(name).append("\") ").append("int ").append(name).append(",");
                                                                } else if (type.equals("float")) {
                                                                    sb.append("@Query(\"").append(name1).append("\") ").append("float ").append(name1).append(",");
                                                                } else {
                                                                    sb.append("@Query(\"").append(name).append("\") ").append("String ").append(name).append(",");
                                                                }
                                                            } else {
                                                                if (type.equals("int")) {
                                                                    sb.append("@Field(\"").append(name).append("\") ").append("int ").append(name).append(",");
                                                                } else if (type.equals("float")) {
                                                                    sb.append("@Field(\"").append(name1).append("\") ").append("float ").append(name1).append(",");
                                                                } else {
                                                                    sb.append("@Field(\"").append(name).append("\") ").append("String ").append(name).append(",");
                                                                }
                                                            }
                                                        }
                                                        sb.deleteCharAt(sb.length() - 1);

                                                    } else {
                                                        sb.append("\n   Observable<Object> ").append(m).append("(");
                                                        //无参数
                                                        assert argEntity1 != null;
                                                        List<ArgEntity.DataBean> data = argEntity1.getData();
                                                        boolean has = false;
                                                        for (ArgEntity.DataBean aData : data) {
                                                            has = true;
                                                            String name = aData.getName();
                                                            String type = aData.getType();
                                                            if (method.equals("GET")) {
                                                                sb.append("@Query(\"").append(name).append("\") ").append("String ").append(name).append(",");
                                                            } else {
                                                                sb.append("@Field(\"").append(name).append("\") ").append("String ").append(name).append(",");
                                                            }
                                                        }
                                                        if (has) {
                                                            sb.deleteCharAt(sb.length() - 1);
                                                        }
                                                    }
                                                    sb.append(");   ");
                                                }
                                            }
                                            sb.append("\n}");
                                            //TODO 创建Api
                                            FileUtils.saveFileToPathWithName(sb.toString(), path, "Api" + i1 + ".java");
                                        }
                                        sbApi.append("\n    private static CookieManager getCookieManager() {");
                                        sbApi.append("\n        CookieManager cookieManager = new CookieManager();");
                                        sbApi.append("\n        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);");
                                        sbApi.append("\n        return cookieManager;");
                                        sbApi.append("\n    }");
                                        sbApi.append("\n}");
                                        //TODO 创建Api调用
                                        FileUtils.saveFileToPathWithName(sbApi.toString(), path, "Api.java");
                                    }
                                }
                            } else {
                                //无全局参数

                            }
                        } else {
                            //无全局参数

                            //接口分组
                            List<ApiEntity.ModulesBean.FoldersBean> folders = modulesBean.getFolders();
                            if (folders != null) {
                                //Api单例类
                                StringBuilder sbApi = new StringBuilder();
                                sbApi.append("package ").append(packageName).append(".common.api;");
                                sbApi.append("\n");
                                sbApi.append("\nimport java.io.File;");
                                sbApi.append("\nimport java.net.CookieHandler;");
                                sbApi.append("\nimport java.net.CookieManager;");
                                sbApi.append("\nimport java.net.CookiePolicy;");
                                sbApi.append("\nimport java.util.concurrent.TimeUnit;");
                                sbApi.append("\n\n");
                                sbApi.append("\nimport ").append(packageName).append(".MyApplication;");
                                sbApi.append("\nimport okhttp3.Cache;");
                                sbApi.append("\nimport okhttp3.OkHttpClient;");
                                sbApi.append("\nimport okhttp3.logging.HttpLoggingInterceptor;");
                                sbApi.append("\n\n");
                                sbApi.append("\nimport retrofit2.Retrofit;");
                                sbApi.append("\nimport retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;");
                                sbApi.append("\nimport retrofit2.converter.gson.GsonConverterFactory;");
                                sbApi.append("\n");
                                sbApi.append("\nimport android.content.Context;");
                                sbApi.append("\n");
                                sbApi.append("\n/**");
                                sbApi.append("\n * ").append("Api调用");
                                sbApi.append("\n */");
                                sbApi.append("\npublic class Api").append(" {");

                                for (int i1 = 0; i1 < folders.size(); i1++) {
                                    ApiEntity.ModulesBean.FoldersBean foldersBean = folders.get(i1);
                                    StringBuilder sb = new StringBuilder();
                                    //包名导入
                                    sb.append("package ").append(packageName).append(".common.api;");
                                    sb.append("\n");
                                    sb.append("\nimport retrofit2.http.*;");
                                    sb.append("\nimport rx.Observable;");
                                    sb.append("\nimport ").append(packageName).append(".MyApplication;");
                                    sb.append("\nimport ").append(packageName).append(".common.api.entity.*;");
                                    sb.append("\n\n");
                                    sb.append("\n/**");
                                    sb.append("\n * ").append(foldersBean.getName());
                                    sb.append("\n */");
                                    sb.append("\npublic interface Api").append(i1).append(" {");
                                    sbApi.append("\n    private static final String SERVER_IP").append(i1).append(" = ").append("\"").append(foldersBean.getIp() == null ? "http://www.baidu.com/" : foldersBean.getIp()).append("\";");
                                    sbApi.append("\n    private static Api").append(i1).append(" api").append(i1).append(";");
                                    sbApi.append("\n    public static Api").append(i1).append(" getApi").append(i1).append("() {");
                                    sbApi.append("\n        if (api").append(i1).append(" == null) {");
                                    sbApi.append("\n            synchronized (Api.class) {");
                                    sbApi.append("\n                if (api").append(i1).append(" == null) {");
                                    sbApi.append("\n                    File cache = MyApplication.getContext().getCacheDir();");
                                    sbApi.append("\n                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();");
                                    sbApi.append("\n                    logging.setLevel(HttpLoggingInterceptor.Level.BASIC);");
                                    sbApi.append("\n                    HttpLoggingInterceptor logging1 = new HttpLoggingInterceptor();");
                                    sbApi.append("\n                    logging1.setLevel(HttpLoggingInterceptor.Level.BODY);");
                                    sbApi.append("\n                    OkHttpClient client = new OkHttpClient.Builder()");
                                    sbApi.append("\n                            .cache(new Cache(cache, 10 * 1024 * 1024))");
                                    sbApi.append("\n                            .readTimeout(20, TimeUnit.SECONDS)");
                                    sbApi.append("\n                            .writeTimeout(20, TimeUnit.SECONDS)");
                                    sbApi.append("\n                            .connectTimeout(20, TimeUnit.SECONDS)");
                                    sbApi.append("\n                            .addInterceptor(logging)");
                                    sbApi.append("\n                            .addInterceptor(logging1)");
                                    sbApi.append("\n                            .build();");
                                    sbApi.append("\n\n");
                                    sbApi.append("\n                    CookieHandler.setDefault(getCookieManager());");
                                    sbApi.append("\n                    Retrofit retrofit = new Retrofit.Builder()");
                                    sbApi.append("\n                            .client(client)");
                                    sbApi.append("\n                            .addConverterFactory(GsonConverterFactory.create())//添加 json 转换器");
                                    sbApi.append("\n                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加 RxJava 适配器");
                                    sbApi.append("\n                            .baseUrl(SERVER_IP").append(i1).append(")");
                                    sbApi.append("\n                            .build();");
                                    sbApi.append("\n                    api").append(i1).append(" = retrofit.create(Api").append(i1).append(".class);");
                                    sbApi.append("\n                }");
                                    sbApi.append("\n            }");
                                    sbApi.append("\n        }");
                                    sbApi.append("\n        return api").append(i1).append(";");
                                    sbApi.append("\n    }");
                                    sbApi.append("\n\n");

                                    //接口
                                    List<ApiEntity.ModulesBean.FoldersBean.ChildrenBean> children = foldersBean.getChildren();
                                    if (children != null) {
                                        for (ApiEntity.ModulesBean.FoldersBean.ChildrenBean childrenBean : children) {

                                            //实体类内容
                                            StringBuilder sbEntity = new StringBuilder();
                                            sbEntity.append("package ").append(packageName).append(".common.api.entity;");
                                            sbEntity.append("\n");
                                            sbEntity.append("\nimport java.util.List;");
                                            sbEntity.append("\n/**");
                                            sbEntity.append("\n * ").append(childrenBean.getName());
                                            sbEntity.append("\n */");

                                            //接口地址
                                            String url = childrenBean.getUrl();
                                            System.out.println(url);

                                            //方法描述
                                            String desc = childrenBean.getDescription();
                                            sb.append("\n   /*");
                                            sb.append("\n    * ").append(desc);
                                            sb.append("\n    */");

                                            //方法名
                                            String m = url.substring(url.lastIndexOf("/") + 1, url.length());
                                            if (m.length() > 0) {
                                                m = m.substring(0, 1).toLowerCase() + m.substring(1);
                                            }
                                            //请求方式GET或POST
                                            String method = childrenBean.getRequestMethod();
                                            if (method.equals("GET")) {
                                                sb.append("\n   @GET").append("(\"").append(url).append("\")");
                                            } else {
                                                sb.append("\n   @FormUrlEncoded");
                                                sb.append("\n   @POST").append("(\"").append(url).append("\")");
                                            }
//                                        System.out.println(method);

                                            String beanClazz = m.substring(0, 1).toUpperCase() + m.substring(1, m.length()) + "Result";


                                            //接口返回数据
                                            String responseData = childrenBean.getResponseArgs();
                                            try {
                                                sbEntity.append("\npublic class ").append(beanClazz).append(" {");
                                                JSONArray root = new JSONArray(responseData);
                                                generateJavaBean2(root, sbEntity);
                                                sbEntity.append("\n}");
                                                System.out.println(sbEntity.toString());
                                                FileUtils.saveFileToPathWithName(sbEntity.toString(), path + File.separator + "entity", beanClazz + ".java");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                System.out.println(url + "接口的返回json实例解析异常");
                                            }

                                            //TODO 创建实体类
                                            //接口请求参数
                                            String requestArgs1 = childrenBean.getRequestArgs();
                                            ArgEntity argEntity1 = gson.fromJson("{\"data\":" + requestArgs1 + "}", ArgEntity.class);
                                            if (argEntity1 != null && argEntity1.getData() != null) {

                                                sb.append("\n   Observable<").append(beanClazz).append("> ").append(m).append("(");
                                                //有参数
                                                List<ArgEntity.DataBean> data = argEntity1.getData();
                                                boolean has = false;
                                                for (ArgEntity.DataBean aData : data) {
                                                    has = true;
                                                    String name = aData.getName();
                                                    String type = aData.getType();
                                                    if (method.equals("GET")) {
                                                        if (type.equals("int")) {
                                                            sb.append("@Query(\"").append(name).append("\") ").append("int ").append(name).append(",");
                                                        } else if (type.equals("float")) {
                                                            sb.append("@Query(\"").append(name).append("\") ").append("float ").append(name).append(",");
                                                        } else {
                                                            sb.append("@Query(\"").append(name).append("\") ").append("String ").append(name).append(",");
                                                        }
                                                    } else {
                                                        if (type.equals("int")) {
                                                            sb.append("@Field(\"").append(name).append("\") ").append("int ").append(name).append(",");
                                                        } else if (type.equals("float")) {
                                                            sb.append("@Field(\"").append(name).append("\") ").append("float ").append(name).append(",");
                                                        } else {
                                                            sb.append("@Field(\"").append(name).append("\") ").append("String ").append(name).append(",");
                                                        }
                                                    }
                                                }
                                                if (has)
                                                    sb.deleteCharAt(sb.length() - 1);
                                            } else {
                                                //无参数
                                                sb.append("\n   Observable<").append(beanClazz).append("> ").append(m).append("(");
                                            }
                                            sb.append(");   ");
                                        }
                                    }
                                    sb.append("\n}");
                                    //TODO 创建Api
                                    FileUtils.saveFileToPathWithName(sb.toString(), path, "Api" + i1 + ".java");
                                }
                                sbApi.append("\n    private static CookieManager getCookieManager() {");
                                sbApi.append("\n        CookieManager cookieManager = new CookieManager();");
                                sbApi.append("\n        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);");
                                sbApi.append("\n        return cookieManager;");
                                sbApi.append("\n    }");
                                sbApi.append("\n}");
                                //TODO 创建Api调用
                                FileUtils.saveFileToPathWithName(sbApi.toString(), path, "Api.java");
                            }

                        }
                    }
                }
            } else {
                System.out.println("格式解析异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("格式解析异常");
        }
    }

    public static void createIOSApi(String json, String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            Gson gson = new GsonBuilder().create();
            ApiEntity api = gson.fromJson(json, ApiEntity.class);

            if (api != null) {

                List<ApiEntity.ModulesBean> modules = api.getModules();
                if (modules != null) {
                    for (int i = 0; i < modules.size(); i++) {
                        ApiEntity.ModulesBean modulesBean = modules.get(i);
                        //全局参数
                        String requestArgs = modulesBean.getRequestArgs();
                        if (requestArgs != null && !requestArgs.equals("[]")) {
                            ArgEntity argEntity = gson.fromJson("{\"data\":" + requestArgs + "}", ArgEntity.class);
                            if (argEntity != null && argEntity.getData() != null) {
                                //有全局参数
                                List<ArgEntity.DataBean> data1 = argEntity.getData();
                                for (int i4 = 0; i4 < data1.size(); i4++) {
                                    ArgEntity.DataBean dataBean1 = data1.get(i4);
                                    String name1 = dataBean1.getName();
                                    String type1 = dataBean1.getType();

                                    //接口分组
                                    List<ApiEntity.ModulesBean.FoldersBean> folders = modulesBean.getFolders();
                                    if (folders != null) {

                                        for (int i1 = 0; i1 < folders.size(); i1++) {
                                            ApiEntity.ModulesBean.FoldersBean foldersBean = folders.get(i1);
                                            String ip = foldersBean.getIp();

                                            //接口
                                            List<ApiEntity.ModulesBean.FoldersBean.ChildrenBean> children = foldersBean.getChildren();
                                            if (children != null) {
                                                for (ApiEntity.ModulesBean.FoldersBean.ChildrenBean childrenBean : children) {
                                                    //接口地址
                                                    String url = childrenBean.getUrl();
                                                    System.out.println(url);

                                                    //方法名
                                                    String m = url.substring(url.lastIndexOf("/") + 1, url.length());
                                                    if (m.length() > 0) {
                                                        m = m.substring(0, 1).toLowerCase() + m.substring(1);
                                                    }
                                                    String clazzName = m.substring(0, 1).toUpperCase() + m.substring(1, m.length());
                                                    try {
                                                        String responseData = childrenBean.getResponseArgs();
                                                        JSONArray root = new JSONArray(responseData);
                                                        StringBuilder sbEntity = new StringBuilder();
                                                        generateModel2IOS(root, sbEntity, path + File.separator + "model"+i1, clazzName);
                                                        sbEntity.append("\n@end");
                                                        System.out.println(sbEntity.toString());
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        System.out.println(url + "接口的返回json实例解析异常");
                                                    }
                                                }
                                            }
                                            //TODO 创建Api
                                        }
                                        //TODO 创建Api调用
                                    }
                                }
                            } else {
                                //无全局参数

                            }
                        } else {
                            //无全局参数

                            //接口分组
                            List<ApiEntity.ModulesBean.FoldersBean> folders = modulesBean.getFolders();
                            if (folders != null) {

                                for (int i1 = 0; i1 < folders.size(); i1++) {
                                    ApiEntity.ModulesBean.FoldersBean foldersBean = folders.get(i1);
                                    StringBuilder sb = new StringBuilder();

                                    //接口
                                    List<ApiEntity.ModulesBean.FoldersBean.ChildrenBean> children = foldersBean.getChildren();
                                    if (children != null) {
                                        for (ApiEntity.ModulesBean.FoldersBean.ChildrenBean childrenBean : children) {
                                            //接口地址
                                            String url = childrenBean.getUrl();
                                            System.out.println(url);

                                            //方法名
                                            String m = url.substring(url.lastIndexOf("/") + 1, url.length());
                                            if (m.length() > 0) {
                                                m = m.substring(0, 1).toLowerCase() + m.substring(1);
                                            }
                                            String clazzName = m.substring(0, 1).toUpperCase() + m.substring(1, m.length());
                                            try {
                                                String responseData = childrenBean.getResponseArgs();
                                                JSONArray root = new JSONArray(responseData);
                                                StringBuilder sbEntity = new StringBuilder();
                                                generateModel2IOS(root, sbEntity, path + File.separator + "model"+i1, clazzName);
                                                sbEntity.append("\n@end");
                                                System.out.println(sbEntity.toString());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                System.out.println(url + "接口的返回json实例解析异常");
                                            }

                                        }
                                    }
                                    //TODO 创建Api
                                }
                                //TODO 创建Api调用
                            }

                        }
                    }
                }
            } else {
                System.out.println("格式解析异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("格式解析异常");
        }
    }

    /**
     * 带注释
     */
    private static void generateJavaBean2(JSONArray jsonArray, StringBuilder sb) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            generateJavaBean3(jsonObject, sb);
        }

    }

    /**
     * 带注释ios
     */
    private static void generateModel2IOS(JSONArray jsonArray, StringBuilder sbEntity, String path, String clazzName) {
        String beanClazz = "GF" + clazzName + "Model";
        //实体类内容
        sbEntity.append("//\n" +
                "//  " + beanClazz + ".h\n" +
                "//  GoFactory\n" +
                "//\n" +
                "//  Created by ZzApiDoc on " + DateUtils.format(new Date(), "yyyy/m/d") + ".\n" +
                "//  Copyright © 2018年 KQZK. All rights reserved.\n" +
                "//");
        sbEntity.append("\n");
        sbEntity.append("\n#import <Foundation/Foundation.h>");
        StringBuilder sbImp = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            generateImport3IOS(jsonObject, sbEntity, sbImp, clazzName);
        }
        sbEntity.append(sbImp.toString());
        sbEntity.append("\n@interface ").append(beanClazz).append(" : NSObject");
        // TODO: 2018/3/21 import
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            generateModel3IOS(jsonObject, sbEntity, path, clazzName);
        }
        sbEntity.append("\n@end");
        try {
            FileUtils.saveFileToPathWithName(sbEntity.toString(), path, beanClazz + ".h");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 带注释ios
     */
    private static void generateImport2IOS(JSONArray jsonArray, StringBuilder sb, String clazzName) {
        StringBuilder sbImp = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            generateImport3IOS(jsonObject, sb, sbImp, clazzName);
        }
        sb.append(sbImp.toString());
    }

    private static void generateImport3IOS(JSONObject jsonObject, StringBuilder sbEntity, StringBuilder sbImp, String clazzName) {
        String name = jsonObject.getString("name");
        if (name != null) {
            name = name.trim();
        } else {
            name = "unKnown";
        }
        String type = jsonObject.getString("type");
        System.out.println("import type=" + type + ", name=" + name);
        switch (type) {
            case "object":
                sbImp.append("\n#import \"GF").append(clazzName).append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("Model").append(".h\"");
/*                JSONArray children = jsonObject.getJSONArray("children");
                if (children != null && children.length() > 0) {
                    StringBuilder sbImp2 = new StringBuilder();
                    for (int j = 0; j < children.length(); j++) {
                        JSONObject jsonObject1 = children.getJSONObject(j);
                        generateImport3IOS(jsonObject1, sbEntity, sbImp2);
                    }
                    sbEntity.append(sbImp2.toString());
                }*/
                break;
            case "array[object]":
                sbImp.append("\n#import \"GF").append(clazzName).append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("Model").append(".h\"");
/*                JSONArray children2 = jsonObject.getJSONArray("children");
                if (children2 != null && children2.length() > 0) {
                    generateImport2IOS(children2, sbEntity);
                }*/
                break;
        }

    }

    private static void generateJavaBean3(JSONObject jsonObject, StringBuilder sb) {

        String name = jsonObject.getString("name");
        if (name != null) {
            name = name.trim();
        }
        String type = jsonObject.getString("type");
        String desc = null;
        try {
            desc = jsonObject.getString("description");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("type=" + type + ", name=" + name);
        switch (type) {
            case "int":
                sb.append("\n       private int ").append(name).append("; ").append(desc == null ? "" : " //" + desc);
                //setter
                sb.append("\n       public void set").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("(int ").append(name).append(") {");
                sb.append("\n           this.").append(name).append(" = ").append(name).append(";");
                sb.append("\n       }");
                //getter
                sb.append("\n       public int get").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("() {");
                sb.append("\n           return ").append(name).append(";");
                sb.append("\n       }");

                break;
            case "float":
                sb.append("\n       private float ").append(name).append("; ").append(desc == null ? "" : " //" + desc);
                //setter
                sb.append("\n       public void set").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("(float ").append(name).append(") {");
                sb.append("\n           this.").append(name).append(" = ").append(name).append(";");
                sb.append("\n       }");
                //getter
                sb.append("\n       public float get").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("() {");
                sb.append("\n           return ").append(name).append(";");
                sb.append("\n       }");

                break;
            case "string":
                generateString(name, desc, sb);
                break;
            case "object":
                sb.append("\n       private ").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("Entity").append(" ").append(name).append(";");

                //setter
                sb.append("\n       public void set").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("(")
                        .append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("Entity ").append(name).append(") {");
                sb.append("\n           this.").append(name).append(" = ").append(name).append(";");
                sb.append("\n       }");
                //getter
                sb.append("\n       public ").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("Entity")
                        .append(" get").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("() {");
                sb.append("\n           return ").append(name).append(";");
                sb.append("\n       }");

                sb.append("\n       public static class ").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("Entity").append(" {");
                JSONArray children = jsonObject.getJSONArray("children");
                if (children != null && children.length() > 0) {
                    for (int j = 0; j < children.length(); j++) {
                        JSONObject jsonObject1 = children.getJSONObject(j);
                        generateJavaBean3(jsonObject1, sb);
                    }
                }
                sb.append("\n       }");
                break;
            case "array[object]":

                sb.append("\n       private List<").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("Entity").append("> ").append(name).append(";");

                //setter
                sb.append("\n       public void set").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("(List<")
                        .append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("Entity").append("> ").append(name).append(") {");
                sb.append("\n           this.").append(name).append(" = ").append(name).append(";");
                sb.append("\n       }");
                //getter
                sb.append("\n       public List<").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("Entity")
                        .append("> get").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("() {");
                sb.append("\n           return ").append(name).append(";");
                sb.append("\n       }");

                sb.append("\n       public static class ").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("Entity").append(" {");
                JSONArray children2 = jsonObject.getJSONArray("children");
                if (children2 != null && children2.length() > 0) {
                    generateJavaBean2(children2, sb);
                }
                sb.append("\n       }");

                break;
        }
    }

    private static void generateModel3IOS(JSONObject jsonObject, StringBuilder sb, String path, String clazzName) {

        String name = jsonObject.getString("name");
        if (name != null) {
            name = name.trim();
        }
        String type = jsonObject.getString("type");
        String desc = null;
        try {
            desc = jsonObject.getString("description");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("type=" + type + ", name=" + name);
        switch (type) {
            case "int":
            case "float":
            case "string":
                generateStringIOS(name, desc, sb);
                break;
            case "object":
                String objName = "GF" + clazzName + name.substring(0, 1).toUpperCase() + name.substring(1, name.length()) + "Model";
                sb.append("\n@property(nonatomic,strong) ").append(objName).append(" *").append(name).append(";");
                JSONArray children = jsonObject.getJSONArray("children");
                if (children != null && children.length() > 0) {
                    StringBuilder sb2 = new StringBuilder();
                    String beanClazz = "GF" + clazzName + "Model";
                    //实体类内容
                    sb2.append("//\n" +
                            "//  " + beanClazz + ".h\n" +
                            "//  GoFactory\n" +
                            "//\n" +
                            "//  Created by ZzApiDoc on " + DateUtils.format(new Date(), "yyyy/m/d") + ".\n" +
                            "//  Copyright © 2018年 KQZK. All rights reserved.\n" +
                            "//");
                    sb2.append("\n");
                    sb2.append("\n#import <Foundation/Foundation.h>");
                    StringBuilder sbImp = new StringBuilder();
                    for (int i = 0; i < children.length(); i++) {
                        JSONObject jsonObject0 = children.getJSONObject(i);
                        generateImport3IOS(jsonObject0, sb2, sbImp, clazzName);
                    }
                    sb2.append(sbImp.toString());
                    sb2.append("\n@interface ").append(beanClazz).append(" : NSObject");
                    for (int j = 0; j < children.length(); j++) {
                        JSONObject jsonObject1 = children.getJSONObject(j);
                        generateModel3IOS(jsonObject1, sb2, path, clazzName);
                    }
                    sb2.append("\n@end");
                    try {
                        FileUtils.saveFileToPathWithName(sb2.toString(), path, objName + ".h");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "array[object]":
                String arryName = clazzName + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
                String filename = "GF"+arryName+"Model";
                sb.append("\n@property(nonatomic,strong) NSArray<").append(filename).append("*> *").append(name).append(";");
                JSONArray children2 = jsonObject.getJSONArray("children");
                if (children2 != null && children2.length() > 0) {
                    generateModel2IOS(children2, new StringBuilder(), path, arryName);
                }

                break;
        }
    }

    private static void generateString(String name, String desc, StringBuilder sb) {
        sb.append("\n       private String ").append(name).append("; ").append(desc == null ? "" : " //" + desc);
        //setter
        sb.append("\n       public void set").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("(String ").append(name).append(") {");
        sb.append("\n           this.").append(name).append(" = ").append(name).append(";");
        sb.append("\n       }");
        //getter
        sb.append("\n       public String get").append(name.substring(0, 1).toUpperCase()).append(name.substring(1, name.length())).append("() {");
        sb.append("\n           return ").append(name).append(";");
        sb.append("\n       }");
    }

    private static void generateStringIOS(String name, String desc, StringBuilder sb) {
        sb.append("\n@property(nonatomic,copy) NSString *").append(name).append("; ").append(desc == null ? "" : " //" + desc);
    }

}

