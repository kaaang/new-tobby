package user.service;

import com.study.newtobby.user.config.AppConfig;
import com.study.newtobby.user.dao.UserDao;
import com.study.newtobby.user.domain.Level;
import com.study.newtobby.user.domain.User;
import com.study.newtobby.user.service.UserService;
import com.study.newtobby.user.service.UserServiceImpl;
import com.study.newtobby.user.service.UserServiceTx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import  static com.study.newtobby.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import  static com.study.newtobby.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;



@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
public class UserServiceTest {
	private List<User> users;

	@Autowired
	private UserService userService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private MailSender mailSender;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@BeforeEach
	public void setUp(){
		users = Arrays.asList(
				new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "bumjin@test.com"),
				new User("joytouch", "강명성", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER+1, 0, "joytouch@test.com"),
				new User("erwins", "신승한", "p1", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1, "erwins@test.com"),
				new User("madnite1", "이상호", "p1", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "madnite1@test.com"),
				new User("green", "오민규", "p1", Level.GOLD, 100, Integer.MAX_VALUE, "green@test.com")
		);
	}

	@Test
	void upgradeLevels() {
		UserServiceImpl userServiceImpl = new UserServiceImpl();

		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);

		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);

		userServiceImpl.upgradeLevels();

		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size()).isEqualTo(2);
		checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

		List<String> requests = mockMailSender.getRequests();
		assertThat(requests.size()).isEqualTo(2);
		assertThat(requests.get(0)).isEqualTo(users.get(1).getEmail());
		assertThat(requests.get(1)).isEqualTo(users.get(3).getEmail());
	}

	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel){
		assertThat(updated.getId()).isEqualTo(expectedId);
		assertThat(updated.getLevel()).isEqualTo(expectedLevel);
	}

	@Test
	public void upgradeAllOrNothing() {
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setTransactionManager(transactionManager);
		testUserService.setMailSender(mailSender);

		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);

		userDao.deleteAll();

		for (User user : users) {
			userDao.add(user);
		}

		try {
			txUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
		}

		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), false);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), false);
		checkLevelUpgraded(users.get(4), false);
	}

	private void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		System.out.println(userUpdate.getName());

		if (upgraded) {
			assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
		} else {
			assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
		}
	}

	static class MockUserDao implements UserDao {
		private List<User> users;
		private List<User> updated = new ArrayList<User>();

		public MockUserDao(List<User> users) {
			this.users = users;
		}

		public List<User> getUpdated() {
			return this.updated;
		}

		public List<User> getAll() {
			return this.users;
		}

		public void update(User user) {
			updated.add(user);
		}

		public void add(User user) {
			throw new UnsupportedOperationException();
		}

		public User get(String id) {
			throw new UnsupportedOperationException();
		}

		public int getCount() {
			throw new UnsupportedOperationException();
		}

		public void deleteAll() {
			throw new UnsupportedOperationException();
		}

	}

	static class TestUserService extends UserServiceImpl {
		private String id;

		private TestUserService(String id) {
			this.id = id;
		}

		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id)) {
				throw new TestUserServiceException();
			}
			super.upgradeLevel(user);
		}

	}

	static class TestUserServiceException extends RuntimeException {
	}
}