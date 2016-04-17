package next.controller.qna;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import next.CannotDeleteException;
import next.controller.UserSessionUtils;
import next.dao.QuestionDao;
import next.model.Question;
import next.model.Result;
import next.service.QnaService;

@RestController
@RequestMapping("/api/qna")
public class ApiQuestionController {
	private QuestionDao questionDao = QuestionDao.getInstance();
	private QnaService qnaService = QnaService.getInstance();
	
	@RequestMapping("/list")
	public List<Question> getQuestionList(){
		return questionDao.findAll();
	}
	
	@RequestMapping("/deleteQuestion")
	public HashMap<String, Object> deleteQuestion(HttpSession session, @RequestParam long questionId){
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		if (!UserSessionUtils.isLogined(session)) {
			resultMap.put("result", Result.fail("Login is required"));
			return resultMap;
		}
		try {
			qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(session));
			resultMap.put("result", Result.ok());
			return resultMap;
		} catch (CannotDeleteException e) {
			resultMap.put("result", Result.fail(e.getMessage()));
			return resultMap;
		}
	}
	
}
