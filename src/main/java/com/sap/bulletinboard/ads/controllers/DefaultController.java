package com.sap.bulletinboard.ads.controllers;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

@RequestMapping("/")
@RestController
public class DefaultController {

    @GetMapping
    public String get() {

        return "ok";
    }

    @GetMapping("/instance-index")
    public String getIndex(@Value("${CF_INSTANCE_INDEX}") String instanceIndex) {
        return "Instance index: " + instanceIndex;
    }

    @GetMapping(path = "/log-level/set/{level}")
    public String setLogLevel(@Value("${CF_INSTANCE_INDEX}") String instanceIndex, @PathVariable("level") String newLevelStr) {
        String body = String.format("Instance index: %s\n\n", instanceIndex);
        
        Level newLevel = Level.toLevel(newLevelStr.toUpperCase());
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
        loggerList.stream().forEach(tmpLogger -> tmpLogger.setLevel(newLevel));
        
        body += String.format("Level set to %s", newLevelStr);

        return body;
    }
    
    @GetMapping(path = "/log-level")
    public String getLogLevel(@Value("${CF_INSTANCE_INDEX}") String instanceIndex) {
        String body = String.format("Instance index: %s\n\n", instanceIndex);
        body += "Log levels";

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
        for (ch.qos.logback.classic.Logger logger : loggerList) {
            body += String.format("\n- %s %s", logger.getLevel(), logger.getName());
        }

        return body;
    }
}