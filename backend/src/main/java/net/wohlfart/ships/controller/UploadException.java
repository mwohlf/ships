package net.wohlfart.ships.controller;

public class UploadException extends RuntimeException {

    public UploadException(String message, Exception cause) {
        super(message, cause);
    }

}
