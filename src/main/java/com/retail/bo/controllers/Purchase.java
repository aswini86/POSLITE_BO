package com.retail.bo.controllers;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.bo.services.RestClient;
import com.retail.bo.util.HashMapResponse;

@Controller
public class Purchase extends BaseController {
	
	@Autowired
    private ApplicationContext context;
	
	@Autowired
	private Environment env;
	
	@Autowired
    private RestTemplate restTemplate;
	
    @GetMapping("createPurchaseInward")
    public ModelAndView createPurchaseInward() {
    	
    	ModelAndView modelAndView = new ModelAndView();
        String productStoreGroupId = getProductStoreGroupId();
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        try {
        	
        	final String uri = env.getProperty("findRateProductPrice");
        	
        	Hashtable<String, Object> params = new Hashtable<String, Object>();
        	HttpHeaders headers = new HttpHeaders();
    		params.put("currencyUomId", currencyUomId);
    		params.put("productStoreGroupId", productStoreGroupId);
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		
        	HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
    		HashMap<String, Object> list = result.getResponse();
    		
    		modelAndView.addObject("productsList", list.get("finalProductPriceInfoList"));
    		
        } catch (Exception e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        
        return new ModelAndView("purchase/create-purchase-inward", modelAndView.getModel());
    }
    
    @GetMapping("createPurchaseOrder")
    public ModelAndView createPurchaseOrder() {
    	
    	ModelAndView modelAndView = new ModelAndView();
        String productStoreGroupId = getProductStoreGroupId();
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        try {
        	final String findVendorUri = env.getProperty("findVendors");
        	final String uri = env.getProperty("findVendorProducts");
        	
        	Hashtable<String, Object> params = new Hashtable<String, Object>();
        	HttpHeaders headers = new HttpHeaders();
    		params.put("currencyUomId", currencyUomId);
    		params.put("productStoreGroupId", productStoreGroupId);
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		
        	HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
    		HashMap<String, Object> list = result.getResponse();
    		
    		modelAndView.addObject("productsList", list.get("finalProductPriceInfoList"));
    		
    		Hashtable<String, Object> findVendorParams = new Hashtable<String, Object>();
    		findVendorParams.put("roleTypeId", "SUPPLIER");
    		HttpEntity findVendorRequest = new HttpEntity(findVendorParams, headers);
    		
    		HashMapResponse findVendorResult = restTemplate.postForObject(findVendorUri, findVendorRequest, HashMapResponse.class);
    		HashMap<String, Object> findVendorList = findVendorResult.getResponse();
    		modelAndView.addObject("partyRoleList", findVendorList.get("partyRoleList"));
    		
        } catch (Exception e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        
        return new ModelAndView("purchase/create-purchase-order", modelAndView.getModel());
    }
    
    @GetMapping("editPurchaseOrder")
    public ModelAndView editPurchaseOrder(String orderId) {
    	
    	ModelAndView modelAndView = new ModelAndView();
        String productStoreGroupId = getProductStoreGroupId();
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        String finalOrderItemArray = new String();
        String finalOrderQtyArray = new String();
        String finalOrderItemMRPArray = new String();
        String finaloiOfferRateArray = new String();
        String finalOrderTaxArray = new String();
        String finalSgstAmtArray = new String();
        String finalCgstAmtArray = new String();
        String finalIgstAmtArray = new String();
        
        try {
        	final String findVendorUri = env.getProperty("findVendors");
        	final String uri = env.getProperty("findVendorProducts");
        	final String orderListURI = env.getProperty("findPOItems");
        	
        	Hashtable<String, Object> params = new Hashtable<String, Object>();
        	HttpHeaders headers = new HttpHeaders();
    		params.put("currencyUomId", currencyUomId);
    		params.put("productStoreGroupId", productStoreGroupId);
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		
        	HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
    		HashMap<String, Object> list = result.getResponse();
    		
    		modelAndView.addObject("productsList", list.get("finalProductPriceInfoList"));
    		
    		Hashtable<String, Object> findVendorParams = new Hashtable<String, Object>();
    		findVendorParams.put("roleTypeId", "SUPPLIER");
    		HttpEntity findVendorRequest = new HttpEntity(findVendorParams, headers);
    		
    		HashMapResponse findVendorResult = restTemplate.postForObject(findVendorUri, findVendorRequest, HashMapResponse.class);
    		HashMap<String, Object> findVendorList = findVendorResult.getResponse();
    		modelAndView.addObject("partyRoleList", findVendorList.get("partyRoleList"));
    		
    		Hashtable<String, Object> findOrderItemParams = new Hashtable<String, Object>();
    		findOrderItemParams.put("orderFlowType", "PURCHASEORDER");
    		findOrderItemParams.put("orderId", orderId);
    		findOrderItemParams.put("currencyUomId", currencyUomId);
    		findOrderItemParams.put("productStoreId", productStoreId);
    		
    		HttpEntity findOIRequest = new HttpEntity(findOrderItemParams, headers);
    		
    		HashMapResponse findOIResult = restTemplate.postForObject(orderListURI, findOIRequest, HashMapResponse.class);
    		
    		HashMap<String, Object> findOIList = findOIResult.getResponse();
    		String orderItemId = findOIList.get("orderItemArray").toString();
    		finalOrderItemArray = orderItemId.substring(1, orderItemId.length()-1);
			if (finalOrderItemArray != null) {
				modelAndView.addObject("finalOrderItemArray",finalOrderItemArray);
			}
    		String orderQty = findOIList.get("orderQuantityArray").toString();
    		finalOrderQtyArray = orderQty.substring(1, orderQty.length()-1);
			if (finalOrderQtyArray != null) {
				modelAndView.addObject("finalOrderQtyArray",finalOrderQtyArray);
			}
    		String orderItemMrp = findOIList.get("orderItemMRPArray").toString();
    		finalOrderItemMRPArray = orderItemMrp.substring(1, orderItemMrp.length()-1);
			if (finalOrderItemMRPArray != null) {
				modelAndView.addObject("finalOrderItemMRPArray",finalOrderItemMRPArray);
			}
			String orderOfferRate = findOIList.get("oiOfferRateArray").toString();
			finaloiOfferRateArray = orderOfferRate.substring(1, orderOfferRate.length()-1);
			if (finaloiOfferRateArray != null) {
				modelAndView.addObject("finaloiOfferRateArray",finaloiOfferRateArray);
			}
			String ordertax = findOIList.get("oiTaxRateArray").toString();
    		finalOrderTaxArray = ordertax.substring(1, ordertax.length()-1);
			if (finalOrderTaxArray != null) {
				modelAndView.addObject("finalOrderTaxArray",finalOrderTaxArray);
			}
			String sgstAmt = findOIList.get("sgstAmtArray").toString();
			finalSgstAmtArray = sgstAmt.substring(1, sgstAmt.length()-1);
			if (finalSgstAmtArray != null) {
				modelAndView.addObject("finalSgstAmtArray",finalSgstAmtArray);
			}
			String cgstAmt = findOIList.get("cgstAmtArray").toString();
			finalCgstAmtArray = cgstAmt.substring(1, cgstAmt.length()-1);
			if (finalCgstAmtArray != null) {
				modelAndView.addObject("finalCgstAmtArray",finalCgstAmtArray);
			}
			String igstAmt = findOIList.get("igstAmtArray").toString();
			finalIgstAmtArray = igstAmt.substring(1, igstAmt.length()-1);
			if (finalIgstAmtArray != null) {
				modelAndView.addObject("finalIgstAmtArray",finalIgstAmtArray);
			}
			
    		modelAndView.addObject("orderItemList", findOIList.get("orderItemList"));
    		modelAndView.addObject("orderId",orderId);
        } catch (Exception e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        
        return new ModelAndView("purchase/edit-purchase-order", modelAndView.getModel());
    }
    
    @GetMapping("approveCreatePurchaseOrder")
    public ModelAndView approveCreatePurchaseOrder(String orderId) {
    	
    	ModelAndView modelAndView = new ModelAndView();
        String productStoreGroupId = getProductStoreGroupId();
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        String finalOrderItemArray = new String();
        String finalOrderQtyArray = new String();
        String finalOrderItemMRPArray = new String();
        String finaloiOfferRateArray = new String();
        String finalOrderTaxArray = new String();
        String finalSgstAmtArray = new String();
        String finalCgstAmtArray = new String();
        String finalIgstAmtArray = new String();
        
        try {
        	final String findVendorUri = env.getProperty("findVendors");
        	final String uri = env.getProperty("findVendorProducts");
        	final String orderListURI = env.getProperty("findPOItems");
        	
        	Hashtable<String, Object> params = new Hashtable<String, Object>();
        	HttpHeaders headers = new HttpHeaders();
    		params.put("currencyUomId", currencyUomId);
    		params.put("productStoreGroupId", productStoreGroupId);
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		
        	HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
    		HashMap<String, Object> list = result.getResponse();
    		
    		modelAndView.addObject("productsList", list.get("finalProductPriceInfoList"));
    		
    		Hashtable<String, Object> findVendorParams = new Hashtable<String, Object>();
    		findVendorParams.put("roleTypeId", "SUPPLIER");
    		HttpEntity findVendorRequest = new HttpEntity(findVendorParams, headers);
    		
    		HashMapResponse findVendorResult = restTemplate.postForObject(findVendorUri, findVendorRequest, HashMapResponse.class);
    		HashMap<String, Object> findVendorList = findVendorResult.getResponse();
    		modelAndView.addObject("partyRoleList", findVendorList.get("partyRoleList"));
    		
    		Hashtable<String, Object> findOrderItemParams = new Hashtable<String, Object>();
    		findOrderItemParams.put("orderFlowType", "PURCHASEORDER");
    		findOrderItemParams.put("orderId", orderId);
    		findOrderItemParams.put("currencyUomId", currencyUomId);
    		findOrderItemParams.put("productStoreId", productStoreId);
    		
    		HttpEntity findOIRequest = new HttpEntity(findOrderItemParams, headers);
    		
    		HashMapResponse findOIResult = restTemplate.postForObject(orderListURI, findOIRequest, HashMapResponse.class);
    		
    		HashMap<String, Object> findOIList = findOIResult.getResponse();
    		String orderItemId = findOIList.get("orderItemArray").toString();
    		finalOrderItemArray = orderItemId.substring(1, orderItemId.length()-1);
			if (finalOrderItemArray != null) {
				modelAndView.addObject("finalOrderItemArray",finalOrderItemArray);
			}
    		String orderQty = findOIList.get("orderQuantityArray").toString();
    		finalOrderQtyArray = orderQty.substring(1, orderQty.length()-1);
			if (finalOrderQtyArray != null) {
				modelAndView.addObject("finalOrderQtyArray",finalOrderQtyArray);
			}
    		String orderItemMrp = findOIList.get("orderItemMRPArray").toString();
    		finalOrderItemMRPArray = orderItemMrp.substring(1, orderItemMrp.length()-1);
			if (finalOrderItemMRPArray != null) {
				modelAndView.addObject("finalOrderItemMRPArray",finalOrderItemMRPArray);
			}
			String orderOfferRate = findOIList.get("oiOfferRateArray").toString();
			finaloiOfferRateArray = orderOfferRate.substring(1, orderOfferRate.length()-1);
			if (finaloiOfferRateArray != null) {
				modelAndView.addObject("finaloiOfferRateArray",finaloiOfferRateArray);
			}
			String ordertax = findOIList.get("oiTaxRateArray").toString();
    		finalOrderTaxArray = ordertax.substring(1, ordertax.length()-1);
			if (finalOrderTaxArray != null) {
				modelAndView.addObject("finalOrderTaxArray",finalOrderTaxArray);
			}
			String sgstAmt = findOIList.get("sgstAmtArray").toString();
			finalSgstAmtArray = sgstAmt.substring(1, sgstAmt.length()-1);
			if (finalSgstAmtArray != null) {
				modelAndView.addObject("finalSgstAmtArray",finalSgstAmtArray);
			}
			String cgstAmt = findOIList.get("cgstAmtArray").toString();
			finalCgstAmtArray = cgstAmt.substring(1, cgstAmt.length()-1);
			if (finalCgstAmtArray != null) {
				modelAndView.addObject("finalCgstAmtArray",finalCgstAmtArray);
			}
			String igstAmt = findOIList.get("igstAmtArray").toString();
			finalIgstAmtArray = igstAmt.substring(1, igstAmt.length()-1);
			if (finalIgstAmtArray != null) {
				modelAndView.addObject("finalIgstAmtArray",finalIgstAmtArray);
			}
			
    		modelAndView.addObject("orderItemList", findOIList.get("orderItemList"));
    		modelAndView.addObject("orderId",orderId);
        } catch (Exception e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        
        return new ModelAndView("purchase/confirm-purchase-order", modelAndView.getModel());
    }
    
    @GetMapping("createPurchaseOutward")
    public ModelAndView createPurchaseOutward() {
    	
    	ModelAndView modelAndView = new ModelAndView();
        String productStoreGroupId = getProductStoreGroupId();
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        try {
        	
        	final String uri = env.getProperty("findRateProductPrice");
        	
        	Hashtable<String, Object> params = new Hashtable<String, Object>();
        	HttpHeaders headers = new HttpHeaders();
    		params.put("currencyUomId", currencyUomId);
    		params.put("productStoreGroupId", productStoreGroupId);
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		
        	HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
    		HashMap<String, Object> list = result.getResponse();
    		
    		modelAndView.addObject("productsList", list.get("finalProductPriceInfoList"));
    		
        } catch (Exception e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
    	
        return new ModelAndView("purchase/create-purchase-outward", modelAndView.getModel());
    }
    
    /**
     * Method Name: searchArticlesByVendor
     * Method Description: Url to Search Articles by Vendor
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "searchArticlesByVendor", method = RequestMethod.GET)
	public ResponseEntity<?> searchArticlesByVendor(@RequestParam("json") String json,
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
		final String uri = env.getProperty("findVendorProducts");
		String supplierId = params.get("supplierId");
		String productId = params.get("productId");
		String currencyUomId = getCurrencyUomId();
    	
		Map<String, String> inputMap = new HashMap<String, String>();
		if(!(productId.isEmpty())) {
			params.put("productId", productId);
		}
		if(!(supplierId.isEmpty())) {
			params.put("supplierId", supplierId);
		}
		params.put("productStoreId", productStoreId);
		params.put("currencyUomId", currencyUomId);
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
    
	/**
     * MethodName: createPurchaseOrder
     * Method Description: To create Purchase Order
     */
    @RequestMapping(value = "/purchaseOrder/Create", method = RequestMethod.GET)
    public ResponseEntity<?> createPurchaseOrder(@RequestParam("json") String json, 
    		@RequestParam("password") String password,
            @RequestParam("username") String username) {
        final String uri = env.getProperty("create.purchaseOrder");
        Hashtable<String, String> params = new Hashtable<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            params.put("createdBy", username);
            params.put("productStoreId", productStoreId);
            params.put("currencyUomId", currencyUomId);
            params.put("login.username", username);
            params.put("login.password", password);
            System.out.println(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in create Purchse Order----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        ModelAndView modelAndView = new ModelAndView();
        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.toString());
        try {
            jsonString = mapper.writeValueAsString(list.get("orderId"));
            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
	
    /**
     * MethodName: createPurchaseOrder
     * Method Description: To create Purchase Order
     */
    @RequestMapping(value = "/purchaseOrder/Edit", method = RequestMethod.GET)
    public ResponseEntity<?> editPurchaseOrder(@RequestParam("json") String json, 
    		@RequestParam("password") String password,
            @RequestParam("username") String username) {
        final String uri = env.getProperty("edit.purchaseOrder");
        Hashtable<String, String> params = new Hashtable<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            params.put("createdBy", username);
            params.put("productStoreId", productStoreId);
            params.put("currencyUomId", currencyUomId);
            params.put("login.username", username);
            params.put("login.password", password);
            System.out.println(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in create Purchse Order----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        ModelAndView modelAndView = new ModelAndView();
        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.toString());
        try {
            jsonString = mapper.writeValueAsString(list.get("orderId"));
            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    /**
     * MethodName: createPurchaseOrder
     * Method Description: To create Purchase Order
     */
    @RequestMapping(value = "/purchaseOrder/Confirm", method = RequestMethod.GET)
    public ResponseEntity<?> confirmPurchaseOrder(@RequestParam("json") String json, 
    		@RequestParam("password") String password,
            @RequestParam("username") String username) {
        final String uri = env.getProperty("edit.purchaseOrder");
        Hashtable<String, String> params = new Hashtable<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            params.put("createdBy", username);
            params.put("productStoreId", productStoreId);
            params.put("currencyUomId", currencyUomId);
            params.put("login.username", username);
            params.put("login.password", password);
            System.out.println(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in create Purchse Order----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        ModelAndView modelAndView = new ModelAndView();
        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.toString());
        try {
            jsonString = mapper.writeValueAsString(list.get("orderId"));
            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    @GetMapping("purchaseInwardList")
    public ModelAndView purchaseInwardList() {
    	ModelAndView modelAndView = new ModelAndView();
        String productStoreGroupId = getProductStoreGroupId();
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        
        try {
        	final String uri = env.getProperty("findPurchaseOrder");
        	
        	Hashtable<String, Object> params = new Hashtable<String, Object>();
        	HttpHeaders headers = new HttpHeaders();
        	params.put("orderFlowType", "INWARD");
    		params.put("currencyUomId", currencyUomId);
    		params.put("productStoreGroupId", productStoreGroupId);
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		
        	HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
    		HashMap<String, Object> list = result.getResponse();
    		
    		modelAndView.addObject("orderList", list.get("orderList"));
    		
        } catch (Exception e) {
        	
        }
        return new ModelAndView("purchase/purchase-inward-list");
    }
    
    @GetMapping("purchaseOrderList")
    public ModelAndView purchaseOrderList() {
    	ModelAndView modelAndView = new ModelAndView();
        String productStoreGroupId = getProductStoreGroupId();
        String productStoreId = getStoreId();
        String currencyUomId = getCurrencyUomId();
        
        try {
        	final String uri = env.getProperty("findPurchaseOrder");
        	
        	Hashtable<String, Object> params = new Hashtable<String, Object>();
        	HttpHeaders headers = new HttpHeaders();
        	params.put("orderFlowType", "PURCHASEORDER");
    		params.put("currencyUomId", currencyUomId);
    		params.put("productStoreGroupId", productStoreGroupId);
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		
        	HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
    		HashMap<String, Object> list = result.getResponse();
    		
    		modelAndView.addObject("orderList", list.get("orderList"));
    		
        } catch (Exception e) {
        	
        }
        return new ModelAndView("purchase/purchase-order-list", modelAndView.getModel());
    }
    
    @GetMapping("purchaseOutwardList")
    public ModelAndView purchaseOutwardList() {
        return new ModelAndView("purchase/purchase-outward-list");
    }
    
}
