package me.zhouzhuo810.zzapidoc.project.utils;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

    public static Criterion[] getInterfaceByGroupId(String groupIds, String search) {
        String result = "";
        try {
            result = new String(search.getBytes("ISO8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("groupId", groupIds),
                Restrictions.or(
                        Restrictions.like("name", "%" + result + "%"),
                        Restrictions.like("note", "%" + result + "%"),
                        Restrictions.like("path", "%" + result + "%")
                )
        };
    }
}
