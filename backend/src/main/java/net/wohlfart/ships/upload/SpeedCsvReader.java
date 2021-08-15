package net.wohlfart.ships.upload;

import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.config.UploadException;
import net.wohlfart.ships.entities.Ship;
import net.wohlfart.ships.entities.Speed;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Reader;

@Slf4j
@Component
public class SpeedCsvReader extends AbstractUploadHandler {

    final String FILENAME = "position_data.csv";  // SHIP,TIMESTAMP,SPEED,LON,LAT

    public SpeedCsvReader(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    @Transactional
    public void offerContent(UploadContent uploadContent) {
        if (uploadContent.fileNameMatches(FILENAME)
            || uploadContent.containsColumns(SHIP, TIMESTAMP, SPEED)) {
            insertContent(uploadContent.newReader());
        }
    }

    @Override
    @Transactional
    public void insertContent(Reader reader) {
        try (CSVParser csvParser = new CSVParser(reader, CSV_FILE_FORMAT)) {
            String currentShipName = "null";
            Ship currentShip = null;
            for (CSVRecord shipRecord : csvParser.getRecords()) {
                if (!currentShipName.equals(shipRecord.get(SHIP)) || currentShip == null) {
                    currentShip = findOrCreateShipForName(shipRecord.get(SHIP)).orElseThrow();
                    currentShipName = shipRecord.get(SHIP);
                }
                Speed speed = new Speed();
                speed.setKnots(Float.parseFloat(shipRecord.get(SPEED)));
                speed.setShip(currentShip);
                speed.setTimestamp(parseUtcDateTimeStringWithBlank(shipRecord.get(TIMESTAMP)));
                entityManager.persist(speed);
            }
        } catch (Exception ex) {
            throw new UploadException("failed Uploading, changes will be rolled back", ex);
        }
    }

}
