package com.jhua.utils;

import com.jhua.utils.resp.Timing;
import lombok.Data;

/**
 * @author xiejiehua
 * @DATE 9/2/2021
 */

@Data
public class Result<T> implements Timing {

    private String code;

    private String msg;

    private T data;

    private Long costMillis;

    private long took;

    @Override
    public Timing setTook(long took) {
        this.took = took;
        return this;
    }
}
