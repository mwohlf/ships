package net.wohlfart.ships.service;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wohlfart.ships.config.FetchException;
import net.wohlfart.ships.upload.PositionJsonReader;
import net.wohlfart.ships.upload.SpeedJsonReader;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FetchService {

    final SpeedJsonReader speedJsonReader;
    final PositionJsonReader positionJsonReader;

    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
        .withZone(ZoneId.of("+0"));

    static final String MARINE_ENDPOINT_PS01 = "https://services.marinetraffic.com/api/exportvesseltrack"
        + "/{apiKey}/v:3/period:hourly/fromdate:{fromDate}/todate:{toDate}/mmsi:{mmsi}/protocol:jsono";

    final OkHttpClient client = new OkHttpClient();

    public void fetchContent(
        String apiKey,
        String mmsi,
        Instant fromDate,
        Instant toDate
    ) throws IOException {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(MARINE_ENDPOINT_PS01).buildAndExpand(
            Map.of(
                "apiKey", apiKey,
                "mmsi", mmsi,        //
                "fromDate", DATE_FORMAT.format(fromDate),
                "toDate", DATE_FORMAT.format(toDate)
            ));
        final String uriString = uriComponents.toUriString();
        log.info("fetching data from {}", uriString);
        Request request = new Request.Builder().url(uriComponents.toUri().toURL()).get().build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            log.warn("request failed, unable to fetch body: {}, request: {}, response: {}", response.networkResponse(), request, response);
            throw new FetchException(response.body().toString(), new Exception("Request failed with response code: " + response.code()));
        }
        String data = response.body().string();
        log.info("data {}", data);
        speedJsonReader.insertContent(new StringReader(data));
        positionJsonReader.insertContent(new StringReader(data));
    }

    // d8445923d23c722b0b6ee750c0a6302cae79fdef
    // Queen Mary 2: MMSI: 310627000, IMO: 9241061
    // USS Ronald Reagan: MMSI: 369970410
    // COOL DISCOVERER: MMSI: 215567000 IMO: 9861031
    // AIDAnova: MMSI: 247389200, IMO: 9781865
}
