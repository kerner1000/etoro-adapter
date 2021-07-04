package com.github.kerner1000.etoro.stats.taxonomyservice;

import com.github.kerner1000.etoro.stats.model.DefaultTaxonomy;
import com.github.kerner1000.etoro.stats.taxonomyservice.persistence.TaxonomyApi2EntityHelper;
import com.github.kerner1000.etoro.stats.taxonomyservice.persistence.TaxonomyEntity;
import com.github.kerner1000.etoro.stats.taxonomyservice.persistence.TaxonomyMapperImpl;
import org.junit.jupiter.api.Test;

class TaxonomyMapperTest {

    @Test
    void entityToApi() {

        TaxonomyEntity entity = new TaxonomyEntity();

        DefaultTaxonomy api = new TaxonomyMapperImpl().entityToApi(entity);

//        assertEquals("identifier",api.getIdentifier());
//        assertEquals("instrument", api.getInstrument());
//        assertEquals("ticker", api.getInstrument());

    }

    @Test
    void apiToEntity() {
        DefaultTaxonomy api = new DefaultTaxonomy();
        api.setValue("value");
        api.setIdentifier("identifer");
        api.setInstrument("instrument");

        TaxonomyEntity taxonomyEntity = new TaxonomyMapperImpl().apiToEntity(api, new TaxonomyApi2EntityHelper());
    }
}
