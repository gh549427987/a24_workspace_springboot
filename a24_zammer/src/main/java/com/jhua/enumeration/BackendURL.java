package com.jhua.enumeration;

/**
 * @author xiejiehua
 * @DATE 8/17/2021
 */

public enum BackendURL {

    PRODUCTION("https://vrservice.netvios.163.com", "https://rawdata.netease.com"),
    DEVELOPMENT("https://vrservicetest.netvios.163.com", "https://test.spinz.netease.com");

    private final String url;
    private final String xc_url;

    BackendURL(String url, String xc_url) {
        this.url = url;
        this.xc_url = xc_url;
    }

    public String getUrl() {
        return this.url;
    }

    public String getXc_url() {
        return this.xc_url;
    }
}
