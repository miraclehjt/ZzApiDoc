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
public class WidgetEntity extends BaseEntity {
    public static final int TYPE_TITLE_BAR = 0;
    public static final int TYPE_SETTING_ITEM = 1;
    public static final int TYPE_EDIT_ITEM = 2;
    public static final int TYPE_INFO_ITEM = 3;
    public static final int TYPE_SUBMIT_BTN_ITEM = 4;
    public static final int TYPE_EXIT_BTN_ITEM = 5;
    public static final int TYPE_LETTER_RV = 6;
    public static final int TYPE_SCROLL_VIEW = 7;
    public static final int TYPE_LINEAR_LAYOUT = 8;
    public static final int TYPE_RELATIVE_LAYOUT = 9;
    public static final int TYPE_IMAGE_VIEW = 10;
    public static final int TYPE_TEXT_VIEW = 11;
    public static final int TYPE_CHECK_BOX = 12;

    @Column(name = "Type")
    private Integer type = TYPE_TITLE_BAR;
    @Column(name = "Name")
    private String name;
    @Column(name = "RelativeId")
    private String relativeId;
    @Column(name = "Title")
    private String title;
    @Column(name = "ResId")
    private String resId;
    @Column(name = "Hint")
    private String hint;
    @Column(name = "DefValue")
    private String defValue;
    @Column(name = "ApplicationId")
    private String applicationId;
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
    /*点击跳转的Activity*/
    @Column(name = "targetActivityId")
    private String targetActivityId;
    /*接口id*/
    @Column(name = "targetApiId")
    private String targetApiId;
    @Column(name = "pid")
    private String pid;
    @Column(name = "Background")
    private String background;
    @Column(name = "Width")
    private Integer width;
    @Column(name = "Height")
    private Integer height;
    @Column(name = "MarginLeft")
    private Integer marginLeft;
    @Column(name = "MarginRight")
    private Integer marginRight;
    @Column(name = "MarginTop")
    private Integer marginTop;
    @Column(name = "MarginBottom")
    private Integer marginBottom;
    @Column(name = "PaddingLeft")
    private Integer paddingLeft;
    @Column(name = "PaddingRight")
    private Integer paddingRight;
    @Column(name = "PaddingTop")
    private Integer paddingTop;
    @Column(name = "PaddingBottom")
    private Integer paddingBottom;
    @Column(name = "Weight")
    private Double weight;
    @Column(name = "Gravity")
    private String gravity;
    @Column(name = "Orientation")
    private String orientation;

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getGravity() {
        return gravity;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(Integer paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public Integer getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(Integer paddingRight) {
        this.paddingRight = paddingRight;
    }

    public Integer getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(Integer paddingTop) {
        this.paddingTop = paddingTop;
    }

    public Integer getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(Integer paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(Integer marginLeft) {
        this.marginLeft = marginLeft;
    }

    public Integer getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(Integer marginRight) {
        this.marginRight = marginRight;
    }

    public Integer getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(Integer marginTop) {
        this.marginTop = marginTop;
    }

    public Integer getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(Integer marginBottom) {
        this.marginBottom = marginBottom;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTargetApiId() {
        return targetApiId;
    }

    public void setTargetApiId(String targetApiId) {
        this.targetApiId = targetApiId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getTargetActivityId() {
        return targetActivityId;
    }

    public void setTargetActivityId(String targetActivityId) {
        this.targetActivityId = targetActivityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
