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
import java.io.Reader;

@Slf4j
@Component
public class PositionCsvReader extends AbstractUploadHandler {

    private static final String FILENAME = "position_data.csv";

    public PositionCsvReader(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    @Transactional
    public void offerContent(UploadContent uploadContent) {
        if (uploadContent.fileNameMatches(FILENAME)
            || uploadContent.containsColumns(SHIP, TIMESTAMP, LON, LAT)) {
            insertContent(uploadContent.newReader());
        }
    }

    @Override
    @Transactional
    public void insertContent(Reader reader) {
        try (CSVParser csvParser = new CSVParser(reader, CSV_FILE_FORMAT)) {
            String currentShipName = "";
            Ship currentShip = null;
            for (CSVRecord shipRecord : csvParser.getRecords()) {
                if (!currentShipName.equals(shipRecord.get(SHIP)) || currentShip == null) {
                    currentShip = findOrCreateShipForName(shipRecord.get(SHIP)).orElseThrow();
                    currentShipName = shipRecord.get(SHIP);
                }
                Position position = new Position();
                position.setLatitude(parseDouble(shipRecord.get(LAT)));
                position.setLongitude(parseDouble(shipRecord.get(LON)));
                position.setShip(currentShip);
                position.setTimestamp(parseUtcDateTimeStringWithBlank(shipRecord.get(TIMESTAMP)));
                entityManager.persist(position);
            }
        } catch (Exception ex) {
            throw new UploadException("failed Uploading, changes will be rolled back", ex);
        }
    }

}
