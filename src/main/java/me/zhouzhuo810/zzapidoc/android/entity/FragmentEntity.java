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
    @Column(name = "ApplicationId")
    private String applicationId;
    @Column(name = "ActivityId")
    private String activityId;
    @Column(name = "Position")
    private Integer position = 0;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

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

}
