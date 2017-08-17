package me.zhouzhuo810.zzapidoc.android.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2017/8/17.
 */
@Entity
@Table(name = "fragment")
public class FragmentEntity extends BaseEntity{
    public static final int TYPE_LV_FGM = 0;
    public static final int TYPE_RV_FGM = 1;
    public static final int TYPE_SETTING_FGM = 2;

    @Column(name = "Type")
    private Integer type = TYPE_LV_FGM;
    @Column(name = "Title")
    private String title;
    @Column(name = "Name")
    private String name;
    @Column(name = "ShowTitle")
    private Boolean showTitle;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(Boolean showTitle) {
        this.showTitle = showTitle;
    }
}
