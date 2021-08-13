package net.wohlfart.ships.upload;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.controller.UploadException;
import net.wohlfart.ships.entities.Ship;
import net.wohlfart.ships.entities.Speed;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Reader;
import java.util.HashMap;


@Slf4j
@Component
public class SpeedJsonReader extends AbstractUploadHandler {

    SpeedJsonReader(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    @Transactional
    public void offerContent(UploadContent uploadContent) {
        if (uploadContent.hasJsonPostfix()) {
            insertContent(uploadContent.newReader());
        }
    }

    @Override
    @Transactional
    public void insertContent(Reader reader) {
        JsonFactory factory = new JsonFactory();
        try (JsonParser parser = factory.createParser(reader)) {
            String currentMarineTrafficShipId = "";
            Ship currentShip = null;
            JsonToken token;
            while ((token = parser.nextToken()) != null) {
                if (token == JsonToken.START_OBJECT) {
                    // found an object, read its properties
                    HashMap<String, String> attributes = OBJECT_MAPPER
                        .readValue(parser, new TypeReference<>() {
                        });
                    if (!currentMarineTrafficShipId.equals(attributes.get(MT_SHIP_ID)) || currentShip == null) {
                        currentMarineTrafficShipId = attributes.get(MT_SHIP_ID);
                        currentShip = findOrCreateShipForMarineTrafficId(currentMarineTrafficShipId).orElseThrow();
                        currentShip.setImo(Long.parseLong(attributes.get(MT_IMO)));
                        currentShip.setMmsi(Long.parseLong(attributes.get(MT_MMSI)));
                        currentShip.setImo(Long.parseLong(attributes.get(MT_IMO)));
                    }
                    Speed speed = new Speed();
                    speed.setKnots(Float.parseFloat(attributes.get(MT_SPEED)) / 10f);
                    speed.setShip(currentShip);
                    speed.setTimestamp(parseUtcDateTimeStringWithT(attributes.get(MT_TIMESTAMP)));
                    entityManager.persist(speed);
                }
            }
        } catch (Exception ex) {
            throw new UploadException("failed Uploading, changes will be rolled back", ex);
        }

    }

}
