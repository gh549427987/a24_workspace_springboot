package com.jhua.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author xiejiehua
 * @DATE 9/12/2021
 */

@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    //ApplicationContextAware 把创建好的工厂对象以参数的形式返回
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    // 根据bean的名字获取Bean对象
    public static Object getBean(String name) {
        return context.getBean(name);
    }
}
