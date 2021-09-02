package com.jhua.util.resp;

import com.jhua.enumeration.ResultEnum;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * @author SiFan Wei - porridge
 * Date: 2018-12-07 14:54
 * Description: 返回数据包实体类
 */

public class HttpResponseEntity<T> extends ResponseEntity<RespData<T>> implements Timing {
    /**
     * 数据，包括code success data msg took
     */
    private RespData<T> respData;

    public HttpResponseEntity() {
        super(HttpStatus.OK);
    }

    public HttpResponseEntity(T data) {
        super(HttpStatus.OK);
        this.buildData(data, ResultEnum.SUCCESS.getMsg(), ResultEnum.SUCCESS.getCode(),0);
        this.setBody();
    }

    public HttpResponseEntity(String code, String msg) {
        super(HttpStatus.OK);
        this.buildData(null, msg, code, 0);
        this.setBody();
    }


    public HttpResponseEntity(T data, String msg, String code, long took, HttpStatus status) {
        super(status);
        this.buildData(data, msg, code, took);
        this.setBody();
    }

    public static <T> HttpResponseEntity<T> build() {
        return new HttpResponseEntity<>();
    }

    public static <T> HttpResponseEntity<T> build(T data) {
        return new HttpResponseEntity<>(data);
    }

    public static <T> HttpResponseEntity<T> build(String code, String msg) {
        return new HttpResponseEntity<>(code, msg);
    }

    public static <T> HttpResponseEntity<T> build(T data, String code, String msg, long took, HttpStatus status) {
        return new HttpResponseEntity<>(data, code, msg, took, status);

    }

    public HttpResponseEntity setData(T data) {
        if (this.respData != null) {
            this.respData.setData(data);
        }
        return this;
    }

    public HttpResponseEntity setMsg(String msg) {
        if (this.respData != null) {
            this.respData.setMsg(msg);
        }
        return this;
    }

    public HttpResponseEntity setCode(String code) {
        if (this.respData != null) {
            this.respData.setCode(code);
        }
        return this;
    }

    @Override
    public HttpResponseEntity setTook(long took) {
        if (this.respData != null) {
            this.respData.setTook(took);
        }
        return this;
    }

    public HttpResponseEntity setHttpStatus(HttpStatus status) {
        this.modifyField(ResponseEntity.class, "status", status);
        return this;
    }

    private void buildData(T data, String message, String code, long took) {
        respData = new RespData<>(data, message, code, took);
    }


    private void setBody() {
        this.modifyField(HttpEntity.class, "body", respData);
    }


    /**
     * @see HttpEntity body字段final
     * @see ResponseEntity status字段final
     * @param fieldName 要修改的字段
     * @param newVal 新值
     * @return 是否修改成功
     */
    private boolean modifyField(Class<?> cls, String fieldName, Object newVal) {
        Field f;
        try {
            f = cls.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
            return false;
        }
        if (f == null) {
            return false;
        }
        boolean accessible = f.isAccessible();
        f.setAccessible(true);
        try {
            f.set(this, newVal);
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
            return false;
        } finally {
            f.setAccessible(accessible);
        }
        return true;
    }
}