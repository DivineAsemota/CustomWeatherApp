package com.bptn.weatherApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.DisplayName;


@SpringBootTest
@ActiveProfiles("test")
class WeatherAppApplicationTests {

	
	@DisplayName("Demo Test")
	@Test
	void contextLoads() {
	}
}
