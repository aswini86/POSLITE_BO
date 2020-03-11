package com.retail.bo.controllers;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.retail.bo.services.RestClient;
import com.retail.bo.util.HashMapResponse;
import com.retail.bo.print.Printer;
import com.retail.bo.print.PrinterService;
import com.retail.bo.print.NetworkPrinter;
import com.retail.bo.print.exceptions.QRCodeException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.NetworkInterface;

@Controller
public class Cart extends BaseController{
	
	@Autowired
    private ApplicationContext context;
	
	@Autowired
	private Environment env;
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
    private HttpSession httpSession;
	
	public static final byte[] LINE_SPACE_24   = {0x1b,0x33,24};
	
	@GetMapping("cart")
    public ModelAndView addAtricles() {
		final String searchArticleUri = env.getProperty("findRateProductPrice");
		final String uri = env.getProperty("findCustomerBO");
		ModelAndView modelAndView = new ModelAndView();
		//code for getting customer details
		Map<Object, Object> custParams = new HashMap<>();
		custParams.put("contactNumber", "");
        ObjectMapper mapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity custRequest = new HttpEntity(custParams, headers);
		
		ResponseEntity response = restTemplate.postForEntity(uri, custRequest, List.class);
		//HashMapResponse custResult = custRestTemplate.postForObject(uri, custRequest, HashMapResponse.class);
		System.out.println("customerList---"+response.getBody());
		modelAndView.addObject("CustomerList", response.getBody());
		//End of code for getting customer details
		Hashtable<String, String> params = new Hashtable<String, String>();
        
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse searchArticleResult = restTemplate.postForObject(searchArticleUri, request, HashMapResponse.class);
		HashMap<String, Object> searchArticleList = searchArticleResult.getResponse();
		
		modelAndView.addObject("productsList", searchArticleList.get("finalProductPriceInfoList"));
		
        return new ModelAndView("cart/cart", modelAndView.getModel());
    }
	
	@SuppressWarnings("null")
	@RequestMapping(value = "cartView", method = RequestMethod.GET)
    public ModelAndView cartView(@RequestParam("dayId") String dayId,
    		RedirectAttributes redirectAttributes) {
		RestClient restClient = applicationContext.getBean(RestClient.class);
		final String searchArticleUri = env.getProperty("findRateProductPrice");
		final String uri = env.getProperty("findCustomerBO");
		final String dayPosCartItemsUri = env.getProperty("getPosCartItems");
		final String getDayPosCartItemsUri = env.getProperty("getDayPosCartItems");
		Map<String, Object> posTerminalMap = new HashMap<String, Object>();
		
		String receiptId = "", cartDayId = "",facilityId = "";
		String posStatus = "", posReceiptId = "", customerMobileNo = "";
		String productStoreId = getStoreId();
		String posTerminalId = (String) httpSession.getAttribute("posTerminalId");
		Map<String, Object> posMap = new HashMap<String, Object>();
		try {
			posTerminalMap = getMacAddress();
			facilityId = getFacilityId();
			posTerminalId = (String) posTerminalMap.get("systemMacVal");
			posMap.put("posTerminalId", posTerminalId);
			posMap.put("facilityId", facilityId);
			restClient.setRequestBody(posMap);
			Map<String, Object> response = restClient.callRetailService("getDayId", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) response.get("responseBody");
			if (!dayDetailsList.isEmpty()) {
				//Map<String,Object> dayDetail = (HashMap<String, Object>) dayDetailsList.get("dayDetail");
				receiptId = (String) dayDetailsList.get("receiptId");
				cartDayId = (String) dayDetailsList.get("dayId");
				httpSession.setAttribute("receiptId", receiptId);
				httpSession.setAttribute("dayId", cartDayId);
				httpSession.setAttribute("posTerminalId", posTerminalId);
			}
	        dayId = (String) response.get("dayId");
			
		} catch (Exception e) {
			
		}
		String username = (String) httpSession.getAttribute("username");
		List<String> posReceiptList = new ArrayList<String>();
		ModelAndView modelAndView = new ModelAndView();
		
		//code for getting customer details
		Map<String, Object> cartItems = new LinkedHashMap<String, Object>();
		Map<String, Object> cartMapItems = new LinkedHashMap<String, Object>();
		Map<Object, Object> custParams = new HashMap<>();
		custParams.put("contactNumber", "");
        ObjectMapper mapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity custRequest = new HttpEntity(custParams, headers);
		JsonParser parser = null;
		
		ResponseEntity response = restTemplate.postForEntity(uri, custRequest, List.class);
		//HashMapResponse custResult = custRestTemplate.postForObject(uri, custRequest, HashMapResponse.class);
		System.out.println("customerList---"+response.getBody());
		modelAndView.addObject("CustomerList", response.getBody());
		//End of code for getting customer details
		Hashtable<String, String> params = new Hashtable<String, String>();
        
        headers.add("Content-Type", "application/json");
        params.put("productStoreId", productStoreId);
        HttpEntity request = new HttpEntity(params, headers);
        
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse searchArticleResult = restTemplate.postForObject(searchArticleUri, request, HashMapResponse.class);
		HashMap<String, Object> searchArticleList = searchArticleResult.getResponse();
		
		modelAndView.addObject("productsList", searchArticleList.get("finalProductPriceInfoList"));
		/*try {
			//Get Day details
			Map<String, Object> dayDetailRequest = new HashMap<String, Object>();
			Map<String, Object> dayDetailResponse = new HashMap<String, Object>();
			dayDetailRequest.put("dayId", dayId);
			
			//RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(dayDetailRequest);
			dayDetailResponse = restClient.callRetailService("findDay", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) dayDetailResponse.get("responseBody");
			if (!dayDetailsList.isEmpty()) {
				Map<String,Object> dayDetail = (HashMap<String, Object>) dayDetailsList.get("dayDetail");
				receiptId = (String) dayDetail.get("receiptId");
				cartDayId = (String) dayDetail.get("dayId");
				httpSession.setAttribute("receiptId", receiptId);
				httpSession.setAttribute("dayId", cartDayId);
			}
		} catch (Exception e) {
			
		}*/
		//getPosCartItems
		try {
			byPassSSLCertificate();
			List<Object> holdPosCartList = new ArrayList<Object>();
			JsonNode dayPosCart = null;
			//get Hold bill details.
			UriComponentsBuilder dayPosCartBuilder = UriComponentsBuilder.fromHttpUrl(getDayPosCartItemsUri).
            		queryParam("dayId", cartDayId).
            		queryParam("posTerminalId", posTerminalId);
			
			HttpEntity<String> dayPosCartEntity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> dayPosCartItemResponse = restTemplate.exchange(dayPosCartBuilder.toUriString(), HttpMethod.POST, dayPosCartEntity, String.class);
            JsonNode dayPosCartItemActualObj = mapper.readTree(dayPosCartItemResponse.getBody());
            System.out.println("dayPosCartItemActualObj----"+dayPosCartItemActualObj);
            if(dayPosCartItemActualObj.get("posCartItemList") != null) {
            	for (int i=0; i < dayPosCartItemActualObj.get("posCartItemList").size(); i++) {
            		Map<String, String> posHoldCartMap = new HashMap<String, String>();
            		dayPosCart = mapper.readTree(dayPosCartItemActualObj.get("posCartItemList").get(i).toString());
            		
            		posStatus = dayPosCart.get("posStatus").toString().substring(1, dayPosCart.get("posStatus").toString().length()-1);
            		posReceiptId = dayPosCart.get("receiptId").toString().substring(1, dayPosCart.get("receiptId").toString().length()-1);
            		customerMobileNo = dayPosCart.get("customerMobileNo").toString().substring(1, dayPosCart.get("customerMobileNo").toString().length()-1);
            		if(posStatus.equals("HOLD") && !(posReceiptList.contains(posReceiptId))) {
            			posHoldCartMap.put("posStatus", posStatus);
            			posHoldCartMap.put("posReceiptId", posReceiptId);
            			posHoldCartMap.put("customerMobileNo", customerMobileNo);
            			posHoldCartMap.put("dayId", cartDayId);
            			posHoldCartMap.put("posTerminalId", posTerminalId);
            			holdPosCartList.add(posHoldCartMap);
            			posReceiptList.add(posReceiptId);
            		}
            	}
            }
            modelAndView.addObject("holdPosCartList", holdPosCartList);
			//get Hold bill details.
            System.out.println("came into get cart Items");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dayPosCartItemsUri).
            		queryParam("receiptId", receiptId).
            		queryParam("posTerminalId", posTerminalId).
            		queryParam("dayId", cartDayId).
            		queryParam("USERNAME", username);
            
            //response.
            String cartItemRequest = request.toString();
            cartItemRequest = cartItemRequest.substring(1, cartItemRequest.length()-1);
            HttpEntity<String> entity = new HttpEntity<String>(cartItemRequest, headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            JsonNode cartObj = null;
            ObjectNode cartObjNode = null;
            Map<String, Object> cartItemsMap = new HashMap<String, Object>();
            List<Object> cartItemList = new ArrayList<Object>();
            if(actualObj.get("cartItems") != null) {
            	for (int i=0; i < actualObj.get("cartItems").size(); i++) {
            		BigDecimal CGST_TAX = BigDecimal.ZERO;
            		BigDecimal SGST_TAX = BigDecimal.ZERO;
            		BigDecimal IGST_TAX = BigDecimal.ZERO;
            		BigDecimal prdPrice = BigDecimal.ZERO;
            		BigDecimal totCartItemAmt = BigDecimal.ZERO;
            		
            		cartObj = mapper.readTree(actualObj.get("cartItems").get(i).toString());
            		cartObjNode = (ObjectNode) cartObj;
            		if(cartObj.get("productTotalAmt") != null) {
            			prdPrice = new BigDecimal(cartObj.get("productTotalAmt").toString());
            			totCartItemAmt = totCartItemAmt.add(prdPrice);
					}
            		
            		if(cartObj.get("itemTax").get("CGST_TAX") != null) {
            			CGST_TAX = new BigDecimal(cartObj.get("itemTax").get("CGST_TAX").toString());
            			totCartItemAmt = totCartItemAmt.add(CGST_TAX);
            		}
					if(cartObj.get("itemTax").get("SGST_TAX") != null) {
						SGST_TAX = new BigDecimal(cartObj.get("itemTax").get("SGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(SGST_TAX);
					}
					if(cartObj.get("itemTax").get("IGST_TAX") != null) {
						IGST_TAX = new BigDecimal(cartObj.get("itemTax").get("IGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(IGST_TAX);
					}
            		
					if(cartObj.get("itemTax").get("CGST_TAX") == null)
            			cartObjNode.put("CGST_TAX", CGST_TAX);
            		else
            			cartObjNode.put("CGST_TAX", cartObj.get("itemTax").get("CGST_TAX"));
					
					if(cartObj.get("itemTax").get("SGST_TAX") == null)
            			cartObjNode.put("SGST_TAX", SGST_TAX);
            		else
            			cartObjNode.put("SGST_TAX", cartObj.get("itemTax").get("SGST_TAX"));
					
            		if(cartObj.get("itemTax").get("IGST_TAX") == null)
            			cartObjNode.put("IGST_TAX", IGST_TAX);
            		else
            			cartObjNode.put("IGST_TAX", cartObj.get("itemTax").get("IGST_TAX"));
            		
            		cartObjNode.put("Item_Tot_Amt", totCartItemAmt.floatValue());
            		cartItemsMap.put("cartItemsMap", cartObjNode);
            		System.out.println("cartItemsMap--"+cartItemsMap.get("cartItemsMap"));
            		cartItemList.add(cartItemsMap.get("cartItemsMap"));
            	}
            	System.out.println("cartItemList--"+cartItemList);
            	if(actualObj.get("customerShipState") != null) {
            		int strLength = actualObj.get("customerShipState").toString().length();
            		httpSession.setAttribute("customerShipState", actualObj.get("customerShipState").toString().substring(1, strLength-1));
            	}
            	modelAndView.addObject("contactNumber", actualObj.get("customerMobileNo").toString().substring(1, actualObj.get("customerMobileNo").toString().length()-1));
            	modelAndView.addObject("customerName", actualObj.get("firstName").toString().substring(1, actualObj.get("firstName").toString().length()-1));
            	modelAndView.addObject("contactAddress", actualObj.get("address1").toString().substring(1, actualObj.get("address1").toString().length()-1));
            	modelAndView.addObject("totalDue", actualObj.get("totalDue"));
            	modelAndView.addObject("subTotal", actualObj.get("subTotal"));
            	modelAndView.addObject("totalSalesTax", actualObj.get("totalSalesTax"));
            	modelAndView.addObject("cartSize", actualObj.get("cartSize"));
            	modelAndView.addObject("displayGrandTotal", actualObj.get("displayGrandTotal"));
            	modelAndView.addObject("balanceAmount", actualObj.get("balanceAmount"));
            	
                //modelAndView.addObject("cartItems", actualObj.get("cartItems"));
                modelAndView.addObject("cartItems", cartItemList);
                modelAndView.addObject("itemTax", cartObj.get("itemTax"));
                //NodeBean toValue = mapper.readValue(parser, NodeBean.class);
            }
		} catch (Exception e) {
			
		}
        return new ModelAndView("cart/cart", modelAndView.getModel());
    }
	
	@SuppressWarnings("null")
	@RequestMapping(value = "cartPage", method = RequestMethod.POST)
    public ModelAndView cartPage(@RequestParam("dayId") String dayId) {
		final String searchArticleUri = env.getProperty("findRateProductPrice");
		final String uri = env.getProperty("findCustomerBO");
		final String dayPosCartItemsUri = env.getProperty("getPosCartItems");
		
		String receiptId = "", cartDayId = "";
		String posTerminalId = (String) httpSession.getAttribute("posTerminalId");
		String username = (String) httpSession.getAttribute("username");
		ModelAndView modelAndView = new ModelAndView();
		
		//code for getting customer details
		Map<String, Object> cartItems = new LinkedHashMap<String, Object>();
		Map<String, Object> cartMapItems = new LinkedHashMap<String, Object>();
		Map<Object, Object> custParams = new HashMap<>();
		custParams.put("contactNumber", "");
        ObjectMapper mapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity custRequest = new HttpEntity(custParams, headers);
		JsonParser parser = null;
		
		ResponseEntity response = restTemplate.postForEntity(uri, custRequest, List.class);
		//HashMapResponse custResult = custRestTemplate.postForObject(uri, custRequest, HashMapResponse.class);
		System.out.println("customerList---"+response.getBody());
		modelAndView.addObject("CustomerList", response.getBody());
		//End of code for getting customer details
		Hashtable<String, String> params = new Hashtable<String, String>();
        
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse searchArticleResult = restTemplate.postForObject(searchArticleUri, request, HashMapResponse.class);
		HashMap<String, Object> searchArticleList = searchArticleResult.getResponse();
		
		modelAndView.addObject("productsList", searchArticleList.get("finalProductPriceInfoList"));
		try {
			//Get Day details
			Map<String, Object> dayDetailRequest = new HashMap<String, Object>();
			Map<String, Object> dayDetailResponse = new HashMap<String, Object>();
			dayDetailRequest.put("dayId", dayId);
			
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(dayDetailRequest);
			dayDetailResponse = restClient.callRetailService("findDay", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) dayDetailResponse.get("responseBody");
			if (!dayDetailsList.isEmpty()) {
				Map<String,Object> dayDetail = (HashMap<String, Object>) dayDetailsList.get("dayDetail");
				receiptId = (String) dayDetail.get("receiptId");
				cartDayId = (String) dayDetail.get("dayId");
				httpSession.setAttribute("receiptId", receiptId);
				httpSession.setAttribute("dayId", cartDayId);
			}
		} catch (Exception e) {
			
		}
		//getPosCartItems
		try {
			byPassSSLCertificate();
            System.out.println("came into get cart Items");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dayPosCartItemsUri).
            		queryParam("receiptId", receiptId).
            		queryParam("posTerminalId", posTerminalId).
            		queryParam("dayId", cartDayId).
            		queryParam("USERNAME", username);
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            JsonNode cartObj = null;
            ObjectNode cartObjNode = null;
            Map<String, Object> cartItemsMap = new HashMap<String, Object>();
            List<Object> cartItemList = new ArrayList<Object>();
            if(actualObj.get("cartItems") != null) {
            	for (int i=0; i < actualObj.get("cartItems").size(); i++) {
            		BigDecimal CGST_TAX = BigDecimal.ZERO;
            		BigDecimal SGST_TAX = BigDecimal.ZERO;
            		BigDecimal IGST_TAX = BigDecimal.ZERO;
            		BigDecimal prdPrice = BigDecimal.ZERO;
            		BigDecimal totCartItemAmt = BigDecimal.ZERO;
            		
            		cartObj = mapper.readTree(actualObj.get("cartItems").get(i).toString());
            		cartObjNode = (ObjectNode) cartObj;
            		if(cartObj.get("productTotalAmt") != null) {
            			prdPrice = new BigDecimal(cartObj.get("productTotalAmt").toString());
            			totCartItemAmt = totCartItemAmt.add(prdPrice);
					}
            		
            		if(cartObj.get("itemTax").get("CGST_TAX") != null) {
            			CGST_TAX = new BigDecimal(cartObj.get("itemTax").get("CGST_TAX").toString());
            			totCartItemAmt = totCartItemAmt.add(CGST_TAX);
            		}
					if(cartObj.get("itemTax").get("SGST_TAX") != null) {
						SGST_TAX = new BigDecimal(cartObj.get("itemTax").get("SGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(SGST_TAX);
					}
					if(cartObj.get("itemTax").get("IGST_TAX") != null) {
						IGST_TAX = new BigDecimal(cartObj.get("itemTax").get("IGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(IGST_TAX);
					}
            		
            		cartObjNode.put("CGST_TAX", cartObj.get("itemTax").get("CGST_TAX"));
            		cartObjNode.put("SGST_TAX", cartObj.get("itemTax").get("SGST_TAX"));
            		cartObjNode.put("IGST_TAX", cartObj.get("itemTax").get("IGST_TAX"));
            		cartObjNode.put("Item_Tot_Amt", totCartItemAmt);
            		cartItemsMap.put("cartItemsMap", cartObjNode);
            		System.out.println("cartItemsMap--"+cartItemsMap.get("cartItemsMap"));
            		cartItemList.add(cartItemsMap.get("cartItemsMap"));
            	}
            	System.out.println("cartItemList--"+cartItemList);
            	if(actualObj.get("customerShipState") != null) {
            		int strLength = actualObj.get("customerShipState").toString().length();
            		httpSession.setAttribute("customerShipState", actualObj.get("customerShipState").toString().substring(1, strLength-1));
            	}
            	modelAndView.addObject("contactNumber", actualObj.get("customerMobileNo"));
            	modelAndView.addObject("customerName", actualObj.get("firstName"));
            	modelAndView.addObject("contactAddress", actualObj.get("address1"));
            	modelAndView.addObject("totalDue", actualObj.get("totalDue"));
            	modelAndView.addObject("subTotal", actualObj.get("subTotal"));
            	modelAndView.addObject("totalSalesTax", actualObj.get("totalSalesTax"));
            	modelAndView.addObject("cartSize", actualObj.get("cartSize"));
            	modelAndView.addObject("displayGrandTotal", actualObj.get("displayGrandTotal"));
            	modelAndView.addObject("balanceAmount", actualObj.get("balanceAmount"));
            	
                //modelAndView.addObject("cartItems", actualObj.get("cartItems"));
                modelAndView.addObject("cartItems", cartItemList);
                modelAndView.addObject("itemTax", cartObj.get("itemTax"));
                //NodeBean toValue = mapper.readValue(parser, NodeBean.class);
            }
		} catch (Exception e) {
			
		}
        return new ModelAndView("cart/cart", modelAndView.getModel());
    }
	/**
     * MethodName: addPosCartItem Method 
     * Description: Ajax call to add Pos Cart Items
     */
    @RequestMapping(value = "/addPosCartItem", method = RequestMethod.GET)
    public ResponseEntity<?> addPosCartItem(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
    	
    	final String uri = env.getProperty("addPosCartBarcode");
    	//get pos cart item params
    	Hashtable<String, String> params = new Hashtable<String, String>();
    	HashMap<String, Object> test = new HashMap<String, Object>();
    	ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
    	try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            
            params.put("createdBy", username);
            params.put("login.username", username);
            params.put("login.password", password);
            System.out.println(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        try {
        	byPassSSLCertificate();
        	Map<String, ?> uriParam = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
        	
        	HttpHeaders headers = new HttpHeaders();
        	headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity request = new HttpEntity(params, headers);
            System.out.println("in create inventory ite mvariance----");
            RestTemplate restTemplate = new RestTemplate();
            //Set latest receiptId in session
            httpSession.setAttribute("receiptId", params.get("receiptId"));
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).
            		queryParam("barcode", params.get("barcode")).
            		queryParam("quantity", params.get("quantity")).
            		queryParam("receiptId", params.get("receiptId")).
            		queryParam("posTerminalId", params.get("posTerminalId")).
            		queryParam("dayId", params.get("dayId")).
            		queryParam("productName", params.get("productName")).
            		queryParam("mrp", params.get("mrp")).
            		queryParam("sp", params.get("sp")).
            		queryParam("USERNAME", params.get("USERNAME")).
            		queryParam("customerShipState", params.get("customerShipState")).
            		queryParam("customerMobileNo", params.get("customerMobileNo"));
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            System.out.println("body---"+loginResponse.getBody());
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            System.out.println("val-----"+actualObj.get("cartItems"));
            jsonString = mapper.writeValueAsString(actualObj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    /**
     * MethodName: addPosCartItem Method 
     * Description: Ajax call to add Pos Cart Items
     */
    @RequestMapping(value = "/updatePosCartItem", method = RequestMethod.GET)
    public ResponseEntity<?> updatePosCartItem(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
    	
    	final String uri = env.getProperty("updatePosCartItem");
    	//get pos cart item params
    	Hashtable<String, String> params = new Hashtable<String, String>();
    	HashMap<String, Object> test = new HashMap<String, Object>();
    	ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        String barcode = "";
    	try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            if(params.get("barcode").startsWith("\""))
            	barcode = params.get("barcode").substring(1, params.get("barcode").length() -1);
            else
            	barcode = params.get("barcode");
            params.put("createdBy", username);
            params.put("login.username", username);
            params.put("login.password", password);
            System.out.println(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        try {
        	byPassSSLCertificate();
        	Map<String, ?> uriParam = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
        	
        	HttpHeaders headers = new HttpHeaders();
        	headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity request = new HttpEntity(params, headers);
            System.out.println("in create inventory ite mvariance----");
            RestTemplate restTemplate = new RestTemplate();
            //Set latest receiptId in session
            httpSession.setAttribute("receiptId", params.get("receiptId"));
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).
            		queryParam("barcode", barcode).
            		queryParam("quantity", params.get("quantity")).
            		queryParam("receiptId", params.get("receiptId")).
            		queryParam("posTerminalId", params.get("posTerminalId")).
            		queryParam("dayId", params.get("dayId")).
            		queryParam("productName", params.get("productName")).
            		queryParam("price", params.get("price")).
            		queryParam("USERNAME", params.get("USERNAME")).
            		queryParam("customerShipState", params.get("customerShipState")).
            		queryParam("customerMobileNo", params.get("customerMobileNo"));
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            System.out.println("body---"+loginResponse.getBody());
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            System.out.println("val-----"+actualObj.get("cartItems"));
            jsonString = mapper.writeValueAsString(actualObj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    /**
     * MethodName: addScanPosCartItem Method 
     * Description: to add Cart Products.
     */
    @RequestMapping(value = "/addScanPosCartItem", method = RequestMethod.POST)
    public ModelAndView addScanPosCartItem(@RequestParam("receiptId") String receiptId,
    		@RequestParam("dayId") String dayId,
    		@RequestParam("add_product_id") String add_product_id,
    		@RequestParam("scustomerShipState") String scustomerShipState,
    		@RequestParam("posTerminalId") String posTerminalId,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username,
    		RedirectAttributes redirectAttributes) {
    	
    	final String uri = env.getProperty("addPosCartBarcode");
    	//get pos cart item params
    	Hashtable<String, String> params = new Hashtable<String, String>();
    	HashMap<String, Object> test = new HashMap<String, Object>();
    	ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
    	try {
            // convert JSON string to Map
            if(scustomerShipState.isEmpty()) {
            	scustomerShipState = (String) httpSession.getAttribute("customerShipState");
            }
    		params.put("receiptId", receiptId);
            params.put("dayId", dayId);
            params.put("quantity", "1");
            params.put("customerShipState", scustomerShipState);
            params.put("posTerminalId", posTerminalId);
            params.put("barcode", add_product_id);
            params.put("login.username", username);
            params.put("login.password", password);
            System.out.println(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        try {
        	byPassSSLCertificate();
        	
        	HttpHeaders headers = new HttpHeaders();
        	headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity request = new HttpEntity(params, headers);
            System.out.println("in create inventory ite mvariance----");
            RestTemplate restTemplate = new RestTemplate();
            //Set latest receiptId in session
            httpSession.setAttribute("receiptId", params.get("receiptId"));
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).
            		queryParam("barcode", params.get("barcode")).
            		queryParam("quantity", params.get("quantity")).
            		queryParam("receiptId", params.get("receiptId")).
            		queryParam("posTerminalId", params.get("posTerminalId")).
            		queryParam("dayId", params.get("dayId")).
            		queryParam("USERNAME", username).
            		queryParam("customerShipState", params.get("customerShipState")).
            		queryParam("customerMobileNo", params.get("customerMobileNo"));
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            System.out.println("body---"+loginResponse.getBody());
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            System.out.println("val-----"+actualObj.get("cartItems"));
            jsonString = mapper.writeValueAsString(actualObj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //redirectAttributes.addAttribute("dayId", dayId);
        return new ModelAndView("redirect:/cartlite");
    }
    
    //code for poslogin and maping to the login page 
    
    @GetMapping("poslogin")
    public ModelAndView poslogin() {
    	
    	ModelAndView modelAndView = new ModelAndView();
    	final String uri = env.getProperty("poslogin");
    	
    	  if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
              return new ModelAndView("cart/poslogin");
          } else {

              try {
             	 Map<String, Object> request = new HashMap<String, Object>();
             	 Map<String, Object> response = new HashMap<String, Object>();
             	 request.put("login.username", "sbadmin");
             	 request.put("login.password", "ofbiz");
                  //Get list of terminals
             	 RestClient restClient = applicationContext.getBean(RestClient.class);
      			 restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
      			
      			
      			 restClient.setRequestBody(request);
      			 response = restClient.callRetailService("login", true);
      			 
      			 Map<String,Object> posLoginList = (LinkedHashMap<String,Object>) response.get("responseBody");
      			 List<Object> terminalList = (ArrayList<Object>) posLoginList.get("productStoreTerminals");
      			 modelAndView.addObject("terminalList", terminalList);
      			 
              } catch (Exception e) {
                  e.printStackTrace();
              }
              return new ModelAndView("cart/terminal" , modelAndView.getModel());
          } 	 
    }
    
    @GetMapping("poslogout")
    public ModelAndView poslogout() {
    	  if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
              return new ModelAndView("cart/poslogin");
          } else {
              return new ModelAndView("cart/terminal");
          } 	 
    }
    
    
    
    //code for adminapproval and mapping to the adminapproval page 

    
    @PostMapping("saveadminapproval")
    public ModelAndView saveadminapproval(@RequestParam("terminalid") String terminalid, RedirectAttributes redirectAttributes)  {
    	
    	final String uri = env.getProperty("findDays");
    	//Set posTerminal in session.
		httpSession.setAttribute("posTerminalId", terminalid); 
		ModelAndView modelAndView = new ModelAndView();
    	try {
    		Map<String, Object> request = new HashMap<String, Object>();
    		Map<String, Object> response = new HashMap<String, Object>();
    		request.put("terminalId", terminalid);
    		
    		RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(request);
			response = restClient.callRetailService("findDays", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) response.get("responseBody");
			modelAndView.addObject("dayDetailsList",dayDetailsList.get("dayDetails"));
			
    	} catch (Exception e) {
    		
    	}
        return new ModelAndView("cart/adminapproval",modelAndView.getModel());
    }
    
    @GetMapping("adminapproval")
    public ModelAndView adminapproval()  {
    	
    	//Set posTerminal in session.
    	String terminalid = (String) httpSession.getAttribute("posTerminalId");
		httpSession.setAttribute("posTerminalId", terminalid);
		ModelAndView modelAndView = new ModelAndView();
    	try {
    		Map<String, Object> request = new HashMap<String, Object>();
    		Map<String, Object> response = new HashMap<String, Object>();
    		request.put("terminalId", terminalid);
    		
    		RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(request);
			response = restClient.callRetailService("findDays", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) response.get("responseBody");
			modelAndView.addObject("dayDetailsList",dayDetailsList.get("dayDetails"));
			
    	} catch (Exception e) {
    		
    	}
        return new ModelAndView("cart/adminapproval",modelAndView.getModel());
    }
    
    @GetMapping("editadminapproval")
    public ModelAndView editAdminApproval(@RequestParam("dayId") String dayId)  {
    	
		ModelAndView modelAndView = new ModelAndView();
    	try {
    		Map<String, Object> request = new HashMap<String, Object>();
    		Map<String, Object> response = new HashMap<String, Object>();
    		request.put("dayId", dayId);
    		
    		RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(request);
			response = restClient.callRetailService("findWebDay", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) response.get("responseBody");
			modelAndView.addObject("dayDetailList",dayDetailsList.get("dayDetail"));
			modelAndView.addObject("openingDayCashDenomination",dayDetailsList.get("openingDayCashDenomination"));
			
    	} catch (Exception e) {
    		
    	}
        return new ModelAndView("cart/editAdminApproval",modelAndView.getModel());
    }
    
    @GetMapping("dayClose")
    public ModelAndView dayClose(@RequestParam("dayId") String dayId)  {
    	
		ModelAndView modelAndView = new ModelAndView();
    	try {
    		Map<String, Object> request = new HashMap<String, Object>();
    		Map<String, Object> response = new HashMap<String, Object>();
    		request.put("dayId", dayId);
    		
    		RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(request);
			response = restClient.callRetailService("findWebDay", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) response.get("responseBody");
			modelAndView.addObject("dayDetailList",dayDetailsList.get("dayDetail"));
			modelAndView.addObject("openingDayCashDenomination",dayDetailsList.get("openingDayCashDenomination"));
			
    	} catch (Exception e) {
    		
    	}
        return new ModelAndView("cart/dayClose",modelAndView.getModel());
    }
    //code for pos transaction and mapping to the pos transaction page 
    
   
    @GetMapping("postransaction")
    public ModelAndView PosDailyTransaction() {
        return new ModelAndView("cart/postransaction");
    }

    //code for terminal user and mapping to the terminal page 
    
    @GetMapping("terminal")
    public ModelAndView terminal() 
    {
    	 final String uri = env.getProperty("poslogin");
    	 ModelAndView modelAndView = new ModelAndView();

         try {
        	 Map<String, Object> request = new HashMap<String, Object>();
        	 Map<String, Object> response = new HashMap<String, Object>();
        	 request.put("login.username", "sbadmin");
        	 request.put("login.password", "ofbiz");
             //Get list of terminals
        	 RestClient restClient = applicationContext.getBean(RestClient.class);
 			 restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
 			
 			
 			 restClient.setRequestBody(request);
 			 response = restClient.callRetailService("login", true);
 			 
 			 Map<String,Object> posLoginList = (LinkedHashMap<String,Object>) response.get("responseBody");
 			 List<Object> terminalList = (ArrayList<Object>) posLoginList.get("productStoreTerminals");
 			 modelAndView.addObject("terminalList", terminalList);
         
         } catch (Exception e) {
             e.printStackTrace();
         }
   
        return new ModelAndView("cart/terminal", modelAndView.getModel());
    }
    
    /**
     * MethodName: openingDay Method 
     * Description: Ajax call for openingDay.
     */
    @RequestMapping(value = "/openingDay", method = RequestMethod.GET)
    public ResponseEntity<?> openingDay(@RequestParam("json") String json,
    		@RequestParam("json") String cashjson,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
    	
    	final String uri = env.getProperty("openingDay");
    	//get pos cart item params
    	Map<String, Object> request = new HashMap<String, Object>();
		Map<String, Object> response = new HashMap<String, Object>();
		
    	Hashtable<String, Object> cashParams = new Hashtable<String, Object>();
    	Hashtable<String, Object> params = new Hashtable<String, Object>();
    	Hashtable<String, Object> credParams = new Hashtable<String, Object>();
    	HashMap<String, Object> test = new HashMap<String, Object>();
    	ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
    	try {
            // convert JSON string to Map
			cashParams = mapper.readValue(json, new TypeReference<Hashtable<String,Object>>() {
			});
			credParams.put("login.username", username);
			credParams.put("login.password", password);
            
			params.put("credentials", credParams);
			params.put("requestBody", cashParams);
			
			request.put("credentials", credParams);
			request.put("requestBody", cashParams);
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(credParams);
			restClient.setRequestBody(cashParams);
			response = restClient.callRetailService("openingDay", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) response.get("responseHeader");
            if(!dayDetailsList.isEmpty()) {
            	jsonString = mapper.writeValueAsString(dayDetailsList.get("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    /**
     * MethodName: openingDay Method 
     * Description: Ajax call for openingDay.
     */
    @RequestMapping(value = "/updateDay", method = RequestMethod.GET)
    public ResponseEntity<?> updateDay(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
    	
    	final String uri = env.getProperty("openingDay");
    	//get pos cart item params
    	Map<String, Object> request = new HashMap<String, Object>();
		Map<String, Object> response = new HashMap<String, Object>();
		
    	Hashtable<String, Object> cashParams = new Hashtable<String, Object>();
    	Hashtable<String, Object> params = new Hashtable<String, Object>();
    	Hashtable<String, Object> credParams = new Hashtable<String, Object>();
    	HashMap<String, Object> test = new HashMap<String, Object>();
    	ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
    	try {
            // convert JSON string to Map
			cashParams = mapper.readValue(json, new TypeReference<Hashtable<String,Object>>() {
			});
			credParams.put("login.username", username);
			credParams.put("login.password", password);
            
			params.put("credentials", credParams);
			params.put("requestBody", cashParams);
			
			request.put("credentials", credParams);
			request.put("requestBody", cashParams);
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(credParams);
			restClient.setRequestBody(cashParams);
			response = restClient.callRetailService("updateDay", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) response.get("responseHeader");
            if(!dayDetailsList.isEmpty()) {
            	jsonString = mapper.writeValueAsString(dayDetailsList.get("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    /**
     * MethodName: voidBill Method 
     * Description: Ajax call for Void Bill.
     */
    @RequestMapping(value = "/voidBill", method = RequestMethod.GET)
    public ResponseEntity<?> voidBill(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
    	
    	final String uri = env.getProperty("voidBill");
    	//get pos cart item params
    	Hashtable<String, String> params = new Hashtable<String, String>();
    	HashMap<String, Object> test = new HashMap<String, Object>();
    	ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
    	try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            
            params.put("createdBy", username);
            params.put("login.username", username);
            params.put("login.password", password);
            System.out.println(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        try {
        	byPassSSLCertificate();
        	Map<String, ?> uriParam = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
        	
        	HttpHeaders headers = new HttpHeaders();
        	headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity request = new HttpEntity(params, headers);
            System.out.println("in create inventory ite mvariance----");
            RestTemplate restTemplate = new RestTemplate();
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).
            		queryParam("receiptId", params.get("receiptId")).
            		queryParam("posTerminalId", params.get("posTerminalId")).
            		queryParam("dayId", params.get("dayId")).
            		queryParam("USERNAME", params.get("USERNAME"));
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            System.out.println("body---"+loginResponse.getBody());
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            System.out.println("val-----"+actualObj.get("cartItems"));
            jsonString = mapper.writeValueAsString(actualObj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    
    
    //HoldBill
    @RequestMapping(value = "/holdBill", method = RequestMethod.GET)
    public ResponseEntity<?> holdBill(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
    	
    	final String uri = env.getProperty("holdBill");
    	final String generatReceiptIdUri = env.getProperty("generateReceiptId");
    	String facilityId = getFacilityId();
    	//get pos cart item params
    	Hashtable<String, String> params = new Hashtable<String, String>();
    	HashMap<String, Object> test = new HashMap<String, Object>();
    	ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        String receiptId = "";
        Hashtable<String, String> generateReceiptParams = new Hashtable<String, String>();
        HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	JsonNode actualObj = null;
    	ObjectNode finalObjNode = null;
    	try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            
            params.put("createdBy", username);
            params.put("login.username", username);
            params.put("login.password", password);
            System.out.println(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
        	byPassSSLCertificate();
        	Map<String, ?> uriParam = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
        	
        	
            HttpEntity request = new HttpEntity(params, headers);
            System.out.println("in create inventory ite mvariance----");
            RestTemplate restTemplate = new RestTemplate();
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).
            		queryParam("receiptId", params.get("receiptId")).
            		queryParam("posTerminalId", params.get("posTerminalId")).
            		queryParam("dayId", params.get("dayId")).
            		queryParam("posPaidAmount", params.get("posPaidAmount")).
            		queryParam("paymentType", params.get("paymentType")).
            		queryParam("customerShipState", params.get("customerShipState")).
            		queryParam("USERNAME", params.get("USERNAME"));
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            System.out.println("body---"+loginResponse.getBody());
            actualObj = mapper.readTree(loginResponse.getBody());
            //System.out.println("val-----"+actualObj.get("cartItems"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
        	generateReceiptParams.put("dayId", params.get("dayId"));
        	generateReceiptParams.put("facilityId", facilityId);
        	HttpEntity request = new HttpEntity(generateReceiptParams, headers);
        	RestTemplate restTemplate = new RestTemplate();
        	
        	HashMapResponse result = restTemplate.postForObject(generatReceiptIdUri, request, HashMapResponse.class);
        	HashMap<String, Object> list = result.getResponse();
        	receiptId = (String) list.get("receiptId");
        	finalObjNode = (ObjectNode) actualObj;
        	finalObjNode.put("receiptId", receiptId);
        	httpSession.setAttribute("receiptId", receiptId);
        	jsonString = mapper.writeValueAsString(finalObjNode);
        	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    
    
    @RequestMapping(value = "/getDayPosCartItems", method = RequestMethod.GET)
    public ResponseEntity<?> populateholdBill(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
    	
    	final String uri = env.getProperty("getDayPosCartItems");
    	//get pos cart item params
    	Hashtable<String, String> params = new Hashtable<String, String>();
    	HashMap<String, Object> test = new HashMap<String, Object>();
    	ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
    	try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            
            params.put("createdBy", username);
            params.put("login.username", username);
            params.put("login.password", password);
            System.out.println(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        try {
        	byPassSSLCertificate();
        	Map<String, ?> uriParam = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
        	
        	HttpHeaders headers = new HttpHeaders();
        	headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity request = new HttpEntity(params, headers);
            System.out.println("in create inventory ite mvariance----");
            RestTemplate restTemplate = new RestTemplate();
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).
            		queryParam("posTerminalId", params.get("posTerminalId")).
            		queryParam("dayId", params.get("dayId")).
            		queryParam("USERNAME", params.get("USERNAME"));
            System.out.println("Builder---"+builder.toUriString());
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            System.out.println("body---"+loginResponse.getBody());
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            //System.out.println("val-----"+actualObj.get("cartItems"));
            jsonString = mapper.writeValueAsString(actualObj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    
    @RequestMapping(value = "/deletePosCartItem", method = RequestMethod.GET)
    public ResponseEntity<?> deletePosCartItem(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
    	
    	final String uri = env.getProperty("deletePosCartItem");
    	//get pos cart item params
    	Hashtable<String, String> params = new Hashtable<String, String>();
    	HashMap<String, Object> test = new HashMap<String, Object>();
    	ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
    	try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            
            params.put("createdBy", username);
            params.put("login.username", username);
            params.put("login.password", password);
            System.out.println(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        try {
        	byPassSSLCertificate();
        	Map<String, ?> uriParam = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
        	
        	HttpHeaders headers = new HttpHeaders();
        	headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity request = new HttpEntity(params, headers);
            System.out.println("in create inventory ite mvariance----");
            RestTemplate restTemplate = new RestTemplate();
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).
            		queryParam("delete_product_id", params.get("delete_product_id")).
            		queryParam("quantity", params.get("quantity")).
            		queryParam("receiptId", params.get("receiptId")).
            		queryParam("posTerminalId", params.get("posTerminalId")).
            		queryParam("dayId", params.get("dayId")).
            		queryParam("USERNAME", params.get("USERNAME")).
            		queryParam("customerShipState", params.get("customerShipState"));
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            System.out.println("body---"+loginResponse.getBody());
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            System.out.println("val-----"+actualObj.get("cartItems"));
            jsonString = mapper.writeValueAsString(actualObj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/createCustomerBO", method = RequestMethod.GET)
	public ResponseEntity<?> createCustomerBO(@RequestParam("json") String json,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
	 	
	 	HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		final String uri = env.getProperty("createCustomer");
		final String findPosCustUri = env.getProperty("findPosCustomers");
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
        ObjectMapper mapper = new ObjectMapper();
        String lastName = "";
        String productStoreId = getStoreId(); 
        
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
			
			System.out.println(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		params.put("productStoreId", productStoreId);
		//
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
		HashMap<String, Object> list = result.getResponse();
		
		Map<Object, Object> custParams = new HashMap<>();
		custParams.put("contactNumber", "");
		HttpEntity custRequest = new HttpEntity(custParams, headers);
		ResponseEntity response = restTemplate.postForEntity(findPosCustUri, custRequest, List.class);
		
		if (list != null) {
			//hashMap.put("message", "Success");
			hashMap.put("CustomerList", response.getBody());
		}

		return new ResponseEntity<>(hashMap, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/findCustomerBO", method = RequestMethod.GET)
	public ResponseEntity<?> findCustomerBO(@RequestParam("json") String json,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
	 	
	 	HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		final String uri = env.getProperty("findCustomerBO");
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

		return new ResponseEntity<>(list, HttpStatus.OK);
	}
    @RequestMapping(value = "payment", method = RequestMethod.POST)
    public ModelAndView payment(@RequestParam("preceiptId") String receiptId,
    		@RequestParam("pdayId") String dayId,
    		RedirectAttributes redirectAttributes) {
    	
    	httpSession.setAttribute("receiptId", receiptId);
    	httpSession.setAttribute("dayId", dayId);
    	
    	String posTerminalId = (String) httpSession.getAttribute("posTerminalId");
		String username = (String) httpSession.getAttribute("username");
		
		final String dayPosCartItemsUri = env.getProperty("getPosCartItems");
    	ModelAndView modelAndView = new ModelAndView();
    	Hashtable<String, String> params = new Hashtable<String, String>();
    	try {
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		ObjectMapper mapper = new ObjectMapper();
    		
			byPassSSLCertificate();
            System.out.println("came into get cart Items");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dayPosCartItemsUri).
            		queryParam("receiptId", receiptId).
            		queryParam("posTerminalId", posTerminalId).
            		queryParam("dayId", dayId).
            		queryParam("USERNAME", username);
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            JsonNode cartObj = null;
            ObjectNode cartObjNode = null;
            Map<String, Object> cartItemsMap = new HashMap<String, Object>();
            List<Object> cartItemList = new ArrayList<Object>();
            if(actualObj.get("cartItems") != null) {
            	modelAndView.addObject("totalDue", actualObj.get("totalDue"));
            	modelAndView.addObject("subTotal", actualObj.get("subTotal"));
            	modelAndView.addObject("totalSalesTax", actualObj.get("totalSalesTax"));
            	modelAndView.addObject("cartSize", actualObj.get("cartSize"));
            	modelAndView.addObject("displayGrandTotal", actualObj.get("displayGrandTotal"));
            	modelAndView.addObject("balanceAmount", actualObj.get("balanceAmount"));
            	
                //modelAndView.addObject("cartItems", actualObj.get("cartItems"));
                modelAndView.addObject("cartItems", cartItemList);
                modelAndView.addObject("itemTax", cartObj.get("itemTax"));
                //NodeBean toValue = mapper.readValue(parser, NodeBean.class);
            }
		} catch (Exception e) {
			
		}
        return new ModelAndView("redirect:/cartPayment");
    }
    
    @RequestMapping(value = "cartPayment", method = RequestMethod.GET)
    public ModelAndView cartPayment() {
    	
    	String receiptId = (String) httpSession.getAttribute("receiptId");
    	String dayId = (String) httpSession.getAttribute("dayId");
    	String posTerminalId = (String) httpSession.getAttribute("posTerminalId");
		String username = (String) httpSession.getAttribute("username");
		
		final String dayPosCartItemsUri = env.getProperty("getPosCartItems");
		final String cartViewPaymentsUri = env.getProperty("cartViewPayments");
    	ModelAndView modelAndView = new ModelAndView();
    	Hashtable<String, String> params = new Hashtable<String, String>();
    	try {
    		byPassSSLCertificate();
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		ObjectMapper mapper = new ObjectMapper();
    		//Code for getting Payments
    		UriComponentsBuilder viewPaymentbuilder = UriComponentsBuilder.fromHttpUrl(cartViewPaymentsUri).
            		queryParam("receiptId", receiptId).
            		queryParam("posTerminalId", posTerminalId).
            		queryParam("dayId", dayId).
            		queryParam("USERNAME", username);
    		
    		String url = viewPaymentbuilder.toUriString();
    		HttpEntity<String> viewPaymentEntity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> viewPaymentResponse = restTemplate.exchange(viewPaymentbuilder.toUriString(), 
            		HttpMethod.POST, viewPaymentEntity, String.class);
            JsonNode viewPaymentObj = mapper.readTree(viewPaymentResponse.getBody());
            
            modelAndView.addObject("paymentItems", viewPaymentObj.get("paymentItems"));
            modelAndView.addObject("paidAmount", viewPaymentObj.get("paidAmount"));
    		//End of code for getting Payments
            System.out.println("came into get cart Items");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dayPosCartItemsUri).
            		queryParam("receiptId", receiptId).
            		queryParam("posTerminalId", posTerminalId).
            		queryParam("dayId", dayId).
            		queryParam("USERNAME", username);
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            JsonNode cartObj = null;
            ObjectNode cartObjNode = null;
            Map<String, Object> cartItemsMap = new HashMap<String, Object>();
            List<Object> cartItemList = new ArrayList<Object>();
            BigDecimal TOT_CGST_TAX = BigDecimal.ZERO;
            BigDecimal TOT_SGST_TAX = BigDecimal.ZERO;
            BigDecimal TOT_IGST_TAX = BigDecimal.ZERO;
            BigDecimal TOT_AMT = BigDecimal.ZERO;
            if(actualObj.get("cartItems") != null) {
            	for (int i=0; i < actualObj.get("cartItems").size(); i++) {
            		BigDecimal CGST_TAX = BigDecimal.ZERO;
            		BigDecimal SGST_TAX = BigDecimal.ZERO;
            		BigDecimal IGST_TAX = BigDecimal.ZERO;
            		BigDecimal prdPrice = BigDecimal.ZERO;
            		BigDecimal totCartItemAmt = BigDecimal.ZERO;
            		
            		cartObj = mapper.readTree(actualObj.get("cartItems").get(i).toString());
            		cartObjNode = (ObjectNode) cartObj;
            		if(cartObj.get("productTotalAmt") != null) {
            			prdPrice = new BigDecimal(cartObj.get("productTotalAmt").toString());
            			totCartItemAmt = totCartItemAmt.add(prdPrice);
            			TOT_AMT = TOT_AMT.add(prdPrice);
					}
            		
            		if(cartObj.get("itemTax").get("CGST_TAX") != null) {
            			CGST_TAX = new BigDecimal(cartObj.get("itemTax").get("CGST_TAX").toString());
            			totCartItemAmt = totCartItemAmt.add(CGST_TAX);
            			TOT_CGST_TAX = TOT_CGST_TAX.add(CGST_TAX);
            			TOT_AMT = TOT_AMT.add(CGST_TAX);
            		}
					if(cartObj.get("itemTax").get("SGST_TAX") != null) {
						SGST_TAX = new BigDecimal(cartObj.get("itemTax").get("SGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(SGST_TAX);
						TOT_SGST_TAX = TOT_SGST_TAX.add(SGST_TAX);
						TOT_AMT = TOT_AMT.add(SGST_TAX);
					}
					if(cartObj.get("itemTax").get("IGST_TAX") != null) {
						IGST_TAX = new BigDecimal(cartObj.get("itemTax").get("IGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(IGST_TAX);
						TOT_IGST_TAX = TOT_IGST_TAX.add(IGST_TAX);
						TOT_AMT = TOT_AMT.add(IGST_TAX);
					}
            		
            		cartObjNode.put("CGST_TAX", cartObj.get("itemTax").get("CGST_TAX"));
            		cartObjNode.put("SGST_TAX", cartObj.get("itemTax").get("SGST_TAX"));
            		cartObjNode.put("IGST_TAX", cartObj.get("itemTax").get("IGST_TAX"));
            		cartObjNode.put("Item_Tot_Amt", totCartItemAmt);
            		cartItemsMap.put("cartItemsMap", cartObjNode);
            		System.out.println("cartItemsMap--"+cartItemsMap.get("cartItemsMap"));
            		cartItemList.add(cartItemsMap.get("cartItemsMap"));
            	}
            	modelAndView.addObject("totalDue", actualObj.get("totalDue"));
            	modelAndView.addObject("subTotal", actualObj.get("subTotal"));
            	modelAndView.addObject("totalSalesTax", actualObj.get("totalSalesTax"));
            	modelAndView.addObject("cartSize", actualObj.get("cartSize"));
            	modelAndView.addObject("displayGrandTotal", actualObj.get("displayGrandTotal"));
            	modelAndView.addObject("balanceAmount", actualObj.get("balanceAmount"));
            	
            	modelAndView.addObject("TOT_CGST_TAX", TOT_CGST_TAX);
            	modelAndView.addObject("TOT_SGST_TAX", TOT_SGST_TAX);
            	modelAndView.addObject("TOT_IGST_TAX", TOT_IGST_TAX);
            	modelAndView.addObject("TOT_AMT", TOT_AMT);
            	
                //modelAndView.addObject("cartItems", actualObj.get("cartItems"));
                modelAndView.addObject("cartItems", cartItemList);
                modelAndView.addObject("itemTax", cartObj.get("itemTax"));
                //NodeBean toValue = mapper.readValue(parser, NodeBean.class);
            }
		} catch (Exception e) {
			
		}
        return new ModelAndView("cart/payment", modelAndView.getModel());
    }
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "cartBOAddPayments", method = RequestMethod.GET)
	public ResponseEntity<?> cartBOAddPayments(@RequestParam("json") String json,
			@RequestParam("calcCreditAndCreditNote") boolean calcCreditAndCreditNote,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
	 	
	 	HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		final String uri = env.getProperty("cartAddPayments");
		final String createPosRetailorCreditUri = env.getProperty("createPosRetailorCredit");
		final String createPosCustomerCreditNoteUri = env.getProperty("createPosCustomerCreditNote");
		
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        String creditId = "", paymentType = "";
        String productStoreId = getStoreId();
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
			
			System.out.println(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//
		try {
			byPassSSLCertificate();
        	Map<String, ?> uriParam = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
        	
        	HttpHeaders headers = new HttpHeaders();
        	headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity request = new HttpEntity(params, headers);
            System.out.println("in create inventory ite mvariance----");
            RestTemplate restTemplate = new RestTemplate();
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).
            		queryParam("creditId", params.get("creditId")).
            		queryParam("receiptId", params.get("receiptId")).
            		queryParam("dayId", params.get("dayId")).
            		queryParam("posTerminalId", params.get("posTerminalId")).
            		queryParam("cashToPay", params.get("cashToPay")).
            		queryParam("paymentType", params.get("paymentType")).
            		queryParam("USERNAME", params.get("username"));
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> addPaymentResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            
            JsonNode actualObj = mapper.readTree(addPaymentResponse.getBody());
            System.out.println("val-----"+actualObj.get("paymentItems"));
            jsonString = mapper.writeValueAsString(actualObj);
            HashMapResponse result = null;
          //Code for create credit trx
            if(calcCreditAndCreditNote) {
            	 Hashtable<String, Object> createCreditParams = new Hashtable<String, Object>();
                 headers.add("Content-Type", "application/json");
                 try {
                	paymentType = (String) params.get("paymentType");
                	 
         			createCreditParams.put("customerMobileNum", params.get("contactNumber"));
         			createCreditParams.put("productStoreId", productStoreId);
         			createCreditParams.put("billId", params.get("receiptId"));
         			createCreditParams.put("dayId", params.get("dayId"));
         			createCreditParams.put("type", params.get("paymentType"));
         			createCreditParams.put("retailer", params.get("retailer"));
         			createCreditParams.put("customer", params.get("customer"));
         			
                 	RestTemplate createCreditRestTemplate = new RestTemplate();
                 	if(paymentType.equals("CREDIT_NOTE")) {
                 		createCreditParams.put("creditId", params.get("creditId"));
                 		createCreditParams.put("paidAmount", params.get("cashToPay"));
                 		HttpEntity createCreditRequest = new HttpEntity(createCreditParams, headers);
                 		result = createCreditRestTemplate.postForObject(createPosCustomerCreditNoteUri, 
                       		 createCreditRequest, HashMapResponse.class);
         			}else {
         				createCreditParams.put("creditAmount", params.get("cashToPay"));
         				HttpEntity createCreditRequest = new HttpEntity(createCreditParams, headers);
         				result = createCreditRestTemplate.postForObject(createPosRetailorCreditUri, 
                       		 createCreditRequest, HashMapResponse.class);
         			}

                     
                     HashMap<String, Object> list = result.getResponse();
                     creditId = (String) list.get("creditId");
                     if(creditId.equals(null) || creditId.equals("") || creditId.equals(" ")) {
                     	return new ResponseEntity<>(jsonString, HttpStatus.BAD_REQUEST);
                     }else {
                     	try {
                             jsonString = mapper.writeValueAsString(creditId);
                             System.out.println("List     " + jsonString);
                         } catch (JsonProcessingException e) {
                             // TODO Auto-generated catch block
                             e.printStackTrace();
                         }
                     }
                     
         			System.out.println(params);
         		} catch (Exception e) {
         			e.printStackTrace();
         		}
            }
            //End of code for create credit txn
		} catch (Exception e) {
			
		}
		return new ResponseEntity<>(jsonString, HttpStatus.OK);
	}
    
    @RequestMapping(value = "cartLiteCheckout", method = RequestMethod.GET)
    public ResponseEntity<?> cartLiteCheckout(@RequestParam("json") String json,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
    	
    	
    	String jsonString = "";
		String customerShipState = (String) httpSession.getAttribute("customerShipState");
		
		final String cartPayFinishUri = env.getProperty("cartPayFinish");
		final String generatReceiptIdUri = env.getProperty("generateReceiptId");
		//final String uri = env.getProperty("cartAddPayments");
		String facilityId = getFacilityId();
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
        ObjectMapper mapper = new ObjectMapper();
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
			
			httpSession.setAttribute("receiptId", params.get("receiptId"));
	    	httpSession.setAttribute("dayId", params.get("dayId"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    	Hashtable<String, String> generateReceiptParams = new Hashtable<String, String>();
    	HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		//Code for cart payment.
		try {
        	Map<String, ?> uriParam = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
        	
        	HttpHeaders paymentHeaders = new HttpHeaders();
        	headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity paymentRequest = new HttpEntity(params, paymentHeaders);
            RestTemplate restTemplate = new RestTemplate();
            
            /*UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).
            		queryParam("receiptId", params.get("receiptId")).
            		queryParam("dayId", params.get("dayId")).
            		queryParam("posTerminalId", params.get("posTerminalId")).
            		queryParam("cashToPay", params.get("cashToPay")).
            		queryParam("paymentType", params.get("paymentType")).
            		queryParam("USERNAME", params.get("username"));*/
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(paymentRequest.toString(), headers);
            //ResponseEntity<String> addPaymentResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            //System.out.println("body---"+addPaymentResponse.getBody());
            //JsonNode actualObj = mapper.readTree(addPaymentResponse.getBody());
            //System.out.println("val-----"+actualObj.get("paymentItems"));
            //jsonString = mapper.writeValueAsString(actualObj);
            
		} catch (Exception e) {
			
		}
		//End of code for cart payment
    	try {
    		
    		HttpEntity request = new HttpEntity(params, headers);
    		
			byPassSSLCertificate();
            System.out.println("came into get cart Items");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(cartPayFinishUri).
            		queryParam("receiptId", params.get("receiptId")).
            		queryParam("dayId", params.get("dayId")).
            		queryParam("posTerminalId", params.get("posTerminalId")).
            		queryParam("USERNAME", username).
            		queryParam("customerShipState", customerShipState).
            		queryParam("amountCash", params.get("amountCash")).
            		queryParam("paymentType", "CASH").
            		queryParam("paymentTransaction", "1111111");
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> checkoutResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            JsonNode actualObj = mapper.readTree(checkoutResponse.getBody());
            jsonString = mapper.writeValueAsString(actualObj);
		} catch (Exception e) {
			
		}
    	try {
        	generateReceiptParams.put("dayId", (String) params.get("dayId"));
        	generateReceiptParams.put("facilityId", facilityId);
        	HttpEntity request = new HttpEntity(generateReceiptParams, headers);
        	RestTemplate restTemplate = new RestTemplate();
        	
        	HashMapResponse result = restTemplate.postForObject(generatReceiptIdUri, request, HashMapResponse.class);
        	HashMap<String, Object> list = result.getResponse();
        	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    @RequestMapping(value = "cartCheckout", method = RequestMethod.POST)
    public ModelAndView cartCheckout(@RequestParam("preceiptId") String receiptId,
    		@RequestParam("pdayId") String dayId,
    		@RequestParam("amountCash") String amountCash,
    		RedirectAttributes redirectAttributes) {
    	
    	httpSession.setAttribute("receiptId", receiptId);
    	httpSession.setAttribute("dayId", dayId);
    	
    	String posTerminalId = (String) httpSession.getAttribute("posTerminalId");
		String username = (String) httpSession.getAttribute("username");
		String customerShipState = (String) httpSession.getAttribute("customerShipState");
		String facilityId = getFacilityId();
		
		final String cartPayFinishUri = env.getProperty("cartPayFinish");
		final String generatReceiptIdUri = env.getProperty("generateReceiptId");
		
    	ModelAndView modelAndView = new ModelAndView();
    	Hashtable<String, String> params = new Hashtable<String, String>();
    	Hashtable<String, String> generateReceiptParams = new Hashtable<String, String>();
    	HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
    	try {
    		
    		HttpEntity request = new HttpEntity(params, headers);
    		ObjectMapper mapper = new ObjectMapper();
    		
			byPassSSLCertificate();
            System.out.println("came into get cart Items");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(cartPayFinishUri).
            		queryParam("receiptId", receiptId).
            		queryParam("dayId", dayId).
            		queryParam("posTerminalId", posTerminalId).
            		queryParam("USERNAME", username).
            		queryParam("customerShipState", customerShipState).
            		queryParam("amountCash", amountCash).
            		queryParam("paymentType", "CASH").
            		queryParam("paymentTransaction", "1111111");
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> checkoutResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            JsonNode actualObj = mapper.readTree(checkoutResponse.getBody());
            
		} catch (Exception e) {
			
		}
    	try {
        	generateReceiptParams.put("dayId", dayId);
        	generateReceiptParams.put("facilityId", facilityId);
        	
        	HttpEntity request = new HttpEntity(generateReceiptParams, headers);
        	RestTemplate restTemplate = new RestTemplate();
        	
        	HashMapResponse result = restTemplate.postForObject(generatReceiptIdUri, request, HashMapResponse.class);
        	HashMap<String, Object> list = result.getResponse();
        	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return new ModelAndView("redirect:/cartlite");
    }
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "connectPrinter", method = RequestMethod.GET)
	public ResponseEntity<?> connectPrinter(@RequestParam("printerIp") String printerIp) {
    	String jsonString = "";
		try {
			Socket socket = new Socket(printerIp, 9100);
			socket.setSoTimeout(100660);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		httpSession.setAttribute("printerIp", printerIp);
    	return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "printCartItem", method = RequestMethod.GET)
	public ResponseEntity<?> printCartItem(@RequestParam("json") String json, @RequestParam("username") String username,
			@RequestParam("password") String password) {

		String prdName = "", productQty = "", productTotalAmt = "";

		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		final String dayPosCartItemsUri = env.getProperty("getPosCartItems");
		final String uri = env.getProperty("cartAddPayments");
		final String getTaxInvoiceUri = env.getProperty("getTaxInvoice");

		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
		Hashtable<String, Object> storeAddressParams = new Hashtable<String, Object>();
		Hashtable<String, Object> storeAddressCredParams = new Hashtable<String, Object>();
		Hashtable<String, Object> storeAddressRequestParams = new Hashtable<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		String facilityName = "", directions = "", postalCode = "", city = "", address2 = "", address1 = "";
		BigDecimal grandTotal = BigDecimal.ZERO;
		BigDecimal spGrandTotal = BigDecimal.ZERO;
		float discAmount = (float) 0.0;
		
		StringBuilder directionsBuilder = new StringBuilder();
		Map<String, Object> response = new HashMap<String, Object>();
		String printerIp = (String) httpSession.getAttribute("printerIp");
		printerIp = "192.168.0.31"; 
		PrinterService printerService = null;
		JsonNode actualObj = null;
		JsonNode cartObj = null;
		ObjectNode cartObjNode = null;
		String printName = "";
		String taxEnable = "N";
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
			// Get Store Address
			storeAddressCredParams.put("login.username", username);
			storeAddressCredParams.put("login.password", password);
			storeAddressRequestParams.put("posTerminalId", params.get("posTerminalId"));

			storeAddressParams.put("credentials", storeAddressCredParams);
			storeAddressParams.put("requestBody", storeAddressRequestParams);

			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());

			restClient.setRequestBody(storeAddressCredParams);
			restClient.setRequestBody(storeAddressRequestParams);
			response = restClient.callRetailService("getPOSStoreAddress", true);
			Map<String, Object> storeAddress = (LinkedHashMap<String, Object>) response.get("response");
			if (!storeAddress.isEmpty()) {
				address1 = (String) storeAddress.get("address1");
				address2 = (String) storeAddress.get("address2");
				city = (String) storeAddress.get("city");
				postalCode = (String) storeAddress.get("postalCode");
				directions = (String) storeAddress.get("directions");
				facilityName = (String) storeAddress.get("facilityName");
				// split by 27
				directionsBuilder = new StringBuilder(directions);
				int f = 0;
				while (f + 20 < directionsBuilder.length() && (f = directionsBuilder.lastIndexOf(" ", f + 20)) != -1) {
					directionsBuilder.replace(f, f + 1, "\n");
				}
			}
			// End of getting Store Address
		} catch (Exception e) {
			e.printStackTrace();
		}
		//
		try {
			byPassSSLCertificate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity request = new HttpEntity(params, headers);
			RestTemplate restTemplate = new RestTemplate();
			//
			UriComponentsBuilder dayPosCartItemBuilder = UriComponentsBuilder.fromHttpUrl(dayPosCartItemsUri)
					.queryParam("receiptId", params.get("receiptId"))
					.queryParam("posTerminalId", params.get("posTerminalId")).queryParam("dayId", params.get("dayId"))
					.queryParam("USERNAME", username)
					.queryParam("isPrint", "Y");

			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
			ResponseEntity<String> posCartResponse = restTemplate.exchange(dayPosCartItemBuilder.toUriString(),
					HttpMethod.POST, entity, String.class);
			actualObj = mapper.readTree(posCartResponse.getBody());

			Map<String, Object> cartItemsMap = new HashMap<String, Object>();
			List<Object> cartItemList = new ArrayList<Object>();
			//code for checking tax enabled or not
    		Hashtable<String, Object> taxEnableParams = new Hashtable<String, Object>();
    		taxEnableParams.put("receiptId", params.get("receiptId"));
    		
    		HttpEntity taxEnableRequest = new HttpEntity(taxEnableParams, headers);
			HashMapResponse taxEnableResult = restTemplate.postForObject(getTaxInvoiceUri,
					taxEnableRequest, HashMapResponse.class);
			HashMap<String, Object> taxEnableList = taxEnableResult.getResponse();
			taxEnable = (String) taxEnableList.get("isChecked");
    		
    		//Code for getting Payments
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			// Code to get list of printers.

			PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
			if (services.length != 0 && services != null) {
				for (PrintService service : services) {
					printName = service.getName();
				}
			}
			// End of code to get list of printers.
			// Code for printer settings
			// Printer printer = new NetworkPrinter("192.168.0.31", 9100);
			OutputStream printer1 = null;
			Printer printer = new NetworkPrinter(printerIp, 9100);
			try {
				//EscPosWebPrinter printer11 = new EscPosWebPrinter("192.168.0.31", 9100);
				//printer11.main();
				Socket socket = new Socket(printerIp, 9100);
				socket.setSoTimeout(1000);
				printer1 = socket.getOutputStream();
				//
				/*socketOut = socket.getOutputStream();
				writer = new OutputStreamWriter(socketOut, "GBK");
				socketOut.write(27);
				socketOut.write(27);
				//
				writer = new OutputStreamWriter(printer1, "GBK");
				printer1.write(27);
				printer1.write(27);*/
				//writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

			printerService = new PrinterService(printer);
			// End of code for printer settings
			// Code for bill print
			printerService.setTextAlignCenter();
			printerService.printLn(facilityName);
			printerService.setTextAlignCenter();
			printerService.printLn(address1);
			printerService.setTextAlignCenter();
			printerService.printLn(address2);
			printerService.setTextAlignCenter();
			printerService.printLn(directionsBuilder.toString());

			// printerService.setTextAlignCenter();
			// printerService.printLn("near Wester Express Highway");

			printerService.setTextAlignCenter();
			printerService.printLn("Bill No:"+params.get("receiptId"));
			printerService.printLn("GSTIN:1100303030338383");
			printerService.lineBreak();
			printerService.printLn("------------------------------------------------");
			// printerService.printLn("Item" + "\t\t" + "Qty" + "\t\t" + "Total");
			//printerService.printLn("Item" + "\t\t" + "  Qty" + "\t\t" + "Total");
			printerService.printLn(String.format("%1$-25s %2$10s %3$10s%n", "Item", "Qty", "Total"));
			/*
			 * printerService.setTextAlignLeft(); printerService.print("Item");
			 * printerService.setTextAlignCenter(); printerService.print("Qty");
			 * printerService.setTextAlignRight(); printerService.print("Total");
			 * printerService.lineBreak();
			 */
			printerService.printLn("------------------------------------------------");
			// loop cart items
			BigDecimal TOT_CGST_TAX = BigDecimal.ZERO;
			BigDecimal TOT_SGST_TAX = BigDecimal.ZERO;
			BigDecimal TOT_IGST_TAX = BigDecimal.ZERO;
			BigDecimal TOT_AMT = BigDecimal.ZERO;
			BigDecimal Addl_Discount = BigDecimal.ZERO;
			BigDecimal Addl_Charge = BigDecimal.ZERO;
			if(actualObj.get("addlDiscAmt") != null) {
				Addl_Discount = (BigDecimal) actualObj.get("addlDiscAmt").decimalValue();
            }
			if(actualObj.get("addlCharge") != null) {
				Addl_Charge = (BigDecimal) actualObj.get("addlCharge").decimalValue();
            }
			if(actualObj.get("displayMRPGrandTotal") != null) {
                grandTotal = (BigDecimal) actualObj.get("displayMRPGrandTotal").decimalValue();
            }
            if(actualObj.get("displaySPGrandTotal") != null) {
            	spGrandTotal = (BigDecimal) actualObj.get("displaySPGrandTotal").decimalValue();
            }
                discAmount = (grandTotal.floatValue() - spGrandTotal.floatValue());
            if(discAmount >= 0)
            	discAmount = discAmount;
            else
            	discAmount = 0.0f;
            
			if (actualObj.get("cartItems") != null) {
				for (int i = 0; i < actualObj.get("cartItems").size(); i++) {
					BigDecimal CGST_TAX = BigDecimal.ZERO;
					BigDecimal SGST_TAX = BigDecimal.ZERO;
					BigDecimal IGST_TAX = BigDecimal.ZERO;
					BigDecimal prdPrice = BigDecimal.ZERO;
					BigDecimal totCartItemAmt = BigDecimal.ZERO;

					cartObj = mapper.readTree(actualObj.get("cartItems").get(i).toString());
					cartObjNode = (ObjectNode) cartObj;

					if (cartObj.get("productTotalAmt") != null) {
						prdPrice = new BigDecimal(cartObj.get("productTotalAmt").toString());
						totCartItemAmt = totCartItemAmt.add(prdPrice);
						TOT_AMT = TOT_AMT.add(prdPrice);
					}

					if (cartObj.get("itemTax").get("CGST_TAX") != null) {
						CGST_TAX = new BigDecimal(cartObj.get("itemTax").get("CGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(CGST_TAX);
						TOT_CGST_TAX = TOT_CGST_TAX.add(CGST_TAX);

						TOT_CGST_TAX = TOT_CGST_TAX.setScale(0, BigDecimal.ROUND_HALF_UP);
						TOT_CGST_TAX = TOT_CGST_TAX.setScale(2, BigDecimal.ROUND_HALF_UP);

						//TOT_AMT = TOT_AMT.add(CGST_TAX);
					}
					if (cartObj.get("itemTax").get("SGST_TAX") != null) {
						SGST_TAX = new BigDecimal(cartObj.get("itemTax").get("SGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(SGST_TAX);
						TOT_SGST_TAX = TOT_SGST_TAX.add(SGST_TAX);

						TOT_SGST_TAX = TOT_SGST_TAX.setScale(0, BigDecimal.ROUND_HALF_UP);
						TOT_SGST_TAX = TOT_SGST_TAX.setScale(2, BigDecimal.ROUND_HALF_UP);

						//TOT_AMT = TOT_AMT.add(SGST_TAX);
					}
					if (cartObj.get("itemTax").get("IGST_TAX") != null) {
						IGST_TAX = new BigDecimal(cartObj.get("itemTax").get("IGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(IGST_TAX);
						TOT_IGST_TAX = TOT_IGST_TAX.add(IGST_TAX);
						//TOT_AMT = TOT_AMT.add(IGST_TAX);
					}
					TOT_AMT = TOT_AMT.setScale(0, BigDecimal.ROUND_HALF_UP);
					TOT_AMT = TOT_AMT.setScale(2, BigDecimal.ROUND_HALF_UP);

					printerService.setTextAlignLeft();
					prdName = cartObjNode.get("productName").toString();
					prdName = prdName.replace("\"", "");
					prdName.trim();
					StringBuilder prdNameBuilder = new StringBuilder(prdName);
					productQty = cartObjNode.get("productQty").toString();
					productTotalAmt = cartObjNode.get("productTotalAmt").toString();
					
					//25 spaces
					//product name length shoul be 25- if less than 25-interduce spaces
					//%1$-25s %2$10s %3$10s%n
					//Code
					if (prdName.length() > 25) {
						Boolean first = true;
						for (int m = 0; m < prdName.length(); m += 25) {
						String productname1 = prdName.substring(m, Math.min(m + 25, prdName.length()));
						if (first) {
							printerService.print(String.format("%1$-25s %2$10s %3$10s%n", productname1, productQty, productTotalAmt));
						first = false;
						} else {
							printerService.print(String.format("%1$-25s %2$10s %3$10s%n", productname1, "", ""));
						}
						}

						} else {
							printerService.print(String.format("%1$-25s %2$10s %3$10s%n", prdName, productQty, productTotalAmt));
						}

					//End of code
					int j = 0;
					while (j + 25 < prdNameBuilder.length() && (j = prdNameBuilder.lastIndexOf(" ", j + 25)) != -1) {
						prdNameBuilder.replace(j, j + 1, "\n");
					}
					
					/*printerService.print(prdNameBuilder.toString());
					printerService.print("\t");

					productQty = cartObjNode.get("productQty").toString();
					printerService.print(productQty);
					printerService.print("\t");

					printerService.setTextAlignRight();
					productTotalAmt = cartObjNode.get("productTotalAmt").toString();
					printerService.setTextAlignRight();
					printerService.print("\t");
					printerService.print(productTotalAmt);*/
					printerService.lineBreak();
				}
			}
			printerService.printLn("------------------------------------------------");
			// End of loop cart items
			String cartSize = actualObj.get("cartSize").toString();
			printerService.setTextAlignRight();
			printerService.setTextProperties("RIGHT", "A", "NORMAL", 30, 1, 9);
			printerService.print("Total Qty : ");

			printerService.setTextAlignLeft();
			printerService.print(cartSize);
			printerService.lineBreak();
			//code for tax
			if((taxEnable != null) && taxEnable.equals("Y")) {
				printerService.setTextAlignRight();
				printerService.setTextProperties("RIGHT", "A", "NORMAL", 1, 1, 9);
				printerService.printLn("SGST : " + TOT_SGST_TAX);

				printerService.setTextAlignRight();
				printerService.setTextProperties("RIGHT", "A", "NORMAL", 1, 1, 9);
				printerService.printLn("CGST : " + TOT_CGST_TAX);

				printerService.setTextAlignRight();
				printerService.setTextProperties("RIGHT", "A", "NORMAL", 1, 1, 9);
				printerService.printLn("IGST : " + "000.00");
				printerService.printLn("------------------------------------------------");
			}
			//Code for Addl Charge
			printerService.setTextAlignRight();
			printerService.setTextProperties("RIGHT", "A", "NORMAL", 1, 1, 9);
			printerService.printLn("Discount : " + Addl_Discount);
			
			printerService.setTextAlignRight();
			printerService.setTextProperties("RIGHT", "A", "NORMAL", 1, 1, 9);
			printerService.printLn("Charge : " + Addl_Charge);
			//End of code for Addl Charge
			//End of code for tax
			printerService.lineBreak();
			printerService.setTextAlignRight();
			printerService.printLn("Total Value : " + TOT_AMT);
			printerService.printLn("------------------------------------------------");
			printerService.printLn("YOU SAVED RS : " + discAmount);
			printerService.printLn("------------------------------------------------");

			printerService.printLn(">>>--------------Thank You-------------<<<");
			printerService.lineBreak();
			printerService.lineBreak();
			printerService.cutFull();
			printerService.close();
			//writer.flush();
			// End of code for bill print

		} catch (IOException e) {
			e.printStackTrace();
		}
		jsonString = "Printer Success";
		return new ResponseEntity<>(jsonString, HttpStatus.OK);
	}
    
    public static Map<String, Object> getMacAddress() throws SocketException{
    	Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
        NetworkInterface inter;
        Map<String, Object> result = new HashMap<String, Object>();
        StringBuilder macString = new StringBuilder();
        String macAddress = "";
        while (networks.hasMoreElements()) {
            inter = networks.nextElement();
            byte[] mac = inter.getHardwareAddress();
            if (mac != null) {
                for (int i = 0; i < mac.length; i++) {
                	String macVal = String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "");
                	macString.append(macVal);
                }
            }
        }
        macAddress = macString.substring(0, Math.min(20, macString.length()));
        result.put("systemMacVal", macAddress);
        //result.put("systemMacVal", macString.substring(0,20).toString());
        return result;
    }
    
    /*@GetMapping("cartlite")
    public ModelAndView cartlite() {
      	final String searchArticleUri = env.getProperty("findRateProductPrice");
		final String uri = env.getProperty("findCustomerBO");
		ModelAndView modelAndView = new ModelAndView();
		//code for getting customer details
		Map<Object, Object> custParams = new HashMap<>();
		custParams.put("contactNumber", "");
        ObjectMapper mapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity custRequest = new HttpEntity(custParams, headers);
		
		ResponseEntity response = restTemplate.postForEntity(uri, custRequest, List.class);
		//HashMapResponse custResult = custRestTemplate.postForObject(uri, custRequest, HashMapResponse.class);
		System.out.println("customerList---"+response.getBody());
		modelAndView.addObject("CustomerList", response.getBody());
		//End of code for getting customer details
		Hashtable<String, String> params = new Hashtable<String, String>();
        
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse searchArticleResult = restTemplate.postForObject(searchArticleUri, request, HashMapResponse.class);
		HashMap<String, Object> searchArticleList = searchArticleResult.getResponse();
		
		modelAndView.addObject("productsList", searchArticleList.get("finalProductPriceInfoList"));
		
        return new ModelAndView("cart/cartlite", modelAndView.getModel());
    }*/
    @SuppressWarnings("null")
	@RequestMapping(value = "cartlite", method = RequestMethod.GET)
    public ModelAndView cartlite(RedirectAttributes redirectAttributes) {
    	
		RestClient restClient = applicationContext.getBean(RestClient.class);
		final String barcodeUri = env.getProperty("findBarCode");
		final String searchArticleUri = env.getProperty("findRateProductPrice");
		final String uri = env.getProperty("findPosCustomers");
		final String dayPosCartItemsUri = env.getProperty("getPosCartItems");
		final String getDayPosCartItemsUri = env.getProperty("getDayPosCartItems");
		final String getCustomerPrevBillUri = env.getProperty("getCustomerPrevBill");
		final String getTaxInvoiceUri = env.getProperty("getTaxInvoice");
		final String getAddlCartDetailsUri = env.getProperty("getAdditionalCartTxn");
		
		Map<String, Object> posTerminalMap = new HashMap<String, Object>();
		
		String receiptId = "", cartDayId = "",facilityId = "";
		String posStatus = "", posReceiptId = "", customerMobileNo = "", firstName = "";
		String productStoreId = getStoreId();
		String posTerminalId = (String) httpSession.getAttribute("posTerminalId");
		float discPer = (float) 0.0;
		float discAmount = (float) 0.0;
		BigDecimal grandTotal = BigDecimal.ZERO;
		BigDecimal spGrandTotal = BigDecimal.ZERO;
		Map<String, Object> posMap = new HashMap<String, Object>();
		try {
			posTerminalMap = getMacAddress();
			facilityId = getFacilityId();
			posTerminalId = (String) posTerminalMap.get("systemMacVal");
			posMap.put("posTerminalId", posTerminalId);
			
			posMap.put("facilityId", facilityId);
			restClient.setRequestBody(posMap);
			System.out.println("posTerminalId------------"+posTerminalId);
			System.out.println("facilityId------------"+facilityId);
			Map<String, Object> response = restClient.callRetailService("getDayId", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) response.get("responseBody");
			if (!dayDetailsList.isEmpty()) {
				//Map<String,Object> dayDetail = (HashMap<String, Object>) dayDetailsList.get("dayDetail");
				receiptId = (String) dayDetailsList.get("receiptId");
				cartDayId = (String) dayDetailsList.get("dayId");
				httpSession.setAttribute("receiptId", receiptId);
				httpSession.setAttribute("dayId", cartDayId);
				httpSession.setAttribute("posTerminalId", posTerminalId);
			}
	        //dayId = (String) response.get("dayId");
			
		} catch (Exception e) {
			
		}
		String username = (String) httpSession.getAttribute("username");
		List<String> posReceiptList = new ArrayList<String>();
		ModelAndView modelAndView = new ModelAndView();
		
		//code for getting customer details
		Map<String, Object> cartItems = new LinkedHashMap<String, Object>();
		Map<String, Object> cartMapItems = new LinkedHashMap<String, Object>();
		Map<Object, Object> custParams = new HashMap<>();
		custParams.put("contactNumber", "");
        ObjectMapper mapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		//code for checking tax enabled or not
		Hashtable<String, Object> taxEnableParams = new Hashtable<String, Object>();
		taxEnableParams.put("receiptId", receiptId);
		
		HttpEntity taxEnableRequest = new HttpEntity(taxEnableParams, headers);
		HashMapResponse taxEnableResult = restTemplate.postForObject(getTaxInvoiceUri,
				taxEnableRequest, HashMapResponse.class);
		HashMap<String, Object> taxEnableList = taxEnableResult.getResponse();
		modelAndView.addObject("taxEnable", taxEnableList.get("isChecked"));
		
		//Code for getting Payments
		HttpEntity custRequest = new HttpEntity(custParams, headers);
		JsonParser parser = null;
		
		ResponseEntity response = restTemplate.postForEntity(uri, custRequest, List.class);
		//HashMapResponse custResult = custRestTemplate.postForObject(uri, custRequest, HashMapResponse.class);
		System.out.println("customerList---"+response.getBody());
		modelAndView.addObject("CustomerList", response.getBody());
		//End of code for getting customer details
		Hashtable<String, String> params = new Hashtable<String, String>();
        
        headers.add("Content-Type", "application/json");
        params.put("productStoreId", productStoreId);
        HttpEntity request = new HttpEntity(params, headers);
        
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse searchArticleResult = restTemplate.postForObject(searchArticleUri, request, HashMapResponse.class);
		HashMap<String, Object> searchArticleList = searchArticleResult.getResponse();
		
		modelAndView.addObject("productsList", searchArticleList.get("finalProductPriceInfoList"));
		//Code for getting barcode
		Map<Object, Object> barcodeParams = new HashMap<>();
		
		barcodeParams.put("productStoreId", productStoreId);
		HttpEntity barcodeRequest = new HttpEntity(barcodeParams, headers);
		
		ResponseEntity barCodeResponse = restTemplate.postForEntity(barcodeUri, barcodeRequest, List.class);
		modelAndView.addObject("barcodeList", barCodeResponse.getBody());
		//end of code for getting barcode
		//getPosCartItems
		try {
			byPassSSLCertificate();
			List<Object> holdPosCartList = new ArrayList<Object>();
			JsonNode dayPosCart = null;
			//get Hold bill details.
			UriComponentsBuilder dayPosCartBuilder = UriComponentsBuilder.fromHttpUrl(getDayPosCartItemsUri).
            		queryParam("dayId", cartDayId).
            		queryParam("posTerminalId", posTerminalId);
			
			HttpEntity<String> dayPosCartEntity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> dayPosCartItemResponse = restTemplate.exchange(dayPosCartBuilder.toUriString(), HttpMethod.POST, dayPosCartEntity, String.class);
            JsonNode dayPosCartItemActualObj = mapper.readTree(dayPosCartItemResponse.getBody());
            System.out.println("dayPosCartItemActualObj----"+dayPosCartItemActualObj);
            if(dayPosCartItemActualObj.get("posCartItemList") != null) {
            	for (int i=0; i < dayPosCartItemActualObj.get("posCartItemList").size(); i++) {
            		Map<String, String> posHoldCartMap = new HashMap<String, String>();
            		dayPosCart = mapper.readTree(dayPosCartItemActualObj.get("posCartItemList").get(i).toString());
            		
            		posStatus = dayPosCart.get("posStatus").toString().substring(1, dayPosCart.get("posStatus").toString().length()-1);
            		posReceiptId = dayPosCart.get("receiptId").toString().substring(1, dayPosCart.get("receiptId").toString().length()-1);
            		customerMobileNo = dayPosCart.get("customerMobileNo").toString().substring(1, dayPosCart.get("customerMobileNo").toString().length()-1);
            		if(dayPosCart.get("firstName") != null) {
            			firstName = dayPosCart.get("firstName").toString().substring(1, dayPosCart.get("firstName").toString().length()-1);
            		}
            		
            		if(posStatus.equals("HOLD") && !(posReceiptList.contains(posReceiptId))) {
            			posHoldCartMap.put("posStatus", posStatus);
            			posHoldCartMap.put("posReceiptId", posReceiptId);
            			posHoldCartMap.put("customerMobileNo", customerMobileNo);
            			posHoldCartMap.put("customerName", firstName);
            			posHoldCartMap.put("dayId", cartDayId);
            			posHoldCartMap.put("posTerminalId", posTerminalId);
            			holdPosCartList.add(posHoldCartMap);
            			posReceiptList.add(posReceiptId);
            		}
            	}
            }
            modelAndView.addObject("holdPosCartList", holdPosCartList);
			//get Hold bill details.
            System.out.println("came into get cart Items");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dayPosCartItemsUri).
            		queryParam("receiptId", receiptId).
            		queryParam("posTerminalId", posTerminalId).
            		queryParam("dayId", cartDayId).
            		queryParam("USERNAME", username);
            
            //response.
            String cartItemRequest = request.toString();
            cartItemRequest = cartItemRequest.substring(1, cartItemRequest.length()-1);
            HttpEntity<String> entity = new HttpEntity<String>(cartItemRequest, headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            JsonNode cartObj = null;
            ObjectNode cartObjNode = null;
            String billAmount = "";
            Map<String, Object> cartItemsMap = new HashMap<String, Object>();
            List<Object> cartItemList = new ArrayList<Object>();
            if(actualObj.get("cartItems") != null) {
            	for (int i=0; i < actualObj.get("cartItems").size(); i++) {
            		BigDecimal CGST_TAX = BigDecimal.ZERO;
            		BigDecimal SGST_TAX = BigDecimal.ZERO;
            		BigDecimal IGST_TAX = BigDecimal.ZERO;
            		BigDecimal prdPrice = BigDecimal.ZERO;
            		BigDecimal totCartItemAmt = BigDecimal.ZERO;
            		BigDecimal totCartMrpAmt = BigDecimal.ZERO;
            		BigDecimal cartAmount = BigDecimal.ZERO;
            		String productName = "", barcode = "";
            		int productQty = 0;
            		
            		cartObj = mapper.readTree(actualObj.get("cartItems").get(i).toString());
            		cartObjNode = (ObjectNode) cartObj;
            		if(cartObj.get("productName") != null) {
            			int prdLen = cartObj.get("productName").toString().length();
            			productName = cartObj.get("productName").toString().substring(1, prdLen-1);
            		}
            		if(cartObj.get("barcode") != null) {
            			int barcodeLen = cartObj.get("barcode").toString().length();
            			barcode = cartObj.get("barcode").toString().substring(1, barcodeLen-1);
            		}
            		if(cartObj.get("productQty") != null) {
            			productQty = cartObj.get("productQty").asInt();
            		}
            		//Code for mrp
            		if(cartObj.get("productTotalAmt") != null) {
            			prdPrice = new BigDecimal(cartObj.get("productTotalAmt").toString());
            			totCartItemAmt = totCartItemAmt.add(prdPrice);
					}
            		if(cartObj.get("itemTax").get("CGST_TAX") != null) {
            			CGST_TAX = new BigDecimal(cartObj.get("itemTax").get("CGST_TAX").toString());
            			totCartItemAmt = totCartItemAmt.add(CGST_TAX);
            		}
					if(cartObj.get("itemTax").get("SGST_TAX") != null) {
						SGST_TAX = new BigDecimal(cartObj.get("itemTax").get("SGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(SGST_TAX);
					}
					if(cartObj.get("itemTax").get("IGST_TAX") != null) {
						IGST_TAX = new BigDecimal(cartObj.get("itemTax").get("IGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(IGST_TAX);
					}
					//End of code for mrp
					//Code to calculate changed Mrpprice
            		if(cartObj.get("productOriginalPrice") != null) {
            			//prdPrice = new BigDecimal(cartObj.get("productOriginalPrice").toString());
            			//prdPrice = new BigDecimal(cartObj.get("mrp").toString());
            			prdPrice = new BigDecimal(cartObj.get("productPrice").toString());
            			totCartMrpAmt = totCartMrpAmt.add(prdPrice);
					}
            		/*if(cartObj.get("posItemTax").get("CGST_TAX") != null) {
            			CGST_TAX = new BigDecimal(cartObj.get("posItemTax").get("CGST_TAX").toString());
            			totCartMrpAmt = totCartMrpAmt.add(CGST_TAX);
            		}
            		
					if(cartObj.get("posItemTax").get("SGST_TAX") != null) {
						SGST_TAX = new BigDecimal(cartObj.get("posItemTax").get("SGST_TAX").toString());
						totCartMrpAmt = totCartMrpAmt.add(SGST_TAX);
					}
					if(cartObj.get("posItemTax").get("IGST_TAX") != null) {
						IGST_TAX = new BigDecimal(cartObj.get("posItemTax").get("IGST_TAX").toString());
						totCartMrpAmt = totCartMrpAmt.add(IGST_TAX);
					}*/
					//End of code to calculate changed Mrpprice
            		
					if(cartObj.get("itemTax").get("CGST_TAX") == null)
            			cartObjNode.put("CGST_TAX", CGST_TAX);
            		else
            			cartObjNode.put("CGST_TAX", cartObj.get("itemTax").get("CGST_TAX"));
					
					if(cartObj.get("itemTax").get("SGST_TAX") == null)
            			cartObjNode.put("SGST_TAX", SGST_TAX);
            		else
            			cartObjNode.put("SGST_TAX", cartObj.get("itemTax").get("SGST_TAX"));
					
            		if(cartObj.get("itemTax").get("IGST_TAX") == null)
            			cartObjNode.put("IGST_TAX", IGST_TAX);
            		else
            			cartObjNode.put("IGST_TAX", cartObj.get("itemTax").get("IGST_TAX"));
            		
            		cartAmount = prdPrice.multiply(new BigDecimal(productQty));
            		cartObjNode.put("Item_Tot_Amt", totCartItemAmt.floatValue());
            		cartObjNode.put("Item_Original_Amt", totCartMrpAmt.floatValue());
            		cartObjNode.put("product_Name", productName);
            		cartObjNode.put("bar_code", barcode);
            		cartObjNode.put("product_qty", productQty);
            		cartObjNode.put("cartAmount", cartAmount.floatValue());
            		
            		cartItemsMap.put("cartItemsMap", cartObjNode);
            		System.out.println("cartItemsMap--"+cartItemsMap.get("cartItemsMap"));
            		cartItemList.add(cartItemsMap.get("cartItemsMap"));
            	}
            	if(actualObj.get("customerShipState") != null) {
            		int strLength = actualObj.get("customerShipState").toString().length();
            		httpSession.setAttribute("customerShipState", actualObj.get("customerShipState").toString().substring(1, strLength-1));
            	}
            	if(actualObj.get("customerMobileNo") != null) {
            		modelAndView.addObject("contactNumber", actualObj.get("customerMobileNo").toString().substring(1, actualObj.get("customerMobileNo").toString().length()-1));
            		modelAndView.addObject("customerName", actualObj.get("firstName").toString().substring(1, actualObj.get("firstName").toString().length()-1));
            		modelAndView.addObject("contactAddress", actualObj.get("address1").toString().substring(1, actualObj.get("address1").toString().length()-1));
            	} else {
            		modelAndView.addObject("contactNumber", "");
            		modelAndView.addObject("customerName", "");
            		modelAndView.addObject("contactAddress", "");
            	}
            	billAmount = actualObj.get("subTotal").toString();
            	modelAndView.addObject("totalDue", actualObj.get("totalDue"));
            	modelAndView.addObject("subTotal", actualObj.get("subTotal"));
            	modelAndView.addObject("totalSalesTax", actualObj.get("totalSalesTax"));
            	//modelAndView.addObject("cartSize", actualObj.get("cartSize"));
            	modelAndView.addObject("cartSize", actualObj.get("totalQuantity"));
            	modelAndView.addObject("displayOrgGrandTotal", actualObj.get("displayOrgGrandTotal"));
            	modelAndView.addObject("displayGrandTotal", actualObj.get("displayGrandTotal"));
            	modelAndView.addObject("displaySPGrandTotal", actualObj.get("displaySPGrandTotal"));
            	modelAndView.addObject("displayMRPGrandTotal", actualObj.get("displayMRPGrandTotal"));
            	modelAndView.addObject("productOriginalPrice", actualObj.get("productOriginalPrice"));
            	modelAndView.addObject("balanceAmount", actualObj.get("balanceAmount"));
            	//Calculate Discount & Disc %
            	if(actualObj.get("displayMRPGrandTotal") != null) {
            		grandTotal = (BigDecimal) actualObj.get("displayMRPGrandTotal").decimalValue();
            	}
            	if(actualObj.get("displaySPGrandTotal") != null) {
            		spGrandTotal = (BigDecimal) actualObj.get("displaySPGrandTotal").decimalValue();
            	}
            	discPer = Math.round((grandTotal.floatValue() - spGrandTotal.floatValue()) / (grandTotal.floatValue()) * ((float) 100));
            	discAmount = (grandTotal.floatValue() - spGrandTotal.floatValue());
            	modelAndView.addObject("discPer", discPer);
            	if(discAmount >= 0)
            		modelAndView.addObject("discAmount", discAmount);
            	else
            		modelAndView.addObject("discAmount", 0.0f);
        		//End of calculating Disc & Disc%
                //modelAndView.addObject("cartItems", actualObj.get("cartItems"));
                modelAndView.addObject("cartItems", cartItemList);
                modelAndView.addObject("itemTax", cartObj.get("itemTax"));
                //NodeBean toValue = mapper.readValue(parser, NodeBean.class);
            }
            
            Hashtable<String, Object> addlCartParams = new Hashtable<String, Object>();
            addlCartParams.put("receiptId", receiptId);
            addlCartParams.put("billAmount", billAmount);
    		
            HttpEntity addlCartRequest = new HttpEntity(addlCartParams, headers);
    		HashMapResponse addlCartResult = restTemplate.postForObject(getAddlCartDetailsUri,
    				addlCartRequest, HashMapResponse.class);
    		
    		HashMap<String, Object> addlCartList = addlCartResult.getResponse();
    		modelAndView.addObject("discountPer", addlCartList.get("discountPer"));
    		modelAndView.addObject("discount", addlCartList.get("discount"));
    		modelAndView.addObject("isPercentage", addlCartList.get("isPercentage"));
    		modelAndView.addObject("charges", addlCartList.get("charges"));
    		modelAndView.addObject("isChargePercentage", addlCartList.get("isChargePercentage"));
    		modelAndView.addObject("chargePercentage", addlCartList.get("chargePer"));
    		
            Hashtable<String, Object> customerPrevBillParams = new Hashtable<String, Object>();
    		customerPrevBillParams.put("contactNumber", customerMobileNo);
    		
    		HttpEntity customerPrevBillRequest = new HttpEntity(customerPrevBillParams, headers);
			HashMapResponse customerPrevBillResult = restTemplate.postForObject(getCustomerPrevBillUri,
					customerPrevBillRequest, HashMapResponse.class);
			HashMap<String, Object> creditPrevBilllist = customerPrevBillResult.getResponse();
			modelAndView.addObject("billReturnMap", creditPrevBilllist.get("billReturnMap"));
		} catch (Exception e) {
			
		}
        //return new ModelAndView("cart/cartlite", modelAndView.getModel());
		return new ModelAndView("cart/cart_new", modelAndView.getModel());
    }
    
    /*public ModelAndView cartlite(RedirectAttributes redirectAttributes) {
		RestClient restClient = applicationContext.getBean(RestClient.class);
		final String barcodeUri = env.getProperty("findBarCode");
		final String searchArticleUri = env.getProperty("findRateProductPrice");
		final String uri = env.getProperty("findCustomerBO");
		final String dayPosCartItemsUri = env.getProperty("getPosCartItems");
		Map<String, Object> posTerminalMap = new HashMap<String, Object>();
		String dayId = "";
		String receiptId = "", cartDayId = "", facilityId = "";
		String posTerminalId = (String) httpSession.getAttribute("posTerminalId");
		String productStoreId = getStoreId();
		Map<String, Object> posMap = new HashMap<String, Object>();
		try {
			posTerminalMap = getMacAddress();
			facilityId = getFacilityId();
			posTerminalId = (String) posTerminalMap.get("systemMacVal");
			posMap.put("posTerminalId", posTerminalId);
			posMap.put("facilityId", facilityId);
			restClient.setRequestBody(posMap);
			Map<String, Object> response = restClient.callRetailService("getDayId", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) response.get("responseBody");
			if (!dayDetailsList.isEmpty()) {
				//Map<String,Object> dayDetail = (HashMap<String, Object>) dayDetailsList.get("dayDetail");
				receiptId = (String) dayDetailsList.get("receiptId");
				cartDayId = (String) dayDetailsList.get("dayId");
				httpSession.setAttribute("receiptId", receiptId);
				httpSession.setAttribute("dayId", cartDayId);
				httpSession.setAttribute("posTerminalId", posTerminalId);
			}
	        dayId = (String) response.get("dayId");
			
		} catch (Exception e) {
			
		}
		String username = (String) httpSession.getAttribute("username");
		ModelAndView modelAndView = new ModelAndView();
		
		//code for getting customer details
		Map<String, Object> cartItems = new LinkedHashMap<String, Object>();
		Map<String, Object> cartMapItems = new LinkedHashMap<String, Object>();
		Map<Object, Object> custParams = new HashMap<>();
		custParams.put("contactNumber", "");
        ObjectMapper mapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity custRequest = new HttpEntity(custParams, headers);
		JsonParser parser = null;
		
		ResponseEntity response = restTemplate.postForEntity(uri, custRequest, List.class);
		//HashMapResponse custResult = custRestTemplate.postForObject(uri, custRequest, HashMapResponse.class);
		System.out.println("customerList---"+response.getBody());
		modelAndView.addObject("CustomerList", response.getBody());
		
		//End of code for getting customer details
		//Code for getting barcode
		Map<Object, Object> barcodeParams = new HashMap<>();
		
		barcodeParams.put("productStoreId", productStoreId);
		HttpEntity barcodeRequest = new HttpEntity(barcodeParams, headers);
		
		ResponseEntity barCodeResponse = restTemplate.postForEntity(barcodeUri, barcodeRequest, List.class);
		modelAndView.addObject("barcodeList", barCodeResponse.getBody());
		//end of code for getting barcode
		Hashtable<String, String> params = new Hashtable<String, String>();
        
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse searchArticleResult = restTemplate.postForObject(searchArticleUri, request, HashMapResponse.class);
		HashMap<String, Object> searchArticleList = searchArticleResult.getResponse();
		
		modelAndView.addObject("productsList", searchArticleList.get("finalProductPriceInfoList"));
		//getPosCartItems
		try {
			byPassSSLCertificate();
            System.out.println("came into get cart Items");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dayPosCartItemsUri).
            		queryParam("receiptId", receiptId).
            		queryParam("posTerminalId", posTerminalId).
            		queryParam("dayId", cartDayId).
            		queryParam("USERNAME", username);
            
            //response.
            String cartItemRequest = request.toString();
            cartItemRequest = cartItemRequest.substring(1, cartItemRequest.length()-1);
            HttpEntity<String> entity = new HttpEntity<String>(cartItemRequest, headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            JsonNode cartObj = null;
            ObjectNode cartObjNode = null;
            Map<String, Object> cartItemsMap = new HashMap<String, Object>();
            List<Object> cartItemList = new ArrayList<Object>();
            if(actualObj.get("cartItems") != null) {
            	for (int i=0; i < actualObj.get("cartItems").size(); i++) {
            		BigDecimal CGST_TAX = BigDecimal.ZERO;
            		BigDecimal SGST_TAX = BigDecimal.ZERO;
            		BigDecimal IGST_TAX = BigDecimal.ZERO;
            		BigDecimal prdPrice = BigDecimal.ZERO;
            		BigDecimal totCartItemAmt = BigDecimal.ZERO;
            		
            		cartObj = mapper.readTree(actualObj.get("cartItems").get(i).toString());
            		cartObjNode = (ObjectNode) cartObj;
            		if(cartObj.get("productTotalAmt") != null) {
            			prdPrice = new BigDecimal(cartObj.get("productTotalAmt").toString());
            			totCartItemAmt = totCartItemAmt.add(prdPrice);
					}
            		
            		if(cartObj.get("itemTax").get("CGST_TAX") != null) {
            			CGST_TAX = new BigDecimal(cartObj.get("itemTax").get("CGST_TAX").toString());
            			totCartItemAmt = totCartItemAmt.add(CGST_TAX);
            		}
					if(cartObj.get("itemTax").get("SGST_TAX") != null) {
						SGST_TAX = new BigDecimal(cartObj.get("itemTax").get("SGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(SGST_TAX);
					}
					if(cartObj.get("itemTax").get("IGST_TAX") != null) {
						IGST_TAX = new BigDecimal(cartObj.get("itemTax").get("IGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(IGST_TAX);
					}
            		
					if(cartObj.get("itemTax").get("CGST_TAX") == null)
            			cartObjNode.put("CGST_TAX", CGST_TAX);
            		else
            			cartObjNode.put("CGST_TAX", cartObj.get("itemTax").get("CGST_TAX"));
					
					if(cartObj.get("itemTax").get("SGST_TAX") == null)
            			cartObjNode.put("SGST_TAX", SGST_TAX);
            		else
            			cartObjNode.put("SGST_TAX", cartObj.get("itemTax").get("SGST_TAX"));
					
            		if(cartObj.get("itemTax").get("IGST_TAX") == null)
            			cartObjNode.put("IGST_TAX", IGST_TAX);
            		else
            			cartObjNode.put("IGST_TAX", cartObj.get("itemTax").get("IGST_TAX"));
            		
            		cartObjNode.put("Item_Tot_Amt", totCartItemAmt.floatValue());
            		cartItemsMap.put("cartItemsMap", cartObjNode);
            		System.out.println("cartItemsMap--"+cartItemsMap.get("cartItemsMap"));
            		cartItemList.add(cartItemsMap.get("cartItemsMap"));
            	}
            	System.out.println("cartItemList--"+cartItemList);
            	if(actualObj.get("customerShipState") != null) {
            		int strLength = actualObj.get("customerShipState").toString().length();
            		httpSession.setAttribute("customerShipState", actualObj.get("customerShipState").toString().substring(1, strLength-1));
            	}
            	modelAndView.addObject("contactNumber", actualObj.get("customerMobileNo"));
            	modelAndView.addObject("customerName", actualObj.get("firstName"));
            	modelAndView.addObject("contactAddress", actualObj.get("address1"));
            	modelAndView.addObject("totalDue", actualObj.get("totalDue"));
            	modelAndView.addObject("subTotal", actualObj.get("subTotal"));
            	modelAndView.addObject("totalSalesTax", actualObj.get("totalSalesTax"));
            	modelAndView.addObject("cartSize", actualObj.get("cartSize"));
            	modelAndView.addObject("displayGrandTotal", actualObj.get("displayGrandTotal"));
            	modelAndView.addObject("balanceAmount", actualObj.get("balanceAmount"));
            	
                //modelAndView.addObject("cartItems", actualObj.get("cartItems"));
                modelAndView.addObject("cartItems", cartItemList);
                modelAndView.addObject("itemTax", cartObj.get("itemTax"));
                //NodeBean toValue = mapper.readValue(parser, NodeBean.class);
            }
		} catch (Exception e) {
			
		}
		return new ModelAndView("cart/cartlite", modelAndView.getModel());
    }*/
    
    @SuppressWarnings("null")
	@RequestMapping(value = "getPosHoldCartItems", method = RequestMethod.GET)
    public ResponseEntity<?> getPosHoldCartItems(@RequestParam("json") String json,
    		@RequestParam("password") String password,
    		@RequestParam("username") String username) {
		
		final String searchArticleUri = env.getProperty("findRateProductPrice");
		final String uri = env.getProperty("findCustomerBO");
		final String dayPosCartItemsUri = env.getProperty("getHoldPosCartItems");
		final String getDayPosCartItemsUri = env.getProperty("getDayPosCartItems");
		
		String receiptId = "", cartDayId = "",jsonString = "";
		String posStatus = "", posReceiptId = "", customerMobileNo = "";
		String productStoreId = getStoreId();
		
		List<String> posReceiptList = new ArrayList<String>();
		ModelAndView modelAndView = new ModelAndView();
		Hashtable<String, String> getPosCartParams = new Hashtable<String, String>();
		
		//code for getting customer details
		Map<String, Object> cartItems = new LinkedHashMap<String, Object>();
		Map<String, Object> cartMapItems = new LinkedHashMap<String, Object>();
		Map<Object, Object> custParams = new HashMap<>();
		custParams.put("contactNumber", "");
        ObjectMapper mapper = new ObjectMapper();
        String posTerminalId = "", firstName = "";
        //get params
        try {
			getPosCartParams = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {});
			posTerminalId = (String) getPosCartParams.get("posTerminalId");
			cartDayId = (String) getPosCartParams.get("dayId");
			receiptId = (String) getPosCartParams.get("posReceiptId");
			httpSession.setAttribute("receiptId", receiptId);
			httpSession.setAttribute("dayId", cartDayId);
			httpSession.setAttribute("posTerminalId", posTerminalId);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        HttpHeaders headers = new HttpHeaders();
        
		headers.add("Content-Type", "application/json");
		HttpEntity custRequest = new HttpEntity(custParams, headers);
		JsonParser parser = null;
		
		ResponseEntity response = restTemplate.postForEntity(uri, custRequest, List.class);
		//HashMapResponse custResult = custRestTemplate.postForObject(uri, custRequest, HashMapResponse.class);
		System.out.println("customerList---"+response.getBody());
		modelAndView.addObject("CustomerList", response.getBody());
		//End of code for getting customer details
		Hashtable<String, String> params = new Hashtable<String, String>();
        
        headers.add("Content-Type", "application/json");
        params.put("productStoreId", productStoreId);
        HttpEntity request = new HttpEntity(params, headers);
        
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse searchArticleResult = restTemplate.postForObject(searchArticleUri, request, HashMapResponse.class);
		HashMap<String, Object> searchArticleList = searchArticleResult.getResponse();
		
		modelAndView.addObject("productsList", searchArticleList.get("finalProductPriceInfoList"));
		/*try {
			//Get Day details
			Map<String, Object> dayDetailRequest = new HashMap<String, Object>();
			Map<String, Object> dayDetailResponse = new HashMap<String, Object>();
			dayDetailRequest.put("dayId", dayId);
			
			RestClient restClient = applicationContext.getBean(RestClient.class);
			restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
			
			restClient.setRequestBody(dayDetailRequest);
			dayDetailResponse = restClient.callRetailService("findDay", true);
			Map<String,Object> dayDetailsList = (LinkedHashMap<String,Object>) dayDetailResponse.get("responseBody");
			if (!dayDetailsList.isEmpty()) {
				Map<String,Object> dayDetail = (HashMap<String, Object>) dayDetailsList.get("dayDetail");
				receiptId = (String) dayDetail.get("receiptId");
				cartDayId = (String) dayDetail.get("dayId");
				
			}
		} catch (Exception e) {
			
		}*/
		
		//getPosCartItems
		try {
			byPassSSLCertificate();
			List<Object> holdPosCartList = new ArrayList<Object>();
			JsonNode dayPosCart = null;
			//get Hold bill details.
			UriComponentsBuilder dayPosCartBuilder = UriComponentsBuilder.fromHttpUrl(getDayPosCartItemsUri).
            		queryParam("dayId", cartDayId).
            		queryParam("posTerminalId", posTerminalId);
			
			HttpEntity<String> dayPosCartEntity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> dayPosCartItemResponse = restTemplate.exchange(dayPosCartBuilder.toUriString(), HttpMethod.POST, dayPosCartEntity, String.class);
            JsonNode dayPosCartItemActualObj = mapper.readTree(dayPosCartItemResponse.getBody());
            System.out.println("dayPosCartItemActualObj----"+dayPosCartItemActualObj);
            if(dayPosCartItemActualObj.get("posCartItemList") != null) {
            	for (int i=0; i < dayPosCartItemActualObj.get("posCartItemList").size(); i++) {
            		Map<String, String> posHoldCartMap = new HashMap<String, String>();
            		dayPosCart = mapper.readTree(dayPosCartItemActualObj.get("posCartItemList").get(i).toString());
            		
            		posStatus = dayPosCart.get("posStatus").toString().substring(1, dayPosCart.get("posStatus").toString().length()-1);
            		posReceiptId = dayPosCart.get("receiptId").toString().substring(1, dayPosCart.get("receiptId").toString().length()-1);
            		customerMobileNo = dayPosCart.get("customerMobileNo").toString().substring(1, dayPosCart.get("customerMobileNo").toString().length()-1);
            		firstName = dayPosCart.get("firstName").toString().substring(1, dayPosCart.get("firstName").toString().length()-1);
            		if(posStatus.equals("HOLD") && !(posReceiptList.contains(posReceiptId))) {
            			posHoldCartMap.put("posStatus", posStatus);
            			posHoldCartMap.put("posReceiptId", posReceiptId);
            			posHoldCartMap.put("customerMobileNo", customerMobileNo);
            			posHoldCartMap.put("customerName", firstName);
            			posHoldCartMap.put("dayId", cartDayId);
            			posHoldCartMap.put("posTerminalId", posTerminalId);
            			holdPosCartList.add(posHoldCartMap);
            			posReceiptList.add(posReceiptId);
            		}
            	}
            }
            modelAndView.addObject("holdPosCartList", holdPosCartList);
			//get Hold bill details.
            System.out.println("came into get cart Items");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dayPosCartItemsUri).
            		queryParam("receiptId", receiptId).
            		queryParam("posTerminalId", posTerminalId).
            		queryParam("dayId", cartDayId).
            		queryParam("USERNAME", username);
            
            //response.
            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            JsonNode actualObj = mapper.readTree(loginResponse.getBody());
            JsonNode cartObj = null;
            ObjectNode cartObjNode = null;
            Map<String, Object> cartItemsMap = new HashMap<String, Object>();
            List<Object> cartItemList = new ArrayList<Object>();
            if(actualObj.get("cartItems") != null) {
            	for (int i=0; i < actualObj.get("cartItems").size(); i++) {
            		BigDecimal CGST_TAX = BigDecimal.ZERO;
            		BigDecimal SGST_TAX = BigDecimal.ZERO;
            		BigDecimal IGST_TAX = BigDecimal.ZERO;
            		BigDecimal prdPrice = BigDecimal.ZERO;
            		BigDecimal totCartItemAmt = BigDecimal.ZERO;
            		
            		cartObj = mapper.readTree(actualObj.get("cartItems").get(i).toString());
            		cartObjNode = (ObjectNode) cartObj;
            		if(cartObj.get("productTotalAmt") != null) {
            			prdPrice = new BigDecimal(cartObj.get("productTotalAmt").toString());
            			totCartItemAmt = totCartItemAmt.add(prdPrice);
					}
            		
            		if(cartObj.get("itemTax").get("CGST_TAX") != null) {
            			CGST_TAX = new BigDecimal(cartObj.get("itemTax").get("CGST_TAX").toString());
            			totCartItemAmt = totCartItemAmt.add(CGST_TAX);
            		}
					if(cartObj.get("itemTax").get("SGST_TAX") != null) {
						SGST_TAX = new BigDecimal(cartObj.get("itemTax").get("SGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(SGST_TAX);
					}
					if(cartObj.get("itemTax").get("IGST_TAX") != null) {
						IGST_TAX = new BigDecimal(cartObj.get("itemTax").get("IGST_TAX").toString());
						totCartItemAmt = totCartItemAmt.add(IGST_TAX);
					}
            		
					if(cartObj.get("itemTax").get("CGST_TAX") == null)
            			cartObjNode.put("CGST_TAX", CGST_TAX);
            		else
            			cartObjNode.put("CGST_TAX", cartObj.get("itemTax").get("CGST_TAX"));
					
					if(cartObj.get("itemTax").get("SGST_TAX") == null)
            			cartObjNode.put("SGST_TAX", SGST_TAX);
            		else
            			cartObjNode.put("SGST_TAX", cartObj.get("itemTax").get("SGST_TAX"));
					
            		if(cartObj.get("itemTax").get("IGST_TAX") == null)
            			cartObjNode.put("IGST_TAX", IGST_TAX);
            		else
            			cartObjNode.put("IGST_TAX", cartObj.get("itemTax").get("IGST_TAX"));
            		
            		cartObjNode.put("Item_Tot_Amt", totCartItemAmt.floatValue());
            		cartItemsMap.put("cartItemsMap", cartObjNode);
            		System.out.println("cartItemsMap--"+cartItemsMap.get("cartItemsMap"));
            		cartItemList.add(cartItemsMap.get("cartItemsMap"));
            	}
            	if(actualObj.get("customerShipState") != null) {
            		int strLength = actualObj.get("customerShipState").toString().length();
            		httpSession.setAttribute("customerShipState", actualObj.get("customerShipState").toString().substring(1, strLength-1));
            	}
            	if(actualObj.get("customerMobileNo") != null) {
            		modelAndView.addObject("contactNumber", actualObj.get("customerMobileNo").toString().substring(1, actualObj.get("customerMobileNo").toString().length()-1));
            	}
            	if(actualObj.get("firstName") != null) {
            		modelAndView.addObject("customerName", actualObj.get("firstName").toString().substring(1, actualObj.get("firstName").toString().length()-1));
            	}
            	if(actualObj.get("address1") != null) {
            		modelAndView.addObject("contactAddress", actualObj.get("address1").toString().substring(1, actualObj.get("address1").toString().length()-1));
            	}
            	
            	modelAndView.addObject("totalDue", actualObj.get("totalDue"));
            	modelAndView.addObject("subTotal", actualObj.get("subTotal"));
            	modelAndView.addObject("totalSalesTax", actualObj.get("totalSalesTax"));
            	modelAndView.addObject("cartSize", actualObj.get("cartSize"));
            	modelAndView.addObject("displayGrandTotal", actualObj.get("displayGrandTotal"));
            	modelAndView.addObject("balanceAmount", actualObj.get("balanceAmount"));
            	
                //modelAndView.addObject("cartItems", actualObj.get("cartItems"));
                modelAndView.addObject("cartItems", cartItemList);
                modelAndView.addObject("itemTax", cartObj.get("itemTax"));
                //NodeBean toValue = mapper.readValue(parser, NodeBean.class);
                jsonString = mapper.writeValueAsString(actualObj);
            }
		} catch (Exception e) {
			
		}
		return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    /*@SuppressWarnings("unchecked")
	@RequestMapping(value = "/createProductBO", method = RequestMethod.GET)
	public ResponseEntity<?> createProductBO(@RequestParam("json") String json,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
	 	
	 	HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		final String uri = env.getProperty("createGlobalProduct");
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
        ObjectMapper mapper = new ObjectMapper();
        String lastName = "";
        
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
			
			System.out.println(params);
		} catch (Exception e) {
			e.printStackTrace();
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
	}*/
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/addProductBO", method = RequestMethod.GET)
	public ResponseEntity<?> addProductBO(@RequestParam("json") String json,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
	 	
	 	HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		final String producturi = env.getProperty("addGlobalProduct");
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
        ObjectMapper mapper = new ObjectMapper();
        String lastName = "";
        
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
			
			System.out.println(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity prdrequest = new HttpEntity(params, headers);
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(producturi, prdrequest, HashMapResponse.class);
		HashMap<String, Object> list = result.getResponse();
		
		if (list != null) {
			hashMap.put("message", "Success");
		}

		return new ResponseEntity<>(hashMap, HttpStatus.OK);
	}
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/createProductBO", method = RequestMethod.GET)
	public ResponseEntity<?> createProductBO(@RequestParam("json") String json,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
	 	
	 	HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		final String producturi = env.getProperty("createGlobalProduct");
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
        ObjectMapper mapper = new ObjectMapper();
        String lastName = "";
        
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
			
			System.out.println(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity prdrequest = new HttpEntity(params, headers);
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(producturi, prdrequest, HashMapResponse.class);
		HashMap<String, Object> list = result.getResponse();
		
		if (list != null) {
			hashMap.put("message", "Success");
		}

		return new ResponseEntity<>(hashMap, HttpStatus.OK);
	}
    @SuppressWarnings("null")
   	@RequestMapping(value = "billSummary", method = RequestMethod.GET)
       public ModelAndView billSummary(@RequestParam("receiptId") String receiptId,
    		   RedirectAttributes redirectAttributes) {
   		RestClient restClient = applicationContext.getBean(RestClient.class);
   		final String dayPosCartItemsUri = env.getProperty("getCstPosCartItems");
   		final String getDayPosCartItemsUri = env.getProperty("getDayPosCartItems");
   		final String getTaxInvoiceUri = env.getProperty("getTaxInvoice");
		final String getAddlCartDetailsUri = env.getProperty("getAdditionalCartTxn");
		
   		Map<String, Object> posTerminalMap = new HashMap<String, Object>();
   		
   		String cartDayId = "",facilityId = "";
   		String posStatus = "", posReceiptId = "", customerMobileNo = "";
   		String productStoreId = getStoreId();
   		String posTerminalId = (String) httpSession.getAttribute("posTerminalId");
   		float discPer = (float) 0.0;
   		float discAmount = (float) 0.0;
   		BigDecimal grandTotal = BigDecimal.ZERO;
   		BigDecimal spGrandTotal = BigDecimal.ZERO;
   		Map<String, Object> posMap = new HashMap<String, Object>();
   		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
   		String username = (String) httpSession.getAttribute("username");
   		List<String> posReceiptList = new ArrayList<String>();
   		ModelAndView modelAndView = new ModelAndView();
   		String billDate = "";
   		String billAmount = "";
   		//getPosCartItems
   		try {
   			byPassSSLCertificate();
   			List<Object> holdPosCartList = new ArrayList<Object>();
   			JsonNode dayPosCart = null;
   			
               System.out.println("came into get cart Items");
               UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dayPosCartItemsUri).
               		queryParam("receiptId", receiptId).
               		queryParam("posTerminalId", posTerminalId).
               		queryParam("dayId", cartDayId).
               		queryParam("USERNAME", username);
               
               //response.
               ObjectMapper mapper = new ObjectMapper();
               HttpHeaders headers = new HttpHeaders();
   			   headers.setContentType(MediaType.APPLICATION_JSON);
               HttpEntity request = new HttpEntity(params, headers);
               String cartItemRequest = request.toString();
               cartItemRequest = cartItemRequest.substring(1, cartItemRequest.length()-1);
               HttpEntity<String> entity = new HttpEntity<String>(cartItemRequest, headers);
               ResponseEntity<String> loginResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
               JsonNode actualObj = mapper.readTree(loginResponse.getBody());
               JsonNode cartObj = null;
               ObjectNode cartObjNode = null;
               Hashtable<String, Object> taxEnableParams = new Hashtable<String, Object>();
               taxEnableParams.put("receiptId", receiptId);
               HttpEntity taxEnableRequest = new HttpEntity(taxEnableParams, headers);
               HashMapResponse taxEnableResult = restTemplate.postForObject(getTaxInvoiceUri,
						taxEnableRequest, HashMapResponse.class);
               HashMap<String, Object> taxEnableList = taxEnableResult.getResponse();
               modelAndView.addObject("taxEnable", taxEnableList.get("isChecked"));
				
               Map<String, Object> cartItemsMap = new HashMap<String, Object>();
               List<Object> cartItemList = new ArrayList<Object>();
               if(actualObj.get("cartItems") != null) {
               	for (int i=0; i < actualObj.get("cartItems").size(); i++) {
               		BigDecimal CGST_TAX = BigDecimal.ZERO;
               		BigDecimal SGST_TAX = BigDecimal.ZERO;
               		BigDecimal IGST_TAX = BigDecimal.ZERO;
               		BigDecimal prdPrice = BigDecimal.ZERO;
               		BigDecimal totCartItemAmt = BigDecimal.ZERO;
               		BigDecimal totCartMrpAmt = BigDecimal.ZERO;
               		BigDecimal cartAmount = BigDecimal.ZERO;
               		String productName = "", barcode = "";
               		int productQty = 0;
               		
               		cartObj = mapper.readTree(actualObj.get("cartItems").get(i).toString());
               		cartObjNode = (ObjectNode) cartObj;
               		if(cartObj.get("productName") != null) {
               			int prdLen = cartObj.get("productName").toString().length();
               			productName = cartObj.get("productName").toString().substring(1, prdLen-1);
               		}
               		if(cartObj.get("barcode") != null) {
               			int barcodeLen = cartObj.get("barcode").toString().length();
               			barcode = cartObj.get("barcode").toString().substring(1, barcodeLen-1);
               		}
               		if(cartObj.get("productQty") != null) {
               			productQty = cartObj.get("productQty").asInt();
               		}
               		//Code for mrp
               		if(cartObj.get("productTotalAmt") != null) {
               			prdPrice = new BigDecimal(cartObj.get("productTotalAmt").toString());
               			totCartItemAmt = totCartItemAmt.add(prdPrice);
   					}
               		if(cartObj.get("itemTax").get("CGST_TAX") != null) {
               			CGST_TAX = new BigDecimal(cartObj.get("itemTax").get("CGST_TAX").toString());
               			totCartItemAmt = totCartItemAmt.add(CGST_TAX);
               		}
   					if(cartObj.get("itemTax").get("SGST_TAX") != null) {
   						SGST_TAX = new BigDecimal(cartObj.get("itemTax").get("SGST_TAX").toString());
   						totCartItemAmt = totCartItemAmt.add(SGST_TAX);
   					}
   					if(cartObj.get("itemTax").get("IGST_TAX") != null) {
   						IGST_TAX = new BigDecimal(cartObj.get("itemTax").get("IGST_TAX").toString());
   						totCartItemAmt = totCartItemAmt.add(IGST_TAX);
   					}
   					//End of code for mrp
   					//Code to calculate changed Mrpprice
               		if(cartObj.get("productOriginalPrice") != null) {
               			//prdPrice = new BigDecimal(cartObj.get("productOriginalPrice").toString());
               			prdPrice = new BigDecimal(cartObj.get("productPrice").toString());
               			totCartMrpAmt = totCartMrpAmt.add(prdPrice);
   					}
               		
   					//End of code to calculate changed Mrpprice
               		
   					if(cartObj.get("itemTax").get("CGST_TAX") == null)
               			cartObjNode.put("CGST_TAX", CGST_TAX);
               		else
               			cartObjNode.put("CGST_TAX", cartObj.get("itemTax").get("CGST_TAX"));
   					
   					if(cartObj.get("itemTax").get("SGST_TAX") == null)
               			cartObjNode.put("SGST_TAX", SGST_TAX);
               		else
               			cartObjNode.put("SGST_TAX", cartObj.get("itemTax").get("SGST_TAX"));
   					
               		if(cartObj.get("itemTax").get("IGST_TAX") == null)
               			cartObjNode.put("IGST_TAX", IGST_TAX);
               		else
               			cartObjNode.put("IGST_TAX", cartObj.get("itemTax").get("IGST_TAX"));
               		
               		cartAmount = prdPrice.multiply(new BigDecimal(productQty));
               		cartObjNode.put("Item_Tot_Amt", totCartItemAmt.floatValue());
               		cartObjNode.put("Item_Original_Amt", totCartMrpAmt.floatValue());
               		cartObjNode.put("product_Name", productName);
               		cartObjNode.put("bar_code", barcode);
               		cartObjNode.put("product_qty", productQty);
               		cartObjNode.put("cartAmount", cartAmount.floatValue());
               		
               		cartItemsMap.put("cartItemsMap", cartObjNode);
               		System.out.println("cartItemsMap--"+cartItemsMap.get("cartItemsMap"));
               		cartItemList.add(cartItemsMap.get("cartItemsMap"));
               	}
               	if(actualObj.get("customerShipState") != null) {
               		int strLength = actualObj.get("customerShipState").toString().length();
               		httpSession.setAttribute("customerShipState", actualObj.get("customerShipState").toString().substring(1, strLength-1));
               	}
               	if(actualObj.get("customerMobileNo") != null) {
               		modelAndView.addObject("contactNumber", actualObj.get("customerMobileNo").toString().substring(1, actualObj.get("customerMobileNo").toString().length()-1));
               	} else {
               		modelAndView.addObject("contactNumber", "");
               	}
               	if(actualObj.get("firstName") != null) {
               		modelAndView.addObject("customerName", actualObj.get("firstName").toString().substring(1, actualObj.get("firstName").toString().length()-1));
               	}else {
               		modelAndView.addObject("customerName", "");
               	}
               	if(actualObj.get("address1") != null) {
               		modelAndView.addObject("contactAddress", actualObj.get("address1").toString().substring(1, actualObj.get("address1").toString().length()-1));
               	}else {
               		modelAndView.addObject("contactAddress", "");
               	}
               	billDate = actualObj.get("billDate").toString().substring(1, actualObj.get("billDate").toString().length()-1);
               	billAmount = actualObj.get("subTotal").toString();
               	modelAndView.addObject("billDate", billDate);
               	modelAndView.addObject("totalDue", actualObj.get("totalDue"));
               	modelAndView.addObject("subTotal", actualObj.get("subTotal"));
               	modelAndView.addObject("totalSalesTax", actualObj.get("totalSalesTax"));
               	//modelAndView.addObject("cartSize", actualObj.get("cartSize"));
               	modelAndView.addObject("cartSize", actualObj.get("totalQuantity"));
               	modelAndView.addObject("displayOrgGrandTotal", actualObj.get("displayOrgGrandTotal"));
               	modelAndView.addObject("displayGrandTotal", actualObj.get("displayGrandTotal"));
               	modelAndView.addObject("displaySPGrandTotal", actualObj.get("displaySPGrandTotal"));
               	modelAndView.addObject("productOriginalPrice", actualObj.get("productOriginalPrice"));
               	modelAndView.addObject("balanceAmount", actualObj.get("balanceAmount"));
               	//Calculate Discount & Disc %
               	if(actualObj.get("displayMRPGrandTotal") != null) {
            		grandTotal = (BigDecimal) actualObj.get("displayMRPGrandTotal").decimalValue();
            	}
               	if(actualObj.get("displaySPGrandTotal") != null) {
               		spGrandTotal = (BigDecimal) actualObj.get("displaySPGrandTotal").decimalValue();
               	}
               	discPer = Math.round((grandTotal.floatValue() - spGrandTotal.floatValue()) / (grandTotal.floatValue()) * ((float) 100));
               	discAmount = (grandTotal.floatValue() - spGrandTotal.floatValue());
               	modelAndView.addObject("discPer", discPer);
               	modelAndView.addObject("discAmount", discAmount);
           		//End of calculating Disc & Disc%
                   //modelAndView.addObject("cartItems", actualObj.get("cartItems"));
                   modelAndView.addObject("cartItems", cartItemList);
                   modelAndView.addObject("itemTax", cartObj.get("itemTax"));
                   modelAndView.addObject("billReceiptId", receiptId);
                   //NodeBean toValue = mapper.readValue(parser, NodeBean.class);
               }
               
               Hashtable<String, Object> addlCartParams = new Hashtable<String, Object>();
               addlCartParams.put("receiptId", receiptId);
               addlCartParams.put("billAmount", billAmount);
       		
               HttpEntity addlCartRequest = new HttpEntity(addlCartParams, headers);
       		   HashMapResponse addlCartResult = restTemplate.postForObject(getAddlCartDetailsUri,
       				addlCartRequest, HashMapResponse.class);
   			
	   		   HashMap<String, Object> addlCartList = addlCartResult.getResponse();
	   		   modelAndView.addObject("discountPer", addlCartList.get("discountPer"));
	   		   modelAndView.addObject("discount", addlCartList.get("discount"));
	   		   modelAndView.addObject("isPercentage", addlCartList.get("isPercentage"));
	   		   modelAndView.addObject("charges", addlCartList.get("charges"));
	   		   modelAndView.addObject("isChargePercentage", addlCartList.get("isChargePercentage"));
	   		   modelAndView.addObject("chargePercentage", addlCartList.get("chargePer"));
	   			
   		} catch (Exception e) {
   			
   		}
           //return new ModelAndView("cart/BillPrint", modelAndView.getModel());
   			return new ModelAndView("cart/BillPrint_new", modelAndView.getModel());
       }
    /**
     * Method Name: enableTaxInvoice 
     * Method Description: Method for Enabling Tax
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "enableTaxInvoice", method = RequestMethod.GET)
	public ResponseEntity<?> enableTaxInvoice(@RequestParam("json") String json,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
    	
    	final String uri = env.getProperty("enableTaxInvoice");
    	ObjectMapper mapper = new ObjectMapper();
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		Map<String, String> inputMap = new HashMap<String, String>();
		String success  = "";
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
			success = (String) list.get("Success");
            if(success.equals(null) || success.equals("") || success.equals(" ")) {
            	return new ResponseEntity<>(jsonString, HttpStatus.BAD_REQUEST);
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	/**
     * Method Name: updateCartTxn 
     * Method Description: Method for Updating Cart transaction
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "updateCartTxn", method = RequestMethod.GET)
	public ResponseEntity<?> updateCartTxn(@RequestParam("json") String json,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
    	
    	final String uri = env.getProperty("updateCartTxn");
    	ObjectMapper mapper = new ObjectMapper();
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		Map<String, String> inputMap = new HashMap<String, String>();
		String success  = "";
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
			success = (String) list.get("Success");
            if(success.equals(null) || success.equals("") || success.equals(" ")) {
            	return new ResponseEntity<>(jsonString, HttpStatus.BAD_REQUEST);
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/findPosCustomers")
    public ResponseEntity<?> findPosCustomers(
    		@RequestParam("contactNumber") String contactNumber) {
    	
    
    	final String uri = env.getProperty("findPosCustomers");
    	//assign
    	Hashtable<String, String> params = new Hashtable<String, String>();
    
    	ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        
        try {
            
            HttpHeaders headers = new HttpHeaders();
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		RestTemplate restTemplate = new RestTemplate();
    		Map<Object, Object> custParams = new HashMap<>();
    		custParams.put("contactNumber", contactNumber);
    		
    		HttpEntity custRequest = new HttpEntity(custParams, headers);
    		
    		ResponseEntity<String> response = restTemplate.postForEntity(uri, custRequest, String.class);
    		JsonNode actualObj = mapper.readTree(response.getBody());
            jsonString = mapper.writeValueAsString(actualObj);
            System.out.println("Mobile number findPosCustomers -----"+jsonString);
            //jsonString = mapper.writeValueAsString(response.getBody());
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
       
    	return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
}
