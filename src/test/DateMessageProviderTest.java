import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

import next.DateMessageProvider;

public class DateMessageProviderTest {

	@Test
	public void 오전() throws Exception {
		DateMessageProvider provider = new DateMessageProvider(); 
		assertThat(provider.getDateMessage(), is("오전"));
	}
	
	@Test
	public void 오후() throws Exception {
		DateMessageProvider provider = new DateMessageProvider(); 
		assertThat(provider.getDateMessage(), is("오후"));
	}
	

}
