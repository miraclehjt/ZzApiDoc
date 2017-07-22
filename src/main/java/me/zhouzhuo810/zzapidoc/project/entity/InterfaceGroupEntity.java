package me.zhouzhuo810.zzapidoc.project.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 接口分组
 * Created by admin on 2017/7/22.
 */
@Entity
@Table(name = "interface_group")
public class InterfaceGroupEntity extends BaseEntity {
    @Column(name = "Name", length = 50)
    private String name;
    @Column(name = "ProjectId", length = 50)
    private String projectId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
