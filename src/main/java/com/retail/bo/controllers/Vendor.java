package com.retail.bo.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.bo.services.RestClient;
import com.retail.bo.util.HashMapResponse;

@Controller
public class Vendor extends BaseController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment env;

    @RequestMapping("vendorMaster")
    public ModelAndView vendorMaster(@ModelAttribute com.retail.bo.model.Vendor vendor) {
        final String uri = env.getProperty("findParty");
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            Map<Object, Object> map = new HashMap<>();
            map.put("roleTypeId", "SUPPLIER");
            map.put("lookupFlag", "Y");
            map.put("VIEW_SIZE", "50");
            HttpEntity requestStore = new HttpEntity(map, headers);
            ResponseEntity response = restTemplate.postForEntity(uri, requestStore, List.class);
            Map<String, Object> model = new HashMap<>();
            model.put("partyList", response.getBody());
            model.put("partyId", getPartyId());
            return new ModelAndView("vendor/vendor-master", model);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("vendorItemMapping")
    public ModelAndView vendorItemMapping(@RequestParam("userID") String userID) {
        final String uri = env.getProperty("getSupplierProductsAndProduct");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            final String uriPartyMapping = env.getProperty("getPartyMapping");
            Map<Object, Object> mapPartyMapping = new HashMap<>();
            mapPartyMapping.put("partyId", getPartyId());
            HttpEntity requestStorePartyMapping = new HttpEntity(mapPartyMapping, headers);
            ResponseEntity responsePartyMapping = restTemplate.postForEntity(uriPartyMapping, requestStorePartyMapping, List.class);

            Map<Object, Object> map = new HashMap<>();
            map.put("partyId", userID);
            HttpEntity requestStore = new HttpEntity(map, headers);
            ResponseEntity response = restTemplate.postForEntity(uri, requestStore, List.class);
            List<Object> responseList = (List<Object>) response.getBody();

            Map<Object, Object> mapProductCategory = new HashMap<>();
            final String uriProductCategory = env.getProperty("getProductCategory");
            HttpEntity requestStoreProductCategory = new HttpEntity(map, headers);
            ResponseEntity responseProductCategory = restTemplate.postForEntity(uriProductCategory, requestStoreProductCategory, List.class);

            final String uriFindParty = env.getProperty("findParty");

            Map<Object, Object> mapFindParty = new HashMap<>();
            mapFindParty.put("roleTypeId", "SUPPLIER");
            mapFindParty.put("lookupFlag", "Y");
            mapFindParty.put("VIEW_SIZE", "50");
            HttpEntity requestStoreFindParty = new HttpEntity(mapFindParty, headers);
            ResponseEntity responseFindParty = restTemplate.postForEntity(uriFindParty, requestStoreFindParty, List.class);

            Map<String, Object> model = new HashMap<>();
            model.put("partyList", responseFindParty.getBody());
            model.put("productCategory", responseProductCategory.getBody());
            model.put("productList", responseList);
            model.put("partyId", getPartyId());
            return new ModelAndView("vendor/vendor-item-mapping", model);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/createUserSupplierProduct", method = RequestMethod.GET)
    public ResponseEntity<?> mapUserSupplierItem(@RequestParam("jsondata") String json, @RequestParam("partyIdSupplier") String partyIdSupplier) {
        final String uri = env.getProperty("createUserSupplierProduct");
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            Map<Object, Object> map = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            List<Object> list = new ArrayList<Object>();
            try {
                // convert JSON string to Map
                list = mapper.readValue(json, new TypeReference<List<Object>>() {
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            map.put("productList", list);
            map.put("partyId", getPartyId());
            HttpEntity requestStore = new HttpEntity(map, headers);
            ResponseEntity response = restTemplate.postForEntity(uri, requestStore, HashMapResponse.class);
            HashMap<Object, Object> hashMap = new HashMap<Object, Object>();

            hashMap.put("message", "Success");
            return new ResponseEntity<>(hashMap, HttpStatus.OK);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("filterSupplierProduct")
    public ResponseEntity<?> searchSupplierProduct(@RequestParam("jsondata") String json) {
        final String uri = env.getProperty("getFilterSupplierProductsAndProduct");
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            Map<Object, Object> map = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();

            try {
                // convert JSON string to Map
                map = mapper.readValue(json, new TypeReference<Map<Object, Object>>() {
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            HttpEntity requestStore = new HttpEntity(map, headers);
            ResponseEntity response = restTemplate.postForEntity(uri, requestStore, List.class);
            Map<String, Object> model = new HashMap<>();
            model.put("productList", response.getBody());
            model.put("partyId", getPartyId());
            return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ResponseEntity<>("Error", HttpStatus.OK);
        }

    }
    
    
    @RequestMapping("/partyMapping")
    public ResponseEntity<?>  partyMapping(@RequestParam("jsondata") String json) {
       
        HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		 ObjectMapper mapper = new ObjectMapper();
		 Map<String, Object> map = new HashMap<>();

         try {
             // convert JSON string to Map
             map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
             });

         } catch (Exception e) {
             e.printStackTrace();
         }
		
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("partyId", getPartyId());
		Map<String, Object> response = new HashMap<String, Object>();
		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		try {
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			restClient.addRequestParameter("partyIdFrom", map.get("partyIdFrom"));
			params.put("partyIdFrom", map.get("partyIdFrom"));
			params.put("partyIdTo", map.get("partyIdTo"));
			
			restClient.setRequestBody(params);
	 response = restClient.callRetailService("partyMapping", true);
			
			if(response.get("message") != null && !response.get("message").equals("Success")) {
				hashMap.put("message",response.get("message"));
			}else {
				String partyIdTo = (String) response.get("partyIdTo");
				String partyIdFrom = (String) (response.get("partyIdFrom"));
	
				hashMap.put("partyIdTo", partyIdTo);
				hashMap.put("partyIdFrom", partyIdFrom);
				hashMap.put("message", "Success");
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 return new ResponseEntity<>(hashMap, HttpStatus.OK);   
    }
}
