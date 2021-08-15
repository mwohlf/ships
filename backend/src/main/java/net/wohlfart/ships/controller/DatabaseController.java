package net.wohlfart.ships.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.config.UploadException;
import net.wohlfart.ships.dtos.TableDetailsResponse;
import net.wohlfart.ships.dtos.StatusResponse;
import net.wohlfart.ships.service.DatabaseService;
import net.wohlfart.ships.service.UploadService;
import net.wohlfart.ships.upload.UploadContent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/database")
public class DatabaseController {

    final UploadService uploadService;
    final DatabaseService databaseService;

    @PostMapping(
        value = "/upload",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload content into the Database.",
        description = "The maximum file size is 100MB, the file will be analyzed and inserted into the Database")
    @ResponseBody
    public StatusResponse uploadDatabaseContent(
        @RequestParam MultipartFile multipartFile
    ) {
        log.info("<uploadDatabaseContent> begin at {}", Instant.now());
        try {
            uploadService.uploadDatabaseContent(UploadContent.create(multipartFile));
            return new StatusResponse(StatusResponse.Status.SUCCESS, "processed the files", "nothing went wronf it seems");
        } catch (Exception ex) {
            throw new UploadException("failed " + ex.getMessage(), ex);
        } finally {
            log.info("<uploadDatabaseContent> finished at {}", Instant.now());
        }
    }

    @DeleteMapping(
        value = "/delete/{entityName}",
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TableDetailsResponse deleteDatabase(
        @PathVariable(required = false) @Parameter(description = "delete the content of a table or the whole database", required = false) String entityName
    ) {
        try {
            databaseService.emptyTable(entityName);
            return new TableDetailsResponse();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while deleting database content", ex);
        }
    }

}
