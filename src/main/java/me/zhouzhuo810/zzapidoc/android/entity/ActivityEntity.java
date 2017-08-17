package me.zhouzhuo810.zzapidoc.android.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2017/8/17.
 */
@Entity
@Table(name = "activity")
public class ActivityEntity extends BaseEntity {

    private String name;
    private int type;
    private String title;
    private String leftTitleText;
    private String righTitletText;
    private String leftTitleImg;
    private String rightTitleImg;
    private Boolean showLeftTitleImg;
    private Boolean showRightTitleImg;
    private Boolean showLeftTitleText;
    private Boolean showRightTitleText;
    private Boolean showLeftTitleLayout;
    private Boolean showRightTitleLayout;


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

    public String getLeftTitleText() {
        return leftTitleText;
    }

    public void setLeftTitleText(String leftTitleText) {
        this.leftTitleText = leftTitleText;
    }

    public String getRighTitletText() {
        return righTitletText;
    }

    public void setRighTitletText(String righTitletText) {
        this.righTitletText = righTitletText;
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
}
