package me.zhouzhuo810.zzapidoc.project.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by admin on 2017/8/13.
 */
@Entity
@Table(name = "request_header")
public class RequestHeaderEntity extends BaseEntity{

    @Column(name = "InterfaceId")
    private String interfaceId;
    @Column(name = "projectId")
    private String projectId;
    @Column(name = "Name")
    private String name;
    @Column(name = "Value")
    private String value;
    @Column(name = "Note")
    private String note;
    @Column(name = "IsGlobal")
    private Boolean isGlobal;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getGlobal() {
        return isGlobal;
    }

    public void setGlobal(Boolean global) {
        isGlobal = global;
    }
}
