package com.github.kerner1000.etoro.stats.taxonomy.morningstar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@SpringBootApplication
public class MorningstarTaxonomyServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(MorningstarTaxonomyServiceApplication.class);

    private final Integer connectionPoolSize;

    public MorningstarTaxonomyServiceApplication(@Value("${spring.datasource.maximum-pool-size:10}")
                                                         Integer connectionPoolSize
    ) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public static void main(String[] args) {
        SpringApplication.run(MorningstarTaxonomyServiceApplication.class, args);
    }

    @Bean
    public Scheduler scheduler() {
        logger.info("Creating a scheduler with connectionPoolSize = " + connectionPoolSize);
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
    }

}
