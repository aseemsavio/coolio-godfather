package com.coolio.godfather.services;

import com.coolio.godfather.templates.AwakeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aseem Savio
 */
@Service
public class CoolioGodFatherService {

    protected Logger log = LoggerFactory.getLogger(CoolioGodFatherService.class);

    @Value("#{'${coolio.godfather.coolioservices.urls}'.split(',')}")
    protected List<String> coolioServiceURLlist;

    @Value("#{'${coolio.godfather.coolioservices.names}'.split(',')}")
    protected List<String> coolioServiceNameList;

    @Value("${coolio.mailman.url}")
    protected String mailmanServiceURL;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    public Map<String, String> mapIt() {
        return getMap();
    }

    public List<AwakeResponse> getHealthReport() {
        List<AwakeResponse> awakeResponses = new ArrayList<>();
        Map<String, String> urlToNameMap = getMap();
        for (Map.Entry<String, String> service : urlToNameMap.entrySet()) {
            if (callService(service.getValue()).equals("1")) {
                AwakeResponse awakeResponse = new AwakeResponse(service.getKey(), "running");
                awakeResponses.add(awakeResponse);
            } else {
                AwakeResponse awakeResponse = new AwakeResponse(service.getKey(), "stopped");
                awakeResponses.add(awakeResponse);
            }
        }
        return awakeResponses;
    }

    public void scheduledHealthCheck() {
        List<AwakeResponse> awakeResponses = new ArrayList<>();
        Map<String, String> urlToNameMap = getMap();
        List<String> failedServiceNames = new ArrayList<>();
        for (Map.Entry<String, String> service : urlToNameMap.entrySet()) {
            if (!callService(service.getValue()).equals("1")) {
                failedServiceNames.add(service.getKey());
                log.error(service.getKey() + " has failed in production.");
            }
        }
        constructServiceFailureEmail(failedServiceNames);
    }

    protected void constructServiceFailureEmail(List<String> serviceNames) {
        if (!serviceNames.isEmpty()) {
            RestTemplate restTemplate = new RestTemplate();
            String endPoint = mailmanServiceURL + "/sendServiceFailureEmail";
            String requestJSON = getJSONforServiceFailure(serviceNames);
            log.info("Request JSON payload " + requestJSON);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<String>(requestJSON, headers);
            String response = restTemplate.postForObject(endPoint, entity, String.class);
            log.info("Scheduled email sent to Aseem for Production Failure.");
        }
    }

    private String getJSONforServiceFailure(List<String> serviceNames) {
        return "{\n" +
                "\t\"firstName\": \"Aseem\",\n" +
                "\t\"from\": \"coolio.mailman@gmail.com\",\n" +
                "\t\"to\": \"aseemsavio3@gmail.com\",\n" +
                "\t\"subject\": \"IMPORTANT: Microservice Down in Production\",\n" +
                "\t\"failedServices\": " + serviceNames.toString().replace("[", "[\"").replace(",", "\",\"").replace("]", "\"]") +
                "}";
    }


    /**
     * This method returns a Map of microservice-names and their URLs.
     *
     * @return urlToNameMap
     */
    protected Map<String, String> getMap() {
        Map<String, String> urlToNameMap = new HashMap<>();
        for (int i = 0; i < coolioServiceNameList.size(); i++) {
            urlToNameMap.put(coolioServiceNameList.get(i), coolioServiceURLlist.get(i));
        }
        return urlToNameMap;
    }

    protected String callService(String url) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        String URI = url + "/all/lub";
        String response = "";
        try {
            response = restTemplate.getForObject(URI, String.class);
        } catch (Exception e) {
            return "0";
        }
        return response.equalsIgnoreCase("dub") ? "1" : "0";
    }

}
