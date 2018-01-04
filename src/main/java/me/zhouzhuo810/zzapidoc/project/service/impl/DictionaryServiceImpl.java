package me.zhouzhuo810.zzapidoc.project.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.project.dao.DictionaryDao;
import me.zhouzhuo810.zzapidoc.project.entity.DictionaryEntity;
import me.zhouzhuo810.zzapidoc.project.service.DictionaryService;
import me.zhouzhuo810.zzapidoc.project.utils.DictionaryUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/22.
 */
@Service
public class DictionaryServiceImpl extends BaseServiceImpl<DictionaryEntity> implements DictionaryService {

    @Resource(name = "userServiceImpl")
    UserService mUserService;

    @Override
    @Resource(name = "dictionaryDaoImpl")
    public void setBaseDao(BaseDao<DictionaryEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public DictionaryDao getBaseDao() {
        return (DictionaryDao) this.baseDao;
    }

    @Override
    public BaseResult addDictionary(String name, String type, String pid, int position, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不合法", new HashMap<String,String>());
        }
        DictionaryEntity entity = new DictionaryEntity();
        entity.setName(name);
        entity.setPid(pid);
        entity.setPosition(position);
        entity.setType(type);
        entity.setCreateUserID(user.getId());
        entity.setCreateUserName(user.getName());
        try {
            getBaseDao().save(entity);
            String id = entity.getId();
            MapUtils map = new MapUtils();
            map.put("id", id);
            return new BaseResult(1, "ok", map.build());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "添加失败", new HashMap<String,String>());
        }
    }

    @Override
    public BaseResult getDictionaryByType(String type) {
        List<DictionaryEntity> list = getBaseDao().executeCriteria(DictionaryUtils.getDicByType(type), Order.asc("createTime"));
        if (list == null) {
            return new BaseResult(0, "暂无数据");
        }
        List<Map<String,Object>> result = new ArrayList<Map<String, Object>>();
        for (DictionaryEntity entity : list) {
            MapUtils map = new MapUtils();
            map.put("id", entity.getId());
            map.put("pid", entity.getPid());
            map.put("name", entity.getName());
            map.put("type", entity.getType());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }
}
