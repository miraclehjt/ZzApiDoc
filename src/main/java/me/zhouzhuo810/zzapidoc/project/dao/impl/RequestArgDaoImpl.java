package me.zhouzhuo810.zzapidoc.project.dao.impl;

import me.zhouzhuo810.zzapidoc.common.dao.impl.BaseDaoImpl;
import me.zhouzhuo810.zzapidoc.project.dao.RequestArgDao;
import me.zhouzhuo810.zzapidoc.project.entity.RequestArgEntity;
import me.zhouzhuo810.zzapidoc.project.utils.ResponseArgUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by admin on 2017/7/22.
 */
@Repository
public class RequestArgDaoImpl extends BaseDaoImpl<RequestArgEntity> implements RequestArgDao {
    @Override
    public void deleteByInterfaceId(String interfaceId) {
        List<RequestArgEntity> args = executeCriteria(ResponseArgUtils.getArgByInterfaceId(interfaceId));
        if (args == null) {
            return;
        }
        String[] ids = new String[args.size()];
        for (int i = 0; i < args.size(); i++) {
            RequestArgEntity responseArgEntity = args.get(i);
            ids[i] = responseArgEntity.getId();
        }
        try {
            deleteLogicByIds(ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
