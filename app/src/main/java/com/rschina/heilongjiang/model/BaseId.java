package com.rschina.heilongjiang.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/9/19.
 */

public class BaseId implements Serializable{


    private List<BasemapinfosBean> basemapinfos;

    public List<BasemapinfosBean> getBasemapinfos() {
        return basemapinfos;
    }

    public void setBasemapinfos(List<BasemapinfosBean> basemapinfos) {
        this.basemapinfos = basemapinfos;
    }

    public static class BasemapinfosBean {
        /**
         * mapinfoid : 0002198ca7f5885949ecb84a7580f8030c3c
         * showname : 0.8m-20150929
         */

        private String mapinfoid;
        private String showname;

        public String getMapinfoid() {
            return mapinfoid;
        }

        public void setMapinfoid(String mapinfoid) {
            this.mapinfoid = mapinfoid;
        }

        public String getShowname() {
            return showname;
        }

        public void setShowname(String showname) {
            this.showname = showname;
        }
    }
}
