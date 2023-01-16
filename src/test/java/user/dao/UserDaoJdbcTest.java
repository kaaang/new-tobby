package user.dao;

import com.study.newtobby.user.config.AppConfig;
import com.study.newtobby.user.dao.UserDao;
import com.study.newtobby.user.domain.Level;
import com.study.newtobby.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class UserDaoJdbcTest {
	private User user1;
	private User user2;
	private User user3;

	@Autowired
	private UserDao userDao;

	@BeforeEach
	public void setUp() {
		this.user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0, "gyumee@test.com");
		this.user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10, "leegw700@test.com");
		this.user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40, "bumjin@test.com");
	}

	@AfterEach
	public void testDeleteAll() {
		userDao.deleteAll();
	}

	@Test
	public void testGetAll() {
		userDao.deleteAll();

		userDao.add(user1);
		List<User> userList1 = userDao.getAll();
		assertThat(userList1.size()).isEqualTo(1);
		checkSameUser(user1, userList1.get(0));

		userDao.add(user2);
		List<User> userList2 = userDao.getAll();
		assertThat(userList2.size()).isEqualTo(2);
		checkSameUser(user1, userList2.get(0));
		checkSameUser(user2, userList2.get(1));

		userDao.add(user3);
		List<User> userList3 = userDao.getAll();
		assertThat(userList3.size()).isEqualTo(3);
		checkSameUser(user3, userList3.get(0));
		checkSameUser(user1, userList3.get(1));
		checkSameUser(user2, userList3.get(2));
	}

	@Test
	public void update() throws Exception {
		userDao.deleteAll();

		userDao.add(user1); // 수정할 사용자
		userDao.add(user2); // 수정하지 않을 사용자

		user1.setName("오민규");
		user1.setPassword("springno6");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		userDao.update(user1);

		checkSameUser(user1, userDao.get(user1.getId()));
		checkSameUser(user2, userDao.get(user2.getId()));
	}

	private void checkSameUser(User givenUser, User actualUser) {
		assertThat(givenUser.getId()).isEqualTo(actualUser.getId());
		assertThat(givenUser.getName()).isEqualTo(actualUser.getName());
		assertThat(givenUser.getPassword()).isEqualTo(actualUser.getPassword());
		assertThat(givenUser.getLevel()).isEqualTo(actualUser.getLevel());
		assertThat(givenUser.getLogin()).isEqualTo(actualUser.getLogin());
		assertThat(givenUser.getRecommend()).isEqualTo(actualUser.getRecommend());
	}
}