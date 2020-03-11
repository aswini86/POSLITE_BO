package com.retail.bo.controllers;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.retail.bo.services.RestClient;
import org.springframework.core.env.Environment;
import com.retail.bo.util.HashMapResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.retail.bo.controllers.Rate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Controller
public class Stock extends BaseController {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Environment env;

    @GetMapping("addAtricles")
    public ModelAndView addAtricles() {
        return new ModelAndView("stock/add-articles");
    }

    @GetMapping("stockAdjustmentList")
    public ModelAndView stockAdjustmentList() {

        final String uriFacility = env.getProperty("performFind");
        Hashtable<String, Object> params = new Hashtable<String, Object>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView();
        
        params.put("entityName", "InventoryItemTrx");
        params.put("noConditionFind", "Y");
        params.put("inputFields", new HashMap<>());

        HttpEntity requestStore = new HttpEntity(params, headers);
        HashMapResponse result1 = restTemplate.postForObject(uriFacility, requestStore, HashMapResponse.class);
        System.out.println("Result 1 is -------" + result1.toString());
        HashMap<String, Object> list1 = result1.getResponse();
        System.out.println("result 1-----" + list1.get("list").toString());
        modelAndView.addObject("inventoryitemVarList", list1.get("list"));

        modelAndView.setViewName("stock/stock-adjustment-list");
        return modelAndView;
    }
    
    @GetMapping("stockAdjustment")
    public ModelAndView stockAdjustment() {

        final String uri = env.getProperty("list.product");
        final String searchArticleUri = env.getProperty("findRateProductPrice");
        Hashtable<String, String> params = new Hashtable<String, String>();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in find product----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        
        HashMapResponse searchArticleResult = restTemplate.postForObject(searchArticleUri, request, HashMapResponse.class);
		HashMap<String, Object> searchArticleList = searchArticleResult.getResponse();
        System.out.println("Result is -------" + result.toString());

        ModelAndView modelAndView = new ModelAndView();
        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.get("inventoryItemList").toString());
        modelAndView.addObject("result", list.get("inventoryItemList"));

        final String uriFacility = env.getProperty("find.facility");

        HttpEntity requestStore = new HttpEntity(params, headers);
        HashMapResponse result1 = restTemplate.postForObject(uriFacility, requestStore, HashMapResponse.class);
        System.out.println("Result 1 is -------" + result1.toString());
        HashMap<String, Object> list1 = result1.getResponse();
        System.out.println("result 1-----" + list1.get("facilityList").toString());
        modelAndView.addObject("result1", list1.get("facilityList"));
        
        modelAndView.addObject("productsList", searchArticleList.get("finalProductPriceInfoList"));
        modelAndView.setViewName("stock/stock-adjustment");

        return modelAndView;
    }
    
    @GetMapping("approveStockAdjustment")
    public ModelAndView approveStockAdjustment(String inventoryItemTxId) {

        final String uri = env.getProperty("findStockAdjustmentTrx");
        final String searchArticleUri = env.getProperty("findRateProductPrice");
        Hashtable<String, String> params = new Hashtable<String, String>();
        HttpHeaders headers = new HttpHeaders();
        String finalinventoryItemArray = new String();
        String finalQuantityArray = new String();
        String finalVarReasonIdArray = new String();
        String productStoreId = getStoreId();
        
        params.put("productStoreId", productStoreId);
        params.put("inventoryItemTxId", inventoryItemTxId);
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in find product----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        
        HashMapResponse searchArticleResult = restTemplate.postForObject(searchArticleUri, request, HashMapResponse.class);
		HashMap<String, Object> searchArticleList = searchArticleResult.getResponse();
        System.out.println("Result is -------" + result.toString());

        ModelAndView modelAndView = new ModelAndView();
        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.get("inventoryItemList").toString());
        String invItemId = list.get("inventoryItemArray").toString();
        finalinventoryItemArray = invItemId.substring(1, invItemId.length()-1);
        
        String quantityArray = list.get("quantityArray").toString();
        finalQuantityArray = quantityArray.substring(1, quantityArray.length()-1);
        
        String varReasonArray = list.get("varReasonIdArray").toString();
        finalVarReasonIdArray = varReasonArray.substring(1, varReasonArray.length()-1);
		
        modelAndView.addObject("result", list.get("inventoryItemList"));
        modelAndView.addObject("finalQuantityArray", finalQuantityArray);
        modelAndView.addObject("finalinventoryItemArray", finalinventoryItemArray);
        modelAndView.addObject("finalVarReasonIdArray", finalVarReasonIdArray);

        final String uriFacility = env.getProperty("find.facility");

        HttpEntity requestStore = new HttpEntity(params, headers);
        HashMapResponse result1 = restTemplate.postForObject(uriFacility, requestStore, HashMapResponse.class);
        System.out.println("Result 1 is -------" + result1.toString());
        HashMap<String, Object> list1 = result1.getResponse();
        System.out.println("result 1-----" + list1.get("facilityList").toString());
        modelAndView.addObject("result1", list1.get("facilityList"));
        
        modelAndView.addObject("productsList", searchArticleList.get("finalProductPriceInfoList"));
        modelAndView.addObject("inventoryItemTxId", inventoryItemTxId);
        modelAndView.setViewName("stock/approve-stock-adjustment");

        return modelAndView;
    }
    
    @GetMapping("viewStockAdjustment")
    public ModelAndView viewStockAdjustment(String inventoryItemTxId) {

        final String uri = env.getProperty("findStockAdjustmentTrx");
        final String searchArticleUri = env.getProperty("findRateProductPrice");
        Hashtable<String, String> params = new Hashtable<String, String>();
        HttpHeaders headers = new HttpHeaders();
        String productStoreId = getStoreId();
        
        params.put("productStoreId", productStoreId);
        params.put("inventoryItemTxId", inventoryItemTxId);
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in find product----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        
        HashMapResponse searchArticleResult = restTemplate.postForObject(searchArticleUri, request, HashMapResponse.class);
		HashMap<String, Object> searchArticleList = searchArticleResult.getResponse();
        System.out.println("Result is -------" + result.toString());

        ModelAndView modelAndView = new ModelAndView();
        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.get("inventoryItemList").toString());
        modelAndView.addObject("result", list.get("inventoryItemList"));

        final String uriFacility = env.getProperty("find.facility");

        HttpEntity requestStore = new HttpEntity(params, headers);
        HashMapResponse result1 = restTemplate.postForObject(uriFacility, requestStore, HashMapResponse.class);
        System.out.println("Result 1 is -------" + result1.toString());
        HashMap<String, Object> list1 = result1.getResponse();
        System.out.println("result 1-----" + list1.get("facilityList").toString());
        modelAndView.addObject("result1", list1.get("facilityList"));
        
        modelAndView.addObject("productsList", searchArticleList.get("finalProductPriceInfoList"));
        modelAndView.setViewName("stock/view-stock-adjustment");

        return modelAndView;
    }

    @GetMapping("stockCheckCriteria")
    public ModelAndView stockCheckCriteria() {
        return new ModelAndView("stock/stock-check-criteria");
    }

    @GetMapping("stockCheckEntry")
    public ModelAndView stockCheckEntry() {
        ModelAndView modelAndView = new ModelAndView();
        //Get Department List
        try {
            List<Object> facilities = findFacility();
            modelAndView.addObject("failityList", facilities);
            //Get list of Faility Users
            List<Object> facilityUserList = findFacilityUser();
            modelAndView.addObject("failityUserList", facilityUserList);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        //find inventory item code:::::::::::::: starts:::::::::::::::::::here
        final String uri = env.getProperty("find.findInventoryItem");
        Hashtable<String, String> params = new Hashtable<String, String>();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in find product----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.get("InventoryItemList").toString());

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            modelAndView.addObject("inventoryItemList", list.get("InventoryItemList"));
            jsonString = mapper.writeValueAsString(list.get("InventoryItemList"));
            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }

        //find inventory item code :::::::::::::::::::::closes:::::::::::::::::::::here
        return new ModelAndView("stock/stock-check-entry", modelAndView.getModel());
    }

    @GetMapping("getStockCheckEntry")
    public ModelAndView getStockCheckEntry(@RequestParam("facilityId") String facilityId,
            @RequestParam("workEffortId") String workEffortId) {
        ModelAndView modelAndView = new ModelAndView();
        Map<String,String> productMap = new HashMap<String, String>();
        //Get work effort list
        try {

            HashMap<String, Object> workEffortList = getWorkEffort(workEffortId);
            modelAndView.addObject("workEffortList", workEffortList.get("workEffort"));
            modelAndView.addObject("partyAssignList", workEffortList.get("partyAssigns"));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        //Get Department List
        try {
            List<Object> facilities = findFacility();
            modelAndView.addObject("failityList", facilities);
            //get facility name
            Object stockFacility = getFacilityName(facilityId);
            modelAndView.addObject("stockFacility", stockFacility);
            //Get list of Faility Users
            List<Object> facilityUserList = findFacilityUser();
            modelAndView.addObject("failityUserList", facilityUserList);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        //find inventory item code:::::::::::::: starts:::::::::::::::::::here
        final String uri = env.getProperty("find.findInventoryItem");
        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("facilityId", facilityId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in find Stock check entry----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.get("InventoryItemList").toString());

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "", productId = "";
        try {
        	//get product MRP.
        	List<LinkedHashMap> listMap  = (List<LinkedHashMap>) list.get("InventoryItemList");
			for (LinkedHashMap listEach : listMap) {
				productId = (String) listEach.get("productId");
				//get product default price for MRP
				productMap = getProductMrp(productId);
				
				listEach.put("productPrice", productMap.get("productPrice"));
				listEach.put("currencyUomId", productMap.get("currencyUomId"));
			}
            modelAndView.addObject("inventoryItemList", list.get("InventoryItemList"));
            jsonString = mapper.writeValueAsString(list.get("InventoryItemList"));
            
            System.out.println("List" + jsonString);
        } catch (Exception e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }

        //find inventory item code :::::::::::::::::::::closes:::::::::::::::::::::here
        return new ModelAndView("stock/stock-check-entry", modelAndView.getModel());
    }

    @RequestMapping(value = "/updateWorkEffort", method = RequestMethod.GET)
    public ResponseEntity<?> updateWorkEffort(@RequestParam("json") String json,
            @RequestParam("password") String password,
            @RequestParam("username") String username) {
        ModelAndView modelAndView = new ModelAndView();

        //update WorkEffort
        final String uri = env.getProperty("updateWorkEffort");
        final String createIIAttrUri = env.getProperty("createInventoryItemAttr");
        final String updateInvItemUri = env.getProperty("updateInventoryItem");
        final String createphysicalInvVar = env.getProperty("create.inventoryItemVariance");
        final String removeIIAttributeUri = env.getProperty("deleteInventoryItemAttribute");
        //get array
        Hashtable<String, Object> params = new Hashtable<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        String statusId = "";
        List inventoryItemArray = new ArrayList<String>();
        List scanQtyArray = new ArrayList<String>();
        List sysQtyArray = new ArrayList<String>();
        BigDecimal sysQtyVar = BigDecimal.ZERO;
        try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, Object>>() {
            });
            statusId = (String) params.get("statusId");
            inventoryItemArray = (ArrayList<String>) params.get("invItemArray");
            scanQtyArray = (ArrayList<String>) params.get("scanQtyArray");
            sysQtyArray = (ArrayList<String>) params.get("sysQtyArray");
            params.put("login.username", username);
            params.put("login.password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //params.put("workEffortId", workEffortId);
        params.put("currentStatusId", statusId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        params.remove("scanQtyArray");
        params.remove("invItemArray");
        params.remove("sysQtyArray");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in update Stock check entry----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        HashMap<String, Object> list = result.getResponse();
        if (statusId.equals("CAL_COMPLETED")) {
            //Update Inventory Item
            for (int i = 0; i < inventoryItemArray.size(); i++) {
                Hashtable<String, String> updateIIParams = new Hashtable<String, String>();
                updateIIParams.put("inventoryItemId", (String) inventoryItemArray.get(i));
                updateIIParams.put("accountingQuantityTotal", (String) scanQtyArray.get(i));

                HttpHeaders IIheaders = new HttpHeaders();
                headers.add("Content-Type", "application/json");
                HttpEntity IIrequest = new HttpEntity(updateIIParams, headers);

                RestTemplate IIrestTemplate = new RestTemplate();
                HashMapResponse IIresult = IIrestTemplate.postForObject(updateInvItemUri, IIrequest, HashMapResponse.class);
            }
            //Update Physical Inventory Variance
            for (int i = 0; i < inventoryItemArray.size(); i++) {
                //calculate sys variance
                String scanQty = (String) scanQtyArray.get(i);
                String sysQty = (String) sysQtyArray.get(i);
                BigDecimal sysQtyB = new BigDecimal(sysQty);
                BigDecimal scanQtyB = new BigDecimal(scanQty);
                sysQtyVar = scanQtyB.subtract(sysQtyB);
                Hashtable<String, Object> updatePhyInvVarParams = new Hashtable<String, Object>();

                updatePhyInvVarParams.put("inventoryItemId", (String) inventoryItemArray.get(i));
                updatePhyInvVarParams.put("quantityOnHandVar", sysQtyVar);
                updatePhyInvVarParams.put("varianceReasonId", "VAR_INTEGR");
                updatePhyInvVarParams.put("comments", "Updated for stock check");
                updatePhyInvVarParams.put("partyId", "admin");

                HttpHeaders IIVheaders = new HttpHeaders();
                IIVheaders.add("Content-Type", "application/json");
                HttpEntity IIVrequest = new HttpEntity(updatePhyInvVarParams, IIVheaders);

                RestTemplate IIVrestTemplate = new RestTemplate();
                HashMapResponse IIVresult = IIVrestTemplate.postForObject(createphysicalInvVar, IIVrequest, HashMapResponse.class);
            }
            //remove Inventory Item Attribute
            for (int i = 0; i < inventoryItemArray.size(); i++) {
                Hashtable<String, String> removeIIAttrParams = new Hashtable<String, String>();
                removeIIAttrParams.put("inventoryItemId", (String) inventoryItemArray.get(i));
                removeIIAttrParams.put("attrName", "ScanQuantity");

                HttpHeaders IIheaders = new HttpHeaders();
                headers.add("Content-Type", "application/json");
                HttpEntity IIrequest = new HttpEntity(removeIIAttrParams, headers);

                RestTemplate IIrestTemplate = new RestTemplate();
                HashMapResponse IIresult = IIrestTemplate.postForObject(removeIIAttributeUri, IIrequest, HashMapResponse.class);
            }
        } else {
            //create InventoryItemAttribute
            for (int i = 0; i < inventoryItemArray.size(); i++) {
                Hashtable<String, String> IIAttrParams = new Hashtable<String, String>();
                IIAttrParams.put("inventoryItemId", (String) inventoryItemArray.get(i));
                IIAttrParams.put("attrName", "ScanQuantity");
                IIAttrParams.put("attrValue", (String) scanQtyArray.get(i));

                HttpHeaders IIheaders = new HttpHeaders();
                headers.add("Content-Type", "application/json");
                HttpEntity IIrequest = new HttpEntity(IIAttrParams, headers);

                RestTemplate IIrestTemplate = new RestTemplate();
                HashMapResponse IIresult = IIrestTemplate.postForObject(createIIAttrUri, IIrequest, HashMapResponse.class);
                list = IIresult.getResponse();
            }
        }

        //ObjectMapper mapper = new ObjectMapper();
        //String jsonString = "";
        try {
            if (list.isEmpty()) {
                jsonString = mapper.writeValueAsString("Success");
            } else {
                jsonString = mapper.writeValueAsString(list.get("InventoryItemList"));
            }
            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }

        //find inventory item code :::::::::::::::::::::closes:::::::::::::::::::::here
        //return new ModelAndView("stock/stock-check-history");
        //return stockCheckHistory();
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }

    @GetMapping("getStockCheckConfirm")
    public ModelAndView getStockCheckConfirm(@RequestParam("facilityId") String facilityId,
            @RequestParam("workEffortId") String workEffortId) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        //Get work effort list
        try {
            HashMap<String, Object> workEffortList = getWorkEffort(workEffortId);
            modelAndView.addObject("workEffortList", workEffortList.get("workEffort"));
            modelAndView.addObject("partyAssignList", workEffortList.get("partyAssigns"));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        //Get Department List
        try {
            List<Object> facilities = findFacility();
            modelAndView.addObject("failityList", facilities);
            //get facility name
            Object stockFacility = getFacilityName(facilityId);
            modelAndView.addObject("stockFacility", stockFacility);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        //find inventory item code:::::::::::::: starts:::::::::::::::::::here
        final String uri = env.getProperty("find.findInventoryItem");
        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("facilityId", facilityId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in find Stock check entry----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.get("InventoryItemList").toString());
        List<Map> finalInvItemlist = new LinkedList<Map>();
        List<Map> inventoryItemlist = (List<Map>) list.get("InventoryItemList");
        List<LinkedHashMap> findInventoryItemAttr = new LinkedList<LinkedHashMap>();

        //Get list of Faility Users
        List<Object> facilityUserList = findFacilityUser();
        modelAndView.addObject("failityUserList", facilityUserList);
        //get Inventory Item Attr
        String invenoryItemId = "";
        for (Map inventoryItemMap : inventoryItemlist) {
            invenoryItemId = (String) inventoryItemMap.get("inventoryItemId");
            findInventoryItemAttr = findInventoryItemAttr(invenoryItemId);
            if (findInventoryItemAttr.size() >= 1) {
                inventoryItemMap.put("attrValue", findInventoryItemAttr.get(0).get("attrValue"));
            } else {
                inventoryItemMap.put("attrValue", "");
            }
            finalInvItemlist.add(inventoryItemMap);
        }

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            modelAndView.addObject("inventoryItemList", finalInvItemlist);
            jsonString = mapper.writeValueAsString(list.get("InventoryItemList"));

            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }

        //find inventory item code :::::::::::::::::::::closes:::::::::::::::::::::here
        return new ModelAndView("stock/stock-check-confirm", modelAndView.getModel());
    }

    @GetMapping("stockCheckHistory")
    public ModelAndView stockCheckHistory() {

        ModelAndView modelAndView = new ModelAndView();
        String workEffortId = "", createdDate = "", approveDate = "";
        //Code to get Stock History    	
        try {
            final String uri = env.getProperty("performFind");
            Hashtable<String, Object> params = new Hashtable<String, Object>();
            
            Map<String, Object> inputMap = new HashMap<String, Object>();
            inputMap.put("workEffortPurposeTypeId", "WEPT_STOCKCHECK");

            params.put("entityName", "WorkEffort");
            params.put("noConditionFind", "Y");
            params.put("inputFields", inputMap);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HttpEntity request = new HttpEntity(params, headers);

            RestTemplate restTemplate = new RestTemplate();
            HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
            HashMap<String, Object> list = result.getResponse();
            List<LinkedHashMap<String, Object>> wrkEffortList = (List<LinkedHashMap<String, Object>>) list.get("list");
            List<LinkedHashMap<String, Object>> finalWrkEffortList = new ArrayList<LinkedHashMap<String, Object>>();
            for (LinkedHashMap<String, Object> wrkEffortEach : wrkEffortList) {
                workEffortId = (String) wrkEffortEach.get("workEffortId");
                HashMap<String, Object> wrkEffortMap = getWorkEffortStatus(workEffortId);
                createdDate = (String) wrkEffortMap.get("createdDate");
                approveDate = (String) wrkEffortMap.get("approvedDate");
                wrkEffortEach.put("createdDate", createdDate);
                wrkEffortEach.put("approveDate", approveDate);
                finalWrkEffortList.add(wrkEffortEach);
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = "";

            modelAndView.addObject("stockCheckTicketList", finalWrkEffortList);

            jsonString = mapper.writeValueAsString(finalWrkEffortList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ModelAndView("stock/stock-check-history", modelAndView.getModel());
    }

    @GetMapping("getStockCheckHistory")
    public ModelAndView getStockCheckHistory(@RequestParam("date_from") String date_from) {

        ModelAndView modelAndView = new ModelAndView();
        String workEffortId = "", createdDate = "", approveDate = "",date_to = "";
        Timestamp fromDate = null, thruDate = null;
        //Code to get Stock History    	
        try {
            final String uri = env.getProperty("performFind");
            Hashtable<String, Object> params = new Hashtable<String, Object>();
            date_to = (String) params.get("date_to");
            if(date_from != null) {
            	fromDate = convertStringToTimestamp(date_from);
            }
            if(date_to != null) {
            	thruDate = convertStringToTimestamp(date_to);
            }
            
            Map<String, Object> inputMap = new HashMap<String, Object>();
            inputMap.put("workEffortPurposeTypeId", "WEPT_STOCKCHECK");
            inputMap.put("filterByDate", "Y");
            inputMap.put("fromDateName", "createdDate");
            inputMap.put("thruDateName", "createdDate");

            params.put("entityName", "WorkEffort");
            params.put("noConditionFind", "Y");
            params.put("filterByDateValue", fromDate);
            params.put("inputFields", inputMap);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HttpEntity request = new HttpEntity(params, headers);

            RestTemplate restTemplate = new RestTemplate();
            HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
            HashMap<String, Object> list = result.getResponse();
            List<LinkedHashMap<String, Object>> wrkEffortList = (List<LinkedHashMap<String, Object>>) list.get("list");
            List<LinkedHashMap<String, Object>> finalWrkEffortList = new ArrayList<LinkedHashMap<String, Object>>();
            for (LinkedHashMap<String, Object> wrkEffortEach : wrkEffortList) {
                workEffortId = (String) wrkEffortEach.get("workEffortId");
                HashMap<String, Object> wrkEffortMap = getWorkEffortStatus(workEffortId);
                createdDate = (String) wrkEffortMap.get("createdDate");
                approveDate = (String) wrkEffortMap.get("approvedDate");
                wrkEffortEach.put("createdDate", createdDate);
                wrkEffortEach.put("approveDate", approveDate);
                finalWrkEffortList.add(wrkEffortEach);
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = "";

            modelAndView.addObject("stockCheckTicketList", finalWrkEffortList);

            jsonString = mapper.writeValueAsString(finalWrkEffortList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ModelAndView("stock/stock-check-history", modelAndView.getModel());
    }
    @GetMapping("stockCheckCreate")
    public ModelAndView stockCheckCreate() {
        ModelAndView modelAndView = new ModelAndView();

        try {
            //Get list of facilities
            List<Object> facilities = findFacility();
            modelAndView.addObject("failityList", facilities);
            //Get list of Faility Users
            List<Object> facilityUserList = findFacilityUser();
            modelAndView.addObject("failityUserList", facilityUserList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("stock/stock-check-create", modelAndView.getModel());
    }

    @GetMapping("stockCheckList")
    public ModelAndView stockCheckList() {
        return new ModelAndView("stock/stock-check-list");
    }

    @GetMapping("stockCheckVerify")
    public ModelAndView stockCheckVerify() {
        return new ModelAndView("stock/stock-check-verify");
    }

    /**
     * MethodName: saveStockCheck Method Description: save Stock Check.
     */
    @RequestMapping(value = "saveStockCheck", method = RequestMethod.GET)
    public ResponseEntity<?> saveStockCheck(@RequestParam("json") String json, @RequestParam("password") String password,
            @RequestParam("username") String username,
            @RequestParam("workEffortPartyId") String workEffortPartyId) {

        ModelAndView modelAndView = new ModelAndView();
        String successMessage = "Stock Check Created Successfully";
        //final String createStockCheckUri = env.getProperty("createStockCheck");
        final String saveStockCheckUri = env.getProperty("saveStockCheck");
        final String assignStockCheckUri = env.getProperty("assignStockUser");
        Hashtable<String, Object> prdPriceRuleParams = new Hashtable<String, Object>();
        Hashtable<String, Object> assignStockUserParams = new Hashtable<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        String workEffortPurposeTypeId = "", facilityId = "", partyId = "",workEffortId = "";
        String statusId = "", roleTypeId = "";
        try {
            // convert JSON string to Map

            prdPriceRuleParams = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            //partyId = (String) prdPriceRuleParams.get("workEffortPartyId");
            prdPriceRuleParams.put("login.username", username);
            prdPriceRuleParams.put("login.password", password);
            //prdPriceRuleParams.put("partyId", partyId);
            roleTypeId = (String) prdPriceRuleParams.get("roleTypeId");
            statusId = (String) prdPriceRuleParams.get("statusId");
            System.out.println(prdPriceRuleParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        workEffortPurposeTypeId = (String) prdPriceRuleParams.get("workEffortPurposeTypeId");
        try {
            if (workEffortPurposeTypeId != null) {
                //Check Any Stock check is pending or not.
                facilityId = (String) prdPriceRuleParams.get("facilityId");
                System.out.println("facilityId----" + facilityId);
                List<Object> inventoryItemAttList = findInventoryAttribute(facilityId);
                if (inventoryItemAttList.size() >= 1) {
                    return ResponseEntity.badRequest().body("Stock Check Already Initaited");
                }
                //Code to create price rule.
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", "application/json");
                HttpEntity request = new HttpEntity(prdPriceRuleParams, headers);

                RestTemplate restTemplate = new RestTemplate();
                HashMapResponse result = restTemplate.postForObject(saveStockCheckUri, request, HashMapResponse.class);
                HashMap<String, Object> list = result.getResponse();

                jsonString = mapper.writeValueAsString(list.get("workEffortId"));
                System.out.println("List" + jsonString);
                workEffortId = (String) list.get("workEffortId");
                
                if(!workEffortPartyId.isEmpty()) {
                	String [] workEffortPartyIdArray = workEffortPartyId.split(",");
                	for (String workEffortPartyIdEach : workEffortPartyIdArray) {
                		assignStockUserParams.put("workEffortId", workEffortId);
                		assignStockUserParams.put("partyId", workEffortPartyIdEach);
                		assignStockUserParams.put("roleTypeId", roleTypeId);
                		assignStockUserParams.put("statusId", statusId);
                		assignStockUserParams.put("facilityId", facilityId);
                		
                		HttpEntity assignStockUserRequest = new HttpEntity(prdPriceRuleParams, headers);
                		
                		RestTemplate assignUserRestTemplate = new RestTemplate();
                        HashMapResponse assignUserResult = assignUserRestTemplate.postForObject(assignStockCheckUri, assignStockUserRequest, HashMapResponse.class);
                        HashMap<String, Object> assignUserlist = assignUserResult.getResponse();
                        System.out.println("successMessage=====" + successMessage);
                	}
                }

            }

            
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
        //return new ModelAndView("stock/stock-check-history");
    }

    @SuppressWarnings("unchecked")
    private List<Object> getProducts() throws Exception {
        RestClient restClient = context.getBean(RestClient.class);
        restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
        restClient.addRequestParameter("product", new HashMap<>());
        List<Object> products = (List<Object>) restClient.callRetailService("findProducts", false).get("productsList");

        return products;
    }

    /**
     * MethodName: getFacilityName Method Description: get Facility by Id List
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getFacilityName(String facilityId) throws Exception {

        RestClient restClient = context.getBean(RestClient.class);
        restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());

        Map<String, String> inputMap = new HashMap<String, String>();
        inputMap.put("facilityId", facilityId);

        restClient.addRequestParameter("entityName", "Facility");
        restClient.addRequestParameter("inputFields", inputMap);
        restClient.addRequestParameter("noConditionFind", "Y");

        List<Map<String, Object>> facilityIdList = (List<Map<String, Object>>) restClient.callRetailService("performFindList", false).get("list");

        return facilityIdList.get(0);
    }

    /**
     * MethodName: findFacility Method Description: get Facility List
     */
    @SuppressWarnings("unchecked")
    public List<Object> findFacility() throws Exception {

        RestClient restClient = context.getBean(RestClient.class);
        restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());

        restClient.addRequestParameter("entityName", "Facility");
        restClient.addRequestParameter("inputFields", new HashMap<>());
        restClient.addRequestParameter("noConditionFind", "Y");

        List<Object> facilityList = (List<Object>) restClient.callRetailService("performFindList", false).get("list");

        return facilityList;
    }

    /**
     * MethodName: findFacilityParty Method Description: Ajax call to get
     * facility users.
     */
    @RequestMapping(value = "/findFacilityParty", method = RequestMethod.GET)
    public ResponseEntity<?> findFacilityParty(@RequestParam("facilityId") String facilityId) {
        final String uri = env.getProperty("performFind");
        Hashtable<String, Object> params = new Hashtable<String, Object>();

        Map<String, String> inputMap = new HashMap<String, String>();
        inputMap.put("facilityId", facilityId);

        params.put("entityName", "FacilityParty");
        params.put("noConditionFind", "Y");
        params.put("inputFields", inputMap);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in find facility----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.get("list").toString());

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(list.get("list"));
            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ResponseEntity<>(list.get("list"), HttpStatus.OK);
    }

    /**
     * MethodName: findFacilityUser Method Description: get Facility User List
     */
    @SuppressWarnings("unchecked")
    public List<Object> findFacilityUser() throws Exception {

        RestClient restClient = context.getBean(RestClient.class);
        restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());

        restClient.addRequestParameter("entityName", "FacilityPartyAndPerson");
        restClient.addRequestParameter("inputFields", new HashMap<>());
        restClient.addRequestParameter("noConditionFind", "Y");

        List<Object> facilityUserList = (List<Object>) restClient.callRetailService("performFindList", false).get("list");

        return facilityUserList;
    }

    /**
     * MethodName: getWorkEffort Method Description: get WorkEffort List
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> getWorkEffort(String workEffortId) throws Exception {

        final String uri = env.getProperty("getWorkEffort");
        Hashtable<String, Object> params = new Hashtable<String, Object>();

        params.put("workEffortId", workEffortId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        HashMap<String, Object> list = result.getResponse();

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";

        return list;
    }

    /**
     * MethodName: findInventoryItemAttr Method Description: get Facility List
     */
    @SuppressWarnings("unchecked")
    public List<LinkedHashMap> findInventoryItemAttr(String inventoryItemId) throws Exception {

        RestClient restClient = context.getBean(RestClient.class);
        restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());

        Hashtable<String, Object> params = new Hashtable<String, Object>();
        params.put("inventoryItemId", inventoryItemId);

        restClient.addRequestParameter("entityName", "InventoryItemAttribute");
        restClient.addRequestParameter("inputFields", params);
        restClient.addRequestParameter("noConditionFind", "Y");

        List<LinkedHashMap> inventoryItemAttrList = (List<LinkedHashMap>) restClient.callRetailService("performFindList", false).get("list");

        return inventoryItemAttrList;
    }

    /**
     * MethodName: findInventoryAttribute Method Description: get Inventory Item
     * Attribute list
     */
    @SuppressWarnings("unchecked")
    public List<Object> findInventoryAttribute(String facilityId) throws Exception {

        RestClient restClient = context.getBean(RestClient.class);
        restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());

        Map<String, String> inputMap = new HashMap<String, String>();
        inputMap.put("facilityId", facilityId);

        restClient.addRequestParameter("entityName", "InventoryItemAndAttribute");
        restClient.addRequestParameter("inputFields", inputMap);
        restClient.addRequestParameter("noConditionFind", "Y");

        List<Object> facilityList = (List<Object>) restClient.callRetailService("performFindList", false).get("list");

        return facilityList;
    }

    /**
     * MethodName: createInventoryItemVariance Method Description: To create
     * Inventory Item variance
     */
    @RequestMapping(value = "/inventoryItemVariance/create", method = RequestMethod.GET)
    public ResponseEntity<?> createInventoryItemVariance(@RequestParam("json") String json, 
    		@RequestParam("password") String password,
            @RequestParam("username") String username) {// @RequestBody Hashtable<String, String> params
        final String uri = env.getProperty("create.inventoryItemVariance");
        Hashtable<String, String> params = new Hashtable<String, String>();
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
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in create inventory ite mvariance----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        ModelAndView modelAndView = new ModelAndView();
        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.toString());
        try {
            jsonString = mapper.writeValueAsString(list.get("inventoryItemTxId"));
            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    /**
     * MethodName: createInventoryItemVariance Method Description: To create
     * Inventory Item variance
     */
    @RequestMapping(value = "/inventoryItemVariance/update", method = RequestMethod.GET)
    public ResponseEntity<?> updateInventoryItemVariance(@RequestParam("json") String json, 
    		@RequestParam("password") String password,
            @RequestParam("username") String username) {// @RequestBody Hashtable<String, String> params
        final String uri = env.getProperty("update.inventoryItemVariance");
        Hashtable<String, String> params = new Hashtable<String, String>();
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
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in create inventory ite mvariance----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        ModelAndView modelAndView = new ModelAndView();
        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.toString());
        try {
            jsonString = mapper.writeValueAsString(list.get("inventoryItemTxId"));
            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }
    
    /**
     * MethodName: createInventoryItemVariance Method Description: To Approve
     * Inventory Item variance
     */
    @RequestMapping(value = "/inventoryItemVariance/approve", method = RequestMethod.GET)
    public ResponseEntity<?> approveInventoryItemVariance(@RequestParam("json") String json, @RequestParam("password") String password,
            @RequestParam("username") String username) {// @RequestBody Hashtable<String, String> params
        final String uri = env.getProperty("approve.inventoryItemVariance");
        Hashtable<String, String> params = new Hashtable<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            // convert JSON string to Map
            params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
            });
            params.put("login.username", username);
            params.put("login.password", password);
            System.out.println(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(params, headers);
        System.out.println("in create inventory ite mvariance----");
        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);
        System.out.println("Result is -------" + result.toString());

        ModelAndView modelAndView = new ModelAndView();
        HashMap<String, Object> list = result.getResponse();
        System.out.println("result-----" + list.toString());
        try {
            jsonString = mapper.writeValueAsString(list.get("inventoryItemTxId"));
            System.out.println("List     " + jsonString);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }

    /**
     * MethodName: getWorkEffortStatus Method Description: get WorkEffort Status
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> getWorkEffortStatus(String workEffortId) throws Exception {

        Timestamp createdDateTime = null;
        Timestamp approveDateTime = null;
        String createdDate = null;
        String approveDate = null;

        HashMap<String, Object> wfaStatusList = new HashMap<String, Object>();
        final String wrkEffortCreateUri = env.getProperty("performFind");
        Hashtable<String, Object> wrkEffortCreateparams = new Hashtable<String, Object>();

        Map<String, String> wrkEffortCreateInputMap = new HashMap<String, String>();
        wrkEffortCreateInputMap.put("workEffortId", workEffortId);
        wrkEffortCreateInputMap.put("statusId", "CAL_TENTATIVE");

        wrkEffortCreateparams.put("entityName", "WorkEffortStatus");
        wrkEffortCreateparams.put("noConditionFind", "Y");
        wrkEffortCreateparams.put("inputFields", wrkEffortCreateInputMap);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(wrkEffortCreateparams, headers);

        RestTemplate restTemplate = new RestTemplate();
        HashMapResponse result = restTemplate.postForObject(wrkEffortCreateUri, request, HashMapResponse.class);
        HashMap<String, Object> list = result.getResponse();
        List<Map<String, Object>> wrkEffrtlist = (List<Map<String, Object>>) list.get("list");
        if (wrkEffrtlist != null) {
            createdDate = (String) wrkEffrtlist.get(0).get("statusDatetime");
            createdDate = createdDate.substring(0, 10);
        }

        wfaStatusList.put("createdDate", createdDate);
        //Get WorkEffort Completed date
        final String workEffortApprovedUri = env.getProperty("performFind");
        Hashtable<String, Object> params = new Hashtable<String, Object>();

        Map<String, String> inputMap = new HashMap<String, String>();
        inputMap.put("workEffortId", workEffortId);
        inputMap.put("statusId", "CAL_COMPLETED");

        params.put("entityName", "WorkEffortStatus");
        params.put("noConditionFind", "Y");
        params.put("inputFields", inputMap);

        HttpEntity wrkEffortApprovedRequest = new HttpEntity(params, headers);

        RestTemplate wfaRestTemplate = new RestTemplate();
        HashMapResponse wfaResult = wfaRestTemplate.postForObject(workEffortApprovedUri, wrkEffortApprovedRequest, HashMapResponse.class);
        HashMap<String, Object> wfaList = wfaResult.getResponse();

        List<Map<String, Object>> wrkEffrtAprrovelist = (List<Map<String, Object>>) wfaList.get("list");
        if (!(wrkEffrtAprrovelist.isEmpty())) {
            approveDate = (String) wrkEffrtAprrovelist.get(0).get("statusDatetime");
            approveDate = approveDate.substring(0, 10);
        }
        wfaStatusList.put("approvedDate", approveDate);

        return wfaStatusList;
    }
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/stockAdjustmentAutoPop", method = RequestMethod.GET)
	public ResponseEntity<?> stockAdjustmentAutoPopulate(@RequestParam("json") String json,
			@RequestParam("password") String password, @RequestParam("username") String username) {
		final String uri = env.getProperty("performFind");
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String,String> inputMap = new HashMap<String,String>();
		try {
			// convert JSON string to Map
			params = mapper.readValue(json, new TypeReference<Hashtable<String, String>>() {
			});
			inputMap.put("inventoryItemTxId", (String)params.get("inventoryItemId"));
			inputMap.put("status", (String)params.get("status"));
			params.put("login.username", username);
			params.put("login.password", password);
			params.put("entityName", "InventoryItemTrx");
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
}
