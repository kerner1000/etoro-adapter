package com.github.kerner1000.etoro.stats.thymeleaf;

import com.github.kerner1000.etoro.stats.io.AccountStatementReader;
import com.github.kerner1000.etoro.stats.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Component
public class DefaultStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStorageService.class);

    private final RestTemplate restTemplate;
    private final String transactionServiceUrl;

    @Autowired
    public DefaultStorageService(RestTemplate restTemplate,

                                 @Value("${app.composite-service.host}") String transactionServiceHost,
                                 @Value("${app.composite-service.port}") int transactionServicePort

                                 ) {
        this.restTemplate = restTemplate;
        this.transactionServiceUrl = "http://" + transactionServiceHost + ":" + transactionServicePort;
    }

    @Override
    public void init() {
        logger.debug("Init");

    }

    @Override
    public void store(MultipartFile file) {
        logger.debug("Store file {}", file);
        try {
            List<Transaction> transactionList = new AccountStatementReader()
                    .readFile(file.getInputStream());
            postTransactions(transactionList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postTransactions(List<Transaction> transactionList) {
        logger.info("Posting new transactions to {}", transactionServiceUrl);
        int failCnt = 0;
        for(int i = 0; i< transactionList.size(); i++){
            try {
                restTemplate.postForObject(transactionServiceUrl + "/transaction", transactionList.get(i), Transaction.class);
            }catch (Exception e){
                failCnt++;
            }
            if(i % 100 == 0){
                logger.debug("{} positions done", i);
            }
        }
        if(failCnt == 0)
        logger.info("All done!");
        else
            logger.error("{} of {} postings failed", failCnt, transactionList.size());
    }



    @Override
    public Stream<Path> loadAll() {
        logger.debug("Load all");
        return Stream.empty();
    }

    @Override
    public Path load(String filename) {
        logger.debug("Load {}", filename);
        return null;
    }

    @Override
    public Resource loadAsResource(String filename) {
        logger.debug("Load as resource {}", filename);
        return null;
    }

    @Override
    public void deleteAll() {
        logger.debug("Delete all");
        restTemplate.delete(transactionServiceUrl + "/delete-all-transactions");
    }
}
