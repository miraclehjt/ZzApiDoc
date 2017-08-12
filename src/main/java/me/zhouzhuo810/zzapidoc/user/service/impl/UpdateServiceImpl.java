package me.zhouzhuo810.zzapidoc.user.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.DataUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.common.utils.StringUtils;
import me.zhouzhuo810.zzapidoc.user.dao.UpdateDao;
import me.zhouzhuo810.zzapidoc.user.dao.UserDao;
import me.zhouzhuo810.zzapidoc.user.entity.UpdateEntity;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UpdateService;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import me.zhouzhuo810.zzapidoc.user.utils.UpdateUtils;
import me.zhouzhuo810.zzapidoc.user.utils.UserUtils;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zz on 2017/7/20.
 */
@Service
public class UpdateServiceImpl extends BaseServiceImpl<UpdateEntity> implements UpdateService {

    @Override
    @Resource(name = "updateDaoImpl")
    public void setBaseDao(BaseDao<UpdateEntity> baseDao) {
        super.setBaseDao(baseDao);
    }

    private UpdateDao getDAO() {
        return (UpdateDao) this.baseDao;
    }

    @Override
    public BaseResult checkUpdate(int versionCode) {
        List<UpdateEntity> updateEntities = getDAO().executeCriteria(UpdateUtils.getUpdateApk(versionCode), Order.desc("versionCode"));
        if (updateEntities != null && updateEntities.size() > 0) {
            UpdateEntity entity = updateEntities.get(0);
            MapUtils map = new MapUtils();
            map.put("versionCode", entity.getVersionCode());
            map.put("versionName", entity.getVersionName());
            map.put("address", entity.getAddress());
            map.put("releaseDate", DataUtils.formatDate(entity.getReleaseDate()));
            map.put("updateInfo", entity.getUpdateInfo());
            return new BaseResult(1, "ok", map.build());
        } else {
            return new BaseResult(0, "已经是最新版本了", new HashMap<String, String>());
        }
    }
}
