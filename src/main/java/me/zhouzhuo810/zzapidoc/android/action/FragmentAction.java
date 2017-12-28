package me.zhouzhuo810.zzapidoc.android.action;

import me.zhouzhuo810.zzapidoc.android.entity.FragmentEntity;
import me.zhouzhuo810.zzapidoc.android.entity.FragmentEntity;
import me.zhouzhuo810.zzapidoc.android.service.FragmentService;
import me.zhouzhuo810.zzapidoc.android.service.FragmentService;
import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/8/17.
 */
@Controller
@RequestMapping(value = "v1/fragment")
public class FragmentAction extends BaseController<FragmentEntity> {

    @Override
    @Resource(name = "fragmentServiceImpl")
    public void setBaseService(BaseService<FragmentEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public FragmentService getBaseService() {
        return (FragmentService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addFragment", method = RequestMethod.POST)
    public BaseResult addFragment(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "position") int position,
            @RequestParam(value = "appId", required = false) String appId,
            @RequestParam(value = "pid", required = false) String pid,
            @RequestParam(value = "activityId", required = false) String activityId,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().addFragment(pid, name, title, type, position, appId, activityId, userId);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllMyFragment", method = RequestMethod.GET)
    public BaseResult getAllMyFragment(
            @RequestParam(value = "activityId") String activityId,
            @RequestParam(value = "pid", required = false) String pid,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().getAllMyFragment(activityId, pid, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/deleteFragment", method = RequestMethod.POST)
    public BaseResult deleteFragment(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "id") String id
    ) {
        return getBaseService().deleteFragment(id, userId);
    }
}
