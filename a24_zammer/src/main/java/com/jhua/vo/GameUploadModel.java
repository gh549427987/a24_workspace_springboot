package com.jhua.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author xiejiehua
 * @DATE 8/17/2021
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameUploadModel {

    private Object game_data;
    private String app;
    private String appkey;
    private String app_channel;
    private String unionid;
    private String extra_data;
    private String sign;

}
