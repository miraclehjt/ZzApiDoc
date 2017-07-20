package me.zhouzhuo810.zzapidoc.common.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 基础的数据表对象
 */
@MappedSuperclass
public class BaseEntity implements Serializable {
    public final static int DELETE_FLAG_YES = 1;
    public final static int DELETE_FLAG_NO = 0;

    /**
     * 主键标示
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "ID", length = 50)
    private String id;

    /**
     * 删除标志  1已经删除，0 正常
     */
    @Column(name = "DelFlag")
    private Integer deleteFlag = DELETE_FLAG_NO;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CreateTime")
    private Date createTime = new Date();

    @Column(name = "CreateUserId",length = 50)
    private String createUserID;

    @Column(name = "CreateUserName",length = 50)
    private String createUserName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ModifyDate")
    private Date modifyTime;

    @Column(name = "ModifyUserId",length = 50)
    private String modifyUserID;

    @Column(name = "ModifyUserName",length = 50)
    private String modifyUserName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserID() {
        return createUserID;
    }

    public void setCreateUserID(String createUserID) {
        this.createUserID = createUserID;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyUserID() {
        return modifyUserID;
    }

    public void setModifyUserID(String modifyUserID) {
        this.modifyUserID = modifyUserID;
    }

    public String getModifyUserName() {
        return modifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
    }
}
