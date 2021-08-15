package net.wohlfart.ships.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.internal.NativeQueryImpl;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationSpeedService {

    static final DateTimeFormatter UTC_DATE_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        .withZone(ZoneId.of("+0"));

    final EntityManager entityManager;

    static final String[] COLUMNS = { "ID", "MMSI", "DAY", "MAX", "AVG" };
    static final String NATIVE_QUERY = """
        select
            ship.id as ship_id,
            ship.mmsi as mmsi,
            date(speed.timestamp) as day,
            max(knots) as max,
            round(cast(avg(knots) as numeric), 2) as avg
        from speed join ship
            on speed.ship_id = ship.id
            group by ship.id, ship.mmsi, day
            order by ship.id, ship.mmsi, day
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
