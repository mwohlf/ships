package net.wohlfart.ships.upload;

import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.controller.UploadException;
import net.wohlfart.ships.entities.Position;
import net.wohlfart.ships.entities.Ship;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Slf4j
@Component
public class PositionTimeseriesReader extends AbstractUploadHandler {

    private static final String SHIP = "SHIP";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String LON = "LON";
    private static final String LAT = "LAT";

    final String FILENAME = "position_data.csv";  // SHIP,TIMESTAMP,SPEED,LON,LAT

    public PositionTimeseriesReader(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    @Transactional
    public void offerContent(UploadContent uploadContent) {
        if (uploadContent.getName().equals(FILENAME)) {
            processContent(uploadContent);
        }
    }

    private void processContent(UploadContent uploadContent) {
        log.info("<processContent> for {}", uploadContent.getName());
        try (CSVParser csvParser = new CSVParser(uploadContent.newReader(), FILE_FORMAT)) {
            String currentShipName = "null";
            Ship currentShip = null;
            for (CSVRecord shipRecord : csvParser.getRecords()) {
                if (!currentShipName.equals(shipRecord.get(SHIP)) || currentShip == null) {
                    currentShip = findOrCreateShip(shipRecord.get(SHIP)).orElseThrow();
                    currentShipName = shipRecord.get(SHIP);
                }
                Position position = new Position();
                position.setLatitude(parseDouble(shipRecord.get(LAT)));
                position.setLongitude(parseDouble(shipRecord.get(LON)));
                position.setShipId(currentShip.getId());
                position.setTimestamp(parseUtcDateTimeString(shipRecord.get(TIMESTAMP)));
                entityManager.persist(position);
            }
        } catch (Exception ex) {
            throw new UploadException("failed Uploading '" + uploadContent.getName() + "', changes will be rolled back", ex);
        }
    }

    private float parseDouble(String string) {
        return Float.parseFloat(string);
    }

}
