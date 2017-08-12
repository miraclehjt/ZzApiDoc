package me.zhouzhuo810.zzapidoc.user.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by admin on 2017/8/12.
 */
@Entity
@Table(name = "apkUpdate")
public class UpdateEntity extends BaseEntity{

    @Column(name = "VersionName")
    private String versionName;
    @Column(name = "VersionCode")
    private Integer versionCode;
    @Column(name = "UpdateInfo", columnDefinition = "TEXT")
    private String updateInfo;
    @Column(name = "Address")
    private String address;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ReleaseDate")
    private Date releaseDate;

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

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

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
