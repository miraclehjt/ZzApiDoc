package me.zhouzhuo810.zzapidoc.user.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2017/7/20.
 */
@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {

    @Column(name = "Name", length = 50)
    private String name;
    @Column(name = "Phone", length = 50)
    private String phone;
    @Column(name = "Password", length = 50)
    private String password;
    @Column(name = "Sex", length = 10)
    private String sex;
    @Column(name = "Email", length = 50)
    private String email;
    @Column(name = "Image")
    private String pic;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
