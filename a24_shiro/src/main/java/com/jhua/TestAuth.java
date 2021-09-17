package com.jhua;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;

/**
 * @author xiejiehua
 * @DATE 9/7/2021
 */

public class TestAuth {

//    public static void main(String[] args) {
//
////        1.创建安全管理器对象
//        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
//
////        2.给安全管理器设置realm
//        defaultSecurityManager.setRealm(new IniRealm("classpath:shiro.ini"));
//
////        3.给管局安全工具类设置安全管理器
//        SecurityUtils.setSecurityManager(defaultSecurityManager);
//
////        4.获得主体
//        Subject subject = SecurityUtils.getSubject();
//
////        5.创建令牌
//        UsernamePasswordToken token = new UsernamePasswordToken("xiaochen", "123");
//
//        try {
//            System.out.println(subject.isAuthenticated());
//            subject.login(token);
//            System.out.println(subject.isAuthenticated());
//        } catch (UnknownAccountException e) {
//            System.out.println("该用户不存在");
//        } catch (IncorrectCredentialsException e) {
//            System.out.println("密码不正确");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
