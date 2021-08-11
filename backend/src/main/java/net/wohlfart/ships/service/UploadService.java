package net.wohlfart.ships.service;


import lombok.RequiredArgsConstructor;
import net.wohlfart.ships.upload.ShipEngineReader;
import net.wohlfart.ships.upload.ShipsPerOwnerReader;
import net.wohlfart.ships.upload.UploadContent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadService {

    final ShipsPerOwnerReader shipsPerOwnerReader;

    final ShipEngineReader shipEngineReader;

    public void uploadDatabaseContent(UploadContent uploadContent) {
        shipsPerOwnerReader.offerContent(uploadContent);
        shipEngineReader.offerContent(uploadContent);
    }

}
