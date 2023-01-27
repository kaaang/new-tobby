package com.study.newtobby.user.config;

import com.study.newtobby.user.dao.UserDao;
import com.study.newtobby.user.dao.UserDaoJdbc;
import com.study.newtobby.user.service.*;
import com.study.newtobby.user.test.TestUserServiceImpl;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
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

//	@Bean
//	public TxProxyFactoryBean userService(){
//		TxProxyFactoryBean txProxyFactoryBean = new TxProxyFactoryBean();
//		txProxyFactoryBean.setServiceInterface(UserService.class);
//		txProxyFactoryBean.setTarget(userServiceImpl(userDao(), dataSource(), transactionManager(dataSource()), mailSender()));
//		txProxyFactoryBean.setTransactionManager(transactionManager(dataSource()));
//		txProxyFactoryBean.setPattern("upgradeLevels");
//
//		return txProxyFactoryBean;
//	}

//	@Bean
//	public UserServiceImpl userServiceImpl(UserDao userDao, DataSource dataSource, PlatformTransactionManager transactionManager, MailSender mailSender) {
//		UserServiceImpl userServiceImpl = new UserServiceImpl();
//
//		userServiceImpl.setUserDao(userDao);
//		userServiceImpl.setDataSource(dataSource);
//		userServiceImpl.setTransactionManager(transactionManager);
//		userServiceImpl.setMailSender(mailSender);
//		return userServiceImpl;
//	}

	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public DummyMailSender mailSender() {
		DummyMailSender mailSender = new DummyMailSender();
		return mailSender;
	}

	@Bean
	public TransactionAdvice transactionAdvice(){
		TransactionAdvice transactionAdvice = new TransactionAdvice();
		transactionAdvice.setTransactionManager(transactionManager(dataSource()));

		return transactionAdvice;
	}

	@Bean
	public NameMatchMethodPointcut transactionPointcut(){
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("upgrade*");

		return pointcut;
	}

	@Bean
	public DefaultPointcutAdvisor transactionAdvisor(){
		DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
		defaultPointcutAdvisor.setAdvice(transactionAdvice());
		defaultPointcutAdvisor.setPointcut(transactionPointcut());

		return defaultPointcutAdvisor;
	}

//	@Bean(name = "userService")
//	public ProxyFactoryBean userService(){
//		ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
//		proxyFactoryBean.setTarget(userServiceImpl(userDao(), dataSource(), transactionManager(dataSource()), mailSender()));
//		proxyFactoryBean.setInterceptorNames("transactionAdvisor");
//
//		return proxyFactoryBean;
//	}

	@Bean(name = "userService")
	public UserServiceImpl userService(){
		UserServiceImpl userService = new UserServiceImpl();
		userService.setUserDao(this.userDao());
		userService.setMailSender(mailSender());

		return userService;
	}

//	@Bean
//	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
//		return new DefaultAdvisorAutoProxyCreator();
//	}

	@Bean(name = "transactionPointcut")
	public NameMatchClassMethodPointCut nameMatchClassMethodPointCut(){
		NameMatchClassMethodPointCut nameMatchClassMethodPointCut = new NameMatchClassMethodPointCut();
		nameMatchClassMethodPointCut.setMappedClassName("*ServiceImpl");
		nameMatchClassMethodPointCut.setMappedName("upgrade*");
		return nameMatchClassMethodPointCut;
	}

	@Bean(name = "testUserService")
	public TestUserServiceImpl testUserService(){
		return new TestUserServiceImpl();
	}

}
