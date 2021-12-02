package com.phy.app.ble.bean;

public class OtaFileInfo {
    private String model;
    private String version;
    private String url;
    private String description;
    private String MD5;

    public OtaFileInfo() {

    }

    public OtaFileInfo(String model, String version, String url, String description, String MD5) {
        this.model = model;
        this.version = version;
        this.url = url;
        this.description = description;
        this.MD5 = MD5;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    @Override
    public String toString() {
        return "OtaFileInfo{" +
                "model='" + model + '\'' +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", MD5='" + MD5 + '\'' +
                '}';
    }
}
