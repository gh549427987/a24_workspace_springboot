package com.jhua.service;

import com.jhua.dao.UserDao;
import com.jhua.model.User;
import com.jhua.utils.security.SaltUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xiejiehua
 * @DATE 9/11/2021
 */

@Service("userService")
@Transactional //当把@Transactional 注解放在类上时，表示所有该类的public方法都配置相同的事务属性信息。
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public void register(User user) {
        // 处理业务调用Dao
        // 明文密码进行md5+salt+hash散列
        String salt = SaltUtils.getSalt(8);
        Md5Hash md5Hash = new Md5Hash(user.getPassword(), salt, 1024);

        user.setPassword(md5Hash.toHex());
        user.setSalt(salt);

        userDao.save(user);
    }

    @Override
    public User findByUserName(String username) {
        return userDao.findByUserName(username);
    }

    @Override
    public User findRolesByUserName(String username) {
        return userDao.findRolesByUserName(username);
    }
}
