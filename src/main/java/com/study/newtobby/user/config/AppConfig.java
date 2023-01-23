package com.study.newtobby.user.config;

import com.study.newtobby.user.dao.UserDao;
import com.study.newtobby.user.dao.UserDaoJdbc;
import com.study.newtobby.user.service.DummyMailSender;
import com.study.newtobby.user.service.UserService;
import com.study.newtobby.user.service.UserServiceImpl;
import com.study.newtobby.user.service.UserServiceTx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
@Component
public class AppConfig {
	@Bean
	public DataSource dataSource(){
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost:3306/tobby");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}

	@Bean
	public UserDao userDao(){
		UserDaoJdbc userDao = new UserDaoJdbc();

		userDao.setDataSource(dataSource());
		return userDao;
	}

	@Bean
	public UserService userService(){
		UserServiceTx userServiceTx = new UserServiceTx();
		userServiceTx.setUserService(userServiceImpl(userDao(), dataSource(), transactionManager(dataSource()), mailSender()));
		userServiceTx.setTransactionManager(transactionManager(dataSource()));

		return userServiceTx;
	}

	@Bean
	public UserServiceImpl userServiceImpl(UserDao userDao, DataSource dataSource, PlatformTransactionManager transactionManager, MailSender mailSender) {
		UserServiceImpl userServiceImpl = new UserServiceImpl();

		userServiceImpl.setUserDao(userDao);
		userServiceImpl.setDataSource(dataSource);
		userServiceImpl.setTransactionManager(transactionManager);
		userServiceImpl.setMailSender(mailSender);
		return userServiceImpl;
	}

	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public DummyMailSender mailSender() {
		DummyMailSender mailSender = new DummyMailSender();
		return mailSender;
	}
}
