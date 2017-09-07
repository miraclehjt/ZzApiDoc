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
@Table(name = "application")
public class ApplicationEntity extends BaseEntity{
    @Column(name = "chName")
    private String chName;
    @Column(name = "AppName")
    private String appName;
    @Column(name = "MinSDK")
    private Integer minSDK;
    @Column(name = "CompileSDK")
    private Integer compileSDK;
    @Column(name = "TargetSDK")
    private Integer targetSDK;
    @Column(name = "VersionCode")
    private Integer versionCode;
    @Column(name = "VersionName")
    private String versionName;
    @Column(name = "Logo")
    private String logo;
    @Column(name = "ColorMain")
    private String colorMain;
    @Column(name = "PackageName")
    private String packageName;
    @Column(name = "MultiDex")
    private Boolean multiDex;
    @Column(name = "MinifyEnabled")
    private Boolean minifyEnabled;
    @Column(name = "ApiId")
    private String apiId;
    @Formula("(SELECT count(*) FROM fragment f WHERE f.ApplicationId = ID AND f.DelFlag = 0)")
    private Integer fgmCount = 0;
    @Formula("(SELECT count(*) FROM activity a WHERE a.ApplicationId = ID AND a.DelFlag = 0)")
    private Integer actCount = 0;

    public Integer getFgmCount() {
        return fgmCount;
    }

    public void setFgmCount(Integer fgmCount) {
        this.fgmCount = fgmCount;
    }

    public Integer getActCount() {
        return actCount;
    }

    public void setActCount(Integer actCount) {
        this.actCount = actCount;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public Boolean getMinifyEnabled() {
        return minifyEnabled;
    }

    public void setMinifyEnabled(Boolean minifyEnabled) {
        this.minifyEnabled = minifyEnabled;
    }

    public Boolean getMultiDex() {
        return multiDex;
    }

    public void setMultiDex(Boolean multiDex) {
        this.multiDex = multiDex;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getColorMain() {
        return colorMain;
    }

    public void setColorMain(String colorMain) {
        this.colorMain = colorMain;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getMinSDK() {
        return minSDK;
    }

    public void setMinSDK(Integer minSDK) {
        this.minSDK = minSDK;
    }

    public Integer getCompileSDK() {
        return compileSDK;
    }

    public void setCompileSDK(Integer compileSDK) {
        this.compileSDK = compileSDK;
    }

    public Integer getTargetSDK() {
        return targetSDK;
    }

    public void setTargetSDK(Integer targetSDK) {
        this.targetSDK = targetSDK;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
