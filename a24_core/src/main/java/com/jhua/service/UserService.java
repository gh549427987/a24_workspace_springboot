package com.jhua.service;

import com.jhua.model.Role;
import com.jhua.model.User;

import java.util.List;

public interface UserService {

    void register(User user);

    User findByUserName(String username);

    User findRolesByUserName(String username);
}
