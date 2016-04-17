package next;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import next.model.User;

public class MyHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
	private final String USER_SESSION_KEY = "user";
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if(parameter.getParameterAnnotation(LoginUser.class)!=null
				&& parameter.getClass().isAssignableFrom(User.class)){
			return true;
		}
		return false;
	}
	
	@Override
	public User resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Object user = webRequest.getAttribute(USER_SESSION_KEY, WebRequest.SCOPE_SESSION);
		if(user == null){
			return null;
		}
		return (User)user;
	}

}
