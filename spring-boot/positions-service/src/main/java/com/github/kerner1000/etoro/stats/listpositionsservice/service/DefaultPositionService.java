package com.github.kerner1000.etoro.stats.listpositionsservice.service;

import com.github.kerner1000.etoro.stats.listpositionsservice.CompositeIntegration;
import com.github.kerner1000.etoro.stats.model.*;
import com.github.kerner1000.etoro.stats.spring.boot.api.PositionService;
import com.github.kerner1000.etoro.stats.spring.boot.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class DefaultPositionService implements PositionService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPositionService.class);

    private final ServiceUtil serviceUtil;

    private final CompositeIntegration compositeIntegration;

    @Autowired
    public DefaultPositionService(ServiceUtil serviceUtil, CompositeIntegration compositeIntegration) {
        this.serviceUtil = serviceUtil;
        this.compositeIntegration = compositeIntegration;
    }

    @Override
    public Position2 getPosition(String instrument) {
        TransactionGroup transactions = compositeIntegration.getTransactions(instrument);
        Position2 position2 = new Position2(transactions);
        return position2;
    }

    @Override
    public Position2 getOpenPosition(String instrument) {
        TransactionGroup transactions = compositeIntegration.getOpenTransactions(instrument);
        Position2 position2 = new Position2(transactions);
        if (!position2.isEmpty()) {
            DefaultTaxonomy industry = compositeIntegration.getTaxonomy("industry", position2.getInstrument());
            DefaultTaxonomy sector = compositeIntegration.getTaxonomy("sector", position2.getInstrument());
            DefaultTaxonomy name = compositeIntegration.getTaxonomy("name", position2.getInstrument());
            position2.setTaxonomies(new TaxonomyGroup(industry, sector, name));
        }
        return position2;
    }

    @Override
    public PositionGroup getOpenPositions() {
        PositionGroup result = new PositionGroup();
        TransactionGroup transactions = compositeIntegration.getOpenTransactions();
        Map<String, List<Transaction>> instrumentMap = new InsrumentMapFactory<>(transactions).getMap();
        for (Map.Entry<String, List<Transaction>> e : instrumentMap.entrySet()) {
            Position2 position2 = new Position2(new TransactionGroup(e.getValue()));
            result.add(position2);
        }
        return result;
    }

    @Override
    public PositionGroups getOpenPositionsGrouped() {
        PositionGroups result = new PositionGroups();
        Map<String, PositionGroup> map = new LinkedHashMap<>();
        TransactionGroup transactions = compositeIntegration.getOpenTransactions();
        Map<String, List<Transaction>> instrumentMap = new InsrumentMapFactory<>(transactions).getMap();
        for (Map.Entry<String, List<Transaction>> e : instrumentMap.entrySet()) {
            TransactionGroup tg = new TransactionGroup(e.getValue());
            tg.setGroupIdentifier(e.getValue().stream().findFirst().get().getInstrument());
            Position2 position2 = new Position2(tg);
            DefaultTaxonomy industry = compositeIntegration.getTaxonomy("industry", e.getKey());
            DefaultTaxonomy sector = compositeIntegration.getTaxonomy("sector", e.getKey());
            DefaultTaxonomy name = compositeIntegration.getTaxonomy("name", e.getKey());
            Taxonomy groupingTaxonomy = sector;
                    position2.setTaxonomies(new TaxonomyGroup(industry, sector, name));
            PositionGroup group = map.get(groupingTaxonomy.getValue());
            if(group == null){
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

    @Override
    public PositionGroups getOpenPositionsGroupedBySector() {
        PositionGroups result = new PositionGroups();
        Map<String, PositionGroup> map = new LinkedHashMap<>();
        TransactionGroup transactions = compositeIntegration.getOpenTransactions();
        Map<String, List<Transaction>> instrumentMap = new InsrumentMapFactory<>(transactions).getMap();
        for (Map.Entry<String, List<Transaction>> e : instrumentMap.entrySet()) {
            TransactionGroup tg = new TransactionGroup(e.getValue());
            tg.setGroupIdentifier(e.getValue().stream().findFirst().get().getInstrument());
            Position2 position2 = new Position2(tg);
            DefaultTaxonomy industry = compositeIntegration.getTaxonomy("industry", e.getKey());
            DefaultTaxonomy sector = compositeIntegration.getTaxonomy("sector", e.getKey());
            DefaultTaxonomy name = compositeIntegration.getTaxonomy("name", e.getKey());
            Taxonomy groupingTaxonomy = sector;
            position2.setTaxonomies(new TaxonomyGroup(industry, sector, name));
            PositionGroup group = map.get(groupingTaxonomy.getValue());
            if(group == null){
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

    @Override
    public PositionGroups getOpenPositionsGroupedByIndustry() {
        PositionGroups result = new PositionGroups();
        Map<String, PositionGroup> map = new LinkedHashMap<>();
        TransactionGroup transactions = compositeIntegration.getOpenTransactions();
        Map<String, List<Transaction>> instrumentMap = new InsrumentMapFactory<>(transactions).getMap();
        for (Map.Entry<String, List<Transaction>> e : instrumentMap.entrySet()) {
            TransactionGroup tg = new TransactionGroup(e.getValue());
            tg.setGroupIdentifier(e.getValue().stream().findFirst().get().getInstrument());
            Position2 position2 = new Position2(tg);
            DefaultTaxonomy industry = compositeIntegration.getTaxonomy("industry", e.getKey());
            DefaultTaxonomy sector = compositeIntegration.getTaxonomy("sector", e.getKey());
            DefaultTaxonomy name = compositeIntegration.getTaxonomy("name", e.getKey());
            Taxonomy groupingTaxonomy = industry;
            position2.setTaxonomies(new TaxonomyGroup(industry, sector, name));
            PositionGroup group = map.get(groupingTaxonomy.getValue());
            if(group == null){
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
}
