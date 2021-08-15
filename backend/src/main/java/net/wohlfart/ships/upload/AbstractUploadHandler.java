package net.wohlfart.ships.upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.entities.Engine;
import net.wohlfart.ships.entities.EngineType;
import net.wohlfart.ships.entities.Owner;
import net.wohlfart.ships.entities.Ship;
import org.apache.commons.csv.CSVFormat;

import javax.persistence.EntityManager;
import java.io.Reader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


/**
 * This is an abstract base class for reader implementation
 * we implement tools like parsers and lookups for db entities
 * to be used by the actual implementation of a database reader.
 * This is also providing the entity manager for the child classes
 */
@Slf4j
public abstract class AbstractUploadHandler {

    final EntityManager entityManager;

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    static final CSVFormat CSV_FILE_FORMAT = CSVFormat.Builder.create()
        .setAllowDuplicateHeaderNames(false)
        .setHeader()
        .setTrim(true)
        .setIgnoreHeaderCase(true)
        .setIgnoreSurroundingSpaces(true)
        .build();

    static final DateTimeFormatter CSV_FILE__DATE_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        .withZone(ZoneId.of("+0"));

    static final DateTimeFormatter JSON_IMPORT__DATE_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        .withZone(ZoneId.of("+0"));


    // common column headers
    static final String SHIP = "SHIP";    // external id used in csv files
    static final String TIMESTAMP = "TIMESTAMP";
    static final String LON = "LON";
    static final String LAT = "LAT";
    static final String SPEED = "SPEED";
    // used in the ship engine file
    static final String MMSI = "mmsi";
    static final String SHIP_NAME = "ship_name";   // csv export


    // marine traffic format and strings
    static final String MT_SPEED = "SPEED";
    static final String MT_LON = "LON";
    static final String MT_LAT = "LAT";
    static final String MT_SHIP_ID = "SHIP_ID";       // id from maritime traffic export
    static final String MT_IMO = "IMO";               // from maritime traffic export
    static final String MT_MMSI = "MMSI";               // from maritime traffic export
    static final String MT_TIMESTAMP = "TIMESTAMP";


    AbstractUploadHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    // entrypoint for analyzing uploaded conternt
    public abstract void offerContent(UploadContent uploadContent);

    // input stream reading
    public abstract void insertContent(Reader reader);


    Optional<Owner> findOrCreateOwner(String ownerName) {
        final List<Owner> owners = entityManager.createNamedQuery(Owner.FIND_OWNER_BY_NAME, Owner.class)
            .setParameter("ownerName", ownerName)
            .getResultList();

        if (owners == null || owners.isEmpty()) {
            Owner result = new Owner();
            result.setName(ownerName);
            log.info("persisting new owner {}", result);
            return Optional.of(entityManager.merge(result));
        }

        if (owners.size() == 1) {
            log.info("found owner {}", ownerName);
            return Optional.of(owners.get(0));
        }

        log.warn("found multiple owner matching name '{}', ignoring for insert", ownerName);
        return Optional.empty();
    }


    Optional<Ship> findOrCreateShipForName(String shipName) {
        final List<Ship> ships = entityManager.createNamedQuery(Ship.FIND_SHIP_BY_NAME, Ship.class)
            .setParameter("shipName", shipName)
            .getResultList();

        if (ships == null || ships.isEmpty()) {
            Ship result = new Ship();
            result.setName(shipName);
            log.info("persisting new ship {}", result);
            return Optional.of(entityManager.merge(result));
        }

        if (ships.size() == 1) {
            log.debug("found ship {}", shipName);
            return Optional.of(ships.get(0));
        }

        log.warn("found multiple ships matching name '{}', ignoring for insert", shipName);
        return Optional.empty();
    }

    Optional<Ship> findOrCreateShipForNameOrMmsi(String shipName, Long mmsi) {
        final List<Ship> ships = entityManager.createNamedQuery(Ship.FIND_SHIP_BY_NAME_OR_MMSI, Ship.class)
            .setParameter("shipName", shipName)
            .setParameter("mmsi", mmsi)
            .getResultList();

        if (ships == null || ships.isEmpty()) {
            Ship result = new Ship();
            result.setName(shipName);
            result.setMmsi(mmsi);
            log.info("persisting new ship {}", result);
            return Optional.of(entityManager.merge(result));
        }

        if (ships.size() == 1) {
            log.debug("found ship {}", shipName);
            Ship result = ships.get(0);
            result.setName(shipName);
            result.setMmsi(mmsi);
            return Optional.of(ships.get(0));
        }

        log.warn("found multiple ships matching '{}'/'{}', ignoring for insert", shipName, mmsi);
        return Optional.empty();
    }

    Optional<Ship> findOrCreateShipForMarineTrafficId(String marineTrafficId) {
        final List<Ship> ships = entityManager.createNamedQuery(Ship.FIND_SHIP_BY_MARINE_TRAFFIC_ID, Ship.class)
            .setParameter("marineTrafficId", marineTrafficId)
            .getResultList();

        if (ships == null || ships.isEmpty()) {
            Ship result = new Ship();
            result.setMarineTrafficId(marineTrafficId);
            log.info("persisting new ship {}", result);
            return Optional.of(entityManager.merge(result));
        }

        if (ships.size() == 1) {
            log.debug("found ship {}", marineTrafficId);
            return Optional.of(ships.get(0));
        }

        log.warn("found multiple ships matching name '{}', ignoring for insert", marineTrafficId);
        return Optional.empty();
    }


    Optional<EngineType> findOrCreateEngineType(String engineTypeName) {
        final List<EngineType> engineTypes = entityManager.createNamedQuery(EngineType.FIND_ENGINE_TYPE_BY_NAME, EngineType.class)
            .setParameter("engineTypeName", engineTypeName)
            .getResultList();

        if (engineTypes == null || engineTypes.isEmpty()) {
            EngineType result = new EngineType();
            result.setName(engineTypeName);
            log.info("persisting new engineType {}", result);
            return Optional.of(entityManager.merge(result));
        }

        if (engineTypes.size() == 1) {
            log.debug("found engineType {}", engineTypeName);
            return Optional.of(engineTypes.get(0));
        }

        log.warn("found multiple engineTypes matching name '{}', ignoring for insert", engineTypeName);
        return Optional.empty();
    }


    Optional<Engine> findOrCreateEngine(String engineId) {
        final List<Engine> engines = entityManager.createNamedQuery(Engine.FIND_ENGINE_BY_ENGINE_ID, Engine.class)
            .setParameter("engineId", engineId)
            .getResultList();

        if (engines == null || engines.isEmpty()) {
            Engine result = new Engine();
            result.setEngineId(engineId);
            log.info("persisting new engine {}", result);
            return Optional.of(entityManager.merge(result));
        }

        if (engines.size() == 1) {
            log.debug("found engine {}", engineId);
            return Optional.of(engines.get(0));
        }

        log.warn("found multiple engines matching engineId '{}', ignoring for insert", engineId);
        return Optional.empty();
    }

    Instant parseUtcDateTimeStringWithBlank(String dateTimeString) {
        return LocalDateTime
            .parse(dateTimeString, CSV_FILE__DATE_FORMAT)
            .atZone(ZoneId.of("+0"))
            .toInstant();
    }

    Instant parseUtcDateTimeStringWithT(String dateTimeString) {
        return LocalDateTime
            .parse(dateTimeString, JSON_IMPORT__DATE_FORMAT)
            .atZone(ZoneId.of("+0"))
            .toInstant();
    }

    float parseDouble(String string) {
        return Float.parseFloat(string);
    }

}
