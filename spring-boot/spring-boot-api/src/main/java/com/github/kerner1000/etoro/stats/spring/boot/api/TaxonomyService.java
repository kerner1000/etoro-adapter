package com.github.kerner1000.etoro.stats.spring.boot.api;

import com.github.kerner1000.etoro.stats.model.DefaultTaxonomy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface TaxonomyService {

    @GetMapping(value="taxonomy/{identifier}/{instrument}", produces = "application/json")
    DefaultTaxonomy getTaxonomy(@PathVariable String identifier, @PathVariable String instrument);

}
