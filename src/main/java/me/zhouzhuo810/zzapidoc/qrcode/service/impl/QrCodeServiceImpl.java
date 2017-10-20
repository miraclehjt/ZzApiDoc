package me.zhouzhuo810.zzapidoc.qrcode.service.impl;

import me.zhouzhuo810.zzapidoc.common.dao.BaseDao;
import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.impl.BaseServiceImpl;
import me.zhouzhuo810.zzapidoc.common.utils.MapUtils;
import me.zhouzhuo810.zzapidoc.qrcode.entity.QrCodeEntity;
import me.zhouzhuo810.zzapidoc.qrcode.service.QrCodeService;
import me.zhouzhuo810.zzapidoc.qrcode.utils.QrCodeUtils;
import me.zhouzhuo810.zzapidoc.user.entity.UserEntity;
import me.zhouzhuo810.zzapidoc.user.service.UserService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zz on 2017/10/20.
 */
@Service
public class QrCodeServiceImpl extends BaseServiceImpl<QrCodeEntity> implements QrCodeService {

    @Resource(name = "userServiceImpl")
    private UserService mUserService;

    @Override
    @Resource(name = "qrCodeDaoImpl")
    public void setBaseDao(BaseDao<QrCodeEntity> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public BaseDao<QrCodeEntity> getBaseDao() {
        return baseDao;
    }

    @Override
    public BaseResult addQrCode(boolean isPrivate, String title, String content, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        QrCodeEntity qrCode = new QrCodeEntity();
        qrCode.setPrivate(isPrivate);
        qrCode.setTitle(title);
        qrCode.setContent(content);
        qrCode.setCreateTime(new Date());
        qrCode.setCreateUserID(user.getId());
        qrCode.setCreateUserName(user.getName());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String realPath = request.getRealPath("");
        String newpath = realPath + File.separator + "QrCode";
        File dir = new File(newpath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".png";
        String savePath = newpath + File.separator + fileName;
        boolean ok = QrCodeUtils.encodeQrCode(200, 200, content, savePath);
        if (ok) {
            qrCode.setUrl("/ZzApiDoc/QrCode/" + fileName);
            qrCode.setFilePath(savePath);
            try {
                save(qrCode);
                return new BaseResult(1, "添加成功！");
            } catch (Exception e) {
                e.printStackTrace();
                return new BaseResult(0, "添加失败！" + e.toString());
            }
        } else {
            return new BaseResult(0, "添加失败！");
        }
    }

    @Override
    public BaseResult updateQrCode(String id, boolean isPrivate, String title, String content, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        QrCodeEntity qrCode = get(id);
        if (qrCode == null) {
            return new BaseResult(0, "二维码不存在或已被删除！");
        }
        qrCode.setPrivate(isPrivate);
        qrCode.setTitle(title);
        qrCode.setContent(content);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String realPath = request.getRealPath("");
        String newpath = realPath + File.separator + "QrCode";
        File dir = new File(newpath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".png";
        String savePath = newpath + File.separator + fileName;
        boolean ok = QrCodeUtils.encodeQrCode(200, 200, content, savePath);
        if (ok) {
            qrCode.setUrl("/ZzApiDoc/QrCode/" + fileName);
            qrCode.setFilePath(savePath);
            try {
                qrCode.setModifyTime(new Date());
                qrCode.setModifyUserID(user.getId());
                qrCode.setModifyUserName(user.getName());
                update(qrCode);
                return new BaseResult(1, "更新成功！");
            } catch (Exception e) {
                e.printStackTrace();
                return new BaseResult(0, "更新失败！" + e.toString());
            }
        } else {
            return new BaseResult(0, "更新失败！");
        }
    }

    @Override
    public BaseResult getAllQrCode(String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        List<QrCodeEntity> qrCodeEntities = getBaseDao().executeCriteria(new Criterion[]{
                Restrictions.or(Restrictions.and(
                        Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                        Restrictions.eq("isPrivate", true),
                        Restrictions.eq("createUserID", user.getId())
                        ), Restrictions.and(
                        Restrictions.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO),
                        Restrictions.eq("isPrivate", false)
                        )
                )
        });
        if (qrCodeEntities == null) {
            return new BaseResult(0, "暂无数据!");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (QrCodeEntity qrCodeEntity : qrCodeEntities) {
            MapUtils map = new MapUtils();
            map.put("id", qrCodeEntity.getId());
            map.put("title", qrCodeEntity.getTitle());
            map.put("content", qrCodeEntity.getContent());
            map.put("url", qrCodeEntity.getUrl());
            map.put("isPrivate", qrCodeEntity.getPrivate() == null ? false : qrCodeEntity.getPrivate());
            result.add(map.build());
        }
        return new BaseResult(1, "ok", result);
    }

    @Override
    public BaseResult deleteQrCode(String id, String userId) {
        UserEntity user = mUserService.get(userId);
        if (user == null) {
            return new BaseResult(0, "用户不存在或不合法！");
        }
        QrCodeEntity qrCode = get(id);
        if (qrCode == null) {
            return new BaseResult(0, "二维码不存在或已被删除！");
        }
        try {
            File file = new File(qrCode.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            qrCode.setModifyTime(new Date());
            qrCode.setModifyUserID(user.getId());
            qrCode.setModifyUserName(user.getName());
            qrCode.setDeleteFlag(BaseEntity.DELETE_FLAG_YES);
            update(qrCode);
            return new BaseResult(1, "删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResult(0, "删除失败！" + e.toString());
        }

    }
}
