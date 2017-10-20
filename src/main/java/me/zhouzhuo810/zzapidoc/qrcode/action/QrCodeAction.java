package me.zhouzhuo810.zzapidoc.qrcode.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.qrcode.entity.QrCodeEntity;
import me.zhouzhuo810.zzapidoc.qrcode.service.QrCodeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 二维码管理
 * Created by zz on 2017/10/20.
 */
@Controller
@RequestMapping(value = "v1/qrcode")
public class QrCodeAction extends BaseController<QrCodeEntity> {

    @Override
    @Resource(name = "qrCodeServiceImpl")
    public void setBaseService(BaseService<QrCodeEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public QrCodeService getBaseService() {
        return (QrCodeService) this.baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addQrCode", method = RequestMethod.POST)
    public BaseResult addQrCode(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "isPrivate", required = false) boolean isPrivate,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content
    ) {
        return getBaseService().addQrCode(isPrivate, title, content, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/updateQrCode", method = RequestMethod.POST)
    public BaseResult addQrCode(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "id") String id,
            @RequestParam(value = "isPrivate", required = false) boolean isPrivate,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content
    ) {
        return getBaseService().updateQrCode(id, isPrivate, title, content, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllQrCode", method = RequestMethod.GET)
    public BaseResult getAllQrCode(
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().getAllQrCode(userId);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteQrCode", method = RequestMethod.POST)
    public BaseResult deleteQrCode(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "id") String id
    ) {
        return getBaseService().deleteQrCode(id, userId);
    }


}
