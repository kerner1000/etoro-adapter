package com.github.kerner1000.etoro.stats.transactionsservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@SpringBootApplication
@ComponentScan("com.github.kerner1000.etoro.stats")
public class TransactionsServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(TransactionsServiceApplication.class);

    private final Integer connectionPoolSize;

    TransactionsServiceApplication(
            @Value("${spring.datasource.maximum-pool-size:10}")
                    Integer connectionPoolSize
    ) {
        this.connectionPoolSize = connectionPoolSize;
    }

    @Bean
    public Scheduler jdbcScheduler() {
        logger.info("Creating a jdbcScheduler with connectionPoolSize = " + connectionPoolSize);
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(TransactionsServiceApplication.class, args);

        String mysqlUri = ctx.getEnvironment().getProperty("spring.datasource.url");
        logger.info("Connected to MySQL: " + mysqlUri);
    }

}
