package com.github.kerner1000.etoro.stats.spring.boot.api;

import com.github.kerner1000.etoro.stats.model.Position2;
import com.github.kerner1000.etoro.stats.model.PositionGroup;
import com.github.kerner1000.etoro.stats.model.PositionGroups;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Api( "REST API for position information.")
public interface PositionService {

    @ApiOperation(
            value = "${api.composite.position.description}",
            notes = "${api.composite.position.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails. See response message for more information.")
    })
    @GetMapping(value="position/{instrument}", produces = "application/json")
    Position2 getPosition(@PathVariable String instrument);

    @GetMapping(value="open-position/{instrument}", produces = "application/json")
    Position2 getOpenPosition(@PathVariable String instrument);

    @GetMapping(value="open-positions", produces = "application/json")
    PositionGroup getOpenPositions();

    @GetMapping(value="open-positions-grouped", produces = "application/json")
    PositionGroups getOpenPositionsGrouped();

    @GetMapping(value="open-positions-grouped-bysector", produces = "application/json")
    PositionGroups getOpenPositionsGroupedBySector();

    @GetMapping(value="open-positions-grouped-byindustry", produces = "application/json")
    PositionGroups getOpenPositionsGroupedByIndustry();
}
