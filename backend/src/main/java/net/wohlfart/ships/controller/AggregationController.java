package net.wohlfart.ships.controller;


import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.service.AggregationEnginesService;
import net.wohlfart.ships.service.AggregationMilesService;
import net.wohlfart.ships.service.AggregationSpeedService;
import net.wohlfart.ships.service.AggregationTimestampService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/aggregation")
public class AggregationController {

    final AggregationSpeedService aggregationSpeedService;

    final AggregationTimestampService aggregationTimestampService;

    final AggregationMilesService aggregationMilesService;

    final AggregationEnginesService aggregationEnginesService;


    @GetMapping(
        value = "/speed",
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayNode aggregationSpeed(
    ) {
        try {
            return aggregationSpeedService.getAggregation();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while calculating aggregation", ex);
        }
    }

    @GetMapping(
        value = "/timestamps",
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayNode aggregationTimeStamps(
    ) {
        try {
            return aggregationTimestampService.getAggregation();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while calculating aggregation", ex);
        }
    }

    @GetMapping(
        value = "/miles",
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayNode aggregationMiles(
    ) {
        try {
            return aggregationMilesService.getAggregation();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while calculating aggregation", ex);
        }
    }

    @GetMapping(
        value = "/engines",
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayNode aggregationEngines(
    ) {
        try {
            return aggregationEnginesService.getAggregation();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while calculating aggregation", ex);
        }
    }


}
