package next.controller.user;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import core.mvc.ModelAndView;
import next.controller.UserSessionUtils;
import next.dao.UserDao;
import next.model.User;

@Controller
@RequestMapping("/users")
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	private UserDao userDao = UserDao.getInstance();

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String showUserList(HttpSession session, Model model) throws SQLException {
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		model.addAttribute("users", userDao.findAll());
		return "/user/list";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpSession session, @RequestParam String userId, @RequestParam String password) {
		User user = userDao.findByUserId(userId);

		if (user == null) {
			throw new NullPointerException("사용자를 찾을 수 없습니다.");
		}

		if (user.matchPassword(password)) {
			session.setAttribute("user", user);
			return "redirect:/";
		} else {
			throw new IllegalStateException("비밀번호가 틀립니다.");
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpSession session) {
		session.removeAttribute("user");
		return "redirect:/";
	}

	@RequestMapping(value = "/loginForm", method = RequestMethod.GET)
	public String showLoginForm() {
		return "/user/login";
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String showJoinForm(Model model) {
		model.addAttribute("user", new User());
		return "/user/form";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String createUser(@RequestParam String userId, @RequestParam String password, @RequestParam String name,
			@RequestParam String email) {
		User user = new User(userId, password, name, email);
		log.debug("User : {}", user);
		userDao.insert(user);
		return "redirect:/";
	}

	@RequestMapping(value = "/updateForm", method = RequestMethod.GET)
	public String showUpdateForm(HttpSession session, @RequestParam String userId, Model model) {
		User user = userDao.findByUserId(userId);
		if (!UserSessionUtils.isSameUser(session, user)) {
			throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
		}
		model.addAttribute("user", user);
		return "/user/updateForm";
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public String updateUser(HttpSession session, @RequestParam String userId, @RequestParam String password,
			@RequestParam String name, @RequestParam String email) {
		User user = userDao.findByUserId(userId);

		if (!UserSessionUtils.isSameUser(session, user)) {
			throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
		}

		User updateUser = new User(userId, password, name, email);
		log.debug("Update User : {}", updateUser);
		user.update(updateUser);
		return "redirect:/";
	}
	
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public String showProfile(@RequestParam String userId, Model model) {
	        model.addAttribute("user", userDao.findByUserId(userId));
	        return "/user/profile";
	}
	

}
