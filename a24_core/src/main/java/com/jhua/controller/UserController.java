package com.jhua.controller;

import com.jhua.model.User;
import com.jhua.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiejiehua
 * @DATE 9/7/2021
 */

@Slf4j
@RestController // 这里不要轻易改成 @RestControler 否则页面会展示 rediret:之类的纯文本
@Api(tags = "用户接口")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /*
     * @Author xiejiehua
     * @Description //TODO 用户验证
     * @Date 9:09 PM 9/11/2021
     * @Param [user]
     * @return java.lang.String
     **/
    @RequestMapping("register")
    public String register(User user) {
        try {
            userService.register(user);
            return "redirect:/login.jsp";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/register.jsp";
        }
    }

    /*
     * @Author xiejiehua
     * @Description //TODO
     * @Date 7:13 PM 9/11/2021
     * @Param [username, password]
     * @return java.lang.String
     **/
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        log.info("用户名：" + user.getUsername() + " 密码：" + user.getPassword());

//         获取主体对象 会自动注入安全管理器
        Subject subject = SecurityUtils.getSubject();

        try {
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
            subject.login(usernamePasswordToken);
//            return "redirect:/index.jsp";
            log.info("登录成功!！");
            return usernamePasswordToken.toString();
        } catch (UnknownAccountException e) {
            System.out.println("登录用户名（" + user.getUsername() + "）错误! " + user.toString());
            return "login failure";
        } catch (IncorrectCredentialsException e) {
            System.out.println("登录密码（" + user.getPassword() + "）错误! " + user.toString());
            return "login failure";
        }
//        return "redirect:/login.jsp";
    }

    /*
     * @Author xiejiehua
     * @Description //TODO 退出
     * @Date 8:03 PM 9/11/2021
     * @Param []
     * @return java.lang.String
     **/
    @RequestMapping("logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/login.jsp";
    }

}
