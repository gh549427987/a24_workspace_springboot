package com.jhua;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xiejiehua
 * @DATE 8/16/2021
 */


@MapperScan({"com.jhua.dao"})
@SpringBootApplication(scanBasePackages = "com.jhua")
public class A24ZammerApplication {

    public static void main(String[] args) {
        SpringApplication.run(A24ZammerApplication.class, args);
    }
}
