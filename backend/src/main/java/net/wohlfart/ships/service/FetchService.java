package net.wohlfart.ships.service;

import com.squareup.okhttp.OkHttpClient;
import lombok.RequiredArgsConstructor;
import net.wohlfart.ships.upload.AbstractUploadHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FetchService {

    final AbstractUploadHandler positionJsonReader;

    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        .withZone(ZoneId.of("+0"));

    static final String MARINE_ENDPOINT = "https://services.marinetraffic.com/api/exportvesseltrack/{apiKey}/v:3/period:hourly/days:2/mmsi:{mmsi}/protocol:jsono";

    final OkHttpClient client = new OkHttpClient();


    public void fetchContent(
        String apiKey,
        String mmsi,
        Instant start,
        Instant end
    ) {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(MARINE_ENDPOINT).buildAndExpand(
            Map.of(
                "apiKey", apiKey,
                "mmsi", mmsi,
                "start", DATE_FORMAT.format(start),
                "end", DATE_FORMAT.format(end)
            ));
        final String uriString = uriComponents.toUriString();
        System.out.println("uriString: " + uriString);
        // Request request = new Request.Builder().url(uriComponents.toUriString()).get().build();
        // System.out.println("request: " + request);
    }

}
