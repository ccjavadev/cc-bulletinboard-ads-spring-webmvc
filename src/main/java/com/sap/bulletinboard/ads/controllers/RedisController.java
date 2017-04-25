package com.sap.bulletinboard.ads.controllers;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.hcp.cf.logging.common.Fields;

@RequestMapping("/redis")
@RestController
public class RedisController {

    public static final String KEY = "some-key";

    @Inject
    HttpServletRequest request;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    public String get() {
        incrementValueInSession();
        String newValue = String.valueOf(getFromSession());
        logger.info("New value: {}", newValue);
        String instance = MDC.get(Fields.COMPONENT_INSTANCE);
        return newValue + " (on instance " + instance + ")";
    }

    private void incrementValueInSession() {
        setInSession(increment(getFromSession()));
    }

    private int increment(Integer value) {
        if (value == null) {
            return 1;
        } else {
            return value + 1;
        }
    }

    private void setInSession(Integer value) {
        HttpSession session = request.getSession();
        session.setAttribute(KEY, value);
    }

    private Integer getFromSession() {
        HttpSession session = request.getSession();
        return (Integer) session.getAttribute(KEY);
    }

}