package me.zhouzhuo810.zzapidoc.version.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2017/10/26.
 */
@Entity
@Table(name = "version_project")
public class VersionProjectEntity extends BaseEntity {

    @Column(name = "Name")
    private String name;
    @Column(name = "Note")
    private String note;

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
