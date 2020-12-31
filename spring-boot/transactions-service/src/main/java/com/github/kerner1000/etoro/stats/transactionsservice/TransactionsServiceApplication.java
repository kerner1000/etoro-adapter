package com.github.kerner1000.etoro.stats.transactionsservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.github.kerner1000.etoro.stats")
public class TransactionsServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(TransactionsServiceApplication.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(TransactionsServiceApplication.class, args);

		String mysqlUri = ctx.getEnvironment().getProperty("spring.datasource.url");
		logger.info("Connected to MySQL: " + mysqlUri);
	}

}
