package me.zhouzhuo810.zzapidoc.android.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2017/8/17.
 */
@Entity
@Table(name = "widget")
public class WidgetEntity extends BaseEntity{
    public static final int TYPE_TITLE_BAR = 1;
    public static final int TYPE_SETTING_ITEM = 2;
    public static final int TYPE_EDIT_ITEM = 3;
    public static final int TYPE_INFO_ITEM = 4;
    public static final int TYPE_BTN_ITEM = 5;

    @Column(name = "Type")
    private Integer type = TYPE_TITLE_BAR;
    @Column(name = "RelativeId")
    private String relativeId;
    @Column(name = "Title")
    private String title;
    @Column(name = "Hint")
    private String hint;
    @Column(name = "DefValue")
    private String defValue;
    @Column(name = "TargetActId")
    private String targetActId;
    @Column(name = "LeftTitleText")
    private String leftTitleText;
    @Column(name = "RightTitleText")
    private String rightTitleText;
    @Column(name = "LeftTitleImg")
    private String leftTitleImg;
    @Column(name = "RightTitleImg")
    private String rightTitleImg;
    @Column(name = "ShowLeftTitleImg")
    private Boolean showLeftTitleImg;
    @Column(name = "ShowRightTitleImg")
    private Boolean showRightTitleImg;
    @Column(name = "ShowLeftTitleText")
    private Boolean showLeftTitleText;
    @Column(name = "ShowRightTitleText")
    private Boolean showRightTitleText;
    @Column(name = "ShowLeftTitleLayout")
    private Boolean showLeftTitleLayout;
    @Column(name = "ShowRightTitleLayout")
    private Boolean showRightTitleLayout;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getLeftTitleText() {
        return leftTitleText;
    }

    public void setLeftTitleText(String leftTitleText) {
        this.leftTitleText = leftTitleText;
    }

    public String getRightTitleText() {
        return rightTitleText;
    }

    public void setRightTitleText(String rightTitleText) {
        this.rightTitleText = rightTitleText;
    }

    public String getLeftTitleImg() {
        return leftTitleImg;
    }

    public void setLeftTitleImg(String leftTitleImg) {
        this.leftTitleImg = leftTitleImg;
    }

    public String getRightTitleImg() {
        return rightTitleImg;
    }

    public void setRightTitleImg(String rightTitleImg) {
        this.rightTitleImg = rightTitleImg;
    }

    public Boolean getShowLeftTitleImg() {
        return showLeftTitleImg;
    }

    public void setShowLeftTitleImg(Boolean showLeftTitleImg) {
        this.showLeftTitleImg = showLeftTitleImg;
    }

    public Boolean getShowRightTitleImg() {
        return showRightTitleImg;
    }

    public void setShowRightTitleImg(Boolean showRightTitleImg) {
        this.showRightTitleImg = showRightTitleImg;
    }

    public Boolean getShowLeftTitleText() {
        return showLeftTitleText;
    }

    public void setShowLeftTitleText(Boolean showLeftTitleText) {
        this.showLeftTitleText = showLeftTitleText;
    }

    public Boolean getShowRightTitleText() {
        return showRightTitleText;
    }

    public void setShowRightTitleText(Boolean showRightTitleText) {
        this.showRightTitleText = showRightTitleText;
    }

    public Boolean getShowLeftTitleLayout() {
        return showLeftTitleLayout;
    }

    public void setShowLeftTitleLayout(Boolean showLeftTitleLayout) {
        this.showLeftTitleLayout = showLeftTitleLayout;
    }

    public Boolean getShowRightTitleLayout() {
        return showRightTitleLayout;
    }

    public void setShowRightTitleLayout(Boolean showRightTitleLayout) {
        this.showRightTitleLayout = showRightTitleLayout;
    }

    public String getRelativeId() {
        return relativeId;
    }

    public void setRelativeId(String relativeId) {
        this.relativeId = relativeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getDefValue() {
        return defValue;
    }

    public void setDefValue(String defValue) {
        this.defValue = defValue;
    }

    public String getTargetActId() {
        return targetActId;
    }

    public void setTargetActId(String targetActId) {
        this.targetActId = targetActId;
    }
}
