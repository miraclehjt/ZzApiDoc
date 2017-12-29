package me.zhouzhuo810.zzapidoc.project.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 错误码
 * Created by zz on 2017/12/29.
 */
@Entity
@Table(name = "error_code")
public class ErrorCodeEntity extends BaseEntity {
    @Column(name = "Code", length = 11)
    private Integer code;
    @Column(name = "Note", length = 100)
    private String note;
    @Column(name = "ProjectId", length = 50)
    private String projectId;
    @Column(name = "InterfaceId", length = 50)
    private String interfaceId;
    @Column(name = "GroupId", length = 50)
    private String groupId;
    @Column(name = "IsGlobal")
    private Boolean isGlobal = false;
    @Column(name = "IsGroup")
    private Boolean isGroup = false;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Boolean getGlobal() {
        return isGlobal;
    }

    public void setGlobal(Boolean global) {
        isGlobal = global;
    }

    public Boolean getGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }
}

