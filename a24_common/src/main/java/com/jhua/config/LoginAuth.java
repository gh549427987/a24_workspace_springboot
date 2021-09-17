package com.jhua.config;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;

/**
 * @author xiejiehua
 * @DATE 8/17/2021
 */

public class LoginAuth {

//    def auth():
//    auth_url = "https://vrservice.netvios.163.com/admin/account/login"
//    auth_payload = "\r\n{\r\n  \"password\": \"2576e6702908f27c371fe31fc506f7cc5c7b3198462fb89169f717ac5c1b2cf3\",\r\n  \"username\": \"SuperNetvios\"\r\n}\r\n"
//    headers = {
//        'content-type': "application/json",
//    }
//    response = requests.post(auth_url,headers=headers, data=auth_payload)
//    auth = eval(response.text)['data']
//            return auth
    static String backendURL = "https://vrservice.netvios.163.com/admin/account/login";

    static String getLoginAuth() {

        String auth = null;
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"password\": \"2576e6702908f27c371fe31fc506f7cc5c7b3198462fb89169f717ac5c1b2cf3\",\"username\": \"SuperNetvios\"}");
            System.out.println("进入后台获取auth:" + backendURL);
            Request request = new Request.Builder()
                    .url(backendURL + "/admin/account/login")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cookie", "Authorization=ADMIN-64398733-77a3-462e-9e7c-29f38d5d7ea1")
                    .build();
            Response response = client.newCall(request).execute();

            ResponseBody res = response.body();

            auth = (String) JSONObject.parseObject(res.string()).get("data");

//            FakeConfig.auth = auth;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return auth;
    }

}
