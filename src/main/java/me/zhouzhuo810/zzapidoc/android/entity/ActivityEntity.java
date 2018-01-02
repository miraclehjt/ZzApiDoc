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
    @Column(name = "IsLandscape")
    private Boolean isLandscape = false;
    @Column(name = "IsFullScreen")
    private Boolean isFullScreen = false;
    //Guide Activity 图片地址（最多5张）
    @Column(name = "GuideImgOne")
    private String guideImgOne;
    @Column(name = "GuideImgTwo")
    private String guideImgTwo;
    @Column(name = "GuideImgThree")
    private String guideImgThree;
    @Column(name = "GuideImgFour")
    private String guideImgFour;
    @Column(name = "GuideImgFive")
    private String guideImgFive;
    @Column(name = "GuideImgCount")
    private Integer guideImgCount = 0;

    public String getGuideImgOne() {
        return guideImgOne;
    }

    public void setGuideImgOne(String guideImgOne) {
        this.guideImgOne = guideImgOne;
    }

    public String getGuideImgTwo() {
        return guideImgTwo;
    }

    public void setGuideImgTwo(String guideImgTwo) {
        this.guideImgTwo = guideImgTwo;
    }

    public String getGuideImgThree() {
        return guideImgThree;
    }

    public void setGuideImgThree(String guideImgThree) {
        this.guideImgThree = guideImgThree;
    }

    public String getGuideImgFour() {
        return guideImgFour;
    }

    public void setGuideImgFour(String guideImgFour) {
        this.guideImgFour = guideImgFour;
    }

    public String getGuideImgFive() {
        return guideImgFive;
    }

    public void setGuideImgFive(String guideImgFive) {
        this.guideImgFive = guideImgFive;
    }

    public Integer getGuideImgCount() {
        return guideImgCount;
    }

    public void setGuideImgCount(Integer guideImgCount) {
        this.guideImgCount = guideImgCount;
    }

    public Boolean getFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(Boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public Boolean getLandscape() {
        return isLandscape;
    }

    public void setLandscape(Boolean landscape) {
        isLandscape = landscape;
    }

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
