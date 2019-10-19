package com.coolio.godfather.services;

import com.coolio.godfather.constants.CoolioConstants;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Aseem Savio
 */
@Service
public class CoolioGodFatherService implements CoolioConstants{

    protected Logger log = LoggerFactory.getLogger(CoolioGodFatherService.class);

    @Value("#{'${coolio.godfather.coolioservices.urls}'.split(',')}")
    protected List<String> coolioServiceURLlist;

    @Value("#{'${coolio.godfather.coolioservices.names}'.split(',')}")
    protected List<String> coolioServiceNameList;

    @Value("${coolio.mailman.url}")
    protected String mailmanServiceURL;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    public List<AwakeResponse> getHealthReport() {
        List<AwakeResponse> awakeResponses = new ArrayList<>();
        Map<String, String> urlToNameMap = zipServiceNameURL(coolioServiceNameList, coolioServiceURLlist);
        for (Map.Entry<String, String> service : urlToNameMap.entrySet()) {
            if (callService(service.getValue()).equals(STRING_ONE)) {
                AwakeResponse awakeResponse = new AwakeResponse(service.getKey(), SERVICE_STATUS_RUNNING);
                awakeResponses.add(awakeResponse);
            } else {
                AwakeResponse awakeResponse = new AwakeResponse(service.getKey(), SERVICE_STATUS_STOPPED);
                awakeResponses.add(awakeResponse);
            }
        }
        AwakeResponse awakeResponse = new AwakeResponse(APP_NAME, SERVICE_STATUS_RUNNING);
        awakeResponses.add(awakeResponse);
        return awakeResponses;
    }

    public void scheduledHealthCheck() {
        List<AwakeResponse> awakeResponses = new ArrayList<>();
        Map<String, String> urlToNameMap = zipServiceNameURL(coolioServiceNameList, coolioServiceURLlist);
        List<String> failedServiceNames = new ArrayList<>();
        for (Map.Entry<String, String> service : urlToNameMap.entrySet()) {
            if (!callService(service.getValue()).equals(STRING_ONE)) {
                failedServiceNames.add(service.getKey());
                log.error(service.getKey() + " has failed in production.");
            }
        }
        constructServiceFailureEmail(failedServiceNames);
    }

    protected void constructServiceFailureEmail(List<String> serviceNames) {
        if (!serviceNames.isEmpty()) {
            RestTemplate restTemplate = new RestTemplate();
            String endPoint = mailmanServiceURL + SEND_SERVICE_FAILURE_EMAIL_ENDPOINT;
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
     * This method zips the two lists it takes - service Names and the Service URLS into a single Map.
     *
     * @param serviceNames
     * @param serviceURLs
     * @return
     */
    protected Map<String, String> zipServiceNameURL(List<String> serviceNames, List<String> serviceURLs) {
        int size = serviceNames.size() == serviceURLs.size() ? serviceNames.size() : 0;
        return IntStream.range(0, size)
                .boxed()
                .collect(Collectors.toMap(serviceNames::get, serviceURLs::get));
    }

    /**
     * The actual RestTemplate call happens here.
     *
     * @param url
     * @return
     */
    protected String callService(String url) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        String URI = url + COMMON_HEALTH_ENDPOINT;
        String response = EMPTY_STRING;
        try {
            response = restTemplate.getForObject(URI, String.class);
        } catch (Exception e) {
            return STRING_ZERO;
        }
        return response.equalsIgnoreCase(HEART_BEAT_RESPONSE) ? STRING_ONE : STRING_ZERO;
    }


}
