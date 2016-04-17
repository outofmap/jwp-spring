package next;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;

import next.config.WebMvcConfig;

public class MyWebInitializer implements WebApplicationInitializer {
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		CharacterEncodingFilter cef = new CharacterEncodingFilter();
		cef.setEncoding("UTF-8");
		cef.setForceEncoding(true);
		servletContext.addFilter("characterEncodingFilter", cef).addMappingForUrlPatterns(null, false, "/*");
		//put과 delete를 사용하려고 더해준 httpMethodeFilter 
		servletContext.addFilter("httpMethodFilter", HiddenHttpMethodFilter.class)
				.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
		
		AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
		webContext.register(WebMvcConfig.class);
		//Servlet 생성 
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("next", new DispatcherServlet(webContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
	}
}
