package me.zhouzhuo810.zzapidoc.cache.entity;

import me.zhouzhuo810.zzapidoc.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zz on 2017/8/11.
 */
@Entity
@Table(name = "cache")
public class CacheEntity extends BaseEntity{
    @Column(name = "cachePath", length = 255)
    private String cachePath;

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }
}
