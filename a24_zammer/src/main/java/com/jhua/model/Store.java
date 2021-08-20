package com.jhua.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Store {

    public String userId;
    public String accountId;
    public String login_channel = "wechat_mp_vr";
    public String storeAddress;
    public String storeName;

}