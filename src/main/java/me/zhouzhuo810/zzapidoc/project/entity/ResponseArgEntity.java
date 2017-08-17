package me.zhouzhuo810.zzapidoc.project.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 返回參數
 * Created by admin on 2017/7/22.
 */
@Entity
@Table(name = "response_args")
public class ResponseArgEntity extends BaseEntity {

    public static final int TYPE_STRING = 0;
    public static final int TYPE_NUMBER = 1;
    public static final int TYPE_OBJECT = 2;
    public static final int TYPE_ARRAY_OBJECT = 3;
    public static final int TYPE_ARRAY_STRING = 4;
    public static final int TYPE_ARRAY = 5;
    public static final int TYPE_FILE = 6;
    public static final int TYPE_UNKNOWN = 7;
    public static final int TYPE_ARRAY_NUMBER = 8;

    @Column(name = "TypeId", length = 11)
    private Integer typeId = 0;
    @Column(name = "Name", length = 50)
    private String name;
    @Column(name = "InterfaceId", length = 50)
    private String interfaceId;
    @Column(name = "ProjectId", length = 50)
    private String projectId;
    @Column(name = "Note", length = 255)
    private String note;
    @Column(name = "Pid", length = 50)
    private String pid;
    @Column(name = "IsGlobal")
    private Boolean isGlobal = false;
    @Column(name = "DefaultValue", length = 255)
    private String defaultValue;

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getGlobal() {
        return isGlobal;
    }

    public void setGlobal(Boolean global) {
        isGlobal = global;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static int getTypeString() {
        return TYPE_STRING;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
