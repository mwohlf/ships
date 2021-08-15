package net.wohlfart.ships.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;


@Data
@RequiredArgsConstructor
public class TableDetailsResponse {

    final ArrayList<SingeTableDetails> tableDetails = new ArrayList<>();

    @Data
    @RequiredArgsConstructor
    public static class SingeTableDetails {
        final String name;
        final Long   rowCount;
    }

}


