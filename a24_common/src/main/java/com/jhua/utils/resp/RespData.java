package com.jhua.utils.resp;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * @author SiFan Wei - porridge
 * Date: 2018-12-07 14:24
 * Description: 数据
 */

public class RespData<T> implements Serializable {
    private T data;
    private String msg;
    private String code;
    private long took;

    public RespData(T data, String msg, String code, long took) {
        this.data = data;
        this.msg = msg;
        this.code = code;
        this.took = took;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTook() {
        return took;
    }

    public void setTook(long took) {
        this.took = took;
    }

    @Override
    public String toString() {
        return "RespData{" +
                "data=" + data +
                ", msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                ", took=" + took +
                '}';
    }
}