package com.jhua.shiro;

import com.jhua.model.Role;
import com.jhua.model.User;
import com.jhua.service.UserService;
import com.jhua.utils.ApplicationContextUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.CollectionUtils;

import java.util.List;

/**
 * @author xiejiehua
 * @DATE 9/11/2021
 * 自定义一个本项目的Realm
 */

public class ShiroRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        // 获取身份信息
        String primaryPrincipal = (String) principalCollection.getPrimaryPrincipal();

        // 根据主身份信息获取角色和权限信息
        // 在工厂中获取service对象
        UserService userService = (UserService) ApplicationContextUtils.getBean("userService");

        User user = userService.findRolesByUserName(primaryPrincipal);

        // 授权角色信息
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            user.getRoles().forEach(role -> {
                simpleAuthorizationInfo.addRole(role.getName());
            });
            return simpleAuthorizationInfo;
        }

        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        // 根据身份信息
        String principal = (String) authenticationToken.getPrincipal();

        // 在工厂中获取service对象
        UserService userService = (UserService) ApplicationContextUtils.getBean("userService");

        User user = userService.findByUserName(principal);

        if (!ObjectUtils.isEmpty(user)) {
            return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), ByteSource.Util.bytes(user.getSalt()), this.getName());
        }

        return null;
    }
}
