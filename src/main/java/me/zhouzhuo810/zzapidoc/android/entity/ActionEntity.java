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
    public static final int TYPE_DIALOG_PROGRESS = 0;
    public static final int TYPE_DIALOG_TWO_BTN = 1;
    public static final int TYPE_DIALOG_EDIT = 2;
    public static final int TYPE_DIALOG_UPDATE = 3;
    public static final int TYPE_DIALOG_LIST = 4;
    public static final int TYPE_DIALOG_TWO_BTN_IOS = 5;
    public static final int TYPE_CHOOSE_PIC = 6;
    public static final int TYPE_USE_API = 7;
    public static final int TYPE_TARGET_ACT = 8;
    public static final int TYPE_CLOSE_ACT = 9;
    public static final int TYPE_CLOSE_ALL_ACT = 10;

    @Column(name = "Type")
    private int type = TYPE_DIALOG_PROGRESS;
    @Column(name = "Name")
    private String name;
    @Column(name = "WidgetId")
    private String widgetId;
    @Column(name = "Title")
    private String title;
    @Column(name = "Msg")
    private String msg;
    @Column(name = "OkText")
    private String okText;
    @Column(name = "CancelText")
    private String cancelText;
    @Column(name = "DefText")
    private String defText;
    @Column(name = "HintText")
    private String hintText;
    @Column(name = "ShowOrHide")
    private Boolean showOrHide = false;
    @Column(name = "OkApiId")
    private String okApiId;
    @Column(name = "OkActId")
    private String okActId;
    @Column(name = "Items")
    private String items;

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getOkApiId() {
        return okApiId;
    }

    public void setOkApiId(String okApiId) {
        this.okApiId = okApiId;
    }

    public String getOkActId() {
        return okActId;
    }

    public void setOkActId(String okActId) {
        this.okActId = okActId;
    }

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
