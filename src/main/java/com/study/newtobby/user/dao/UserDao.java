package com.study.newtobby.user.dao;

import com.study.newtobby.user.domain.User;


import java.util.List;

public interface UserDao {
	public void add(User user);

	public User get(String id);

	public void deleteAll();

	public int getCount();

	public List<User> getAll();

	void update(User user);
}
