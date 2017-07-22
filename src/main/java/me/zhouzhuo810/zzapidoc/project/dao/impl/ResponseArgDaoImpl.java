package me.zhouzhuo810.zzapidoc.project.dao.impl;

import me.zhouzhuo810.zzapidoc.common.dao.impl.BaseDaoImpl;
import me.zhouzhuo810.zzapidoc.project.dao.ResponseArgDao;
import me.zhouzhuo810.zzapidoc.project.entity.ResponseArgEntity;
import me.zhouzhuo810.zzapidoc.project.utils.ResponseArgUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by admin on 2017/7/22.
 */
@Repository
public class ResponseArgDaoImpl extends BaseDaoImpl<ResponseArgEntity> implements ResponseArgDao {
    @Override
    public void deleteByInterfaceId(String interfaceId) {
        List<ResponseArgEntity> args = executeCriteria(ResponseArgUtils.getArgByInterfaceId(interfaceId));
        if (args == null) {
            return;
        }
        String[] ids = new String[args.size()];
        for (int i = 0; i < args.size(); i++) {
            ResponseArgEntity responseArgEntity = args.get(i);
            ids[i] = responseArgEntity.getId();
        }
        try {
            deleteLogicByIds(ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
