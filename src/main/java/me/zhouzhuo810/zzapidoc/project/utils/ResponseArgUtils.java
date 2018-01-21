package me.zhouzhuo810.zzapidoc.project.utils;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * Created by admin on 2017/7/22.
 */
public class ResponseArgUtils {

    public static Criterion[] getArgByInterfaceId(String interfaceId) {
        return new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("interfaceId", interfaceId)
        };
    }

    public static Criterion[] getArgByInterfaceIdAndPid(String interfaceId, String pid) {
        return new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("interfaceId", interfaceId),
                Restrictions.eq("isGlobal", false),
                Restrictions.eq("pid", pid)
        };
    }


    public static Criterion[] getGlobal(String projectId) {
        return new Criterion[] {
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("projectId", projectId),
                Restrictions.eq("isGlobal", true)
        };
    }

    public static Criterion[] getGlobalWithProjectIdAndPid(String projectId, String pid) {
        return new Criterion[] {
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("projectId", projectId),
                Restrictions.eq("pid", pid),
                Restrictions.eq("isGlobal", true)
        };
    }

}
