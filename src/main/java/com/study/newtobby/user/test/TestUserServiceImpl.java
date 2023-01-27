package com.study.newtobby.user.test;

import com.study.newtobby.user.domain.User;
import com.study.newtobby.user.service.UserServiceImpl;

public class TestUserServiceImpl extends UserServiceImpl {
    private String id = "madnite1";

    protected void upgradeLevel(User user) {
        if (user.getId().equals(this.id)) {
            throw new TestUserServiceException();
        }
        super.upgradeLevel(user);
    }
}
