Documentation: Fuel Station Data Collector
The following documentation handles a Fuel Station Data Collector developed by Volkan Altindas, Taner Köse and Milorad Bozic. Each one of the group members had a different task to fulfill, which was given to them before the very start of the assignment. 
Volkan – SpringBootApplication, DataCollectionDispatcher, StationDataCollector
Taner – PDFGenerator
Milorad – JavaFX, DataCollectionReceiver
By using SpringBoot, RabbitMQ, JavaFX, Java, PostgresSQL and the given docker files the project was set up. It consists of the following MicroServices which communicate through a messaging queue via rabbitmq:
SpringBootApplication: 
The main entry point for the REST API, it manages the lifecycle of the data collection and PDF generation tasks. The Application also receives HTTP requests from the JavaFX GUI. Depending on the request the application sends a message to the “DataCollectionDispatcher” to start the collection job. It is also able to retrieve the generated invoices from “PDF_Files”.
Consists of a Post and a Get request.
DataCollectionDispatcher:
Manages and dispatches data collection jobs to various “StationDataCollector” instances. Notifies “DataCollectionReceiver” when a new job is initiated. 
StationDataCollector: 
Sends the collected information to the “DataCollectionReceiver” through the “Sender” class which receives its Data from the “Collector” class. The received information depends on the desired customer which is determined at the start of the application through “CustomerID”.
DataCollectionReceiver:
Receives and aggregates data from multiple “StationDataCollector” instances. Checks if all expected data is received and formats the data in the desired format, which is then displayed in the PDF through the PDFGenerator.
PDFGenerator:
Generates a PDF invoice from the aggregated data and saves it into “PDF_Files”.
JavaFX (GUI)/user-guide:
JavaFX enables regular consumers to use the application by clicking on Generate invoice, beforehand they must enter the consumer ID (1-3), they are able to check the state of the file by pressing enter. During this process, the application starts working and generates an invoice. “Download PDF” enables the user to download the invoice. All the generated files will be saved in the “PDF_Files” file and are accessible at any point in time. Besides that the user is able to see the history of searched IDS.
UML-Diagram:

![image](https://github.com/MiloooMilo/Disys/assets/131277273/364ee6ce-fe08-4d8c-bd48-13a3112f5a88)
 
Learned Lessons:
We learned that due to the MicroService structure you often have to be aware of how the services really communicate and how to ensure their functionality without writing lines of unnecessary code.
The biggest thing we learned from this project is time management and workload-management, due to us starting very late and putting a lot of pressure on ourselves as a consequence of this.

Unit testing decisions
By including these tests, we wanted to ensure that our application is both correctly configured and functionally sound, but also providing a robust foundation for further development and deployment.
StationDataCollector: The “contextLoads” test ensures that the Spring application context loads correctly, indicating that the application's configuration is valid. “testProcessStationData” test verifies that the “processStationData” method in the “StationDataCollectorController” processes data correctly. It uses a mocked “DataCollectionService” to simulate the response and ensures that the controller's method is called with the expected message. The “testRun” test ensures that the run method in the “StationDataCollectorController” starts the process of listening for messages correctly. It verifies that the method is called with the expected arguments.
PDFGenerator: The purpose of this test is to verify that the main method of “PdfGeneratorApplication” runs without throwing any exceptions. This includes ensuring that the Spring Boot application context starts up correctly. Confirming that there are no misconfigurations or issues that prevent the application from starting.
Tracked time: 63 hours of work
GitLink: https://github.com/MiloooMilo/Disys.git
		
