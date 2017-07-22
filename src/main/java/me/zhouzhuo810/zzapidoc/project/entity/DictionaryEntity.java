package me.zhouzhuo810.zzapidoc.project.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by admin on 2017/7/22.
 */
@Entity
@Table(name = "dictionary")
public class DictionaryEntity extends BaseEntity {
    @Column(name = "Pid", length = 50)
    private String pid;
    @Column(name = "Type" , length = 50)
    private String type;
    @Column(name = "Name", length = 50)
    private String name;
    @Column(name = "Position", length = 11)
    private Integer position;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
