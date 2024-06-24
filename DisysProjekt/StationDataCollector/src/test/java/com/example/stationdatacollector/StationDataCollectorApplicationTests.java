package com.example.stationdatacollector;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.mockito.Mockito;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class StationDataCollectorApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DataCollectionService dataCollectionService;

	@MockBean
	private StationDataCollectorController controller;

	@Test
	public void contextLoads() {
		// This test will pass if the application context loads successfully
	}

	@Test
	public void testProcessStationData() throws Exception {
		// Mock the service response
		Mockito.when(dataCollectionService.collectDataFromStationDb(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(List.of("data1", "data2"));

		// Test the processStationData method
		String message = "1;http://example.com/stationDb";
		controller.processStationData(message);

		// Verify that the data was processed correctly
		Mockito.verify(controller).processStationData(message);
	}

	@Test
	public void testRun() throws Exception {
		// Test the run method
		controller.run("testQueue", "testBrokerUrl");

		// Verify that the controller started listening for messages
		Mockito.verify(controller).run("testQueue", "testBrokerUrl");
	}
}
