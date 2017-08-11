package me.zhouzhuo810.zzapidoc.user.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.FileUtils;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.common.utils.StringUtils;
import me.zhouzhuo810.zzapidoc.user.dao.UserDao;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import me.zhouzhuo810.zzapidoc.user.utils.UserUtils;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.ServiceContextHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
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
            return new BaseResult(0, "手机号不能为空！", new HashMap<String, String>());
        if (StringUtils.isEmpty(password))
            return new BaseResult(0, "密码不能为空！", new HashMap<String, String>());
        List<UserEntity> userEntities = getDAO().executeCriteria(UserUtils.getPhoneCriterion(phone));
        if (userEntities == null || userEntities.size() == 0)
            return new BaseResult(0, "用户不存在！", new HashMap<String, String>());
        UserEntity userEntity = getDAO().executeCriteriaForObject(UserUtils.getPhoneAndPasswordCriterion(phone, password));
        if (userEntity == null)
            return new BaseResult(0, "用户名或密码错误！", new HashMap<String, String>());
        MapUtils map = new MapUtils();
        map.put("id", userEntity.getId());
        map.put("pic", userEntity.getPic());
        map.put("name", userEntity.getName());
        return new BaseResult(1, "登录成功！", map.build());
    }

    @Override
    public BaseResult doRegister(String phone, String password, String name, String sex, String email) {
        if (StringUtils.isEmpty(phone))
            return new BaseResult(0, "手机号不能为空！", new HashMap<String, String>());
        if (StringUtils.isEmpty(password))
            return new BaseResult(0, "密码不能为空！", new HashMap<String, String>());
        UserEntity entity = new UserEntity();
        entity.setName(name);
        entity.setPhone(phone);
        entity.setEmail(email);
        entity.setSex(sex);
        entity.setPassword(password);
        try {
            getDAO().save(entity);
            String id = entity.getId();
            MapUtils map = new MapUtils();
            map.put("id", id);
            return new BaseResult(1, "注册成功！", map.build());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0 , "注册失败！", new HashMap<String, String>());
        }
    }

    @Override
    public BaseResult revisePswd(String userId, String oldPswd, String newPswd) {
        UserEntity userEntity = getDAO().get(userId);
        if (userEntity == null) {
            return new BaseResult(0, "用户名或密码错误！");
        }
        if (oldPswd==null) {

        }
        if (!userEntity.getPassword().equals(oldPswd)) {
            return new BaseResult(0, "原密码错误！");
        }
        userEntity.setPassword(newPswd);
        userEntity.setModifyTime(new Date());
        userEntity.setModifyUserID(userEntity.getId());
        userEntity.setModifyUserName(userEntity.getName());
        try {
            getDAO().update(userEntity);
            return new BaseResult(1, "修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "修改失败！"+e.toString());
        }
    }
}
