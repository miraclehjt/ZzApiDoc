package me.zhouzhuo810.zzapidoc.version.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2017/10/26.
 */
@Entity
@Table(name = "version_record")
public class VersionRecordEntity extends BaseEntity {

    @Column(name = "VersionId")
    private String versionId;
    @Column(name = "ProjectId")
    private String projectId;
    @Column(name = "Note")
    private String note;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
