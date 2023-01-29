package com.study.newtobby.user.service;

import com.study.newtobby.user.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserService {
    void add(User user);
    void deleteAll();
    void update(User user);
    void upgradeLevels();

    @Transactional(readOnly = true)
    User get(String id);

    @Transactional(readOnly = true)
    List<User> getAll();
}
