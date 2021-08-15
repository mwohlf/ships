package net.wohlfart.ships.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

import static net.wohlfart.ships.service.HaversineDistance.calculateHaversineDistance;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationMilesService {

    static final DateTimeFormatter UTC_DATE_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        .withZone(ZoneId.of("+0"));

    final EntityManager entityManager;

    static final String NATIVE_QUERY = """

select
    o.id as owner_id,
    ship.id as ship_id,
    ship.mmsi as mmsi,
    _start.timestamp as _start,
    _start.latitude as _start_lat,   --  index 4
    _start.longitude as _start_lon,  --  index 5
    _end.timestamp as _end,
    _end.latitude as _end_lat,       --  index 7
    _end.longitude as _end_lon       --  index 8
from ship

         join position as _start on _start.ship_id = ship.id and _start.timestamp = (
    select max(timestamp) from position s
    where s.ship_id = ship.id
      and cast(s.timestamp at time zone 'utc' as date) = cast(_start.timestamp at time zone 'utc' as date)
      and cast(s.timestamp at time zone 'utc' as time) < '12:00:00+0')

         join position as _end on _end.ship_id = ship.id and _end.timestamp = (
    select max(timestamp) from position e
    where e.ship_id = ship.id
      and cast(e.timestamp at time zone 'utc' as date) = cast(_start.timestamp at time zone 'utc' as date)
      and cast(e.timestamp at time zone 'utc' as time) < '14:00:00+0')

         left join owner as o on ship.owner_id = o.id

group by o.id, ship.id, ship.mmsi, _start.timestamp, _end.timestamp,
         _start.latitude, _start.longitude, _end.latitude, _end.longitude
order by o.id
        """;


    @Transactional
    public ArrayNode getAggregation() {
        log.info("NATIVE_QUERY:" + NATIVE_QUERY);
        Query nativeQuery = entityManager.createNativeQuery(NATIVE_QUERY);
        List<Object[]> resultList = nativeQuery.getResultList();

        HashMap<String, OwnerDistance> aggregator = new HashMap<String, OwnerDistance>();
        resultList.stream().forEach(
                array -> {
                    String ownerId = String.valueOf(array[0]);
                    OwnerDistance ownerDistance = aggregator.get(ownerId);
                    if (ownerDistance == null) {
                        ownerDistance = new OwnerDistance(0, 0);
                        aggregator.put(ownerId, ownerDistance);
                    }
                    ownerDistance.divisor += 1;
                    ownerDistance.sum += calculateHaversineDistance(
                        Double.parseDouble(String.valueOf(array[4])),  // lat1
                        Double.parseDouble(String.valueOf(array[5])),  // lon1
                        Double.parseDouble(String.valueOf(array[7])),  // lat2
                        Double.parseDouble(String.valueOf(array[8]))); // lon2
                }
            );

        final ArrayNode result = new ArrayNode(JsonNodeFactory.instance);
        aggregator.forEach((ownerId, ownerDistance) -> {
            final ObjectNode row = new ObjectNode(JsonNodeFactory.instance);
            row.set("OWNER_ID", new TextNode(String.valueOf(ownerId)));
            row.set("AVG_DISTANCE", new TextNode(String.valueOf(ownerDistance.sum / ownerDistance.divisor)));
            result.add(row);
        });

        return result;
    }


    // https://en.wikipedia.org/wiki/Haversine_formula


    @Data
    @AllArgsConstructor
    static class OwnerDistance {
        double divisor;
        double sum;
    }

}
