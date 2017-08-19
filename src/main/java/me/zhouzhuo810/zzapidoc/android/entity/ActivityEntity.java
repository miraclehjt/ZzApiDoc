package me.zhouzhuo810.zzapidoc.android.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2017/8/17.
 */
@Entity
@Table(name = "activity")
public class ActivityEntity extends BaseEntity {

    public static final int TYPE_LV_ACT = 0;
    public static final int TYPE_RV_ACT = 1;
    public static final int TYPE_MAIN_ACT = 2;
    public static final int TYPE_TAB_ACT = 3;
    public static final int TYPE_SETTING = 4;
    public static final int TYPE_SPLASH = 5;
    public static final int TYPE_SUBMIT = 6;
    public static final int TYPE_DETAILS = 7;

    @Column(name = "Name")
    private String name;
    @Column(name = "Type")
    private Integer type = TYPE_LV_ACT;
    @Column(name = "Title")
    private String title;
    @Column(name = "SplashImg")
    private String splashImg;
    @Column(name = "ShowTitleBar")
    private Boolean showTitleBar;
    @Column(name = "ApplicationId")
    private String applicationId;
    @Column(name = "TargetActId")
    private String targetActId;
    @Column(name = "SplashDuration")
    private Integer splashDuration;

    public String getTargetActId() {
        return targetActId;
    }

    public void setTargetActId(String targetActId) {
        this.targetActId = targetActId;
    }

    public Integer getSplashDuration() {
        return splashDuration;
    }

    public void setSplashDuration(Integer splashDuration) {
        this.splashDuration = splashDuration;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Boolean getShowTitleBar() {
        return showTitleBar;
    }

    public void setShowTitleBar(Boolean showTitleBar) {
        this.showTitleBar = showTitleBar;
    }

    public String getSplashImg() {
        return splashImg;
    }

    public void setSplashImg(String splashImg) {
        this.splashImg = splashImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
