package com.sferadev.danacast.utils;

import java.io.FileInputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {

    String filePath;

    public WebServer(String path) {
        super(8080);
        filePath = path;
    }

    @Override
    public Response serve(String uri, Method method,
                          Map<String, String> header,
                          Map<String, String> parameters,
                          Map<String, String> files) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new NanoHTTPD.Response(Response.Status.OK, "video/mp4", fileInputStream);
    }
}
