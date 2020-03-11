package com.retail.bo.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import antlr.collections.List;

@Service
public class RestClient {

    @Autowired
    private RestTemplate restTemplate;

    private final static String URL = "http://localhost:9082/";
    //private final static String ADD_CART_URL = "https://localhost:8443/webpos/control/";

    private Map<String, Object> requestBody = new HashMap<>();
    private Map<String, Object> credentials = new HashMap<>();

    public void addAuthentication(Authentication authentication) {
        this.credentials.put("login.username", authentication.getName());
        this.credentials.put("login.password", authentication.getCredentials());
    }

    public void addRequestParameter(String key, Object value) {
        requestBody.put(key, value);
    }

    public void setRequestBody(Map<String, Object> requestBody) {
        this.requestBody = requestBody;
    }

    public Map<String, Object> callRetailService(String serviceName, boolean isGenericService) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<Object, Object> map = new HashMap<>();
        map.put("requestBody", requestBody);
        map.put("credentials", credentials);

        HttpEntity<Map<Object, Object>> entity = new HttpEntity<>(map, headers);

        String URL = RestClient.URL + serviceName;
        if (!isGenericService) {
            URL = RestClient.URL + "retailService";
            map.put("service", serviceName);
        }
        Map<String, Object> response = (Map) restTemplate.exchange(URL, HttpMethod.POST, entity, Map.class).getBody();
        requestBody = new HashMap<>();
        return response;
    }

    public Object callRetailServiceForList(String serviceName) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        Map<Object, Object> map = new HashMap<>();
        map.put("roleTypeId", "SUPPLIER");
        map.put("lookupFlag", "Y");
        map.put("VIEW_SIZE", "50");
        HttpEntity<Map<Object, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<List> result = restTemplate.postForEntity(URL, map, List.class);

        return result;
    }
    
    /*public Map<String, Object> callCartRetailService(String serviceName, boolean isGenericService) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<Object, Object> map = new HashMap<>();
        map.put("requestBody", requestBody);
        map.put("credentials", credentials);

        HttpEntity<Map<Object, Object>> entity = new HttpEntity<>(map, headers);

        String URL = RestClient.ADD_CART_URL + serviceName;
        if (!isGenericService) {
            URL = RestClient.URL + "retailService";
            map.put("service", serviceName);
        }
        Map<String, Object> response = (Map) restTemplate.exchange(URL, HttpMethod.POST, entity, Map.class).getBody();
        requestBody = new HashMap<>();
        return response;
    }*/
}
