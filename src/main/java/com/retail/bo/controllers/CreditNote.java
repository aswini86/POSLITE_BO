package com.retail.bo.controllers;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.bo.services.RestClient;
import com.retail.bo.util.HashMapResponse;

@Controller
public class CreditNote extends BaseController {
	
	@Autowired
    private ApplicationContext context;
	
	@Autowired
	private Environment env;
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
    private HttpSession httpSession;
	
	@GetMapping(value = "/creditNote")
	public ModelAndView creditnote()
	{
		ModelAndView modelAndView = new ModelAndView();
		//getCustomerCreditNoteTxns
		final String uri = env.getProperty("getCustomerCreditNoteTxns");
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("customerId", "");
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			HttpEntity request = new HttpEntity(params, headers);
			
			RestTemplate restTemplate = new RestTemplate();
			HashMapResponse creditNoteResult = restTemplate.postForObject(uri, request, HashMapResponse.class);
			HashMap<String, Object> list = creditNoteResult.getResponse();
			
			modelAndView.addObject("customerCreditNoteTxnList", list.get("customerCreditNoteTxnList"));
		} catch(Exception e) {
			
		}
		modelAndView.setViewName("creditNote/creditNote");
		return modelAndView; 
		
	}
	@GetMapping(value = "/issueCreditNoteSummary")
	public ModelAndView issueCreditNoteSummary()
	{
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("creditNote/issue-credit-note-bill-summary");
		return modelAndView; 
		
	}
	@GetMapping(value = "/issueCreditNote")
	public ModelAndView issueCreditNote(@RequestParam("receiptId") String receiptId)
	{
		ModelAndView modelAndView = new ModelAndView();
		final String uri = env.getProperty("getBillItems");
		String productStoreGroupId = getProductStoreGroupId();
		String currencyUomId = getCurrencyUomId();
		//code to get bill items
		
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		//Map<String, Object> getBillItemRequest = new HashMap<String, Object>();
		Map<String, Object> getBillItemResponse = new HashMap<String, Object>();
		
		params.put("receiptId", receiptId);
		params.put("productStoreGroupId", productStoreGroupId);
		params.put("currencyUomId", currencyUomId);
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			HttpEntity request = new HttpEntity(params, headers);
			
			RestTemplate restTemplate = new RestTemplate();
			HashMapResponse billItemResult = restTemplate.postForObject(uri, request, HashMapResponse.class);
			//Map<String,Object> billItemList = (LinkedHashMap<String,Object>) getBillItemResponse.get("responseBody");
			HashMap<String, Object> list = billItemResult.getResponse();
			
			modelAndView.addObject("posCartItemList", list.get("productListMap"));
			modelAndView.addObject("receiptId", receiptId);
			modelAndView.addObject("customerId", list.get("customerId"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ModelAndView("creditNote/issue-credit-note", modelAndView.getModel());
		
	}
	
	
	/**
     * MethodName: issueCreditNote
     * Method Description: Issue credit Note
     */
    @RequestMapping(value = "/issueCreditNoteToCustomer", method = RequestMethod.GET)
    public ResponseEntity<?> issueCreditNoteToCustomer(@RequestParam("json") String json, 
    		@RequestParam("password") String password,
            @RequestParam("username") String username) {
        final String uri = env.getProperty("createIssueCreditNote");
        Hashtable<String, String> params = new Hashtable<String, String>();
        Hashtable<String, String> issueCNparams = new Hashtable<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        String facilityId = getFacilityId();
        String productStoreGroupId = getProductStoreGroupId();
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        String selectedProductId = "", totalPrice = "", returnQty = "", prdMrp = "";
        String productId = "",returnQuantity = "", totalPrices = "", billId = "", customerId = "", productPrice = "";
        String creditId = "";
        try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            selectedProductId = params.get("productIdArray");
            totalPrice = params.get("totalPriceArray");
            returnQty = params.get("returnQtyArray");
            billId = params.get("receiptId");
            prdMrp = params.get("productMrpArray");
            customerId = params.get("customerId");
            
            String [] productIdArray = selectedProductId.split(",");
            String [] totalPriceArray = totalPrice.split(",");
            String [] returnQtyArray = returnQty.split(",");
            String [] productMrpArray = prdMrp.split(",");
            
            int length = productIdArray.length;
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            
            for (int i=0; i < length; i++) {
            	
            	productId = productIdArray[i];
            	returnQuantity = returnQtyArray[i];
            	totalPrices = totalPriceArray[i];
            	productPrice = productMrpArray[i];
            	
            	issueCNparams.put("facilityId", facilityId);
            	issueCNparams.put("productId", productId);
            	issueCNparams.put("billId", billId);
            	issueCNparams.put("customerId", customerId);
            	issueCNparams.put("returnQuantity", returnQuantity);
            	issueCNparams.put("productPrice", productPrice);
            	issueCNparams.put("creditNoteAmount", totalPrices);
            	issueCNparams.put("productStoreId", productStoreId);
            	issueCNparams.put("customer", "0");
            	issueCNparams.put("retailer", "1");
            	issueCNparams.put("type", "CREDIT_NOTE");
            	
            	issueCNparams.put("login.username", username);
            	issueCNparams.put("login.password", password);
            	HttpEntity request = new HttpEntity(issueCNparams, headers);
            	RestTemplate restTemplate = new RestTemplate();
                HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
                System.out.println("Result is -------" + result.toString());
                HashMap<String, Object> list = result.getResponse();
                creditId = (String) list.get("creditId");
                if(creditId.equals(null) || creditId.equals("") || creditId.equals(" ")) {
                	return new ResponseEntity<>(jsonString, HttpStatus.BAD_REQUEST);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            jsonString = mapper.writeValueAsString(creditId);
            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }

}
