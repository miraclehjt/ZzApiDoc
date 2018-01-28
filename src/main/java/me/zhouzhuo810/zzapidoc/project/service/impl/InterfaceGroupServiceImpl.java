package me.zhouzhuo810.zzapidoc.project.service.impl;

import me.zhouzhuo810.zzapidoc.android.utils.ZipUtils;
import me.zhouzhuo810.zzapidoc.android.widget.apicreator.ApiTool;
import me.zhouzhuo810.zzapidoc.cache.entity.CacheEntity;
import me.zhouzhuo810.zzapidoc.cache.service.CacheService;
import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.result.WebResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.InterfaceGroupDao;
import me.zhouzhuo810.zzapidoc.project.entity.*;
import me.zhouzhuo810.zzapidoc.project.service.*;
import me.zhouzhuo810.zzapidoc.project.utils.InterfaceUtils;
import me.zhouzhuo810.zzapidoc.project.utils.JSONTool;
import me.zhouzhuo810.zzapidoc.project.utils.ResponseArgUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.apache.log4j.Logger;
import org.aspectj.util.FileUtil;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/22.
 */
@Service
public class InterfaceGroupServiceImpl extends BaseServiceImpl<InterfaceGroupEntity> implements InterfaceGroupService {

    private static Logger LOGGER = Logger.getLogger(InterfaceServiceImpl.class.getSimpleName());

    @Resource(name = "userServiceImpl")
    UserService mUserService;

    @Resource(name = "projectServiceImpl")
    ProjectService mProjectService;

    @Resource(name = "interfaceServiceImpl")
    InterfaceService mInterfaceService;

    @Resource(name = "cacheServiceImpl")
    CacheService mCacheService;

    @Resource(name = "responseArgServiceImpl")
    ResponseArgService mResponseArgService;

    @Resource(name = "requestHeaderServiceImpl")
    RequestHeaderService mRequestHeaderService;

    @Resource(name = "requestArgServiceImpl")
    RequestArgService mRequestArgService;


    @Override
    @Resource(name = "interfaceGroupDaoImpl")
    public void setBaseDao(BaseDao<InterfaceGroupEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public InterfaceGroupDao getBaseDao() {
        return (InterfaceGroupDao) baseDao;
    }


    @Override
    public BaseResult addInterfaceGroup(String name, String projectId, String ip, String userId) {
        UserEntity user = mUserService.get(userId);
        InterfaceGroupEntity entity = new InterfaceGroupEntity();
        entity.setName(name);
        entity.setProjectId(projectId);
        entity.setIp(ip);
        if (user != null) {
            entity.setCreateUserID(user.getId());
            entity.setCreateUserName(user.getName());
        }
        try {
            getBaseDao().save(entity);
            MapUtils map = new MapUtils();
            map.put("id", entity.getId());
            return new BaseResult(1, "添加成功！", map.build());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败！", new HashMap<String, String>());
        }
    }

    @Override
    public BaseResult updateInterfaceGroup(String interfaceGroupId, String name, String projectId, String ip, String userId) {
        UserEntity user = mUserService.get(userId);
        InterfaceGroupEntity entity = getBaseDao().get(interfaceGroupId);
        if (entity == null) {
            return new BaseResult(0, "该接口分组不存在或已被删除！");
        }
        entity.setIp(ip);
        entity.setName(name);
        entity.setProjectId(projectId);
        if (user != null) {
            entity.setModifyUserID(user.getId());
            entity.setModifyUserName(user.getName());
        }
        try {
            getBaseDao().update(entity);
            return new BaseResult(1, "修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "修改失败！");
        }
    }

    @Override
    public BaseResult deleteInterfaceGroup(String id, String userId) {
        InterfaceGroupEntity entity = getBaseDao().get(id);
        if (entity == null) {
            return new BaseResult(0, "该接口分组不存在或已被删除！");
        }
        UserEntity user = mUserService.get(userId);
        if (user != null) {
            entity.setModifyUserID(user.getId());
            entity.setModifyUserName(user.getName());
        }
        try {
            update(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getBaseDao().deleteLogicByIds(new String[]{id});
            return new BaseResult(1, "刪除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "刪除失败！");
        }
    }

    @Override
    public BaseResult getAllInterfaceGroup(String projectId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！");
        }
        List<InterfaceGroupEntity> groups = getBaseDao().executeCriteria(InterfaceUtils.getInterfaceByProjectId(projectId), Order.asc("createTime"));
        if (groups == null) {
            return new BaseResult(0, "暂无数据！");
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (InterfaceGroupEntity group : groups) {
            MapUtils map = new MapUtils();
            map.put("id", group.getId());
            map.put("name", group.getName());
            map.put("ip", group.getIp());
            map.put("interfaceNo", group.getInterfaceNo());
            map.put("createTime", DataUtils.formatDate(group.getCreateTime()));
            map.put("createUserName", group.getCreateUserName());
            map.put("createUserId", group.getCreateUserID());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }

    @Override
    public WebResult getAllInterfaceGroupWeb(String projectId, int indexPage, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new WebResult(1, 1, 0);
        }
        int rowCount = getBaseDao().executeCriteriaRow(InterfaceUtils.getInterfaceByProjectId(projectId));
        List<InterfaceGroupEntity> groups = getBaseDao().executeCriteria(InterfaceUtils.getInterfaceByProjectId(projectId), indexPage - 1, 10, Order.asc("createTime"));
        int pageCount = rowCount / 10;
        if (rowCount % 10 > 0) {
            pageCount++;
        }
        if (groups == null) {
            return new WebResult(1, 1, 0);
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (InterfaceGroupEntity group : groups) {
            MapUtils map = new MapUtils();
            map.put("id", group.getId());
            map.put("name", group.getName());
            map.put("ip", group.getIp());
            map.put("interfaceNo", group.getInterfaceNo());
            map.put("createTime", DataUtils.formatDate(group.getCreateTime()));
            map.put("createUserName", group.getCreateUserName());
            map.put("createUserId", group.getCreateUserID());
            result.add(map.build());
        }
        return new WebResult(indexPage, pageCount, rowCount, result);
    }


    /***********************************下载 group API 开始**************************************/
    @Override
    public ResponseEntity<byte[]> downloadApi(String groupId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return null;
        }

        InterfaceGroupEntity group = get(groupId);
        if (group == null) {
            return null;
        }

        ProjectEntity project = mProjectService.get(group.getProjectId());
        if (project == null) {
            return null;
        }

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String realPath = request.getRealPath("");
            if (realPath != null) {
                String mPath = realPath + File.separator + "API";
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
                    LOGGER.error("API ERROR", e);
                }
                /*Api*/
                String appName = project.getName();
                String appDirPath = mPath + File.separator + appName;
                File appDir = new File(appDirPath);
                if (!appDir.exists()) {
                    /*如果不存在，创建app目录*/
                    appDir.mkdirs();
                } else {
                    /*如果存在，删除该目录里的所有文件*/
                    me.zhouzhuo810.zzapidoc.common.utils.FileUtils.deleteFiles(appDirPath);
                }

                String packageName = project.getPackageName();
                if (packageName == null || packageName.length() == 0) {
                    packageName = "com.example.zzapidoc";
                }
                String packagePath = packageName.replace(".", File.separator);
                String javaDir = appDirPath
                        + File.separator + "app"
                        + File.separator + "src"
                        + File.separator + "main"
                        + File.separator + "java"
                        + File.separator + packagePath
                        + File.separator + "common"
                        + File.separator + "api";
                ApiTool.createApi(convertToJson(project, group), packageName, javaDir);

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
    public String convertToJson(ProjectEntity project, InterfaceGroupEntity interfaceGroupEntity) {

        JSONStringer stringer = new JSONStringer();
        /*工程*/
        stringer.object()
                .key("project").object()
                .key("name").value(project.getName())
                .key("userId").value(project.getCreateUserID())
                .key("description").value(project.getNote() == null ? "" : project.getNote())
                .key("permission").value(project.getProperty())
                .key("createTime").value(DataUtils.formatDate(project.getCreateTime()))
                .endObject();
        /*模块数组开始*/
        stringer
                .key("modules").array();
            /*分组数组开始*/
        stringer.object()
                .key("name").value("默认模块")
                   /*请求参数*/
                .key("requestHeaders").value(globalHeaderToJson(project.getId()))
                .key("requestArgs").value(globalRequestToJson(project.getId()))
                    /*返回参数*/
                .key("responseArgs").value(globalResponseToJson(project.getId()))
                .key("folders").array();
                /*分组基本信息*/
        stringer.object()
                .key("id").value(interfaceGroupEntity.getId())
                .key("name").value(interfaceGroupEntity.getName())
                .key("ip").value(interfaceGroupEntity.getIp())
                .key("createTime").value(DataUtils.formatDate(interfaceGroupEntity.getCreateTime()))
                .key("createUser").value(interfaceGroupEntity.getCreateUserName());
        List<InterfaceEntity> list = mInterfaceService.executeCriteria(InterfaceUtils.getInterfaceByGroupId(interfaceGroupEntity.getId()));
        stringer.key("children").array();
        if (list != null) {
            for (InterfaceEntity entity : list) {
                    /*接口基本信息*/
                stringer.object()
                        .key("id").value(entity.getId())
                        .key("name").value(entity.getName())
                        .key("example").value(entity.getExample() == null ? "" : entity.getExample())
                        .key("requestMethod").value(entity.getHttpMethodName())
                        .key("requestHeaders").value(headersToJson(entity.getId()))
                        .key("url").value(entity.getPath())
                        .key("description").value((entity.getName() == null ? "" : entity.getName()) + (entity.getNote() == null ? "" : "(" + entity.getNote() + ")"));

                    /*请求参数*/
                stringer.key("requestArgs").value(requestToJson(entity.getId(), "0"));

                    /*返回参数*/
                stringer.key("responseArgs").value(responseAndGlobalToJson(project.getId(), entity.getId(), "0"));

                    /*接口信息结束*/
                stringer.endObject();
            }
        }
                /*接口数组结束*/
        stringer.endArray();
                /*接口分组结束*/
        stringer.endObject();
            /*接口分组数组结束*/
        stringer.endArray();
            /*模块结束*/
        stringer.endObject();
        /*模块分组结束*/
        stringer.endArray();
        /*工程结束*/
        stringer.endObject();
        return stringer.toString();
    }

    @Override
    public BaseResult deleteInterfaceGroupWeb(String ids, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法！", new HashMap<String, String>());
        }
        if (ids != null && ids.length() > 0) {
            if (ids.contains(",")) {
                String[] id = ids.split(",");
                for (String s : id) {
                    InterfaceGroupEntity entity = getBaseDao().get(s);
                    if (entity == null) {
                        continue;
                    }
                    entity.setModifyUserID(user.getId());
                    entity.setModifyUserName(user.getName());
                    entity.setDeleteFlag(BaseEntity.DELETE_FLAG_YES);
                    try {
                        update(entity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                InterfaceGroupEntity entity = getBaseDao().get(ids);
                entity.setModifyUserID(user.getId());
                entity.setModifyUserName(user.getName());
                entity.setDeleteFlag(BaseEntity.DELETE_FLAG_YES);
                try {
                    update(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            return new BaseResult(0, "请先选择要删除的行！", new HashMap<String, String>());
        }
        return new BaseResult(1, "刪除成功！", new HashMap<String, String>());
    }

    @Override
    public BaseResult generateExample(String groupId, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法", new HashMap<String, String>());
        }
        InterfaceGroupEntity interfaceGroupEntity = getBaseDao().get(groupId);
        if (interfaceGroupEntity == null) {
            return new BaseResult(0, "接口分组不存在或已被删除！", new HashMap<String, String>());
        }
        List<InterfaceEntity> list = mInterfaceService.executeCriteria(InterfaceUtils.getInterfaceByGroupId(groupId), Order.asc("createTime"));
        if (list == null || list.size() == 0) {
            return new BaseResult(0, "该分组没有接口！", new HashMap<String, String>());
        }
        for (InterfaceEntity entity : list) {

            List<ResponseArgEntity> globalResponseArgs = mResponseArgService.getGlobalResponseArgs(entity.getProjectId());
            JSONStringer stringer = new JSONStringer();
            stringer.object();
            if (globalResponseArgs != null) {
                for (ResponseArgEntity resG : globalResponseArgs) {
                    if (resG.getTypeId() == null) {
                        resG.setTypeId(7);
                    }
                    switch (resG.getTypeId()) {
                        case ResponseArgEntity.TYPE_STRING:
                            try {
                                stringer.key(resG.getName()).value(resG.getNote() == null ? "" : resG.getNote());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case ResponseArgEntity.TYPE_INT:
                            try {
                                stringer.key(resG.getName()).value(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case ResponseArgEntity.TYPE_FLOAT:
                            try {
                                stringer.key(resG.getName()).value(0.1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case ResponseArgEntity.TYPE_ARRAY:
                            try {
                                stringer.key(resG.getName()).array().endArray();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case ResponseArgEntity.TYPE_ARRAY_INT:
                            try {
                                stringer.key(resG.getName());
                                stringer.array();
                                stringer.value(0);
                                stringer.value(1);
                                stringer.endArray();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case ResponseArgEntity.TYPE_ARRAY_FLOAT:
                            try {
                                stringer.key(resG.getName());
                                stringer.array();
                                stringer.value(0.1);
                                stringer.value(0.2);
                                stringer.endArray();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case ResponseArgEntity.TYPE_ARRAY_STRING:
                            try {
                                stringer.key(resG.getName());
                                stringer.array();
                                stringer.value("string1");
                                stringer.value("string2");
                                stringer.endArray();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case ResponseArgEntity.TYPE_FILE:
                            try {
                                stringer.key(resG.getName()).value("");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            try {
                                stringer.key(resG.getName()).value("");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            }
            List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(entity.getId(), "0"));
            for (ResponseArgEntity res : responseArgEntities) {
                if (res.getTypeId() == null) {
                    res.setTypeId(7);
                }
                switch (res.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        try {
                            stringer.key(res.getName()).value(res.getNote() == null ? "" : res.getNote());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        try {
                            stringer.key(res.getName()).value(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        try {
                            stringer.key(res.getName()).value(0.0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        try {
                            stringer.key(res.getName());
                            stringer.object();
                            generateChildExeample(stringer, entity.getId(), res.getId());
                            stringer.endObject();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                        try {
                            stringer.key(res.getName());
                            stringer.array();
                            stringer.object();
                            generateChildExeample(stringer, entity.getId(), res.getId());
                            stringer.endObject();
                            stringer.endArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_INT:
                        try {
                            stringer.key(res.getName());
                            stringer.array();
                            stringer.value(0);
                            stringer.value(1);
                            stringer.endArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_FLOAT:
                        try {
                            stringer.key(res.getName());
                            stringer.array();
                            stringer.value(0.1);
                            stringer.value(0.2);
                            stringer.endArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_STRING:
                        try {
                            stringer.key(res.getName());
                            stringer.array();
                            stringer.value("string1");
                            stringer.value("string2");
                            stringer.endArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        try {
                            stringer.key(res.getName()).array().endArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        try {
                            stringer.key(res.getName()).value("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        try {
                            stringer.key(res.getName()).value("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                }
            }
            stringer.endObject();
            String result = stringer.toString();
            String formatJson = new JSONTool().stringToJSON(result);
            entity.setExample(formatJson);
            try {
                mInterfaceService.update(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new BaseResult(1, "生成成功！", new HashMap<String, String>());
    }

    private void generateChildExeample(JSONStringer stringer, String interfaceId, String pid) {
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid));
        for (ResponseArgEntity res : responseArgEntities) {
            switch (res.getTypeId()) {
                case ResponseArgEntity.TYPE_STRING:
                    try {
                        stringer.key(res.getName()).value(res.getNote() == null ? "" : res.getNote());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_INT:
                    try {
                        stringer.key(res.getName()).value(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_FLOAT:
                    try {
                        stringer.key(res.getName()).value(0.0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_OBJECT:
                    try {
                        stringer.key(res.getName());
                        stringer.object();
                        generateChildExeample(stringer, interfaceId, res.getId());
                        stringer.endObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.object();
                        generateChildExeample(stringer, interfaceId, res.getId());
                        stringer.endObject();
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_INT:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.value(0);
                        stringer.value(1);
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_FLOAT:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.value(0.1);
                        stringer.value(0.2);
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY_STRING:
                    try {
                        stringer.key(res.getName());
                        stringer.array();
                        stringer.value("string1");
                        stringer.value("string2");
                        stringer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_ARRAY:
                    try {
                        stringer.key(res.getName()).array().endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseArgEntity.TYPE_FILE:
                    try {
                        stringer.key(res.getName()).value("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    try {
                        stringer.key(res.getName()).value("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    private String requestToJson(String interfaceId, String pid) {
        JSONStringer stringer = new JSONStringer();
        List<RequestArgEntity> requestArgEntities = mRequestArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid));
        stringer.array();
        if (requestArgEntities != null) {
            for (RequestArgEntity requestArgEntity : requestArgEntities) {
                stringer.object()
                        .key("id").value(requestArgEntity.getId())
                        .key("name").value(requestArgEntity.getName())
                        .key("pid").value(requestArgEntity.getPid())
                        .key("defaultValue").value(requestArgEntity.getDefaultValue() == null ? "" : requestArgEntity.getDefaultValue())
                        .key("require").value(requestArgEntity.getRequire() == null ? true : requestArgEntity.getRequire())
                        .key("typeId").value(requestArgEntity.getTypeId())
                        .key("description").value(requestArgEntity.getNote() == null ? "" : requestArgEntity.getNote());
                if (requestArgEntity.getTypeId() == null) {
                    requestArgEntity.setTypeId(7);
                }
                switch (requestArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                        stringer.key("type").value("array[object]");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        stringer.key("type").value("array");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        stringer.key("type").value("file");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }

    private String responseAndGlobalToJson(String projectId, String interfaceId, String pid) {
        JSONStringer stringer = new JSONStringer();
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid));
        stringer.array();

        List<ResponseArgEntity> globals = mResponseArgService.executeCriteria(ResponseArgUtils.getGlobal(projectId));
        if (globals != null) {
            for (ResponseArgEntity responseArgEntity : globals) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
                        .key("defaultValue").value(responseArgEntity.getDefaultValue() == null ? "" : responseArgEntity.getDefaultValue())
                        .key("pid").value(responseArgEntity.getPid())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                switch (responseArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                        stringer.key("type").value("array[object]");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }

        if (responseArgEntities != null) {
            for (ResponseArgEntity responseArgEntity : responseArgEntities) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
                        .key("defaultValue").value(responseArgEntity.getDefaultValue() == null ? "" : responseArgEntity.getDefaultValue())
                        .key("pid").value(responseArgEntity.getPid())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                if (responseArgEntity.getTypeId() == null) {
                    responseArgEntity.setTypeId(7);
                }
                switch (responseArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                        stringer.key("type").value("array[object]");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        stringer.key("type").value("array");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        stringer.key("type").value("file");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }


    private String headersToJson(String interfaceId) {
        JSONStringer stringer = new JSONStringer();
        List<RequestHeaderEntity> headers = mRequestHeaderService.executeCriteria(ResponseArgUtils.getArgByInterfaceId(interfaceId));
        stringer.array();
        if (headers != null) {
            for (RequestHeaderEntity req : headers) {
                stringer.object()
                        .key("id").value(req.getId())
                        .key("name").value(req.getName())
                        .key("defaultValue").value(req.getValue())
                        .key("description").value(req.getNote() == null ? "" : req.getNote())
                        .key("require").value(true)
                        .key("children").array().endArray();
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }


    private String globalHeaderToJson(String projectId) {
        JSONStringer stringer = new JSONStringer();
        List<RequestHeaderEntity> headers = mRequestHeaderService.executeCriteria(ResponseArgUtils.getGlobal(projectId));
        stringer.array();
        if (headers != null) {
            for (RequestHeaderEntity req : headers) {
                stringer.object()
                        .key("id").value(req.getId())
                        .key("name").value(req.getName())
                        .key("defaultValue").value(req.getValue())
                        .key("description").value(req.getNote() == null ? "" : req.getNote())
                        .key("require").value(true)
                        .key("children").array().endArray();
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }


    private String globalRequestToJson(String projectId) {
        JSONStringer stringer = new JSONStringer();
        List<RequestArgEntity> requestArgEntities = mRequestArgService.executeCriteria(ResponseArgUtils.getGlobal(projectId));
        stringer.array();
        if (requestArgEntities != null) {
            for (RequestArgEntity req : requestArgEntities) {
                stringer.object()
                        .key("id").value(req.getId())
                        .key("name").value(req.getName())
                        .key("pid").value(req.getPid())
                        .key("defaultValue").value(req.getDefaultValue() == null ? "" : req.getDefaultValue())
                        .key("require").value(req.getRequire() == null ? true : req.getRequire())
                        .key("typeId").value(req.getTypeId())
                        .key("description").value(req.getNote() == null ? "" : req.getNote());
                if (req.getTypeId() == null) {
                    req.setTypeId(7);
                }
                switch (req.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        stringer.key("type").value("array");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        stringer.key("type").value("file");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }

    private String globalResponseToJson(String projectId) {
        JSONStringer stringer = new JSONStringer();
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getGlobal(projectId));
        stringer.array();
        if (responseArgEntities != null) {
            for (ResponseArgEntity responseArgEntity : responseArgEntities) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
                        .key("pid").value(responseArgEntity.getPid())
                        .key("defaultValue").value(responseArgEntity.getDefaultValue() == null ? "" : responseArgEntity.getDefaultValue())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                if (responseArgEntity.getTypeId() == null) {
                    responseArgEntity.setTypeId(7);
                }
                switch (responseArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        stringer.key("type").value("array");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        stringer.key("type").value("file");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }
        stringer.endArray();
        return stringer.toString();
    }

    private void responseToChildJson(JSONStringer stringer, String interfaceId, String pid) {
        List<ResponseArgEntity> responseArgEntities = mResponseArgService.executeCriteria(ResponseArgUtils.getArgByInterfaceIdAndPid(interfaceId, pid));
        stringer.key("children").array();
        if (responseArgEntities != null) {
            for (ResponseArgEntity responseArgEntity : responseArgEntities) {
                stringer.object()
                        .key("id").value(responseArgEntity.getId())
                        .key("name").value(responseArgEntity.getName())
                        .key("defaultValue").value(responseArgEntity.getDefaultValue() == null ? "" : responseArgEntity.getDefaultValue())
                        .key("pid").value(responseArgEntity.getPid())
                        .key("typeId").value(responseArgEntity.getTypeId())
                        .key("description").value(responseArgEntity.getNote() == null ? "" : responseArgEntity.getNote());
                if (responseArgEntity.getTypeId() == null) {
                    responseArgEntity.setTypeId(7);
                }
                switch (responseArgEntity.getTypeId()) {
                    case ResponseArgEntity.TYPE_STRING:
                        stringer.key("type").value("string");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_INT:
                        stringer.key("type").value("int");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FLOAT:
                        stringer.key("type").value("float");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_ARRAY_OBJECT:
                        stringer.key("type").value("array[object]");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_OBJECT:
                        stringer.key("type").value("object");
                        responseToChildJson(stringer, interfaceId, responseArgEntity.getId());
                        break;
                    case ResponseArgEntity.TYPE_ARRAY:
                        stringer.key("type").value("array");
                        stringer.key("children").array().endArray();
                        break;
                    case ResponseArgEntity.TYPE_FILE:
                        stringer.key("type").value("file");
                        stringer.key("children").array().endArray();
                        break;
                    default:
                        stringer.key("type").value("未知");
                        stringer.key("children").array().endArray();
                        break;
                }
                stringer.endObject();
            }
        }
        stringer.endArray();
    }

    /***********************************下载 group API 结束**************************************/
}
