package com.taotao.cart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.sso.query.bean.User;
import com.taotao.sso.query.service.UserQueryService;

@Service
public class UserService {

    @Autowired
    private UserQueryService userQueryService;

    public User queryUserByToken(String token) {
        return this.userQueryService.queryUserByToken(token);
    }

}
