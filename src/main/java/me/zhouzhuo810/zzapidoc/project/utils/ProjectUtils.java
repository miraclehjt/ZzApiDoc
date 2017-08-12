package me.zhouzhuo810.zzapidoc.project.utils;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ProjectEntity;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * Created by admin on 2017/7/22.
 */
public class ProjectUtils {
    public static Criterion[] getProjectByUserId(String userId) {
        return new Criterion[]{
                        Restrictions.or(
                                Restrictions.and(
                                        Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                                        Restrictions.eq("property", ProjectEntity.PROPERTY_PUBLIC))
                                ,Restrictions.and(
                                        Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                                        Restrictions.eq("property", ProjectEntity.PROPERTY_PRIVATE),
                                        Restrictions.eq("createUserID", userId)
                                )
                        )
        };
    }

}
