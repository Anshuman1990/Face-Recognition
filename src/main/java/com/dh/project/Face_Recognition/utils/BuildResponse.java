package com.dh.project.Face_Recognition.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class BuildResponse {

    private MultiValueMap<String, Object> responseHeaders;

    public BuildResponse() {
        this.responseHeaders = new LinkedMultiValueMap<>();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ResponseEntity createResponse(Object data, HttpStatus status) {
        Map _result = new HashMap();
        this.createResponseHeaders();
        return new ResponseEntity(data, this.responseHeaders, status);
    }


    private void createResponseHeaders() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        this.responseHeaders.add("date", formatter.format(date));
        this.responseHeaders.add("content-type", "application/json");
    }

    public void clean() {
        this.responseHeaders.clear();
    }
}
