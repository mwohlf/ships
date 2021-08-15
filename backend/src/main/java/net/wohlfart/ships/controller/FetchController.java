package net.wohlfart.ships.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.config.UploadException;
import net.wohlfart.ships.dtos.FetchRequest;
import net.wohlfart.ships.dtos.StatusResponse;
import net.wohlfart.ships.service.FetchService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fetch")
public class FetchController {

    final FetchService fetchService;

    @PostMapping(
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public StatusResponse fetchContent(
        @RequestBody @Valid @Parameter(required = true) FetchRequest fetchRequest
    ) {
        log.info("<uploadDatabaseContent> begin at {}", Instant.now());
        try {
            fetchService.fetchContent(
                fetchRequest.getApiKey(),
                fetchRequest.getMmsi(),
                fetchRequest.getBegin(),
                fetchRequest.getEnd()
            );
            return new StatusResponse(StatusResponse.Status.SUCCESS, "processed the files", "nothing went wrong it seems");
        } catch (Exception ex) {
            throw new UploadException("failed " + ex.getMessage(), ex);
        } finally {
            log.info("<uploadDatabaseContent> finished at {}", Instant.now());
        }
    }

}
