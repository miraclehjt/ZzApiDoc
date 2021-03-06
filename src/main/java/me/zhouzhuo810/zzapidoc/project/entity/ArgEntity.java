package me.zhouzhuo810.zzapidoc.project.entity;

import java.util.List;

/**
 * Created by admin on 2017/5/19.
 */
public class ArgEntity {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * require : true
         * children : []
         * type : string
         * name : method
         * description : 方法
         * defaultValue : Submit
         */

        private String require;
        private String type;
        private String name;
        private String description;
        private String defaultValue;
        private List<DataBean> children;

        public String getRequire() {
            return require;
        }

        public void setRequire(String require) {
            this.require = require;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public List<DataBean> getChildren() {
            return children;
        }

        public void setChildren(List<DataBean> children) {
            this.children = children;
        }
    }
}
