package com.github.kerner1000.etoro.stats.spring.boot.api;

import com.github.kerner1000.etoro.stats.model.DefaultTaxonomy;
import com.github.kerner1000.etoro.stats.model.Position;
import com.github.kerner1000.etoro.stats.model.Taxonomy;
import com.github.kerner1000.etoro.stats.model.TaxonomyProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

public interface TaxonomyService extends TaxonomyProvider {

    @GetMapping(value="taxonomy/{identifier}/{instrument}", produces = "application/json")
    DefaultTaxonomy getTaxonomy(@PathVariable String identifier, @PathVariable String instrument);

}
