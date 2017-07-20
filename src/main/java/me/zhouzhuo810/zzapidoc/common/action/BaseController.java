package me.zhouzhuo810.zzapidoc.common.action;


import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.common.utils.GenericUtil;
import me.zhouzhuo810.zzapidoc.common.utils.JsonUtil;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class BaseController<E extends BaseEntity> {

    protected BaseService<E> baseService;

    private Class<E> entityClass;

    public abstract void setBaseService(BaseService<E> baseService);

    public BaseService<E> getBaseService() {
        return baseService;
    }

    @SuppressWarnings("unchecked")
    public BaseController() {
        try {
            entityClass = GenericUtil.getActualClass(this.getClass(), 0);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    /**
     * 后台list页面
     * 如请求地址为：   	http://localhost:8080/web/sysmanRole/list
     * 则返回的页面应该在    /web/WEB-INF/views/sysmanRole/list.jsp
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list(ModelAndView model) {
        RequestMapping rm = this.getClass().getAnnotation(RequestMapping.class);
        String moduleName = "";
        if (rm != null) {
            String[] values = rm.value();
            if (ArrayUtils.isNotEmpty(values)) {
                moduleName = values[0];
            }
        }
        if (moduleName.endsWith("/")) {
            moduleName = moduleName.substring(0, moduleName.length() - 1);
        }

        model.setViewName("views/" + moduleName + "/list");
        model.addObject("moduleName", moduleName);
        return model;
    }

    /**
     * 过滤属性集合
     *
     * @param condition
     */
    protected void decorateCondition(DetachedCriteria condition) {
        condition.add(Expression.eq("deleteFlag", BaseEntity.DELETE_FLAG_NO));
    }

    /**
     * json 化
     *
     * @param data
     * @param response
     * @throws IOException
     */
    public void renderJson(Object data, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(JsonUtil.object2String(data));
        writer.flush();
    }


    /**
     * 调用保存方法之前进行的方法调用
     *
     * @param request
     * @param entity  对应实体信息
     * @throws Exception
     */
    protected void beforeDoSave(HttpServletRequest request, E entity) throws Exception {

    }

    /**
     * 电泳保存方法之后进行的方法调用
     *
     * @param request
     * @param entity  对应实体信息
     * @throws Exception
     */
    protected void afterDoSave(HttpServletRequest request, E entity) throws Exception {

    }


    /**
     * 调用更新操作之前进行的操作
     *
     * @param request
     * @param entity
     * @throws Exception
     */
    protected void beforeDoUpdate(HttpServletRequest request, E entity) throws Exception {

    }

    /**
     * 调用更新操作之后进行的操作
     *
     * @param request
     * @param entity
     * @throws Exception
     */
    protected void afterDoUpdate(HttpServletRequest request, E entity) throws Exception {

    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BaseResult resolveException(MissingServletRequestParameterException errors) {
        return new BaseResult(0, errors.getParameterName() + " 不能为空");
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

}
