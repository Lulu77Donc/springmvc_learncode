package com.ljx.service.impl;

import com.ljx.domain.User;
import com.ljx.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public void save(User user) {
        System.out.println("user service...");
    }
}
