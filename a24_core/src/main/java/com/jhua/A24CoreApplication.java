package com.jhua;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xiejiehua
 * @DATE 8/16/2021
 */


@MapperScan(basePackages = {"com.jhua.dao", "com.jhua.dao"})
@SpringBootApplication(scanBasePackages = "com.jhua")
public class A24CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(A24CoreApplication.class, args);
    }
}
