package next.controller.qna;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import core.jdbc.DataAccessException;
import core.mvc.ModelAndView;
import next.controller.UserSessionUtils;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.model.Answer;
import next.model.Result;
import next.model.User;

@RestController
@RequestMapping("/api/qna/answer/")
public class ApiAnswerController {
	private static final Logger log = LoggerFactory.getLogger(AddAnswerController.class);
	private QuestionDao questionDao = QuestionDao.getInstance();
	private AnswerDao answerDao = AnswerDao.getInstance();

	@RequestMapping(value = "add", method = RequestMethod.POST)
	public HashMap<String, Object> addAnswer(HttpSession session, @RequestParam String contents,
			@RequestParam long questionId) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		if (!UserSessionUtils.isLogined(session)) {
			resultMap.put("result", Result.fail("Login is required"));
			return resultMap;
		}

		User user = UserSessionUtils.getUserFromSession(session);
		Answer answer = new Answer(user.getUserId(), contents, questionId);
		log.debug("answer : {}", answer);

		Answer savedAnswer = answerDao.insert(answer);
		questionDao.updateCountOfAnswer(savedAnswer.getQuestionId());

		resultMap.put("answer", savedAnswer);
		resultMap.put("result", Result.ok());
		return resultMap;
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public Result deleteAnswer(@RequestParam long answerId) {
		try {
			answerDao.delete(answerId);
			return Result.ok();
		} catch (DataAccessException e) {
			return Result.fail(e.getMessage());
		}
	}

}
