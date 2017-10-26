package me.zhouzhuo810.zzapidoc.version.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2017/10/26.
 */
@Entity
@Table(name = "versions")
public class VersionEntity extends BaseEntity{
    @Column(name = "VersionName", length = 20)
    private String versionName;
    @Column(name = "VersionCode", length = 11)
    private Integer versionCode;
    @Column(name = "VersionDesc", columnDefinition = "TEXT")
    private String versionDesc;
    @Column(name = "ProjectId")
    private String projectId;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
