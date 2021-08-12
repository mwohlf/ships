package net.wohlfart.ships.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.service.DatabaseService;
import net.wohlfart.ships.service.UploadService;
import net.wohlfart.ships.upload.UploadContent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/database")
public class DatabaseController {

    final UploadService uploadService;
    final DatabaseService databaseService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload content into the Database.",
            description = "The maximum file size is 100MB, the file will be analyzed and inserted into the Database")
    @ResponseBody
    public UploadResponse uploadDatabaseContent(@RequestParam("file") MultipartFile multipartFile) {
        log.info("<uploadDatabaseContent> begin at {}", Instant.now());
        try {
            uploadService.uploadDatabaseContent(UploadContent.create(multipartFile));
            return new UploadResponse("success");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new UploadResponse("failed " + ex.getMessage());
        } finally {
            log.info("<uploadDatabaseContent> finished at {}", Instant.now());
        }
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TableDataResponse deleteDatabase() {
        databaseService.emptyDatabase();
        return new TableDataResponse();
    }

}
