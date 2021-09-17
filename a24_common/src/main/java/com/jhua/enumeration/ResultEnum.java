package com.jhua.enumeration;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author xiejiehua
 * @DATE 9/2/2021
 */

@ApiModel("返回码")
public enum ResultEnum {

    @ApiModelProperty(value = "000000:success")
    SUCCESS("000000", "success"),
    PENDING("000001", "操作成功，请稍后查询处理结果");

    private String code;
    private String msg;

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ResultEnum setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
