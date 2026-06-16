package no.raj.klp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class KlpCaseApplication {

	private static final Logger logger = LogManager.getLogger(KlpCaseApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(KlpCaseApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		logger.info("KLP Case Application is ready and running!");
	}

	@EventListener(ContextClosedEvent.class)
	public void onApplicationShutdown() {
		logger.info("KLP Case Application is shutting down...");
	}

}
