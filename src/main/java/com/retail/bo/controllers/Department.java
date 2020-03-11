package com.retail.bo.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.bo.services.RestClient;
import com.retail.bo.util.HashMapResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class Department extends BaseController {

    @Autowired
    private Environment env;

    @GetMapping("departmentTransfer")
    public ModelAndView departmentTransfer() {
        return new ModelAndView("department/department-transfer");
    }

    @GetMapping("interDepartmentTransfer")
    public ModelAndView interDepartmentTransfer() {
        return new ModelAndView("department/inter-department-transfer");
    }

    @GetMapping("interDepartmentTransferList")
    public ModelAndView interDepartmentTransferList() {
        return new ModelAndView("department/inter-department-transfer-list");
    }

    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/transferInventory/create", method = RequestMethod.GET)
	public ResponseEntity<?> transferInventory(@RequestParam("json") String json) {
		final String uri = env.getProperty("transfer.inventory.create");
		final String checkInventoryUri = env.getProperty("transfer.inventory.check");
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
        ObjectMapper mapper = new ObjectMapper();
        
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});

			System.out.println(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//check inventory transfer
		HttpHeaders checkInventoryHeaders = new HttpHeaders();
		checkInventoryHeaders.add("Content-Type", "application/json");
		HttpEntity crequest = new HttpEntity(params, checkInventoryHeaders);
		RestTemplate crestTemplate = new RestTemplate();
		HashMapResponse cresult = crestTemplate.postForObject(checkInventoryUri, crequest, HashMapResponse.class);
		HashMap<String, Object> clist = cresult.getResponse();
		String errorMsg = (String) clist.get("errormsg");
		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		if(errorMsg != null) {
			return new ResponseEntity<>(hashMap, HttpStatus.BAD_REQUEST);
		}
		//
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		HashMap<String, Object> list = result.getResponse();
		
		if (list != null) {
			hashMap.put("message", "Success");
		}

		return new ResponseEntity<>(hashMap, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/transferInventory/complete", method = RequestMethod.GET)
	public ResponseEntity<?> completeTransferInventory(
			@RequestParam("inventoryTransferId") String inventoryTransferId) {

		final String uri = env.getProperty("transfer.inventory.complete");
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
		ObjectMapper mapper = new ObjectMapper();

		try {

			params.put("inventoryTransferId", inventoryTransferId);
			System.out.println(params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		System.out.println("in find product----");
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		System.out.println("Result is -------" + result.toString());

		ModelAndView modelAndView = new ModelAndView();
		HashMap<String, Object> list = result.getResponse();
		System.out.println("result-----" + list.toString());

		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();

		hashMap.put("message", "Success");
		return new ResponseEntity<>(hashMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/findinventoryItem", method = RequestMethod.GET)
	public ResponseEntity<?> findInventoryItem(@RequestParam("type") String type,
			@RequestParam("department_from") String department_from) { 
		final String uri = env.getProperty("findinventoryItem");
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();

		params.put("productId", type);
		params.put("facilityId", department_from);
		//params.put("searchByPartyId", "admin");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("productStoreGroupId", "");
		request.put("currencyUomId", "");
		request.put("criteria",params);
		System.out.println("in find product----");
		RestTemplate restTemplate = new RestTemplate();
		
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			
			restClient.setRequestBody(request);
			response = restClient.callRetailService("getInventoryItems", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Object> inventoryListWithEAN = (List<Object>) response.get("responseBody");
		System.out.println("result-----" + inventoryListWithEAN);

		return new ResponseEntity<>(inventoryListWithEAN, HttpStatus.OK);
	}

	@RequestMapping(value = "/listProduct", method = RequestMethod.GET)
	public ModelAndView listProduct(HttpSession session) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		RestTemplate restTemplate = new RestTemplate();
		ModelAndView modelAndView = new ModelAndView();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> request = new HashMap<String, Object>();
		//params.put("partyId", getPartyId());
		params.put("facilityId",getFacilityId());
		request.put("criteria",params);
		request.put("productStoreGroupId", "");
		request.put("currencyUomId", "");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			
			restClient.setRequestBody(request);
			response = restClient.callRetailService("getInventoryItems", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Object> inventoryListWithEAN = (List<Object>) response.get("responseBody");
		modelAndView.addObject("result", inventoryListWithEAN);

		final String uriFacility = env.getProperty("find.facility");
		params.put("searchByOwnerPartyId", "admin");
		HttpEntity requestStore = new HttpEntity(params, headers);
		HashMapResponse result1 = restTemplate.postForObject(uriFacility, requestStore, HashMapResponse.class);
//		System.out.println("Result 1 is -------" + result1.toString());
		HashMap<String, Object> list1 = result1.getResponse();
//		System.out.println("result 1-----" + list1.get("facilityList").toString());
		String facilityId = getFacilityId();
		modelAndView.addObject("facilityId",getFacilityId());
		modelAndView.addObject("facilityName",getFacilityname(facilityId));
		modelAndView.addObject("result1", list1.get("facilityList"));

		modelAndView.addObject("partyId", SecurityContextHolder.getContext().getAuthentication().getName());
		String inventoryItemSearchUrl = env.getProperty("ajax.inventory.search");
        String inventoryTransferCreate = env.getProperty("ajax.inventory.create");
        
		session.setAttribute("findInventoryItemUrl", inventoryItemSearchUrl);
        session.setAttribute("inventoryTransfer", inventoryTransferCreate);
		modelAndView.setViewName("interdepatment-transfer");
		return modelAndView;
	}

    @RequestMapping(value = "/findProduct", method = RequestMethod.POST)
    public ResponseEntity<?> findProduct(@RequestParam("type") String type) {
        final String uri = env.getProperty("find.product");
        Hashtable<Object, Object> params = new Hashtable<Object, Object>();
        params.put("searchByProductName", type);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in find product----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.get("productsList").toString());

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(list.get("productsList"));
            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    @SuppressWarnings("unchecked")
    private String getFacilityname(String facilityId) throws Exception {
    	String facilityName = "";
    	HashMap facilityMap = new HashMap<>();
    	facilityMap.put("facilityId", facilityId);
    	
        RestClient restClient = applicationContext.getBean(RestClient.class);
        restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
        restClient.addRequestParameter("entityName", "Facility");
        restClient.addRequestParameter("inputFields", facilityMap);
        restClient.addRequestParameter("noConditionFind", "Y");
        
        List<LinkedHashMap> facilityList = (List<LinkedHashMap>) restClient.callRetailService("performFindList", false).get("list");
        
        facilityName = (String) facilityList.get(0).get("facilityName");
        return facilityName;
    }
    
    @RequestMapping(value = "/departmentTransferList", method = RequestMethod.GET)
	public ModelAndView departmentTransferList(HttpSession session) {
		final String uri = env.getProperty("interDepartmentTransfer");
		Hashtable<String, String> params = new Hashtable<String, String>();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		

		ModelAndView modelAndView = new ModelAndView();
		HashMap<String, Object> list = result.getResponse();
		
		modelAndView.addObject("result", list.get("inventoryTransferList"));

		final String uriFacility = env.getProperty("find.facility");
		params.put("searchByOwnerPartyId", "admin");
		HttpEntity requestStore = new HttpEntity(params, headers);
		HashMapResponse result1 = restTemplate.postForObject(uriFacility, requestStore, HashMapResponse.class);
		
		HashMap<String, Object> list1 = result1.getResponse();
		String transferComplete = env.getProperty("ajax.inventory.complete");
		String inventoryTransferSearch = env.getProperty("ajax.inventory.transfer.search");
		String listProduct = env.getProperty("ajax.createnew.inventory.transfer");
		session.setAttribute("transferComplete", transferComplete);
		session.setAttribute("inventoryTransferSearch", inventoryTransferSearch);
		session.setAttribute("listProduct", listProduct);
		modelAndView.addObject("result1", list1.get("facilityList"));
		modelAndView.setViewName("interdepartment-transfer-list");
		return modelAndView;
	}
}
