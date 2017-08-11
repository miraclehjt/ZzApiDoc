package me.zhouzhuo810.zzapidoc.project.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 项目
 * Created by admin on 2017/7/22.
 */
@Entity
@Table(name = "project")
public class ProjectEntity extends BaseEntity {

    public static final String PROPERTY_PRIVATE = "1";
    public static final String PROPERTY_PUBLIC = "0";

    @Column(name = "Property", length = 50)
    private String property = "0";

    @Column(name = "Name", length = 50)
    private String name;

    @Column(name = "Note",columnDefinition = "TEXT")
    private String note;

    @Formula("(SELECT count(*) FROM interfaces i WHERE i.ProjectId = ID AND i.DelFlag = 0)")
    private int interfaceNo;

    public int getInterfaceNo() {
        return interfaceNo;
    }

    public void setInterfaceNo(int interfaceNo) {
        this.interfaceNo = interfaceNo;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
