package net.wohlfart.ships.config;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UploadException extends RuntimeException {

    public UploadException(String message, Exception cause) {
        super(message, cause);
    }

}
