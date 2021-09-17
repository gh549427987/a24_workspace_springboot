package com.jhua.dao;

import com.jhua.model.Role;
import com.jhua.model.User;

import java.util.List;

/**
 * @author xiejiehua
 * @DATE 9/11/2021
 */

public interface UserDao {

    void save(User user);

    User findByUserName(String username);

    User findRolesByUserName(String username);
}
