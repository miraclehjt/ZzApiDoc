package me.zhouzhuo810.zzapidoc.android.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2018/1/2.
 */
@Entity
@Table(name = "items")
public class ItemEntity extends BaseEntity {
    public static final int TYPE_RV_ITEM = 0;
    public static final int TYPE_LV_ITEM = 1;
    public static final int TYPE_HEADER = 2;
    public static final int TYPE_FOOTER = 3;

    @Column(name = "type")
    private Integer type = TYPE_RV_ITEM;
    @Column(name = "name")
    private String name;
    @Column(name = "resId")
    private String resId;
    @Column(name = "widgetId")
    private String widgetId;
    @Formula("(SELECT w.Name FROM widget w WHERE w.ID = widgetId)")
    private String widgetName;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public String getWidgetName() {
        return widgetName;
    }

    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }

}
