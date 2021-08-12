package net.wohlfart.ships.service;


import lombok.RequiredArgsConstructor;
import net.wohlfart.ships.upload.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadService {

    final static Object monitor = new Object();

    final ShipsPerOwnerReader shipsPerOwnerReader;

    final ShipEngineReader shipEngineReader;

    final PositionTimeseriesReader positionTimeseriesReader;

    final SpeedTimeseriesReader speedTimeseriesReader;


    public void uploadDatabaseContent(UploadContent uploadContent) {
        synchronized (monitor) { // only run one file insert at a time to avoid deadlocks in transactions
            shipsPerOwnerReader.offerContent(uploadContent);
            shipEngineReader.offerContent(uploadContent);
            positionTimeseriesReader.offerContent(uploadContent);
            speedTimeseriesReader.offerContent(uploadContent);
        }
    }

}
