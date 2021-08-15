package net.wohlfart.ships.upload;

import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.config.UploadException;
import net.wohlfart.ships.entities.Engine;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ShipEngineReader extends AbstractUploadHandler {

    final String FILENAME = "ship_engines.csv";

    public static final List<String> ENGINE_PREFIXES = List.of("engine1", "engine2", "engine3");

    public ShipEngineReader(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    @Transactional
    public void offerContent(UploadContent uploadContent) {
        if (uploadContent.fileNameMatches(FILENAME) || uploadContent.containsColumns(SHIP_NAME, MMSI)) {
            insertContent(uploadContent.newReader());
        }
    }

    @Override
    @Transactional
    public void insertContent(Reader reader) {
        try (CSVParser csvParser = new CSVParser(reader, CSV_FILE_FORMAT)) {
            for (CSVRecord shipRecord : csvParser.getRecords()) {
                String shipName = shipRecord.get(SHIP_NAME);
                Long mmsi = Long.parseLong(shipRecord.get(MMSI));
                findOrCreateShipForNameOrMmsi(shipName, mmsi).ifPresent(ship -> {
                    ship.setEngines(findEngines(shipRecord));
                    entityManager.persist(ship); // need to persist because the engines changed
                });
            }
        } catch (Exception ex) {
            throw new UploadException("failed Uploading, changes will be rolled back", ex);
        }
    }

    private Set<Engine> findEngines(CSVRecord shipRecord) {
        return ENGINE_PREFIXES
            .stream()
            .map(engineNo -> {
                String engineId = shipRecord.get(engineNo + "_id");
                String engineTypeName = shipRecord.get(engineNo + "_name");
                if (engineId == null || engineTypeName == null || engineId.isEmpty() || engineTypeName.isEmpty()) {
                    return null;
                } else {
                    Engine engine = findOrCreateEngine(engineId).orElseThrow();
                    engine.setEngineType(findOrCreateEngineType(engineTypeName).orElseThrow());
                    entityManager.persist(engine);
                    return engine;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

}
