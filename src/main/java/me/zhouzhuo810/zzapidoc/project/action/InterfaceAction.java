package me.zhouzhuo810.zzapidoc.project.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.result.WebResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.InterfaceEntity;
import me.zhouzhuo810.zzapidoc.project.entity.ProjectEntity;
import me.zhouzhuo810.zzapidoc.project.service.InterfaceService;
import me.zhouzhuo810.zzapidoc.project.service.ProjectService;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * Created by admin on 2017/7/22.
 */
@Controller
@RequestMapping(value = "v1/interface")
public class InterfaceAction extends BaseController<InterfaceEntity> {

    @Override
    @Resource(name = "interfaceServiceImpl")
    public void setBaseService(BaseService<InterfaceEntity> baseService) {
        this.baseService = baseService;
    }

    public InterfaceService getService() {
        return (InterfaceService) baseService;
    }
/**
 * responseArgs 示例格式
 *
[
    {
        "name": "android",
        "typeId": 1,
        "note": ""
    },
    {
        "name": "msg",
        "typeId": 0,
        "note": ""
    },
    {
        "name": "data",
        "typeId": 3,
        "note": "",
        "child": [
            {
                "name": "phone",
                "typeId": 0,
                "note": "手机号"
            },
            {
                "name": "name",
                "typeId": 0,
                "note": "名字"
            }
        ]
    }
]
*/
    @ResponseBody
    @RequestMapping(value = "/addInterface", method = RequestMethod.POST)
    public BaseResult addInterface(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "path") String path,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "groupId") String groupId,
            @RequestParam(value = "httpMethodId") String httpMethodId,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "requestArgs", required = false) String requestArgs,
            @RequestParam(value = "responseArgs", required = false) String responseArgs
    ) {
        return getService().addInterface(name, path, projectId, groupId, httpMethodId, note, userId, requestArgs, responseArgs);
    }


    @ResponseBody
    @RequestMapping(value = "/updateInterface", method = RequestMethod.POST)
    public BaseResult updateInterface(
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "path") String path,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "groupId") String groupId,
            @RequestParam(value = "httpMethodId") String httpMethodId,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "requestArgs", required = false) String requestArgs,
            @RequestParam(value = "responseArgs", required = false) String responseArgs
    ) {
        return getService().updateInterface(interfaceId, name, path, projectId, groupId, httpMethodId, note, userId, requestArgs, responseArgs);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteInterface", method = RequestMethod.POST)
    public BaseResult deleteInterface(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().deleteInterface(id, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllInterface", method = RequestMethod.GET)
    public BaseResult getInterfaceAll(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getAllInterface(projectId, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/getInterfaceByGroupId", method = RequestMethod.GET)
    public BaseResult getInterfaceByGroupId(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "groupId") String groupId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getInterfaceByGroupId(projectId, groupId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getInterfaceByGroupIdWeb", method = RequestMethod.GET)
    public WebResult getInterfaceByGroupIdWeb(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "groupId") String groupId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getInterfaceByGroupIdWeb(projectId, groupId, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/getInterfaceDetails", method = RequestMethod.GET)
    public BaseResult getInterfaceDetails(
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().getInterfaceDetails(interfaceId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/addInterfaceExample", method = RequestMethod.POST)
    public BaseResult addInterfaceExample(
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "example") String example,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().addInterfaceExample(interfaceId, example, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/setTestFinish", method = RequestMethod.POST)
    public BaseResult setTest(
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().setTestFinish(interfaceId, userId);
    }



    @ResponseBody
    @RequestMapping(value = "/generateEmptyExample", method = RequestMethod.GET)
    public BaseResult generateEmptyExample(
            @RequestParam(value = "interfaceId") String interfaceId,
            @RequestParam(value = "userId") String userId
    ) {
        return getService().generateEmptyExample(interfaceId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/downloadJson", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "userId") String userId
    ) throws IOException {
        return getService().download(projectId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/downloadPdf", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "userId") String userId
    ) throws IOException {
        return getService().downloadPdf(projectId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/downloadApi", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadApi(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "userId") String userId
    ) throws IOException {
        return getService().downloadApi(projectId, userId);
    }



}
