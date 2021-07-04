package com.github.kerner1000.etoro.stats.taxonomyservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@SpringBootApplication
@ComponentScan("com.github.kerner1000.etoro.stats")
public class TaxonomyServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(TaxonomyServiceApplication.class);

	private final Integer connectionPoolSize;

	TaxonomyServiceApplication(@Value("${spring.datasource.maximum-pool-size:10}")
									   Integer connectionPoolSize){
		this.connectionPoolSize = connectionPoolSize;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(TaxonomyServiceApplication.class, args);

		String mysqlUri = ctx.getEnvironment().getProperty("spring.datasource.url");
		logger.info("Connected to MySQL: " + mysqlUri);
	}

	@Bean
	public Scheduler scheduler() {
		logger.info("Creating a scheduler with connectionPoolSize = " + connectionPoolSize);
		return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}



}
