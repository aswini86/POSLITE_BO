package com.retail.bo.controllers;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.context.ApplicationContext;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.bo.services.RestClient;
import com.retail.bo.util.HashMapResponse;

@Controller
public class Rate extends BaseController{
	
	@Autowired
    private ApplicationContext context;
	
	@Autowired
	private Environment env;
	
	/**
     * Method Name: createRateAdjustment
     * Method Description: Url to create RateAdjustment
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "createRateAdjustment", method = RequestMethod.GET)
	public ModelAndView createRateAdjustment() {
    	String productId = "";
    	String productPrice = "";
    	Map<String,String> productMap = new HashMap<String, String>();
    	Map<String,Double> productSaleMap = new HashMap<String, Double>();
		ArrayList<String> productIdArray = new ArrayList<String>();
		Map<String, Object> response = new HashMap<String, Object>();
		String finalPrdIdArray = new String();
    	ModelAndView modelAndView = new ModelAndView();
        String productStoreGroupId = getProductStoreGroupId();
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        RestClient restClient = applicationContext.getBean(RestClient.class);
    	
		final String uri = env.getProperty("findRateProductPrice");
		final String categoryUri = env.getProperty("findStoreCategory");
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		Hashtable<String, Object> catParams = new Hashtable<String, Object>();
		
		HttpHeaders headers = new HttpHeaders();
		params.put("currencyUomId", currencyUomId);
		params.put("productStoreGroupId", productStoreGroupId);
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		HashMap<String, Object> list = result.getResponse();
		
		HttpHeaders categoryHeaders = new HttpHeaders();
		catParams.put("productStoreId", productStoreId);
		HttpEntity categoryRequest = new HttpEntity(catParams, headers);
		
		HashMapResponse catResult = restTemplate.postForObject(categoryUri, categoryRequest, HashMapResponse.class);
		HashMap<String, Object> catList = catResult.getResponse();

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			List<HashMap> listMap  = (List<HashMap>) list.get("finalProductPriceInfoList");
			
			Integer rateAdjustmentTransId = createNewRateAdjTransactionId();
			//
			/*
			 * ArrayList<String> prdIds = (ArrayList<String>) list.get("productIdArray"); if
			 * (!prdIds.isEmpty()) { for (HashMap prdListEach : listMap) { productId =
			 * (String) prdListEach.get("productId");
			 * 
			 * RestClient prestClient = applicationContext.getBean(RestClient.class);
			 * prestClient.addAuthentication(SecurityContextHolder.getContext().
			 * getAuthentication()); prestClient.addRequestParameter("productId",
			 * productId); prestClient.addRequestParameter("productStoreGroupId",
			 * getProductStoreGroupId()); prestClient.addRequestParameter("currencyUomId",
			 * getCurrencyUomId()); response =
			 * restClient.callRetailService("getProductByProductId", true);
			 * 
			 * } }
			 */
			String prdId = list.get("productIdArray").toString();
			finalPrdIdArray = prdId.substring(1, prdId.length()-1);
			
			modelAndView.addObject("productIdArray",finalPrdIdArray);
			modelAndView.addObject("rateAdjustmentTransId", rateAdjustmentTransId);
			modelAndView.addObject("productsList", list.get("finalProductPriceInfoList"));
			
			jsonString = mapper.writeValueAsString(list.get("finalProductPriceInfoList"));
			//modelAndView.addObject("categoryList", getCategory());
			modelAndView.addObject("categoryList", catList.get("categoryList"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ModelAndView("rate/create-rate-adjustment", modelAndView.getModel());
	}
	
	/**
     * Method Name: approveRateAdjustment
     * Method Description: Url to Approve RateAdjustment
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "approveRateAdjustmentList", method = RequestMethod.GET)
	public ModelAndView approveRateAdjustmentList(String productPriceTxnId) {
    	String productId = "";
    	String productPrice = "";
    	Map<String,String> productMap = new HashMap<String, String>();
    	Map<String,Double> productSaleMap = new HashMap<String, Double>();
		ArrayList<String> productIdArray = new ArrayList<String>();
		Map<String, Object> response = new HashMap<String, Object>();
		String finalPrdIdArray = new String();
		String finalRatePerArray = new String();
    	ModelAndView modelAndView = new ModelAndView();
        String productStoreGroupId = getProductStoreGroupId();
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        RestClient restClient = applicationContext.getBean(RestClient.class);
    	
		final String uri = env.getProperty("findRateAdjustmentTrx");
		final String categoryUri = env.getProperty("findStoreCategory");
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		Hashtable<String, Object> catParams = new Hashtable<String, Object>();
		
		HttpHeaders headers = new HttpHeaders();
		params.put("currencyUomId", currencyUomId);
		params.put("productStoreId", productStoreId);
		params.put("productPriceTxnId", productPriceTxnId);
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		HashMap<String, Object> list = result.getResponse();
		
		HttpHeaders categoryHeaders = new HttpHeaders();
		catParams.put("productStoreId", productStoreId);
		HttpEntity categoryRequest = new HttpEntity(catParams, headers);
		
		HashMapResponse catResult = restTemplate.postForObject(categoryUri, categoryRequest, HashMapResponse.class);
		HashMap<String, Object> catList = catResult.getResponse();

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			List<HashMap> listMap  = (List<HashMap>) list.get("finalProductPriceInfoList");
			
			//Integer rateAdjustmentTransId = createNewRateAdjTransactionId();
			Integer rateAdjustmentTransId = Integer.parseInt(productPriceTxnId);
			String prdId = list.get("productIdArray").toString();
			finalPrdIdArray = prdId.substring(1, prdId.length()-1);
			
			String ratePerArray = list.get("ratePercentageArray").toString();
			finalRatePerArray = ratePerArray.substring(1, ratePerArray.length()-1);
			
			modelAndView.addObject("ratePercentageArray",finalRatePerArray);
			modelAndView.addObject("productIdArray",finalPrdIdArray);
			modelAndView.addObject("rateAdjustmentTransId", rateAdjustmentTransId);
			modelAndView.addObject("productsList", list.get("finalProductPriceInfoList"));
			modelAndView.addObject("productPriceTxnId",productPriceTxnId);
			
			jsonString = mapper.writeValueAsString(list.get("finalProductPriceInfoList"));
			//modelAndView.addObject("categoryList", getCategory());
			modelAndView.addObject("categoryList", catList.get("categoryList"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ModelAndView("rate/approve-rate-adjustment", modelAndView.getModel());
	}
    
	@RequestMapping(value = "approveRateAdjustmentTxn", method = RequestMethod.GET)
    public ResponseEntity<?> approveRateAdjustmentTxn(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username,
    		@RequestParam("productIdArray") String[] productIdArray,
    		@RequestParam("ratePerArray") String[] ratePerArray) {
    	
    	ModelAndView modelAndView = new ModelAndView();
    	String successMessage = "Product Price Rule Approved Successfully";
    	Timestamp fromDate = null;
    	ObjectMapper mapper = new ObjectMapper();
		String jsonString = "", remark = "",amountval = "0.0", rateFromDate = "";
		String productPriceTxnId = "";
		Hashtable<String, String> params = new Hashtable<String, String>();
		String productPriceRuleId = "";
		Double rateAmt = 0.0;
		ArrayList<String> productIdArrays = new ArrayList<String>();
		ArrayList<String> ratePerArrays = new ArrayList<String>();
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    	final String approveProductPriceTxnUri = env.getProperty("approveProductPriceTxn");
    	Hashtable<String, Object> approveProductPriceTxnParams = new Hashtable<String, Object>();
    	
    	final String saveUpdateProductPriceTxnUri = env.getProperty("saveUpdateProductPriceTxn");
    	Hashtable<String, Object> saveUpdateProductPriceTxnParams = new Hashtable<String, Object>();
    	
    	try {
    		
    		if (params.get("amountval") != null) {
    			amountval = params.get("amountval");
    		}
    		rateFromDate = params.get("rateFromDate");
    		//rateAmt = Double.valueOf(amountval);
    		Date date = new Date();
    		fromDate = new Timestamp(date.getTime());
    		if(rateFromDate != null) {
    			fromDate = convertStringToTimestamp(rateFromDate);
    		}
    		remark = params.get("remark");
    		productPriceTxnId = params.get("productPriceTxnId");
    		
    		HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
    		if(productIdArray != null) {
    			for (int i=0; i < productIdArray.length; i++) {
    				productIdArrays.add(productIdArray[i]);
    			}
    			for (int i=0; i < ratePerArray.length; i++) {
    				ratePerArrays.add(ratePerArray[i]);
    			}
    			String prdIds = productIdArrays.toString();
    			String finalPrdIdArray = prdIds.substring(1, prdIds.length()-1);
    			
    			String ratePerVal = ratePerArrays.toString();
    			String finalRatePerArray = ratePerVal.substring(1, ratePerVal.length()-1);
    			//check for product rateAdjustment exists
    			//for (int i=0; i < productIdArray.length; i++) {
    				//if rateAdjustment exists update txn
    				saveUpdateProductPriceTxnParams.put("productPriceRuleTxId", productPriceTxnId);
    				saveUpdateProductPriceTxnParams.put("finalPrdIdArray", finalPrdIdArray);
    				saveUpdateProductPriceTxnParams.put("finalRatePerArray", finalRatePerArray);
    				//saveUpdateProductPriceTxnParams.put("ruleName", "STS"+productIdArray[i]);
    				saveUpdateProductPriceTxnParams.put("description", remark);
    				saveUpdateProductPriceTxnParams.put("fromDate", fromDate);
    				saveUpdateProductPriceTxnParams.put("status", "APPROVED");
    				saveUpdateProductPriceTxnParams.put("createdBy", username);
        			//approveProductPriceTxnParams.put("amount", rateAmt);
    				saveUpdateProductPriceTxnParams.put("inputParamEnumId", "PRIP_PRODUCT_ID");
    				saveUpdateProductPriceTxnParams.put("operatorEnumId", "PRC_EQ");
    				//saveUpdateProductPriceTxnParams.put("condValue", productIdArray[i]);
    				saveUpdateProductPriceTxnParams.put("productPriceActionTypeId", "PRICE_POL");
        			
        			HttpEntity saveUpdateRateAdjRequest = new HttpEntity(saveUpdateProductPriceTxnParams, headers);
    				
    				RestTemplate saveUpdateRateAdjRestTemplate = new RestTemplate();
    				HashMapResponse saveUpdateRateAdjResult = saveUpdateRateAdjRestTemplate.postForObject(saveUpdateProductPriceTxnUri, saveUpdateRateAdjRequest, HashMapResponse.class);
    				HashMap<String, Object> saveUpdateRateAdjlist = saveUpdateRateAdjResult.getResponse();
    			//}
    			
    			
    				//else create rateAdjustment txn
    			
    				//Approve rateAdjustment
    			
    				approveProductPriceTxnParams.put("productPriceTxnId", productPriceTxnId);
        			HttpEntity request = new HttpEntity(approveProductPriceTxnParams, headers);
    				
    				RestTemplate restTemplate = new RestTemplate();
    				HashMapResponse result = restTemplate.postForObject(approveProductPriceTxnUri, request, HashMapResponse.class);
    				HashMap<String, Object> list = result.getResponse();
    				
    				productPriceRuleId = (String) list.get("productPriceTxnId");
    				jsonString = mapper.writeValueAsString(productPriceRuleId);
    		}
    	} catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    	
    	return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
	
    @RequestMapping(value = "approveRateAdjustment", method = RequestMethod.GET)
    public ResponseEntity<?> approveRateAdjustment(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username,
    		@RequestParam("productIdArray") String[] productIdArray) {
    	
    	ModelAndView modelAndView = new ModelAndView();
    	String successMessage = "Product Price Rule Created Successfully";
    	Timestamp fromDate = null;
    	ObjectMapper mapper = new ObjectMapper();
		String jsonString = "", remark = "",amountval = "0.0", rateFromDate = "";
		String productPriceTxnId = "";
		Hashtable<String, String> params = new Hashtable<String, String>();
		String productPriceRuleId = "";
		ArrayList<String> productIdArrays = new ArrayList<String>();
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
    	final String createPrdPriceRuleUri = env.getProperty("createProductPriceRule");
    	Hashtable<String, Object> prdPriceRuleParams = new Hashtable<String, Object>();
    	final String createProductPriceCondUri = env.getProperty("createProductPriceCond");
    	Hashtable<String, Object> prdPriceCondParams = new Hashtable<String, Object>();
    	final String createProductPriceActionUri = env.getProperty("createProductPriceAction");
    	Hashtable<String, Object> prdPriceActionParams = new Hashtable<String, Object>();
    	
    	try {
    		if (params.get("amountval") != null) {
    			amountval = params.get("amountval");
    		}
    		rateFromDate = params.get("rateFromDate");
    		Double rateAmt = Double.valueOf(amountval);
    		Date date = new Date();
    		fromDate = new Timestamp(date.getTime());
    		if(rateFromDate != null) {
    			fromDate = convertStringToTimestamp(rateFromDate);
    		}
    		remark = params.get("remark");
    		productPriceTxnId = params.get("productPriceTxnId");
    		
    		if(productIdArray != null) {
    			//update Rate Adjustment
    			for (int i=0; i < productIdArray.length; i++) {
    				productIdArrays.add(productIdArray[i]);
    			}
    			HttpHeaders headers = new HttpHeaders();
    			headers.add("Content-Type", "application/json");
    			final String uri = env.getProperty("performFind");
				Hashtable<String, Object> checkTxnParams = new Hashtable<String, Object>();
				HashMap<String, Object> prdPriceMap = new HashMap<String, Object>();
				prdPriceMap.put("productPriceRuleTxId", productPriceTxnId);
				checkTxnParams.put("entityName", "ProductPriceTrx");
				checkTxnParams.put("noConditionFind", "Y");
				checkTxnParams.put("inputFields", prdPriceMap);
				HttpEntity checkPriceRuleTxn = new HttpEntity(checkTxnParams, headers);
				
				RestTemplate cprRestTemplate = new RestTemplate();
				HashMapResponse cprResult = cprRestTemplate.postForObject(uri, checkPriceRuleTxn, HashMapResponse.class);
				HashMap<String, Object> cprList = cprResult.getResponse();
				
    			String productPriceRuleOp = updateRateAdjustmentTrx(productPriceTxnId, productIdArrays, remark, fromDate,
						username, rateAmt, cprList);
    			jsonString = mapper.writeValueAsString(productPriceRuleOp);
    			//Code to save newly added products in Approval RateAdjustment screen. 
    			for (int i=0; i < productIdArray.length; i++) {
    				//Code to create price rule.
    				prdPriceRuleParams.put("ruleName", "STS"+productIdArray[i]);
    				prdPriceRuleParams.put("description", remark);
    				prdPriceRuleParams.put("fromDate", fromDate);
    				
    				HttpEntity request = new HttpEntity(prdPriceRuleParams, headers);
    				
    				RestTemplate restTemplate = new RestTemplate();
    				HashMapResponse result = restTemplate.postForObject(createPrdPriceRuleUri, request, HashMapResponse.class);
    				HashMap<String, Object> list = result.getResponse();
    				
    				productPriceRuleId = (String) list.get("productPriceRuleId");
    				//successMessage = (String) list.get("successMessage");
    				System.out.println("successMessage====="+successMessage);
					
    	    		//Code to create price rule cond.
    				prdPriceCondParams.put("productPriceRuleId", productPriceRuleId);
    				prdPriceCondParams.put("inputParamEnumId", "PRIP_PRODUCT_ID");
    				prdPriceCondParams.put("operatorEnumId", "PRC_EQ");
    				prdPriceCondParams.put("condValue", productIdArray[i]);
    				
    				HttpEntity prdPriceCondrequest = new HttpEntity(prdPriceCondParams, headers);
    				
    				RestTemplate prdPriceCondRestTemplate = new RestTemplate();
    				HashMapResponse prdPriceCondResult = prdPriceCondRestTemplate.postForObject(createProductPriceCondUri, prdPriceCondrequest, HashMapResponse.class);
    				
    	    		//Code to create price rule action.
    				
    				prdPriceActionParams.put("productPriceRuleId", productPriceRuleId);
    				prdPriceActionParams.put("productPriceActionTypeId", "PRICE_POL");
    				prdPriceActionParams.put("amount", rateAmt);
    				
    				HttpEntity prdPriceRulerequest = new HttpEntity(prdPriceActionParams, headers);
    				
    				RestTemplate prdPriceRuleRestTemplate = new RestTemplate();
    				HashMapResponse prdPriceRuleResult = prdPriceRuleRestTemplate.postForObject(createProductPriceActionUri, prdPriceRulerequest, HashMapResponse.class);
    				HashMap<String, Object> prdPriceRulelist = prdPriceRuleResult.getResponse();
    				
    			}
    		}
    		jsonString = mapper.writeValueAsString(productPriceRuleId);
    		System.out.println("List     " + jsonString);
    		return new ResponseEntity<>(jsonString, HttpStatus.OK);
    	} catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    	
    	return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    @RequestMapping(value = "saveRateAdjustmentTrx", method = RequestMethod.GET)
    public ResponseEntity<?> saveRateAdjustmentTrx(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username,
    		@RequestParam("productIdArray") String[] productIdArray) {
    	
    	ModelAndView modelAndView = new ModelAndView();
    	String successMessage = "Product Price Rule Created Successfully";
    	Timestamp fromDate = null;
    	ObjectMapper mapper = new ObjectMapper();
		String jsonString = "", remark = "",amountval = "0.0", rateFromDate = "";
		String productPriceRuleTrxId = "";
		Hashtable<String, String> params = new Hashtable<String, String>();
		ArrayList<String> productIdArrays = new ArrayList<String>();
		String productPriceRuleId = "";
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		final String createProductPriceTrxUri = env.getProperty("createProductPriceTrx");
    	Hashtable<String, Object> prdPriceTrxParams = new Hashtable<String, Object>();
    	final String createPrdPriceRuleTrxUri = env.getProperty("createProductPriceTrxRule");
    	Hashtable<String, Object> prdPriceRuleTrxParams = new Hashtable<String, Object>();
    	final String createProductPriceCondTrxUri = env.getProperty("createProductPriceTrxCond");
    	Hashtable<String, Object> prdPriceCondTrxParams = new Hashtable<String, Object>();
    	final String createProductPriceActionTrxUri = env.getProperty("createProductPriceTrxAction");
    	Hashtable<String, Object> prdPriceActionTrxParams = new Hashtable<String, Object>();
    	
    	try {
    		if (params.get("amountval") != null) {
    			amountval = params.get("amountval");
    		}
    		rateFromDate = params.get("rateFromDate");
    		productPriceRuleTrxId = params.get("productPriceRuleTrxId");
    		Double rateAmt = Double.valueOf(amountval);
    		Date date = new Date();
    		fromDate = new Timestamp(date.getTime());
    		if(rateFromDate != null) {
    			fromDate = convertStringToTimestamp(rateFromDate);
    		}
    		remark = params.get("remark");
    		if(productIdArray != null) {
    			for (int i=0; i < productIdArray.length; i++) {
    				productIdArrays.add(productIdArray[i]);
    			}
    			HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				//check productPriceRule transaction exists
				final String uri = env.getProperty("performFind");
				Hashtable<String, Object> checkTxnParams = new Hashtable<String, Object>();
				HashMap<String, Object> prdPriceMap = new HashMap<String, Object>();
				prdPriceMap.put("productPriceRuleTxId", productPriceRuleTrxId);
				checkTxnParams.put("entityName", "ProductPriceTrx");
				checkTxnParams.put("noConditionFind", "Y");
				checkTxnParams.put("inputFields", prdPriceMap);
				HttpEntity checkPriceRuleTxn = new HttpEntity(checkTxnParams, headers);
				
				RestTemplate cprRestTemplate = new RestTemplate();
				HashMapResponse cprResult = cprRestTemplate.postForObject(uri, checkPriceRuleTxn, HashMapResponse.class);
				HashMap<String, Object> cprList = cprResult.getResponse();
				ArrayList<Object> cprArrayList = (ArrayList<Object>) cprList.get("list");
				
				if((cprArrayList.isEmpty())) {//create
					//Code to create Product Price txn
					prdPriceTrxParams.put("productPriceRuleTxId", productPriceRuleTrxId);
					prdPriceTrxParams.put("description", remark);
					prdPriceTrxParams.put("status", "CREATED");
					prdPriceTrxParams.put("createdBy", username);
					prdPriceTrxParams.put("createdDate", fromDate);
					
					HttpEntity priceTxnRequest = new HttpEntity(prdPriceTrxParams, headers);
					
					RestTemplate priceTxnRestTemplate = new RestTemplate();
					HashMapResponse priceTxnResult = priceTxnRestTemplate.postForObject(createProductPriceTrxUri, 
							priceTxnRequest, HashMapResponse.class);
				} else {//update
					successMessage = "Product Price Rule Updated Successfully";
					String productPriceRuleOp = updateRateAdjustmentTrx(productPriceRuleTrxId, productIdArrays, remark, fromDate,
							username, rateAmt, cprList);
					
					jsonString = mapper.writeValueAsString(productPriceRuleOp);
					return new ResponseEntity<>(jsonString, HttpStatus.OK);
				}
				
    			for (int i=0; i < productIdArray.length; i++) {
    				//Code to create price rule.
    				prdPriceRuleTrxParams.put("productPriceRuleTxId", productPriceRuleTrxId);
    				prdPriceRuleTrxParams.put("ruleName", "STS"+productIdArray[i]);
    				prdPriceRuleTrxParams.put("description", remark);
    				prdPriceRuleTrxParams.put("fromDate", fromDate);
    				prdPriceRuleTrxParams.put("status", "CREATED");
    				prdPriceRuleTrxParams.put("createdBy", username);
    				
    				HttpEntity request = new HttpEntity(prdPriceRuleTrxParams, headers);
    				
    				RestTemplate restTemplate = new RestTemplate();
    				HashMapResponse result = new HashMapResponse();
    				result = restTemplate.postForObject(createPrdPriceRuleTrxUri, request, HashMapResponse.class);
    				
    				HashMap<String, Object> list = result.getResponse();
    				productPriceRuleId = (String) list.get("productPriceRuleId");
    				//successMessage = (String) list.get("successMessage");
    				System.out.println("successMessage====="+successMessage);
					
    	    		//Code to create price rule cond.
    				prdPriceCondTrxParams.put("productPriceRuleTxId", productPriceRuleId);
    				prdPriceCondTrxParams.put("inputParamEnumId", "PRIP_PRODUCT_ID");
    				prdPriceCondTrxParams.put("operatorEnumId", "PRC_EQ");
    				prdPriceCondTrxParams.put("condValue", productIdArray[i]);
    				
    				HttpEntity prdPriceCondrequest = new HttpEntity(prdPriceCondTrxParams, headers);
    				
    				RestTemplate prdPriceCondRestTemplate = new RestTemplate();
    				HashMapResponse prdPriceCondResult = prdPriceCondRestTemplate.postForObject(createProductPriceCondTrxUri, prdPriceCondrequest, HashMapResponse.class);
    				
    	    		//Code to create price rule action.
    				
    				prdPriceActionTrxParams.put("productPriceRuleTxId", productPriceRuleId);
    				prdPriceActionTrxParams.put("productPriceActionTypeId", "PRICE_POL");
    				prdPriceActionTrxParams.put("amount", rateAmt);
    				
    				HttpEntity prdPriceRulerequest = new HttpEntity(prdPriceActionTrxParams, headers);
    				
    				RestTemplate prdPriceRuleRestTemplate = new RestTemplate();
    				HashMapResponse prdPriceRuleResult = prdPriceRuleRestTemplate.postForObject(createProductPriceActionTrxUri, prdPriceRulerequest, HashMapResponse.class);
    				HashMap<String, Object> prdPriceRulelist = prdPriceRuleResult.getResponse();
    				
    			}
    		}
    		jsonString = mapper.writeValueAsString(productPriceRuleId);
    		System.out.println("List     " + jsonString);
    		return new ResponseEntity<>(jsonString, HttpStatus.OK);
    	} catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    	
    	return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    public String updateRateAdjustmentTrx(String productPriceRuleTrxId,ArrayList<String> productIdArray, String remark, Timestamp fromDate,
    		String username, Double rateAmt, HashMap<String, Object> cprList) {
    	
    	String productPriceRuleId = "";
    	HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		
    	final String updateProductPriceTrxUri = env.getProperty("updateProductPriceTrx");
    	Hashtable<String, Object> updatePrdPriceTrxParams = new Hashtable<String, Object>();
    	final String updatePrdPriceRuleTrxUri = env.getProperty("updateProductPriceTrxRule");
    	Hashtable<String, Object> uprdPriceRuleTrxParams = new Hashtable<String, Object>();
    	
    	//Code to update Product Price txn.
		ArrayList<LinkedHashMap> prdList = (ArrayList<LinkedHashMap>) cprList.get("list");
		String prdPriceCreatedBy = (String) prdList.get(0).get("createdBy");
		updatePrdPriceTrxParams.put("productPriceRuleTxId", productPriceRuleTrxId);
		updatePrdPriceTrxParams.put("description", remark);
		updatePrdPriceTrxParams.put("status", "CREATED");
		updatePrdPriceTrxParams.put("createdBy", prdPriceCreatedBy);
		updatePrdPriceTrxParams.put("createdDate", fromDate);
		updatePrdPriceTrxParams.put("updatedBy", username);
		
		HttpEntity priceTxnRequest = new HttpEntity(updatePrdPriceTrxParams, headers);
		
		RestTemplate priceTxnRestTemplate = new RestTemplate();
		HashMapResponse priceTxnResult = priceTxnRestTemplate.postForObject(updateProductPriceTrxUri, 
				priceTxnRequest, HashMapResponse.class);
		
		//for (int i=0; i < productIdArray.length; i++) {
			//update Product Price Rule.
			String prdIds = productIdArray.toString();
			String finalPrdIdArray = prdIds.substring(1, prdIds.length()-1);
			
			uprdPriceRuleTrxParams.put("productPriceRuleTxId", productPriceRuleTrxId);
			uprdPriceRuleTrxParams.put("finalPrdIdArray", finalPrdIdArray);
			uprdPriceRuleTrxParams.put("description", remark);
			uprdPriceRuleTrxParams.put("fromDate", fromDate);
			
			uprdPriceRuleTrxParams.put("inputParamEnumId", "PRIP_PRODUCT_ID");
			uprdPriceRuleTrxParams.put("operatorEnumId", "PRC_EQ1");
			uprdPriceRuleTrxParams.put("finalPrdIdArray", finalPrdIdArray);
			uprdPriceRuleTrxParams.put("productPriceActionTypeId", "PRICE_POL");
			uprdPriceRuleTrxParams.put("amount", rateAmt);
			
			HttpEntity request = new HttpEntity(uprdPriceRuleTrxParams, headers);
			
			RestTemplate restTemplate = new RestTemplate();
			HashMapResponse result = new HashMapResponse();
			result = restTemplate.postForObject(updatePrdPriceRuleTrxUri, request, HashMapResponse.class);
			
			HashMap<String, Object> list = result.getResponse();
			productPriceRuleId = (String) list.get("productPriceRuleId");
			//successMessage = (String) list.get("successMessage");
			System.out.println("successMessage====="+"Updated Successfully");
		//}
			return productPriceRuleId;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "rateAdjustmentList", method = RequestMethod.GET)
	public ModelAndView rateAdjustmentList() {
    	
    	ModelAndView modelAndView = new ModelAndView();
    	
		final String uri = env.getProperty("performFind");
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		
		params.put("entityName", "ProductPriceTrx");
		params.put("noConditionFind", "Y");
		params.put("inputFields", new HashMap<>());
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		HashMap<String, Object> list = result.getResponse();

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			modelAndView.addObject("rateAdjutmentList", list.get("list"));
			
			jsonString = mapper.writeValueAsString(list.get("list"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ModelAndView("rate/rate-adjustment-list", modelAndView.getModel());
	}
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "rateAdjustmentTxnList", method = RequestMethod.GET)
	public ModelAndView rateAdjustmentTxnList() {
    	
    	ModelAndView modelAndView = new ModelAndView();
    	
		final String uri = env.getProperty("findRateAdjustmentTrx");
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		
		params.put("entityName", "ProductPriceTrx");
		params.put("noConditionFind", "Y");
		params.put("inputFields", new HashMap<>());
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		HashMap<String, Object> list = result.getResponse();

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			modelAndView.addObject("rateAdjutmentList", list.get("list"));
			
			jsonString = mapper.writeValueAsString(list.get("list"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ModelAndView("rate/rate-adjustment-list", modelAndView.getModel());
	}
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    private List<Object> getRateAdjustmentList() throws Exception {
    	RestClient restClient = context.getBean(RestClient.class);
    	restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
        restClient.addRequestParameter("entityName", "ProductPriceRule");
        restClient.addRequestParameter("inputFields", new HashMap<>());
        restClient.addRequestParameter("noConditionFind", "Y");
        
        List<Object> rateAdjutmentList = (List<Object>) restClient.callRetailService("performFindList", false).get("list");
        
        return rateAdjutmentList;
    }
    
    @SuppressWarnings("unchecked")
    private List<Object> getCategory() throws Exception {
    	RestClient restClient = context.getBean(RestClient.class);
    	restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
        restClient.addRequestParameter("entityName", "ProductCategory");
        restClient.addRequestParameter("inputFields", new HashMap<>());
        restClient.addRequestParameter("noConditionFind", "Y");
        
        List<Object> categoryList = (List<Object>) restClient.callRetailService("performFindList", false).get("list");
        
        return categoryList;
    }
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    private List<LinkedHashMap> getRateAdjustmentProductWiseList() throws Exception {
    	List<LinkedHashMap> finalPrdPriceRuleList = null;
    	String prdDesc = null;
    	Double prdprice = 0d;
    	String currencyUomId = null;
    	Timestamp thruDate = null;
    	
    	RestClient restClient = context.getBean(RestClient.class);
    	restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
        restClient.addRequestParameter("entityName", "ProductPriceRuleCondAndAction");
        restClient.addRequestParameter("inputFields", new HashMap<>());
        restClient.addRequestParameter("noConditionFind", "Y");
        
        List<LinkedHashMap> productPriceRuleList = (List<LinkedHashMap>) restClient.callRetailService("performFindList", false).get("list");
        
        
		for (LinkedHashMap productPriceRule : productPriceRuleList) {
			String productId = (String) productPriceRule.get("condValue");
			//get productDetails
			if(productId != null && productId != "") {
				List<LinkedHashMap> productPriceMap = (List<LinkedHashMap>) getProducts(productId);
				for (LinkedHashMap productPriceEach : productPriceMap) {
					prdDesc = (String) productPriceEach.get("description");
					prdprice = (Double) productPriceEach.get("price");
					currencyUomId = (String) productPriceEach.get("currencyUomId");
					thruDate = (Timestamp) productPriceEach.get("thruDate");
				}
				
				
				productPriceRule.put("description", prdDesc);
				productPriceRule.put("price", prdprice);
				productPriceRule.put("currencyUomId", currencyUomId);
				productPriceRule.put("expiryDate", thruDate);
			}
			
		}
        
        return productPriceRuleList;
    }
    
    @SuppressWarnings("unchecked")
    private List<LinkedHashMap> getProducts(String productId) throws Exception {
    	HashMap productPriceMap = new HashMap<>();
    	productPriceMap.put("productId", productId);
    	productPriceMap.put("productPricePurposeId", "PURCHASE");
    	
        RestClient restClient = context.getBean(RestClient.class);
        restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
        restClient.addRequestParameter("idToFind", productId);
        restClient.addRequestParameter("entityName", "ProductAndPriceView");
        restClient.addRequestParameter("inputFields", productPriceMap);
        restClient.addRequestParameter("noConditionFind", "Y");
        
        List<LinkedHashMap> productPriceList = (List<LinkedHashMap>) restClient.callRetailService("performFindList", false).get("list");

        return productPriceList;
    }
    
    /**
     * Method Name: findRateAdjustment
     * Method Description: Url to create RateAdjustment
     * and search the productlist
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "findRateAdjustment", method = RequestMethod.GET)
	public ResponseEntity<?> findRateAdjustment(@RequestParam("productId") String productId) {
    	String productPrice = "";
    	Map<String,String> productMap = new HashMap<String, String>();
    	Map<String,Double> productSaleMap = new HashMap<String, Double>();
    	
		ArrayList<String> productIdArray = new ArrayList<String>();
    	ModelAndView modelAndView = new ModelAndView();
    	String finalPrdIdArray = new String();
    	
    	final String uri = env.getProperty("performFind");
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		Map<String, String> inputMap = new HashMap<String, String>();
		inputMap.put("productId", productId);
		
		params.put("entityName", "Product");
		params.put("noConditionFind", "Y");
		params.put("inputFields", inputMap);
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		HashMap<String, Object> list = result.getResponse();

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			List<LinkedHashMap> listMap  = (List<LinkedHashMap>) list.get("list");
			for (LinkedHashMap listEach : listMap) {
				productId = (String) listEach.get("productId");
				productIdArray.add(productId);
				//get product default price for MRP
				productMap = getProductMrp(productId);
				//Calculate Sale rate
				productSaleMap = getProductSaleRate(productId);
				//Calculate Cost Rate
				
				listEach.put("productSalePrice", productSaleMap.get("productSalePrice"));
				listEach.put("productNewSalePrice", productSaleMap.get("newProductSalePrice"));
				listEach.put("productPrice", productMap.get("productPrice"));
				listEach.put("currencyUomId", productMap.get("currencyUomId"));
			}
			
			//Integer rateAdjustmentTransId = createNewRateAdjTransactionId();
			
			String prdId = productIdArray.toString();
			finalPrdIdArray = prdId.substring(1, prdId.length()-1);
			if (finalPrdIdArray != null) {
				list.put("finalPrdIdArray", finalPrdIdArray);
			}
			//modelAndView.addObject("productIdArray",finalPrdIdArray);
			
			//modelAndView.addObject("rateAdjustmentTransId", rateAdjustmentTransId);
			//modelAndView.addObject("productsList", list.get("list"));
			
			jsonString = mapper.writeValueAsString(list.get("list"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
		//return new ModelAndView("rate/create-rate-adjustment", modelAndView.getModel());
	}
	
	/**
     * Method Name: createNewRateAdjTransactionId
     * Method Description: Method to get New Rate Adjustment Transaction id
     * @return
     */
    @GetMapping("/createNewRateAdjTransactionId")
    @ResponseBody
    public Integer createNewRateAdjTransactionId() {
    	Integer rateAdjustmentId = 1000;
    	//get the latest rate adjustment id
    	try {
    		final String uri = env.getProperty("createRateAdjustmentTrx");
    		Hashtable<String, Object> params = new Hashtable<String, Object>();
    		
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		
    		RestTemplate restTemplate = new RestTemplate();
    		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
    		HashMap<String, Object> list = result.getResponse();
    		
    		rateAdjustmentId = Integer.parseInt(list.get("rateAdjustmentId").toString());
    		
    	} catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
        return rateAdjustmentId; // The calculation
    }
    
    
    /**
     * Method Name: convertStringToTimestamp
     * Method Description: Method to convert String date val to Timestamp
     * @return
     */
    public Timestamp convertStringToTimestamp(String strDate) {
    	try {
	      DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	       // you can change format of date
	      Date date = formatter.parse(strDate);
	      Timestamp timeStampDate = new Timestamp(date.getTime());

	      return timeStampDate;
	    } catch (ParseException e) {
	      System.out.println("Exception :" + e);
	      return null;
	    }
    }
    /**
     * MethodName: getProductMrp
     * MethodDesc: method to get Product wise MRP
     */
    @SuppressWarnings("unchecked")
    private Map<String,String> getProductMrp(String productId) throws Exception {
    	String productPrice = "0.00";
    	String currencyUomId = "";
    	Map<String,String> result = new HashMap<String, String>();
    	
    	RestClient restClient = context.getBean(RestClient.class);
    	restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
    	
    	Map<String, String> inputMap = new HashMap<String, String>();
		inputMap.put("productId", productId);
		inputMap.put("productPriceTypeId", "DEFAULT_PRICE");
		
        restClient.addRequestParameter("entityName", "ProductPrice");
        restClient.addRequestParameter("inputFields", inputMap);
        restClient.addRequestParameter("noConditionFind", "Y");
        
        List<LinkedHashMap> productList = (List<LinkedHashMap>) restClient.callRetailService("performFindList", false).get("list");
        if(productList != null && productList.size() != 0) {
        	
        	productPrice = (String) productList.get(0).get("price").toString();
        	currencyUomId = (String) productList.get(0).get("currencyUomId").toString();
        }
        result.put("productPrice", productPrice);
        result.put("currencyUomId", currencyUomId);
        return result;
    }
    
    /**
     * MethodName: getProductSaleMrp
     * MethodDesc: method to get Product wise MRP
     */
    @SuppressWarnings("unchecked")
    private Double getProductSaleMrp(String productId) throws Exception {
    	Double productPrice = 0.0d;
    	RestClient restClient = context.getBean(RestClient.class);
    	restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
    	
    	Map<String, String> inputMap = new HashMap<String, String>();
		inputMap.put("productId", productId);
		inputMap.put("productPriceTypeId", "LIST_PRICE");
		
        restClient.addRequestParameter("entityName", "ProductPrice");
        restClient.addRequestParameter("inputFields", inputMap);
        restClient.addRequestParameter("noConditionFind", "Y");
        
        List<LinkedHashMap> productList = (List<LinkedHashMap>)restClient.callRetailService("performFindList", false).get("list");
        if(productList != null && productList.size() != 0) {
        	
        	productPrice = (Double) productList.get(0).get("price");
        }
        
        return productPrice;
    }
    
    /**
     * MethodName: getProductSaleRate
     * MethodDesc: method to get Product Sale Rate
     */
    @SuppressWarnings("unchecked")
    private Map<String,Double> getProductSaleRate(String productId) throws Exception {
    	Double productRulePrice = 0.0d;
    	Double productSalePrice = getProductSaleMrp(productId);
    	Double newProductSalePrice = 0.0d;
    	
    	Map<String,Double> result = new HashMap<String, Double>();
    	
    	RestClient restClient = context.getBean(RestClient.class);
    	restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
    	
    	Map<String, String> inputMap = new HashMap<String, String>();
		inputMap.put("priceRulePrdID", productId);
		
        restClient.addRequestParameter("entityName", "ProductPriceRuleCondAndAction");
        restClient.addRequestParameter("inputFields", inputMap);
        restClient.addRequestParameter("noConditionFind", "Y");
        
        List<LinkedHashMap> productSaleList = (List<LinkedHashMap>) restClient.callRetailService("performFindList", false).get("list");
        if(productSaleList != null && productSaleList.size() != 0) {
        	
        	productRulePrice = (Double) productSaleList.get(0).get("amount");
        	newProductSalePrice = productSalePrice *(productRulePrice/100);
        }
        result.put("productSalePrice", productSalePrice);
        result.put("newProductSalePrice", newProductSalePrice);
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/getRateAdjustment", method = RequestMethod.GET)
	public ResponseEntity<?> getRateAdjustment(@RequestParam("json") String json,
			@RequestParam("password") String password, @RequestParam("username") String username) {
		
		/*CHECKING FOR MY PURPOSE
		 * getting the values from the ofbiz layer and passing it in the database(HTML) layer
		 */    	
    	final String uri = env.getProperty("performFind");
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String,String> inputMap = new HashMap<String,String>();
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
			inputMap.put("productPriceRuleTxId", (String)params.get("transactionId"));
			inputMap.put("status", (String)params.get("statusId"));
			//inputMap.put("rulename", (String)params.get("ruleName"));
			
            params.put("login.username", username);
			params.put("login.password", password);
			params.put("entityName", "ProductPriceTrx");
			params.put("noConditionFind", "Y");
			params.put("inputFields", inputMap);
			System.out.println(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		HashMap<String, Object> list = result.getResponse();
		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		if(list != null) {
			hashMap.put("message", "Success");
		}
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
    
    /**
     * Method Name: searchArticlesByCategory
     * Method Description: Url to Search Articles by Category
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "searchArticlesByCategory", method = RequestMethod.GET)
	public ResponseEntity<?> searchArticlesByCategory(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
		
		String productStoreId = getStoreId();
		ObjectMapper mapper = new ObjectMapper();
		Hashtable<String, String> params = new Hashtable<String, String>();
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		String finalPrdIdArray = new String();
		final String uri = env.getProperty("findRateProductPrice");
		String categoryId = params.get("categoryId");
		String productId = params.get("productId");
    	
		Map<String, String> inputMap = new HashMap<String, String>();
		if(!(productId.isEmpty())) {
			params.put("productId", productId);
		}
		if(!(categoryId.isEmpty())) {
			params.put("categoryId", categoryId);
		}
		params.put("productStoreId", productStoreId);
		params.put("noConditionFind", "Y");
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		HashMap<String, Object> list = new HashMap<String, Object>();
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
			list = result.getResponse();
			
			String prdId = list.get("productIdArray").toString();
			finalPrdIdArray = prdId.substring(1, prdId.length()-1);
			if (finalPrdIdArray != null) {
				list.put("finalPrdIdArray", finalPrdIdArray);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value = "newSaveRateAdjustmentTrx", method = RequestMethod.GET)
    public ResponseEntity<?> newSaveRateAdjustmentTrx(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username,
    		@RequestParam("productIdArray") String[] productIdArray,
    		@RequestParam("ratePerArray") String[] ratePerArray) {
    	
    	ModelAndView modelAndView = new ModelAndView();
    	String successMessage = "Product Price Rule Updated Successfully";
    	Timestamp fromDate = null;
    	ObjectMapper mapper = new ObjectMapper();
		String jsonString = "", remark = "",amountval = "0.0", rateFromDate = "";
		String productPriceRuleTrxId = "";
		Hashtable<String, String> params = new Hashtable<String, String>();
		ArrayList<String> productIdArrays = new ArrayList<String>();
		ArrayList<String> ratePerArrays = new ArrayList<String>();
		String productPriceRuleId = "";
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		final String saveUpdateProductPriceTxnUri = env.getProperty("saveUpdateProductPriceTxn");
    	Hashtable<String, Object> prdTrxParams = new Hashtable<String, Object>();
    	
    	try {
    		if (params.get("amountval") != null) {
    			amountval = params.get("amountval");
    		}
			
    		rateFromDate = params.get("rateFromDate");
    		productPriceRuleTrxId = params.get("productPriceRuleTrxId");
    		Double rateAmt = Double.valueOf(amountval);
    		Date date = new Date();
    		fromDate = new Timestamp(date.getTime());
    		if(rateFromDate != null) {
    			fromDate = convertStringToTimestamp(rateFromDate);
    		}
    		remark = params.get("remark");
    		if(productIdArray != null) {
    			for (int i=0; i < productIdArray.length; i++) {
    				productIdArrays.add(productIdArray[i]);
    			}
    			for (int i=0; i < ratePerArray.length; i++) {
    				ratePerArrays.add(ratePerArray[i]);
    			}
    			
    			String prdIds = productIdArrays.toString();
    			String finalPrdIdArray = prdIds.substring(1, prdIds.length()-1);
    			
    			String ratePerVal = ratePerArrays.toString();
    			String finalRatePerArray = ratePerVal.substring(1, ratePerVal.length()-1);
    			
    			HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				
    			//for (int i=0; i < productIdArray.length; i++) {
    				//Code to create price rule.
    				prdTrxParams.put("productPriceRuleTxId", productPriceRuleTrxId);
    				//prdTrxParams.put("ruleName", "STS"+productIdArray[i]);
    				prdTrxParams.put("description", remark);
    				prdTrxParams.put("fromDate", fromDate);
    				prdTrxParams.put("status", "CREATED");
    				prdTrxParams.put("createdBy", username);
    				prdTrxParams.put("finalPrdIdArray", finalPrdIdArray);
    				prdTrxParams.put("finalRatePerArray", finalRatePerArray);
    				/*HttpEntity request = new HttpEntity(prdPriceRuleTrxParams, headers);
    				
    				RestTemplate restTemplate = new RestTemplate();
    				HashMapResponse result = new HashMapResponse();
    				result = restTemplate.postForObject(createPrdPriceRuleTrxUri, request, HashMapResponse.class);*/
    				
    				/*HashMap<String, Object> list = result.getResponse();
    				productPriceRuleId = (String) list.get("productPriceRuleId");*/
    				//successMessage = (String) list.get("successMessage");
    				System.out.println("successMessage====="+successMessage);
					
    	    		//Code to create price rule cond.
    				//prdTrxParams.put("productPriceRuleTxId", productPriceRuleId);
    				prdTrxParams.put("inputParamEnumId", "PRIP_PRODUCT_ID");
    				prdTrxParams.put("operatorEnumId", "PRC_EQ");
    				//prdTrxParams.put("condValue", productIdArray[i]);
    				
    				/*HttpEntity prdPriceCondrequest = new HttpEntity(prdPriceCondTrxParams, headers);
    				
    				RestTemplate prdPriceCondRestTemplate = new RestTemplate();
    				HashMapResponse prdPriceCondResult = prdPriceCondRestTemplate.postForObject(createProductPriceCondTrxUri, prdPriceCondrequest, HashMapResponse.class);*/
    				
    	    		//Code to create price rule action.
    				
    				//prdTrxParams.put("productPriceRuleTxId", productPriceRuleId);
    				prdTrxParams.put("productPriceActionTypeId", "PRICE_POL");
    				prdTrxParams.put("amount", rateAmt);
    				
    				HttpEntity prdPriceRuleRequest = new HttpEntity(prdTrxParams, headers);
    				
    				RestTemplate prdPriceRuleRestTemplate = new RestTemplate();
    				HashMapResponse prdPriceRuleResult = prdPriceRuleRestTemplate.postForObject(saveUpdateProductPriceTxnUri, prdPriceRuleRequest, HashMapResponse.class);
    				HashMap<String, Object> prdPriceRulelist = prdPriceRuleResult.getResponse();
    				
    			//}
    		}
    		jsonString = mapper.writeValueAsString(successMessage);
    		System.out.println("List     " + jsonString);
    		return new ResponseEntity<>(jsonString, HttpStatus.OK);
    	} catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    	
    	return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
	
	/**
     * Method Name: approveRateAdjustment
     * Method Description: Url to Approve RateAdjustment
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "viewRateAdjustmentList", method = RequestMethod.GET)
	public ModelAndView viewRateAdjustmentList(String productPriceTxnId) {
    	String productId = "";
    	String productPrice = "";
    	Map<String,String> productMap = new HashMap<String, String>();
    	Map<String,Double> productSaleMap = new HashMap<String, Double>();
		ArrayList<String> productIdArray = new ArrayList<String>();
		Map<String, Object> response = new HashMap<String, Object>();
		String finalPrdIdArray = new String();
		String finalRatePerArray = new String();
    	ModelAndView modelAndView = new ModelAndView();
        String productStoreGroupId = getProductStoreGroupId();
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        RestClient restClient = applicationContext.getBean(RestClient.class);
    	
		final String uri = env.getProperty("findRateAdjustmentTrx");
		final String categoryUri = env.getProperty("findStoreCategory");
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		Hashtable<String, Object> catParams = new Hashtable<String, Object>();
		
		HttpHeaders headers = new HttpHeaders();
		params.put("currencyUomId", currencyUomId);
		params.put("productStoreId", productStoreId);
		params.put("productPriceTxnId", productPriceTxnId);
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		HashMap<String, Object> list = result.getResponse();
		
		HttpHeaders categoryHeaders = new HttpHeaders();
		catParams.put("productStoreId", productStoreId);
		HttpEntity categoryRequest = new HttpEntity(catParams, headers);
		
		HashMapResponse catResult = restTemplate.postForObject(categoryUri, categoryRequest, HashMapResponse.class);
		HashMap<String, Object> catList = catResult.getResponse();

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			List<HashMap> listMap  = (List<HashMap>) list.get("finalProductPriceInfoList");
			
			//Integer rateAdjustmentTransId = createNewRateAdjTransactionId();
			Integer rateAdjustmentTransId = Integer.parseInt(productPriceTxnId);
			String prdId = list.get("productIdArray").toString();
			finalPrdIdArray = prdId.substring(1, prdId.length()-1);
			
			String ratePerArray = list.get("ratePercentageArray").toString();
			finalRatePerArray = ratePerArray.substring(1, ratePerArray.length()-1);
			
			modelAndView.addObject("ratePercentageArray",finalRatePerArray);
			modelAndView.addObject("productIdArray",finalPrdIdArray);
			modelAndView.addObject("rateAdjustmentTransId", rateAdjustmentTransId);
			modelAndView.addObject("productsList", list.get("finalProductPriceInfoList"));
			modelAndView.addObject("productPriceTxnId",productPriceTxnId);
			
			jsonString = mapper.writeValueAsString(list.get("finalProductPriceInfoList"));
			//modelAndView.addObject("categoryList", getCategory());
			modelAndView.addObject("categoryList", catList.get("categoryList"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ModelAndView("rate/view-rate-adjustment", modelAndView.getModel());
	}
	
	/**
     * Method Name: searchInventoryArticles
     * Method Description: Url to Search Inventory Articles
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "searchInventoryArticles", method = RequestMethod.GET)
	public ResponseEntity<?> searchInventoryArticles(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
		
		String productStoreId = getStoreId();
		String inventoryArray = "";
		ObjectMapper mapper = new ObjectMapper();
		Hashtable<String, String> params = new Hashtable<String, String>();
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		String finalInventoryItemArray = new String();
		final String uri = env.getProperty("findInventoryArticles");
		String productId = params.get("productId");
		if (params.get("inventoryArray") != null) {
			inventoryArray = params.get("inventoryArray").toString();
		}
		
		Map<String, String> inputMap = new HashMap<String, String>();
		if(!(productId.isEmpty())) {
			params.put("productId", productId);
		}
		params.put("productStoreId", productStoreId);
		params.put("inventoryArray", inventoryArray);
		params.put("noConditionFind", "Y");
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		HashMap<String, Object> list = new HashMap<String, Object>();
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
			list = result.getResponse();
			
			String invItemId = list.get("inventoryItemArray").toString();
			finalInventoryItemArray = invItemId.substring(1, invItemId.length()-1);
			if (finalInventoryItemArray != null) {
				list.put("finalInventoryItemArray", finalInventoryItemArray);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}
