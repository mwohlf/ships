package net.wohlfart.ships.service;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.junit.Test;

import java.io.IOException;

public class RequestToken {

    @Test // see: https://rapidapi.com/Privatix/api/temp-mail
    public void fetch() throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
            .url("https://privatix-temp-mail-v1.p.rapidapi.com/request/mail/id/null/")
            .get()
            .addHeader("x-rapidapi-key", "0d0e4aeb91msh064ec861b341217p122cb8jsna1e058d4e13f")
            .addHeader("x-rapidapi-host", "privatix-temp-mail-v1.p.rapidapi.com")
            .build();

        Response response = client.newCall(request).execute();


/*
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url("https://privatix-temp-mail-v1.p.rapidapi.com/request/domains/")
            .get()
            .addHeader("x-rapidapi-key", "0d0e4aeb91msh064ec861b341217p122cb8jsna1e058d4e13f")
            .addHeader("x-rapidapi-host", "privatix-temp-mail-v1.p.rapidapi.com")
            .build();

        Response response = client.newCall(request).execute();
        */

    }


}
