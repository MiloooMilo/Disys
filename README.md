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
JavaFX enables regular consumers to use the application by clicking on Generate invoice, beforehand they must enter the consumer ID (1-3). During this process, the application starts working and generates an invoice. After a few seconds a second button appears which says “Download PDF” which enables the user to download the invoice. All the generated files will be saved in the “PDF_Files” file and are accessible at any point in time. 
UML-Diagram:

Learned Lessons:
We learned that due to the MicroService structure you often have to be aware of how the services really communicate and how to ensure their functionality without writing lines of unnecessary code.
The biggest thing we learned from this project is time management and workload-management, due to us starting very late and putting a lot of pressure on ourselves as a consequence of this.
Tracked time: 63 hours of work
GitLink: https://github.com/MiloooMilo/Disys.git
		
