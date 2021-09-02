package com.jhua.util;

import com.jhua.util.resp.Timing;
import com.sun.org.apache.xalan.internal.xsltc.dom.SimpleResultTreeImpl;
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
