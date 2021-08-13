package net.wohlfart.ships.upload;

import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.controller.UploadException;
import net.wohlfart.ships.entities.Owner;
import net.wohlfart.ships.entities.Ship;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ShipsPerOwnerReader extends AbstractUploadHandler {

    final String FILENAME = "ships_per_owner.csv";

    public ShipsPerOwnerReader(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    @Transactional
    public void offerContent(UploadContent uploadContent) {
        if (uploadContent.fileNameMatches(FILENAME)) {
            insertContent(uploadContent.newReader());
        }
    }

    @Override
    @Transactional
    public void insertContent(Reader reader) {
        try (CSVParser csvParser = new CSVParser(reader, CSV_FILE_FORMAT)) {
            Map<String, Integer> ownerName2CsvColumn = csvParser.getHeaderMap();
            List<CSVRecord> allRecords = csvParser.getRecords();
            for (String ownerName : ownerName2CsvColumn.keySet()) {
                findOrCreateOwner(ownerName).ifPresent(owner -> {
                    int ownerColumn = ownerName2CsvColumn.get(owner.getName());
                    Collection<String> shipNamesInColumn = filterForColumn(ownerColumn, allRecords);
                    attachNewShips(owner, shipNamesInColumn);
                    entityManager.persist(owner);
                });
            }
        } catch (Exception ex) {
            throw new UploadException("failed Uploading, changes will be rolled back", ex);
        }
    }

    void attachNewShips(Owner owner, Collection<String> shipNamesInColumn) {
        Collection<String> alreadyKnownShipNames = readAllOwnedShipNames(owner);
        shipNamesInColumn.removeAll(alreadyKnownShipNames);
        for (String shipName : shipNamesInColumn) {
            owner.getShips().add(findOrCreateShipForName(shipName).orElseThrow());
        }
    }

    Collection<String> filterForColumn(Integer columnNumber, List<CSVRecord> allRecords) {
        return allRecords.stream()
            .map(strings -> strings.get(columnNumber))
            .filter(string -> string != null && !string.isEmpty())
            .map(String::trim)
            .collect(Collectors.toList());
    }

    Collection<String> readAllOwnedShipNames(Owner owner) {
        return owner.getShips().stream()
            .map(Ship::getName)
            .collect(Collectors.toSet());
    }

}
