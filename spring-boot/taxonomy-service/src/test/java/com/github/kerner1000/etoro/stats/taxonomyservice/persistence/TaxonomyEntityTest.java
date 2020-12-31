package com.github.kerner1000.etoro.stats.taxonomyservice.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
public class TaxonomyEntityTest {

    @Autowired
    private TaxonomyRepository repository;
    private TaxonomyEntity savedEntity;

    @org.junit.Before
    public void setUp() {
        repository.deleteAll();
        TaxonomyEntity entity = new TaxonomyEntity("identifier");
        savedEntity = repository.save(entity);
        assertEquals(entity, savedEntity);

    }

    @Test
    public void test(){

    }

}