package me.zhouzhuo810.zzapidoc.project.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;

/**
 * 接口
 * Created by admin on 2017/7/22.
 */
@Entity
@Table(name = "interfaces")
public class InterfaceEntity extends BaseEntity {

    @Column(name = "Name", length = 50)
    private String name;
    @Column(name = "HttpMethodId", length = 50)
    private String httpMethodId;
    @Formula("(SELECT d.Name FROM dictionary d WHERE d.ID = httpMethodId)")
    private String httpMethodName;
    @Column(name = "Note", length = 255)
    private String note;
    @Column(name = "ProjectId", length = 50)
    private String projectId;
    @Formula("(SELECT p.Name FROM project p WHERE p.ID = ProjectId)")
    private String projectName;
    @Column(name = "GroupId", length = 50)
    private String groupId;
    @Formula("(SELECT i.Name FROM interface_group i WHERE i.ID = GroupId)")
    private String groupName;
    @Column(name = "Path", length = 255)
    private String path;
    @Column(name = "Example", columnDefinition = "TEXT")
    private String example;
    @Formula("(SELECT count(*) FROM request_header r WHERE r.InterfaceId = ID AND r.DelFlag = 0)")
    private int requestHeadersNo;
    @Formula("(SELECT count(*) FROM request_args r WHERE r.InterfaceId = ID AND r.DelFlag = 0)")
    private int requestParamsNo;
    @Formula("(SELECT count(*) FROM response_args r WHERE r.InterfaceId = ID AND r.DelFlag = 0)")
    private int responseParamsNo;
    @Column(name = "IsTest")
    private Boolean isTest;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TestTime")
    private Date testTime;
    @Column(name = "TestUserId")
    private String testUserId;
    @Formula("(SELECT u.Name FROM user u WHERE u.ID = TestUserId)")
    private String testUserName;

    public String getTestUserId() {
        return testUserId;
    }

    public void setTestUserId(String testUserId) {
        this.testUserId = testUserId;
    }

    public String getTestUserName() {
        return testUserName;
    }

    public void setTestUserName(String testUserName) {
        this.testUserName = testUserName;
    }

    public Date getTestTime() {
        return testTime;
    }

    public void setTestTime(Date testTime) {
        this.testTime = testTime;
    }

    public Boolean getTest() {
        return isTest;
    }

    public void setTest(Boolean test) {
        isTest = test;
    }

    public int getRequestHeadersNo() {
        return requestHeadersNo;
    }

    public void setRequestHeadersNo(int requestHeadersNo) {
        this.requestHeadersNo = requestHeadersNo;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public int getRequestParamsNo() {
        return requestParamsNo;
    }

    public void setRequestParamsNo(int requestParamsNo) {
        this.requestParamsNo = requestParamsNo;
    }

    public int getResponseParamsNo() {
        return responseParamsNo;
    }

    public void setResponseParamsNo(int responseParamsNo) {
        this.responseParamsNo = responseParamsNo;
    }

    public String getHttpMethodName() {
        return httpMethodName;
    }

    public void setHttpMethodName(String httpMethodName) {
        this.httpMethodName = httpMethodName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHttpMethodId() {
        return httpMethodId;
    }

    public void setHttpMethodId(String httpMethodId) {
        this.httpMethodId = httpMethodId;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
