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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationEnginesService {

    static final DateTimeFormatter UTC_DATE_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        .withZone(ZoneId.of("+0"));

    final EntityManager entityManager;

    static final String[] COLUMNS = { "SHIP_ID", "MMSI", "MAX", "ENGINE_ID", "ENGINE_TYPE" };
    static final String NATIVE_QUERY = """
select
    ship.id as ship_id,
    ship.mmsi as mmsi,
    max(knots) as max,
    engine.id as engineId,
    engine_type.name as engine_type_name
from speed
    join ship on speed.ship_id = ship.id
    join engine on ship.id = engine.ship_id
    join engine_type on engine.engine_type_id = engine_type.id
group by ship.id, ship.mmsi, engine.id, engine_type.name
                   order by engine_type.name
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
                row.set(COLUMNS[i], new TextNode(String.valueOf(columns[i])));
            }
            result.add(row);
        }
        return result;
    }
}
