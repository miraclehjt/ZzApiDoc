package me.zhouzhuo810.zzapidoc.android.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2017/11/10.
 */
@Entity
@Table(name = "actions")
public class ActionEntity extends BaseEntity {
    public static final int TYPE_DIALOG_TWO_PROGRESS = 0;
    public static final int TYPE_DIALOG_TWO_BTN = 1;
    public static final int TYPE_DIALOG_EDIT = 2;
    public static final int TYPE_DIALOG_UPDATE = 3;
    public static final int TYPE_DIALOG_LIST = 4;

    @Column(name = "Type")
    private int type = TYPE_DIALOG_TWO_PROGRESS;
    @Column(name = "Name")
    private String name;
    @Column(name = "WidgetId")
    private String widgetId;
    @Column(name = "title")
    private String title;
    @Column(name = "msg")
    private String msg;
    @Column(name = "okText")
    private String okText;
    @Column(name = "cancelText")
    private String cancelText;
    @Column(name = "defText")
    private String defText;
    @Column(name = "hintText")
    private String hintText;
    @Column(name = "showOrHide")
    private Boolean showOrHide = false;

    public Boolean getShowOrHide() {
        return showOrHide;
    }

    public void setShowOrHide(Boolean showOrHide) {
        this.showOrHide = showOrHide;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOkText() {
        return okText;
    }

    public void setOkText(String okText) {
        this.okText = okText;
    }

    public String getCancelText() {
        return cancelText;
    }

    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    public String getDefText() {
        return defText;
    }

    public void setDefText(String defText) {
        this.defText = defText;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }
}
