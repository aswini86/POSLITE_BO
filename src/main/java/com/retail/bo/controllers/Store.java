package com.retail.bo.controllers;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
public class Store extends BaseController{

	@Autowired
	private Environment env;

	@GetMapping("storeTransferIn")
	public ModelAndView storeTransferIn() {
		return new ModelAndView("store/store-transfer-in");
	}

	@GetMapping("storeTransferOut")
	public ModelAndView storeTransferOut() {
		return new ModelAndView("store/store-transfer-out");
	}

	@GetMapping("storeTransferInList")
	public ModelAndView storeTransferInList() {
		return new ModelAndView("store/store-transfer-in-list");
	}

	@RequestMapping(value = "/storetransferoutlist", method = RequestMethod.GET)
	public ModelAndView findInventoryTransfer(HttpSession session) {
		final String uri = env.getProperty("storetransferoutlist");
		Hashtable<String, String> params = new Hashtable<String, String>();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		

		ModelAndView modelAndView = new ModelAndView();
		HashMap<String, Object> list = result.getResponse();
		
		modelAndView.addObject("result", list.get("inventoryTransferList"));

		final String uriFacility = env.getProperty("find.store.facility");
		
		//params.put("searchByOwnerPartyId", "admin");
		HttpEntity requestStore = new HttpEntity(params, headers);
		HashMapResponse result1 = restTemplate.postForObject(uriFacility, requestStore, HashMapResponse.class);
		
		HashMap<String, Object> list1 = result1.getResponse();
		String transferComplete = env.getProperty("ajax.inventory.complete");
		String inventoryTransferSearch = env.getProperty("ajax.inventory.transfer.search");
		String listProduct = env.getProperty("ajax.createnew.inventory.transfer");
		session.setAttribute("transferComplete", transferComplete);
		session.setAttribute("inventoryTransferSearch", inventoryTransferSearch);
		session.setAttribute("listProduct", listProduct);
		modelAndView.addObject("result1", list1.get("productStoreList"));
		modelAndView.setViewName("storetransfer-out-list");
		return modelAndView;
	}

	@RequestMapping(value = "/filter/storetransferoutlist", method = RequestMethod.GET)
	public ResponseEntity<?> filterInventoryTransfer(@RequestParam("json") String json) {
		final String uri = env.getProperty("storetransferoutlist");
		Hashtable<String, String> params = new Hashtable<String, String>();

		ObjectMapper mapper = new ObjectMapper();

		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
			
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

		HashMap<String, Object> list = result.getResponse();
		System.out.println("result-----" + list.get("inventoryTransferList").toString());

		return new ResponseEntity<>(list.get("inventoryTransferList"), HttpStatus.OK);
	}

	@RequestMapping(value = "/findFacility", method = RequestMethod.GET)
	public ModelAndView findFacility() {
		final String uri = env.getProperty("find.facility");
		Hashtable<String, String> params = new Hashtable<String, String>();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		System.out.println("in find product----");
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		System.out.println("Result is -------" + result.toString());
		ModelAndView modelAndView = new ModelAndView();
		HashMap<String, Object> list = result.getResponse();
		System.out.println("result-----" + list.get("facilityList").toString());
		modelAndView.addObject("result", list.get("facilityList"));
		modelAndView.setViewName("interdepatment-transfer");
		return modelAndView;
	}
	
	@RequestMapping(value = "/createStoreTransfer", method = RequestMethod.GET)
	public ModelAndView createStoreTransfer(HttpSession session) throws Exception {

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

		final String uriFacility = env.getProperty("find.store.facility");
		params.put("searchByOwnerPartyId", "admin");
		HttpEntity requestStore = new HttpEntity(params, headers);
		HashMapResponse result1 = restTemplate.postForObject(uriFacility, requestStore, HashMapResponse.class);
//		System.out.println("Result 1 is -------" + result1.toString());
		HashMap<String, Object> list1 = result1.getResponse();
//		System.out.println("result 1-----" + list1.get("facilityList").toString());
		String facilityId = getFacilityId();
		modelAndView.addObject("facilityId",getFacilityId());
		modelAndView.addObject("storeName",getStoreName());
		modelAndView.addObject("result1", list1.get("productStoreList"));

		modelAndView.addObject("partyId", SecurityContextHolder.getContext().getAuthentication().getName());
		String inventoryItemSearchUrl = env.getProperty("ajax.inventory.search");
        String inventoryTransferCreate = env.getProperty("ajax.inventory.create");
        
		session.setAttribute("findInventoryItemUrl", inventoryItemSearchUrl);
        session.setAttribute("inventoryTransfer", inventoryTransferCreate);
		modelAndView.setViewName("create-store-transfer");
		return modelAndView;
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
}
