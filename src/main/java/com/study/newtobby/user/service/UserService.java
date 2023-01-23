package com.study.newtobby.user.service;

import com.study.newtobby.user.domain.User;

public interface UserService {
    void add(User user);
    void upgradeLevels();
}
