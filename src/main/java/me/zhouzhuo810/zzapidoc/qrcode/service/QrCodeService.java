package me.zhouzhuo810.zzapidoc.qrcode.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.qrcode.entity.QrCodeEntity;

/**
 * Created by zz on 2017/10/20.
 */
public interface QrCodeService extends BaseService<QrCodeEntity> {

    BaseResult addQrCode(boolean isPrivate, String title, String content, String userId);

    BaseResult updateQrCode(String id, boolean isPrivate, String title, String content, String userId);

    BaseResult getAllQrCode(String userId);

    BaseResult deleteQrCode(String id, String userId);
}
