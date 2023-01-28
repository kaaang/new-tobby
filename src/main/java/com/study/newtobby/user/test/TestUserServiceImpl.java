package com.study.newtobby.user.test;

import com.study.newtobby.user.domain.User;
import com.study.newtobby.user.service.UserServiceImpl;

import java.util.List;

public class TestUserServiceImpl extends UserServiceImpl {
    private String id = "madnite1";

    protected void upgradeLevel(User user) {
        if (user.getId().equals(this.id)) {
            throw new TestUserServiceException();
        }
        super.upgradeLevel(user);
    }

    public List<User> getAll(){
        for (User user : super.getAll()) {
            super.update(user);
        }
        return null;
    }
}
