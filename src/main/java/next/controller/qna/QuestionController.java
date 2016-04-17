package next.controller.qna;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import next.CannotDeleteException;
import next.LoginUser;
import next.controller.UserSessionUtils;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.model.Answer;
import next.model.Question;
import next.model.User;
import next.service.QnaService;

@Controller
@RequestMapping("/qna")
public class QuestionController {
	private QuestionDao questionDao = QuestionDao.getInstance();
	private AnswerDao answerDao = AnswerDao.getInstance();
	private QnaService qnaService = QnaService.getInstance();
	
	@RequestMapping("/show")
	public String showQuestion(@RequestParam long questionId, Model model){
        Question question = questionDao.findById(questionId);
        List<Answer> answers = answerDao.findAllByQuestionId(questionId);
        model.addAttribute("question", question);
        model.addAttribute("answers", answers);
		return "/qna/show";
	}
	
	@RequestMapping("/form")
	public String showQuestionForm(HttpSession session, Model model){
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		model.addAttribute("question", new Question());
		return "/qna/form";
	}
	@RequestMapping(value="/create", method = RequestMethod.POST)
	public String createQuestion(HttpSession session, @RequestParam String title, @RequestParam String contents){
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
    	User user = UserSessionUtils.getUserFromSession(session);
    	Question question = new Question(user.getUserId(), title, contents);
    	questionDao.insert(question);
		return "redirect:/";
	}
	@RequestMapping("/updateForm")
	public String showQuestionUpdateForm(HttpSession session, @RequestParam long questionId, Model model){
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		
		Question question = questionDao.findById(questionId);
		if (!question.isSameUser(UserSessionUtils.getUserFromSession(session))) {
			throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
		}
		model.addAttribute("question", question);
		return "/qna/update";
	}
	
	@RequestMapping(value="/update", method=RequestMethod.PUT)
	public String updateQuestion(HttpSession session,@RequestParam long questionId, @RequestParam String title,
			@RequestParam String contents){
		if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}
		Question question = questionDao.findById(questionId);
		if (!question.isSameUser(UserSessionUtils.getUserFromSession(session))) {
			throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
		}
		
		Question newQuestion = new Question(question.getWriter(),title, contents);
		question.update(newQuestion);
		questionDao.update(question);
		return "redirect:/";
	}
	@RequestMapping(value="/delete", method=RequestMethod.DELETE)
	public String deleteQuestion(HttpSession session,@RequestParam long questionId, Model model){
		if (!UserSessionUtils.isLogined(session)) {
			return"redirect:/users/loginForm";
		}
		
		try {
			qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(session));
			return "redirect:/";
		} catch (CannotDeleteException e) {
			model.addAttribute("question", qnaService.findById(questionId));
			model.addAttribute("answers", qnaService.findAllByQuestionId(questionId));
			model.addAttribute("errorMessage", e.getMessage());
			return "/qna/show";
		}
	}
	
}
