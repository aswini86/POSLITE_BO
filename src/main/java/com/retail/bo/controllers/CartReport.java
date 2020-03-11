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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.bo.services.RestClient;
import com.retail.bo.forms.beans.ItemFormBean;
import com.retail.bo.model.Item;

@Controller
public class CartReport extends BaseController{

	
	@Autowired
    private ApplicationContext context;
	
	@Autowired
	private Environment env;
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
    private HttpSession httpSession;
	
	
	@GetMapping("posmartCashReport")
    public ModelAndView posmartCashReport() {
    	
    	ModelAndView modelAndView = new ModelAndView();
    	final String uri = env.getProperty("posmartCashReport");
		
		Map<Object, Object> cartSummaryParams = new HashMap<>();
		cartSummaryParams.put("contactNumber", "");
		
		try {
			//Get Cart Summary
			Map<String, Object> posSummaryRequest = new HashMap<String, Object>();
			Map<String, Object> posSummaryResponse = new HashMap<String, Object>();
			
			posSummaryRequest.put("posTerminalId", "");
			posSummaryRequest.put("fromDate", "");
			posSummaryRequest.put("thruDate", "");
			
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(posSummaryRequest);
			posSummaryResponse = restClient.callRetailService("posmartCashReport", true);
			Map<String,Object> posSummaryList = (LinkedHashMap<String,Object>) posSummaryResponse.get("responseBody");
			if (!posSummaryList.isEmpty()) {
				Object posCartSummary = (Object) posSummaryList.get("posCartSummary");
				List<Object> posCartSummaryMapList = (ArrayList<Object>) posSummaryList.get("posCartSummaryList");
				
				Object posCartSummaryList = (Object) posCartSummaryMapList;
				modelAndView.addObject("posCartSummary", posCartSummary);
				modelAndView.addObject("posCartSummaryList", posCartSummaryList);
			}
		} catch (Exception e) {
			
		}
		
		return new ModelAndView("cart/summary", modelAndView.getModel()); 
    }
	@GetMapping("getPosmartCashReport")
    public ResponseEntity<?> getPosmartCashReport(@RequestParam("json") String reportjson,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
    	
		String jsonString = "";
    	final String uri = env.getProperty("posmartCashReport");
    	Hashtable<String, Object> reportCredParams = new Hashtable<String, Object>();
		Hashtable<String, Object> reportRequestParams = new Hashtable<String, Object>();
		
		Map<Object, Object> cartSummaryParams = new HashMap<>();
		cartSummaryParams.put("contactNumber", "");
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			reportRequestParams = mapper.readValue(reportjson, new TypeReference<Hashtable<String,Object>>() {
			});
			reportCredParams.put("login.username", username);
			reportCredParams.put("login.password", password);
			
			//Get Cart Summary
			Map<String, Object> posSummaryRequest = new HashMap<String, Object>();
			Map<String, Object> posSummaryResponse = new HashMap<String, Object>();
			
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(reportCredParams);
			restClient.setRequestBody(reportRequestParams);
			posSummaryResponse = restClient.callRetailService("posmartCashReport", true);
			Map<String,Object> posSummaryList = (LinkedHashMap<String,Object>) posSummaryResponse.get("responseBody");
			if (!posSummaryList.isEmpty()) {
				
				Object posCartSummary = (Object) posSummaryList.get("posCartSummary");
				List<Object> posCartSummaryMapList = (ArrayList<Object>) posSummaryList.get("posCartSummaryList");
				
				Object posCartSummaryList = (Object) posCartSummaryMapList;
				jsonString = mapper.writeValueAsString(posSummaryList);
			}
		} catch (Exception e) {
			
		}
		
		return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
	@GetMapping("getPosmartSummaryCashReport")
    public ResponseEntity<?> getPosmartSummaryCashReport(@RequestParam("json") String reportjson,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
    	
		String jsonString = "";
    	Hashtable<String, Object> reportCredParams = new Hashtable<String, Object>();
		Hashtable<String, Object> reportRequestParams = new Hashtable<String, Object>();
		
		Map<Object, Object> cartSummaryParams = new HashMap<>();
		cartSummaryParams.put("contactNumber", "");
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			reportRequestParams = mapper.readValue(reportjson, new TypeReference<Hashtable<String,Object>>() {
			});
			reportCredParams.put("login.username", username);
			reportCredParams.put("login.password", password);
			
			//Get Cart Summary
			Map<String, Object> posSummaryRequest = new HashMap<String, Object>();
			Map<String, Object> posSummaryResponse = new HashMap<String, Object>();
			
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(reportCredParams);
			restClient.setRequestBody(reportRequestParams);
			posSummaryResponse = restClient.callRetailService("getPosmartSummaryCashReport", true);
			Map<String,Object> posSummaryList = (LinkedHashMap<String,Object>) posSummaryResponse.get("responseBody");
			if (!posSummaryList.isEmpty()) {
				
				Object posCartSummary = (Object) posSummaryList.get("posCartSummary");
				List<Object> posCartSummaryMapList = (ArrayList<Object>) posSummaryList.get("posCartSummaryList");
				
				Object posCartSummaryList = (Object) posCartSummaryMapList;
				jsonString = mapper.writeValueAsString(posSummaryList);
			}
		} catch (Exception e) {
			
		}
		
		return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
	
	@GetMapping("PosbillSummary")
    public ModelAndView PosbillSummary() {
    	
    	ModelAndView modelAndView = new ModelAndView();
    	final String uri = env.getProperty("billSummary");
		
		Map<Object, Object> cartSummaryParams = new HashMap<>();
		cartSummaryParams.put("contactNumber", "");
		
		try {
			//Get Cart Summary
			Map<String, Object> posSummaryRequest = new HashMap<String, Object>();
			Map<String, Object> posSummaryResponse = new HashMap<String, Object>();
			
			posSummaryRequest.put("customerMobileNo", "");
			posSummaryRequest.put("fromDate", "");
			posSummaryRequest.put("thruDate", "");
			
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(posSummaryRequest);
			posSummaryResponse = restClient.callRetailService("billSummary", true);
			Map<String,Object> posSummaryList = (LinkedHashMap<String,Object>) posSummaryResponse.get("responseBody");
			if (!posSummaryList.isEmpty()) {
				Object posCartSummary = (Object) posSummaryList.get("posCartSummary");
				List<Object> posCartSummaryMapList = (ArrayList<Object>) posSummaryList.get("posCartSummaryList");
				
				Object posCartSummaryList = (Object) posCartSummaryMapList;
				modelAndView.addObject("posCartSummaryList", posCartSummaryList);
			}
		} catch (Exception e) {
			
		}
		
		return new ModelAndView("cart/bill-summary", modelAndView.getModel()); 
    }
	@GetMapping("customerReport")
    public ModelAndView customerReport() {
    	
    	ModelAndView modelAndView = new ModelAndView();
    	final String uri = env.getProperty("customerReport");
		
		Map<Object, Object> cartSummaryParams = new HashMap<>();
		cartSummaryParams.put("contactNumber", "");
		
		try {
			List<Object> productStores = findStores();
            modelAndView.addObject("productStoreList", productStores);
            
			//Get Cart Summary
			Map<String, Object> posSummaryRequest = new HashMap<String, Object>();
			Map<String, Object> posSummaryResponse = new HashMap<String, Object>();
			
			posSummaryRequest.put("posTerminalId", "");
			posSummaryRequest.put("fromDate", "");
			posSummaryRequest.put("thruDate", "");
			
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(posSummaryRequest);
			posSummaryResponse = restClient.callRetailService("customerReport", true);
			Map<String,Object> posSummaryList = (LinkedHashMap<String,Object>) posSummaryResponse.get("responseBody");
			if (!posSummaryList.isEmpty()) {
				List<Object> posCartCustomerList = (ArrayList<Object>) posSummaryList.get("posCartCustomerList");
				
				modelAndView.addObject("posCartCustomerList", posCartCustomerList);
			}
		} catch (Exception e) {
			
		}
		
		return new ModelAndView("cart/customerReport", modelAndView.getModel()); 
    }
	
	@GetMapping("ExpiryDate")
	public ModelAndView ExpiryDate() {
	Map<String, Object> model = new HashMap<>();
	        ItemFormBean itemFormBean = new ItemFormBean();
	        model.put("itemFormBean", itemFormBean);
	       
	ModelAndView modelAndView = new ModelAndView();
	final String uri = env.getProperty("expiredProductReport");
	String productStoreGroupId = getProductStoreGroupId();
	String currencyUomId = getCurrencyUomId();

	Map<String, Object> expReportRequest = new HashMap<String, Object>();
	Map<String, Object> expiredReportResponse = new HashMap<String, Object>();
	String facilityId = getFacilityId();
	expReportRequest.put("facilityId", facilityId);
	expReportRequest.put("productStoreGroupId", productStoreGroupId);
	expReportRequest.put("currencyUomId", currencyUomId);
	expReportRequest.put("fromDate", "");
	expReportRequest.put("thruDate", "");
	try {
	RestClient restClient = applicationContext.getBean(RestClient.class);
	restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());

	restClient.setRequestBody(expReportRequest);
	expiredReportResponse = restClient.callRetailService("expiredProductReport", true);
	//Map<String,Object> expReportList = (LinkedHashMap<String,Object>) expiredReportResponse.get("responseBody");
	if (!expiredReportResponse.isEmpty()) {
	List<Object> expPrdsReportList = (ArrayList<Object>) expiredReportResponse.get("responseBody");

	for (int i = 0; i < expPrdsReportList.size(); i++) {
        Map<String, Object> inventoryItem = (Map) expPrdsReportList.get(i);
        Map<String, Object> productPrice = (Map) inventoryItem.get("productPrice");

        Item item = new Item();
        item.setEan(inventoryItem.get("ean").toString());
        if (inventoryItem.get("description") != null) {
            item.setDescription(inventoryItem.get("description").toString());
        }
        if (inventoryItem.get("productName") != null) {
            item.setProductName(inventoryItem.get("productName").toString());
        }
        if(inventoryItem.get("lotId") != null) {
        item.setLotId(inventoryItem.get("lotId").toString());
        }
        if(inventoryItem.get("batchNumber") != null) {
        item.setBatchNumber(inventoryItem.get("batchNumber").toString());
        }
        if(inventoryItem.get("quantityOnHandTotal") != null)
        item.setQuantity(Double.parseDouble(inventoryItem.get("quantityOnHandTotal").toString()));
        if(productPrice != null && productPrice.get("listPrice") != null)
        item.setMrp(Double.parseDouble(productPrice.get("listPrice").toString()));
        item.setCp(Double.parseDouble(inventoryItem.get("unitCost").toString()));
        if(productPrice != null && productPrice.get("defaultPrice") != null)
        item.setSp(Double.parseDouble(productPrice.get("defaultPrice").toString()));
        if (inventoryItem.get("expireDate") != null) {
            item.setExpiryDate(inventoryItem.get("expireDate").toString());
        }
        if (inventoryItem.get("manufacturingDate") != null) {
            item.setManufacturingDate(inventoryItem.get("manufacturingDate").toString());
        }
        itemFormBean.getMappedItems().add(item);
    }
	modelAndView.addObject("expPrdsReportList", expPrdsReportList);
	}
	}catch (Exception e) {

	}

	        return new ModelAndView("ExpiryDate", model);
	    }

	@GetMapping("getExpiryDate")
	public ResponseEntity<?> getExpiryDate(@RequestParam("days") String days) {
	String jsonString = "";
	Map<String, Object> model = new HashMap<>();
	        ItemFormBean itemFormBean = new ItemFormBean();
	        model.put("itemFormBean", itemFormBean);
	       
	final String uri = env.getProperty("expiredProductReport");
	String productStoreGroupId = getProductStoreGroupId();
	String currencyUomId = getCurrencyUomId();

	Map<String, Object> expReportRequest = new HashMap<String, Object>();
	Map<String, Object> expiredReportResponse = new HashMap<String, Object>();
	String facilityId = getFacilityId();
	expReportRequest.put("facilityId", facilityId);
	expReportRequest.put("days", days);
	expReportRequest.put("productStoreGroupId", productStoreGroupId);
	expReportRequest.put("currencyUomId", currencyUomId);
	expReportRequest.put("fromDate", "");
	expReportRequest.put("thruDate", "");
	try {
	ObjectMapper mapper = new ObjectMapper();
	RestClient restClient = applicationContext.getBean(RestClient.class);
	restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());

	restClient.setRequestBody(expReportRequest);
	expiredReportResponse = restClient.callRetailService("expiredProductReport", true);
	//Map<String,Object> expReportList = (LinkedHashMap<String,Object>) expiredReportResponse.get("responseBody");
	if (!expiredReportResponse.isEmpty()) {
	List<Object> expPrdsReportList = (ArrayList<Object>) expiredReportResponse.get("responseBody");

	for (int i = 0; i < expPrdsReportList.size(); i++) {
	                    Map<String, Object> inventoryItem = (Map) expPrdsReportList.get(i);
	                    Map<String, Object> productPrice = (Map) inventoryItem.get("productPrice");

	                    Item item = new Item();
	                    item.setEan(inventoryItem.get("ean").toString());
	                    if (inventoryItem.get("description") != null) {
	                        item.setDescription(inventoryItem.get("description").toString());
	                    }
	                    if (inventoryItem.get("productName") != null) {
	                        item.setProductName(inventoryItem.get("productName").toString());
	                    }
	                    if(inventoryItem.get("lotId") != null) {
	                    item.setLotId(inventoryItem.get("lotId").toString());
	                    }
	                    if(inventoryItem.get("batchNumber") != null) {
	                    item.setBatchNumber(inventoryItem.get("batchNumber").toString());
	                    }
	                    if(inventoryItem.get("quantityOnHandTotal") != null)
	                    item.setQuantity(Double.parseDouble(inventoryItem.get("quantityOnHandTotal").toString()));
	                    if(productPrice != null && productPrice.get("listPrice") != null)
	                    item.setMrp(Double.parseDouble(productPrice.get("listPrice").toString()));
	                    item.setCp(Double.parseDouble(inventoryItem.get("unitCost").toString()));
	                    if(productPrice != null && productPrice.get("defaultPrice") != null)
	                    item.setSp(Double.parseDouble(productPrice.get("defaultPrice").toString()));
	                    if (inventoryItem.get("expireDate") != null) {
	                        item.setExpiryDate(inventoryItem.get("expireDate").toString());
	                        System.out.println("This is the expiry date: ------------"+inventoryItem.get("expireDate").toString());
	                    }
	                    if (inventoryItem.get("manufacturingDate") != null) {
	                        item.setManufacturingDate(inventoryItem.get("manufacturingDate").toString());
	                    }
	                    itemFormBean.getMappedItems().add(item);
	                }
	jsonString = mapper.writeValueAsString(itemFormBean.getMappedItems());
	}
	}catch (Exception e) {

	}

	return new ResponseEntity<>(jsonString, HttpStatus.OK);
	    }
}
