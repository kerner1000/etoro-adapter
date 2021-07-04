package com.github.kerner1000.etoro.stats.listpositionsservice.service;

import com.github.kerner1000.etoro.stats.listpositionsservice.PositionIntegration;
import com.github.kerner1000.etoro.stats.model.*;
import com.github.kerner1000.etoro.stats.spring.boot.api.PositionService;
import com.github.kerner1000.etoro.stats.spring.boot.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.scheduler.Scheduler;

import java.util.*;

@RestController
public class DefaultPositionService implements PositionService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPositionService.class);

    private final ServiceUtil serviceUtil;

    private final PositionIntegration positionIntegration;

    private final Scheduler scheduler;

    @Autowired
    public DefaultPositionService(Scheduler scheduler, ServiceUtil serviceUtil, PositionIntegration positionIntegration) {
        this.scheduler = Objects.requireNonNull(scheduler);
        this.positionIntegration = Objects.requireNonNull(positionIntegration);
        this.serviceUtil = Objects.requireNonNull(serviceUtil);
    }

    @Override
    public Position2 getPosition(String instrument) {
        return getPosition(instrument, positionIntegration.getTransactions(instrument));
    }

    @Override
    public Position2 getOpenPosition(String instrument) {
        return getPosition(instrument, positionIntegration.getOpenTransactions(instrument));
    }

    public Position2 getPosition(String instrument, List<Transaction> transactions) {
        TaxonomyGroup taxonomyGroupMono = getTaxonomyGroupMono(instrument);
        Position2 result = new Position2();
        transactions.forEach(t -> result.addTransaction(t));
        return setTaxonomies(result, taxonomyGroupMono);
    }

    static Map<String, List<Transaction>> getGroupedTransactions(List<Transaction> transactionFlux) {
        Map<String, List<Transaction>> map = new HashMap<>();
        transactionFlux.forEach(transaction -> {
            List<Transaction> groupedTransactions = map.get(transaction.getInstrument());
            if (groupedTransactions == null) {
                groupedTransactions = new ArrayList<>();
                map.put(transaction.getInstrument(), groupedTransactions);
            }
            groupedTransactions.add(transaction);
        });
        return map;
    }

    @Override
    public PositionGroup getOpenPositions() {

        Map<String, List<Transaction>> hans = getGroupedTransactions(positionIntegration.getOpenTransactions());

        PositionGroup result = new PositionGroup();
        for (Map.Entry<String, List<Transaction>> e : hans.entrySet()) {
            Position2 position2 = new Position2(new TransactionGroup(e.getValue()));
            result.add(position2);
        }
        return result;

    }

    @Override
    public PositionGroups getOpenPositionsGrouped() {
        return getOpenPositionsGroupedBySector();
    }

    @Override
    public PositionGroups getOpenPositionsGroupedBySector() {
        return toPositionGroups(positionIntegration.getOpenTransactions(), "sector");
    }

    @Override
    public PositionGroups getOpenPositionsGroupedByIndustry() {
        return toPositionGroups(positionIntegration.getOpenTransactions(), "industry");
    }

    PositionGroups toPositionGroups(List<Transaction> transactionFlux, String groupBy) {

        Map<String, List<Transaction>> groupedTransactions = getGroupedTransactions(transactionFlux);

        PositionGroups result = transformToPositionGroups(groupedTransactions, groupBy);

        return result;
    }

    private PositionGroups transformToPositionGroups(Map<String, List<Transaction>> stringListMap, String groupBy) {
        PositionGroups result = new PositionGroups();
        Map<String, PositionGroup> map = new LinkedHashMap<>();

        for (Map.Entry<String, List<Transaction>> e : stringListMap.entrySet()) {

            TransactionGroup tg = new TransactionGroup(e.getValue());
            tg.setGroupIdentifier(e.getValue().stream().findFirst().get().getInstrument());
            Position2 position2 = new Position2(tg);

            List<DefaultTaxonomy> taxonomyFlux = getTaxonomyFlux(e.getKey());

            TaxonomyGroup taxonomyGroup = getTaxonomyGroupMono(e.getKey());
            DefaultTaxonomy groupingTaxonomy = getGroupingTaxonomy(groupBy, taxonomyFlux);

            position2.setTaxonomies(taxonomyGroup);
            PositionGroup group = map.get(groupingTaxonomy.getValue());
            if (group == null) {
                group = new PositionGroup();
                group.setGroupIdentifier(groupingTaxonomy.getValue());
                map.put(groupingTaxonomy.getValue(), group);
            }
            group.add(position2);
        }
        List<PositionGroup> groupList = new ArrayList<>(map.values());
        groupList.sort((e1, e2) -> e2.getAmount().compareTo(e1.getAmount()));
        groupList.forEach(result::add);

        return result;
    }

    private Position2 setTaxonomies(Position2 result, TaxonomyGroup taxonomyGroup) {
        if (!result.isEmpty()) {
            result.setTaxonomies(taxonomyGroup);
        }
        return result;
    }

    TaxonomyGroup getTaxonomyGroupMono(String instrument) {
        return
                new TaxonomyGroup((DefaultTaxonomy) positionIntegration.getTaxonomy("industry", instrument), (DefaultTaxonomy) positionIntegration.getTaxonomy("sector", instrument),
                        (DefaultTaxonomy) positionIntegration.getTaxonomy("name", instrument));
    }

    List<DefaultTaxonomy> getTaxonomyFlux(String instrument) {
        DefaultTaxonomy industry = positionIntegration.getTaxonomy("industry", instrument);
        DefaultTaxonomy sector = positionIntegration.getTaxonomy("sector", instrument);
        DefaultTaxonomy name = positionIntegration.getTaxonomy("name", instrument);
        return List.of(industry, sector, name);
    }

    /**
     * @param groupBy
     * @param taxonomyFlux
     * @return
     * @deprecated work with transactionRepository#getAllIds instead
     */
    @Deprecated
    DefaultTaxonomy getGroupingTaxonomy(String groupBy, List<DefaultTaxonomy> taxonomyFlux) {
        return taxonomyFlux.stream().filter(t -> t.getIdentifier().equals(groupBy)).findFirst().orElseThrow();
    }

}
