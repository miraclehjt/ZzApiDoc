package me.zhouzhuo810.zzapidoc.user.utils;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * Created by zz on 2017/7/20.
 */
public class UserUtils {

    public static Criterion[] getPhoneAndPasswordCriterion(String phone, String password) {
        return new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("phone", phone),
                Restrictions.eq("password", password)
        };
    }

    public static Criterion[] getPhoneCriterion(String phone) {
        return new Criterion[]{
                Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                Restrictions.eq("phone", phone)
        };
    }
}
