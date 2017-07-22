package me.zhouzhuo810.zzapidoc.project.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.DictionaryEntity;
import me.zhouzhuo810.zzapidoc.project.service.DictionaryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by admin on 2017/7/22.
 */
@Controller
@RequestMapping(value = "v1/dictionary")
public class DictionaryAction extends BaseController<DictionaryEntity> {
    @Override
    @Resource(name = "dictionaryServiceImpl")
    public void setBaseService(BaseService<DictionaryEntity> baseService) {
        this.baseService = baseService;
    }

    @Override
    public DictionaryService getBaseService() {
        return (DictionaryService) baseService;
    }

    @ResponseBody
    @RequestMapping(value = "/addDictionary", method = RequestMethod.POST)
    public BaseResult addDictionary(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "pid") String pid,
            @RequestParam(value = "position") int position,
            @RequestParam(value = "userId") String userId
    ) {
        return getBaseService().addDictionary(name, type, pid, position, userId);
    }


    @ResponseBody
    @RequestMapping(value = "/getDictionary", method = RequestMethod.GET)
    public BaseResult getDictionary(
            @RequestParam(value = "type") String type
    ) {
        return getBaseService().getDictionaryByType(type);
    }




}
