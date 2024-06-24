package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@SpringBootTest
public class PdfGeneratorApplicationTests {

	@Test
	public void testMain() {
		assertDoesNotThrow(() -> {
			PdfGeneratorApplication.main(new String[]{});
		});
	}
}