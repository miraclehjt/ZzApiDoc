package me.zhouzhuo810.zzapidoc.common.utils;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * Created by Administrator on 2017/7/6.
 */
public class CriterionUtils {

    public static Criterion[] getAbleCriterion(String nodeID, String[] relativeID, Integer[] pType) {
        return new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("defineNodeID", nodeID),
                Restrictions.in("relativeID", relativeID),
                Restrictions.in("permissionType", pType),
        };
    }
}
