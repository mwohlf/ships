package net.wohlfart.ships.service;


import lombok.RequiredArgsConstructor;
import net.wohlfart.ships.upload.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadService {

    static final Object MONITOR = new Object();

    final AbstractUploadHandler positionCsvReader;

    final AbstractUploadHandler positionJsonReader;

    final AbstractUploadHandler shipEngineReader;

    final AbstractUploadHandler shipsPerOwnerReader;

    final AbstractUploadHandler speedCsvReader;

    final AbstractUploadHandler speedJsonReader;


    public void uploadDatabaseContent(UploadContent uploadContent) {
        synchronized (MONITOR) {

            shipEngineReader.offerContent(uploadContent);
            shipsPerOwnerReader.offerContent(uploadContent);

            positionCsvReader.offerContent(uploadContent);
            positionJsonReader.offerContent(uploadContent);

            speedCsvReader.offerContent(uploadContent);
            speedJsonReader.offerContent(uploadContent);
        }
    }

}
