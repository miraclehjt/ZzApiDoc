package me.zhouzhuo810.zzapidoc.common.result;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/30.
 */
public class WebResult {

    private int total;
    private Object rows;


    public WebResult(int total) {
        this(0, new ArrayList<String>());
    }


    public WebResult(int total, Object rows) {
        this.total = total;
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Object getRows() {
        return rows;
    }

    public void setRows(Object rows) {
        this.rows = rows;
    }
}
