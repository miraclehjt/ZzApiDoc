package me.zhouzhuo810.zzapidoc.project.utils;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * Created by admin on 2017/7/22.
 */
public class InterfaceUtils {
    public static Criterion[] getInterfaceByProjectId(String projectId) {
        return new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("projectId", projectId)
        };
    }

    public static Criterion[] getInterfaceByGroupId(String groupIds) {
        return new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("groupId", groupIds)
        };
    }
}
