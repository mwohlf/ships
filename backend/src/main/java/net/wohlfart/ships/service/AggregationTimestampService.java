package net.wohlfart.ships.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationTimestampService {

    static final DateTimeFormatter UTC_DATE_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        .withZone(ZoneId.of("+0"));


    final EntityManager entityManager;

    static final String[] COLUMNS = { "OWNER", "SHIP_ID", "MMSI", "START", "END" };
    static final String NATIVE_QUERY = """

select
    o.id as owner_id,
    ship.id as ship_id,
    ship.mmsi as mmsi,
    _start.timestamp as _start,
    _end.timestamp as _end
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
;

        """;


    @Transactional
    public ArrayNode getAggregation() {
        log.info("NATIVE_QUERY:" + NATIVE_QUERY);
        Query nativeQuery = entityManager.createNativeQuery(NATIVE_QUERY);
        List<Object[]> resultList = nativeQuery.getResultList();

        final ArrayNode result = new ArrayNode(JsonNodeFactory.instance);
        for (Object[] columns : resultList) {
            final ObjectNode row = new ObjectNode(JsonNodeFactory.instance);
            for (int i = 0; i < COLUMNS.length; i++) {
                if (columns[i] instanceof Timestamp) {
                    Timestamp timestamp = (Timestamp) columns[i];
                    row.set(COLUMNS[i], new TextNode(UTC_DATE_FORMAT.format(timestamp.toInstant())));
                } else {
                    row.set(COLUMNS[i], new TextNode(String.valueOf(columns[i])));
                }
            }
            result.add(row);
        }
        return result;
    }

}
