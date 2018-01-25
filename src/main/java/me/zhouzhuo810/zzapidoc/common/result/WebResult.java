package me.zhouzhuo810.zzapidoc.common.result;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/30.
 */
public class WebResult {

    private int indexPage;
    private int totalPage;
    private int totalRow;

    private Object rows;

    public WebResult(int indexPage, int totalPage, int totalRow) {
        this(indexPage,totalPage, totalRow, new ArrayList<String>());
    }


    public WebResult(int indexPage, int totalPage, int totalRow, Object rows) {
        this.indexPage = indexPage;
        this.totalPage = totalPage;
        this.totalRow = totalRow;
        this.rows = rows;
    }

    public int getIndexPage() {
        return indexPage;
    }

    public void setIndexPage(int indexPage) {
        this.indexPage = indexPage;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public Object getRows() {
        return rows;
    }

    public void setRows(Object rows) {
        this.rows = rows;
    }
}
