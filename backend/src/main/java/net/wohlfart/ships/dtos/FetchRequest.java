package net.wohlfart.ships.dtos;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@RequiredArgsConstructor
public class FetchRequest {

    String apiKey;

    String mmsi;

    Instant begin;

    Instant end;

}
