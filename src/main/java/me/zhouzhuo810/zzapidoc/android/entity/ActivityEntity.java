package me.zhouzhuo810.zzapidoc.android.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2017/8/17.
 */
@Entity
@Table(name = "activity")
public class ActivityEntity extends BaseEntity {

    public static final int TYPE_EMPTY_ACT = 0;
    public static final int TYPE_SPLASH = 1;
    public static final int TYPE_GUIDE = 2;
    public static final int TYPE_BOTTOM_FRAGMENT = 3;
    public static final int TYPE_TOP_FRAGMENT = 4;

    @Column(name = "Name")
    private String name;
    @Column(name = "Type")
    private Integer type = TYPE_EMPTY_ACT;
    @Column(name = "Title")
    private String title;
    @Column(name = "SplashImg")
    private String splashImg;
    @Column(name = "IsFirst")
    private Boolean isFirst = false;
    @Column(name = "ApplicationId")
    private String applicationId;
    @Column(name = "TargetActId")
    private String targetActId;
    @Formula("(SELECT a.Name FROM activity a WHERE a.ID = TargetActId)")
    private String targetActName;
    @Column(name = "SplashSecond")
    private Integer splashSecond = 5;

    public Boolean getFirst() {
        return isFirst;
    }

    public void setFirst(Boolean first) {
        isFirst = first;
    }

    public String getTargetActName() {
        return targetActName;
    }

    public void setTargetActName(String targetActName) {
        this.targetActName = targetActName;
    }

    public String getTargetActId() {
        return targetActId;
    }

    public void setTargetActId(String targetActId) {
        this.targetActId = targetActId;
    }

    public Integer getSplashSecond() {
        return splashSecond;
    }

    public void setSplashSecond(Integer splashSecond) {
        this.splashSecond = splashSecond;
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
