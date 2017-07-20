package me.zhouzhuo810.zzapidoc.user.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.StringUtils;
import me.zhouzhuo810.zzapidoc.user.dao.UserDao;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import me.zhouzhuo810.zzapidoc.user.utils.UserUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zz on 2017/7/20.
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<UserEntity> implements UserService {

    @Override
    @Resource(name = "userDaoImpl")
    public void setBaseDao(BaseDao<UserEntity> baseDao) {
        super.setBaseDao(baseDao);
    }

    private UserDao getDAO() {
        return (UserDao) this.baseDao;
    }

    @Override
    public BaseResult doLogin(String phone, String password) {
        if (StringUtils.isEmpty(phone))
            return new BaseResult(0, "手机号不能为空！");
        if (StringUtils.isEmpty(password))
            return new BaseResult(0, "密码不能为空！");
        List<UserEntity> userEntities = getDAO().executeCriteria(UserUtils.getPhoneCriterion(phone));
        if (userEntities == null || userEntities.size() == 0)
            return new BaseResult(0, "用户不存在！");
        UserEntity userEntity = getDAO().executeCriteriaForObject(UserUtils.getPhoneAndPasswordCriterion(phone, password));
        if (userEntity== null)
            return new BaseResult(0, "用户名或密码错误！");
        return new BaseResult(1, "登录成功！");
    }
}
