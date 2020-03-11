package com.retail.bo.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.bo.util.HashMapResponse;

@Controller
public class Credit extends BaseController {
	
	@Autowired
    private ApplicationContext context;
	
	@Autowired
	private Environment env;
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
    private HttpSession httpSession;
	
	@GetMapping(value = "/credit")
	public ModelAndView test()
	{
		final String uri = env.getProperty("getCustomerCreditTxns");
		ModelAndView modelAndView = new ModelAndView();
		
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("customerId", "");
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			HttpEntity request = new HttpEntity(params, headers);
			
			RestTemplate restTemplate = new RestTemplate();
			HashMapResponse customerCreditTxns = restTemplate.postForObject(uri, request, HashMapResponse.class);
			
			HashMap<String, Object> list = customerCreditTxns.getResponse();
			
			modelAndView.addObject("customerCreditTxnList", list.get("customerCreditTxnList"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		modelAndView.setViewName("credit/credit");
		return modelAndView; 
		
	}
	@SuppressWarnings("null")
	@GetMapping(value = "/setlimit")
	public ModelAndView setlimit()
	{
		ModelAndView modelAndView = new ModelAndView();
		//final String uri = env.getProperty("findCustomerBO");
		final String uri = env.getProperty("findPosCustomers");
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			Map<Object, Object> custParams = new HashMap<>();
			custParams.put("contactNumber", "");
			HttpEntity custRequest = new HttpEntity(custParams, headers);
			ResponseEntity response = restTemplate.postForEntity(uri, custRequest, List.class);
			
			modelAndView.addObject("CustomerList", response.getBody());
		} catch (Exception e) {
			// TODO: handle exception
		}
		modelAndView.setViewName("credit/setlimit");
		return modelAndView; 
		
	}
	@GetMapping(value = "/creditview")
	public ModelAndView creditview(@RequestParam("customerId") String customerId)
	{
		final String uri = env.getProperty("getCustomerCreditTakenTxns");
		ModelAndView modelAndView = new ModelAndView();
		
		Hashtable<String, Object> customerCreditTakenParams = new Hashtable<String, Object>();
		customerCreditTakenParams.put("customerId", customerId);
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			HttpEntity customerCreditTakenRequest = new HttpEntity(customerCreditTakenParams, headers);
			HashMapResponse customerCreditTakenResult = restTemplate.postForObject(uri,
					customerCreditTakenRequest, HashMapResponse.class);
			HashMap<String, Object> list = customerCreditTakenResult.getResponse();
			modelAndView.addObject("customerCreditTakenTxnList", list.get("customerCreditTakenTxnList"));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		modelAndView.setViewName("credit/creditview");
		return modelAndView; 
		
	}
	@GetMapping(value = "/creditedit")
	public ModelAndView creditedit(@RequestParam("customerId") String customerId)
	{
		ModelAndView modelAndView = new ModelAndView();
		final String customerTotCreditAmtUri = env.getProperty("getCustomerTotalCreditAmt");
		final String customerTotCreditDueUri = env.getProperty("getCustomerTotalCreditDue");
		
		Hashtable<String, Object> customerTotCreditAmtParams = new Hashtable<String, Object>();
		customerTotCreditAmtParams.put("customerId", customerId);
		
		Hashtable<String, Object> customerTotCreditDueParams = new Hashtable<String, Object>();
		customerTotCreditDueParams.put("customerId", customerId);
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			HttpEntity customerTotCreditAmtRequest = new HttpEntity(customerTotCreditAmtParams, headers);
			HashMapResponse customerTotCreditAmtResult = restTemplate.postForObject(customerTotCreditAmtUri,
					customerTotCreditAmtRequest, HashMapResponse.class);
			HashMap<String, Object> list = customerTotCreditAmtResult.getResponse();
			modelAndView.addObject("totCreditAmt", list.get("totCreditAmt"));
			
			HttpEntity customerTotCreditDueRequest = new HttpEntity(customerTotCreditDueParams, headers);
			HashMapResponse customerTotCreditDueResult = restTemplate.postForObject(customerTotCreditDueUri,
					customerTotCreditDueRequest, HashMapResponse.class);
			HashMap<String, Object> creditDuelist = customerTotCreditDueResult.getResponse();
			modelAndView.addObject("totCreditDueAmt", creditDuelist.get("totCreditDueAmt"));
			
			modelAndView.addObject("customerId", customerId);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		modelAndView.setViewName("credit/creditedit");
		return modelAndView; 
	}
	/**
     * Method Name: createPosCustomerCredit 
     * Method Description: Method for capturing Customer Paid Credit Txns
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "createPosCustomerCredit", method = RequestMethod.GET)
	public ResponseEntity<?> createPosCustomerCredit(@RequestParam("json") String json,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
    	
    	final String uri = env.getProperty("createPosCustomerCredit");
    	ObjectMapper mapper = new ObjectMapper();
    	String productStoreId = getStoreId();
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		Map<String, String> inputMap = new HashMap<String, String>();
		String creditId  = "";
		HashMap<String, Object> list = new HashMap<String, Object>();
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		params.put("productStoreId", productStoreId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);

		String jsonString = "";
		try {
			list = result.getResponse();
            creditId = (String) list.get("creditId");
            if(creditId.equals(null) || creditId.equals("") || creditId.equals(" ")) {
            	return new ResponseEntity<>(jsonString, HttpStatus.BAD_REQUEST);
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
		//return new ModelAndView("rate/create-rate-adjustment", modelAndView.getModel());
	}
	/**
     * Method Name: updateCreditLimit 
     * Method Description: Method for Updating Customer Credit Limit
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "updateCreditLimit", method = RequestMethod.GET)
	public ResponseEntity<?> updateCreditLimit(@RequestParam("json") String json,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
    	
    	final String uri = env.getProperty("updateCreditLimit");
    	ObjectMapper mapper = new ObjectMapper();
    	String productStoreId = getStoreId();
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		Map<String, String> inputMap = new HashMap<String, String>();
		String partyId  = "";
		HashMap<String, Object> list = new HashMap<String, Object>();
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);

		String jsonString = "";
		try {
			list = result.getResponse();
			partyId = (String) list.get("partyId");
            if(partyId.equals(null) || partyId.equals("") || partyId.equals(" ")) {
            	return new ResponseEntity<>(jsonString, HttpStatus.BAD_REQUEST);
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
		//return new ModelAndView("rate/create-rate-adjustment", modelAndView.getModel());
	}
}
