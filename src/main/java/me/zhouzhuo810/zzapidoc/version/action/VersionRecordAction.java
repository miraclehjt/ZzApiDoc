package me.zhouzhuo810.zzapidoc.version.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.version.entity.VersionRecordEntity;
import me.zhouzhuo810.zzapidoc.version.service.VersionRecordService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by zz on 2017/10/26.
 */
@Controller
@RequestMapping(value = "v1/versionRecord")
public class VersionRecordAction extends BaseController<VersionRecordEntity> {
    @Override
    @Resource(name = "versionRecordServiceImpl")
    public void setBaseService(BaseService<VersionRecordEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public VersionRecordService getBaseService() {
        return (VersionRecordService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addVersionRecord", method = RequestMethod.POST)
    public BaseResult addVersionRecord(
            @RequestParam(value = "versionId") String versionId,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "note") String note,
            @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().addVersionRecord(versionId, projectId, note, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/updateVersionRecord", method = RequestMethod.POST)
    public BaseResult updateVersionRecord(
            @RequestParam(value = "recordId") String recordId,
            @RequestParam(value = "versionId") String versionId,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "note") String note,
            @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().updateVersionRecord(recordId, versionId, projectId, note, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/deleteVersionRecord", method = RequestMethod.POST)
    public BaseResult deleteVersionRecord(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {

        return getBaseService().deleteVersionRecord(id, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/getAllVersionRecord", method = RequestMethod.GET)
    public BaseResult getAllVersionRecord(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "versionId") String versionId
    ) {

        return getBaseService().getAllVersionRecord(userId, versionId);
    }


}
