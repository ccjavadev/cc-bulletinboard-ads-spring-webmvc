package com.sap.bulletinboard.ads.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.text.StrBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "/", produces = MediaType.TEXT_PLAIN_VALUE)
@RestController
public class DefaultController {
    @Inject
    ServletContext context;

    @GetMapping
    public String get() throws IOException {
        InputStream inputStream = context.getResourceAsStream("/META-INF/MANIFEST.MF");
        if (inputStream != null) {
            return readFromResource(inputStream);
        }
        return context.getRealPath("/");
    }

    private String readFromResource(InputStream in) throws IOException {
        StrBuilder stringBuilder = new StrBuilder();
        Manifest manifest = new Manifest(in);
        Attributes attributes = manifest.getMainAttributes();
        for (Attributes.Entry<Object, Object> entry : attributes.entrySet()) {
            stringBuilder.appendln(entry.getKey() + " : " + entry.getValue());
        }
        return stringBuilder.toString();
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerError(HttpServletRequest req, Exception exception) {
        return "Request: " + req.getRequestURI() + " raised " + exception;
    }
}