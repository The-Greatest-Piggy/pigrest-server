package app.pigrest;

import app.pigrest.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {PigrestApplication.class, TestSecurityConfig.class})
@ActiveProfiles("test")
class PigrestApplicationTests {

	@Test
	void contextLoads() {
	}

}
