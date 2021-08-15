package net.wohlfart.ships.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StatusResponse {

    public enum Status {
        SUCCESS,
        FAILED,
        UNDEFINED,
    }

    private final Status status;

    private final String info;

    private final String description;

}
