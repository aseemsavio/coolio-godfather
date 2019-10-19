package com.coolio.godfather.controllers;

import com.coolio.godfather.constants.CoolioConstants;
import com.coolio.godfather.services.CoolioGodFatherService;
import com.coolio.godfather.templates.AwakeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Aseem Savio
 * Microservice to keep alive microservices of the Coolio Ecosystem.
 */

@RestController
public class CoolioGodFatherController {

    @Autowired
    CoolioGodFatherService coolioGodFatherService;

    @GetMapping("/healthCheck")
    public List<AwakeResponse> healthCheck(){
        return coolioGodFatherService.getHealthReport();
    }

    @Scheduled(cron = CoolioConstants.GODFATHER_CRON_EVERY_15_MINS)
    public void scheduledHealthCheck(){
        coolioGodFatherService.scheduledHealthCheck();
    }

    @GetMapping("/all/lub")
    public String oldHealthCheck() {
        return "dub";
    }

}
