import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import next.controller.HomeController;

public class ApplicationContextTest {
	private AnnotationConfigApplicationContext ac;
	
	@Before
	public void setup(){
		ac = new AnnotationConfigApplicationContext(ApplicationConfig.class);
	}
	
	@Test
	public void test() {
		HomeController home1 = ac.getBean(HomeController.class); 
		HomeController home2 = ac.getBean(HomeController.class);
		assertTrue(home1 == home2);
	}
	
	@After
	public void teardown(){
		ac.close();
	}

}
