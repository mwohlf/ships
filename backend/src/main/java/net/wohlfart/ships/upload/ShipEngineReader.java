package net.wohlfart.ships.upload;

import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.controller.UploadException;
import net.wohlfart.ships.entities.Engine;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ShipEngineReader extends UploadHandler {

    private static final String MMSI = "mmsi";
    private static final String SHIP_NAME = "ship_name";
    public static final List<String> ENGINE_PREFIXES = List.of("engine1", "engine2", "engine3");

    final String FILENAME = "ship_engines.csv";

    public ShipEngineReader(EntityManager entityManager) {
        super(entityManager);
    }

    static final CSVFormat FILE_FORMAT = CSVFormat.Builder.create()
            .setAllowDuplicateHeaderNames(false)
            .setHeader()
            .setTrim(true)
            .setIgnoreHeaderCase(true)
            .setIgnoreSurroundingSpaces(true)
            .build();


    @Override
    @Transactional // persisting a whole file within a single transaction
    public void offerContent(UploadContent uploadContent) {
        if (uploadContent.getName().equals(FILENAME)) {
            processContent(uploadContent);
        }
    }

    void processContent(UploadContent uploadContent) {
        try (CSVParser csvParser = new CSVParser(uploadContent.newReader(), FILE_FORMAT)) {
            for (CSVRecord shipRecord : csvParser.getRecords()) {
                String shipName = shipRecord.get(SHIP_NAME);
                findOrCreateShip(shipName).ifPresent(ship -> {
                    ship.setMmsi(Long.parseLong(shipRecord.get(MMSI))); // TODO: validate before parsing
                    ship.setEngines(findEngines(shipRecord));
                    entityManager.persist(ship); // need to persist because the engines changed
                });
            }
        } catch (Exception ex) {
            throw new UploadException("failed Uploading '" + uploadContent.getName() + "', changes will be rolled back", ex);
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
