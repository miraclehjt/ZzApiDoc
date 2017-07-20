package me.zhouzhuo810.zzapidoc.common.result;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/30.
 */
public class BaseResult {

    private int code;
    private String msg;

    public BaseResult(int code, String msg) {
        this(code,msg, new ArrayList<String>());
    }

    public BaseResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
