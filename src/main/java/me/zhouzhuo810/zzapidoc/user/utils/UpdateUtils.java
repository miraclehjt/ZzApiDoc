package me.zhouzhuo810.zzapidoc.user.utils;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.util.Date;

/**
 * Created by zz on 2017/7/20.
 */
public class UpdateUtils {

    public static Criterion[] getUpdateApk(int versionCode) {
        return new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.gt("versionCode", versionCode),
                Restrictions.lt("releaseDate", new Date())
        };
    }

}
