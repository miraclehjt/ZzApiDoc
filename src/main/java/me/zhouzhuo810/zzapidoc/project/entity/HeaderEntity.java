package me.zhouzhuo810.zzapidoc.project.entity;

import java.util.List;

/**
 * Created by admin on 2017/8/14.
 */
public class HeaderEntity {

    private List<HeaderDataEntity> data;

    public List<HeaderDataEntity> getData() {
        return data;
    }

    public void setData(List<HeaderDataEntity> data) {
        this.data = data;
    }

    public static class HeaderDataEntity {
        /**
         * require : true
         * children : []
         * name : test
         * defaultValue : 123
         * description : 啊哈哈哈哈
         */

        private String require;
        private String name;
        private String defaultValue;
        private String description;
        private List<HeaderDataEntity> children;

        public String getRequire() {
            return require;
        }

        public void setRequire(String require) {
            this.require = require;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<HeaderDataEntity> getChildren() {
            return children;
        }

        public void setChildren(List<HeaderDataEntity> children) {
            this.children = children;
        }
    }
}
