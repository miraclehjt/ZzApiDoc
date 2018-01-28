package me.zhouzhuo810.zzapidoc.project.action;

import me.zhouzhuo810.zzapidoc.common.action.BaseController;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.DictionaryEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by admin on 2018/1/27.
 */
@Controller
@RequestMapping(value = "")
public class CommonAction extends BaseController<DictionaryEntity> {
    @Override
    @Resource(name = "dictionaryServiceImpl")
    public void setBaseService(BaseService<DictionaryEntity> baseService) {
        this.baseService = baseService;
    }

    @RequestMapping("/home")
    public void actionHome(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }

    @RequestMapping("/interface")
    public void actionInterface(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.getRequestDispatcher("interfaceList.jsp").forward(req,resp);
    }

    @RequestMapping("/group")
    public void actionGroup(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.getRequestDispatcher("groupList.jsp").forward(req,resp);
    }

    @RequestMapping("/reqArg")
    public void actionReqArg(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.getRequestDispatcher("reqArgList.jsp").forward(req,resp);
    }

    @RequestMapping("/respArg")
    public void actionRespArg(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.getRequestDispatcher("resArgList.jsp").forward(req,resp);
    }

    @RequestMapping("/errorCode")
    public void actionErrorCode(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.getRequestDispatcher("errorCodeList.jsp").forward(req,resp);
    }
}
