package net.wohlfart.ships.upload;

import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.entities.Engine;
import net.wohlfart.ships.entities.EngineType;
import net.wohlfart.ships.entities.Owner;
import net.wohlfart.ships.entities.Ship;
import org.apache.commons.csv.CSVFormat;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

@Slf4j
public abstract class AbstractUploadHandler {

    final EntityManager entityManager;

    static final CSVFormat FILE_FORMAT = CSVFormat.Builder.create()
        .setAllowDuplicateHeaderNames(false)
        .setHeader()
        .setTrim(true)
        .setIgnoreHeaderCase(true)
        .setIgnoreSurroundingSpaces(true)
        .build();

    AbstractUploadHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    abstract void offerContent(UploadContent uploadContent);


    Optional<Owner> findOrCreateOwner(String ownerName) {
        final List<Owner> owners = entityManager.createNamedQuery(Owner.FIND_OWNER_BY_NAME, Owner.class)
                .setParameter("ownerName", ownerName)
                .getResultList();

        if (owners == null || owners.size() == 0) {
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


    Optional<Ship> findOrCreateShip(String shipName) {
        final List<Ship> ships = entityManager.createNamedQuery(Ship.FIND_SHIP_BY_NAME, Ship.class)
                .setParameter("shipName", shipName)
                .getResultList();

        if (ships == null || ships.size() == 0) {
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


    Optional<EngineType> findOrCreateEngineType(String engineTypeName) {
        final List<EngineType> engineTypes = entityManager.createNamedQuery(EngineType.FIND_ENGINE_TYPE_BY_NAME, EngineType.class)
                .setParameter("engineTypeName", engineTypeName)
                .getResultList();

        if (engineTypes == null || engineTypes.size() == 0) {
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

        if (engines == null || engines.size() == 0) {
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

    Instant parseUtcDateTimeString(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" , Locale.US ).withZone(ZoneId.of("+0"));  // Specify locale to determine human language and cultural norms used in translating that input string.
        LocalDateTime localDateTime = LocalDateTime.parse( dateTimeString , formatter);
        ZoneId zoneId = ZoneId.of("+0");
        ZonedDateTime zdt = localDateTime.atZone( zoneId ) ;
        return zdt.toInstant() ;
    }

}
