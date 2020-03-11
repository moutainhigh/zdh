package com.zyc.zdh.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "zdh_ha_info")
public class ZdhHaInfo extends PageBase implements Serializable {


    @Id
    @Column(name = "ID")
    private String id;
    private String zdh_instance;
    private String zdh_url;
    private String zdh_host;
    private String zdh_port;
    private String zdh_status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZdh_instance() {
        return zdh_instance;
    }

    public void setZdh_instance(String zdh_instance) {
        this.zdh_instance = zdh_instance;
    }

    public String getZdh_url() {
        return zdh_url;
    }

    public void setZdh_url(String zdh_url) {
        this.zdh_url = zdh_url;
    }

    public String getZdh_host() {
        return zdh_host;
    }

    public void setZdh_host(String zdh_host) {
        this.zdh_host = zdh_host;
    }

    public String getZdh_port() {
        return zdh_port;
    }

    public void setZdh_port(String zdh_port) {
        this.zdh_port = zdh_port;
    }

    public String getZdh_status() {
        return zdh_status;
    }

    public void setZdh_status(String zdh_status) {
        this.zdh_status = zdh_status;
    }
}
