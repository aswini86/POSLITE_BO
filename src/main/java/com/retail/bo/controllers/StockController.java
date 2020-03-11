package com.retail.bo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.bo.forms.beans.ItemFormBean;
import com.retail.bo.model.Item;
import com.retail.bo.model.UnmapProduct;
import com.retail.bo.services.ExportXLS;
import com.retail.bo.services.RestClient;
import com.retail.bo.services.XLParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;

@Controller
public class StockController extends BaseController {

    @GetMapping("stock")
    public ModelAndView stock() {
        return new ModelAndView("stock/stock", getStockModel());
    }

    @GetMapping("uploadItems")
    public ModelAndView uploadItems() {
        Map<String, Object> model = new HashMap<>();
        model.put("itemFormBean", new ItemFormBean());
        return new ModelAndView("stock/upload-items", model);
    }

    @GetMapping("scanItems")
    public ModelAndView scanItems() {
        Map<String, Object> model = new HashMap<>();
        model.put("itemFormBean", new ItemFormBean());
        return new ModelAndView("stock/scan-items", model);
    }
    
    /**
     * Handle request to download an Excel document
     * @throws Exception 
     */
	/*
	 * @RequestMapping(value = "/exportArticle", method = RequestMethod.GET) public
	 * ModelAndView exportArticle(Map<String, Object> model, XSSFWorkbook workbook,
	 * HttpServletRequest request,HttpServletResponse response) throws Exception {
	 * 
	 * ExportXLS parser = (ExportXLS) applicationContext.getBean(ExportXLS.class);
	 * parser.buildExcelDocument(workbook, response); return new
	 * ModelAndView("stock/scan-items", model); }
	 */
   /* @RequestMapping(value = "/exportArticle", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> exportArticle(XSSFWorkbook workbook,HttpServletResponse response) throws Exception {
		
    	ByteArrayInputStream in = ExportXLS.buildExcelDocument(workbook,response);
    	
    	String fileName = "products.xlsx";
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(new MediaType("text", "xlsx"));
		respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		
    	return ResponseEntity.ok()
                .headers(respHeaders)
                .body(new InputStreamResource(in));

	}*/
    
    @RequestMapping(value = "/exportArticle", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> exportArticle() throws Exception {
		
    	Path inventoryTemplatePath = Paths.get("export_template/" + "ExportArticle.xlsx");
    	
    	//File file = new File(inventoryTemplatePath.toString());
    	File file = inventoryTemplatePath.toFile();
    	InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
    	
    	return ResponseEntity.ok()
    			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
    			.contentType(new MediaType("text", "xlsx"))
    			.contentLength(file.length())
    			.body(resource);
		//return new ResponseEntity<byte[]>(isr, respHeaders, HttpStatus.OK);
	}

    @PostMapping("mapUnmappedItem")
    public ModelAndView mapUnmappedItem(ItemFormBean itemFormBean) {
        try {
            String ean = itemFormBean.getEan();
            String itemId = itemFormBean.getItemId();

            RestClient restClient = applicationContext.getBean(RestClient.class);
            restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());

            restClient.addRequestParameter("facilityId", getFacilityId());
            List unmappedItems = (List) restClient.callRetailService("getUnmappedItems", true).get("responseBody");;

            Item item = new Item();
            for (int i = 0; i < unmappedItems.size(); i++) {
                Map<String, Object> unmappedItem = (Map) unmappedItems.get(i);
                if (unmappedItem.get("itemId").toString().equals(itemId)) {

                    item.setId(unmappedItem.get("itemId").toString());
                    item.setEan(ean);
                    if (unmappedItem.get("description") != null) {
                        item.setDescription(unmappedItem.get("description").toString());
                    }
                    if (unmappedItem.get("lotId") != null) {
                        item.setLotId(unmappedItem.get("lotId").toString());
                    }
                    if (unmappedItem.get("expiryDate") != null) {
                        item.setExpiryDate(unmappedItem.get("expiryDate").toString());
                    }
                    if (unmappedItem.get("mfd") != null) {
                        item.setManufacturingDate(unmappedItem.get("mfd").toString());
                    }
                    if (unmappedItem.get("batchNumber") != null) {
                        item.setBatchNumber(unmappedItem.get("batchNumber").toString());
                    }
                    item.setQuantity(Double.parseDouble(unmappedItem.get("quantity").toString()));
                    item.setMrp(Double.parseDouble(unmappedItem.get("mrp").toString()));
                    item.setSp(Double.parseDouble(unmappedItem.get("sp").toString()));
                    item.setCp(Double.parseDouble(unmappedItem.get("cp").toString()));
                }
            }

            List<Item> items = new ArrayList<>();
            items.add(item);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("items", items);

            requestBody.put("facilityId", getFacilityId());
            restClient.setRequestBody(requestBody);
            restClient.addRequestParameter("productStoreGroupId", getProductStoreGroupId());
            restClient.addRequestParameter("currencyUomId", getCurrencyUomId());
            restClient.callRetailService("addItemsToInventory", true);

            requestBody = new HashMap<>();
            requestBody.put("itemId", itemId);
            restClient.setRequestBody(requestBody);
            restClient.callRetailService("deleteUnmappedItem", true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("stock/stock", getStockModel());
    }

    @SuppressWarnings("unchecked")
    @PostMapping("uploadExcel")
    public ModelAndView uploadExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            return new ModelAndView("stock/upload-items");
        }

        try {

            byte[] bytes = file.getBytes();
            Path path = Paths.get("uploads/" + RequestContextHolder.currentRequestAttributes().getSessionId() + "-" + file.getOriginalFilename());
            Files.write(path, bytes);
            XLParser parser = (XLParser) applicationContext.getBean(XLParser.class);
            List<Item> items = parser.getItemsFromXlxToMap(path.toFile());
            path.toFile().delete();

            Map<String, Object> model = new HashMap<>();

            ItemFormBean itemFormBean = new ItemFormBean();
            itemFormBean.setItems(items);
            model.put("itemFormBean", itemFormBean);

            return new ModelAndView("stock/upload-items", model);

        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("stock/upload-items");
        }
    }

    @PostMapping("addScanItem")
    public String addScanItem(ItemFormBean itemFormBean) {
        try {
            String ean = itemFormBean.getEan();

            boolean itemAlreadyAdded = false;
            List<Item> items = itemFormBean.getItems();
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if (item.getEan().equals(ean)) {
                    itemAlreadyAdded = true;
                    item.setQuantity(item.getQuantity() + 1);
                }
            }

            if (!itemAlreadyAdded) {
                RestClient restClient = applicationContext.getBean(RestClient.class);
                restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
                restClient.addRequestParameter("ean", ean);
                restClient.addRequestParameter("productStoreGroupId", getProductStoreGroupId());
                restClient.addRequestParameter("currencyUomId", getCurrencyUomId());
                Map<String, Object> response = restClient.callRetailService("getProductByEAN", true);
                Map<String, Object> responseHeader = (Map) response.get("responseHeader");
                if (responseHeader.get("status").toString().equals("PRODUCT_FOUND")) {
                    Map<String, Object> product = (Map) response.get("responseBody");

                    String productName = "";

                    if (product.get("productName") != null) {
                        productName = product.get("productName").toString();
                    }

                    String description = "";

                    if (product.get("description") != null) {
                        description = product.get("description").toString();
                    }

                    Map<Object, Object> productPrice = (Map) product.get("productPrice");
                    double mrp = Double.parseDouble(productPrice.get("basePrice").toString());
                    double cp = Double.parseDouble(productPrice.get("price").toString());
                    double sp = Double.parseDouble(productPrice.get("defaultPrice").toString());

                    restClient.addRequestParameter("ean", ean);
                    restClient.addRequestParameter("facilityId", getFacilityId());
                    restClient.addRequestParameter("productStoreGroupId", getProductStoreGroupId());
                    restClient.addRequestParameter("currencyUomId", getCurrencyUomId());

                    response = restClient.callRetailService("getInventoryItemsByEAN", true);
                    responseHeader = (Map) response.get("responseHeader");
                    String lotId = "", batchNumber = "";
                    String expiryDate = "", manufacturingDate = "";
                    if (responseHeader.get("status").toString().equals("INVENTORY_ITEM_FOUND")) {
                        Map<Object, Object> inventoryItem = (Map) response.get("responseBody");
                        lotId = inventoryItem.get("lotId").toString();
                        expiryDate = inventoryItem.get("expireDate").toString();
                    }

                    Item item = new Item(ean, productName, description, lotId, batchNumber, 1, mrp, cp, sp, manufacturingDate, expiryDate);
                    itemFormBean.getItems().add(item);
                } else {
                    itemFormBean.getItems().add(new Item(ean));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "stock/scan-items";
    }

    @PostMapping("addItemsToInventory")
    public ModelAndView addItemsToInventory(ItemFormBean itemFormBean) {
        try {
            RestClient restClient = applicationContext.getBean(RestClient.class);
            restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("items", itemFormBean.getItems());
            requestBody.put("productStoreGroupId", getProductStoreGroupId());
            requestBody.put("currencyUomId", getCurrencyUomId());
            requestBody.put("facilityId", getFacilityId());
            restClient.setRequestBody(requestBody);
            
            restClient.callRetailService("addItemsToInventory", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("stock/stock", getStockModel());
    }

    private Map<String, Object> getStockModel() {
        Map<String, Object> model = new HashMap<>();
        ItemFormBean itemFormBean = new ItemFormBean();
        model.put("itemFormBean", itemFormBean);

        try {
            RestClient restClient = applicationContext.getBean(RestClient.class);
            restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());
            
            Map<String, Object> requestBody = new HashMap<>();
            
            Map<String, Object> criteria = new HashMap<>();
            criteria.put("facilityId", getFacilityId());
            
            requestBody.put("criteria", criteria);
            requestBody.put("productStoreGroupId", getProductStoreGroupId());
            requestBody.put("currencyUomId", getCurrencyUomId());
            restClient.setRequestBody(requestBody);

            Map<String, Object> response = restClient.callRetailService("getInventoryItems", true);
            Map<String, Object> responseHeader = (Map<String, Object>) response.get("responseHeader");

            if (responseHeader.get("status").toString().equals("INVENTORY_ITEMS_FOUND")) {
                List inventoryItems = (List) response.get("responseBody");
                for (int i = 0; i < inventoryItems.size(); i++) {
                    Map<String, Object> inventoryItem = (Map) inventoryItems.get(i);
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
            }

            restClient.addRequestParameter("searchBy", "productName");
            restClient.addRequestParameter("searchValue", "");
            response = restClient.callRetailService("getProducts", true);
            responseHeader = (Map<String, Object>) response.get("responseHeader");

            if (responseHeader.get("status").toString().equals("PRODUCTS_FOUND")) {
                List products = (List) response.get("responseBody");
                for (int i = 0; i < products.size(); i++) {
                    Map<String, Object> product = (Map) products.get(i);

                    Item item = new Item();
                    item.setId(product.get("productId").toString());
                    item.setEan(product.get("ean").toString());

                    if (product.get("productName") != null) {
                        item.setProductName(product.get("productName").toString());
                    }
                    if (product.get("description") != null) {
                        item.setDescription(product.get("description").toString());
                    }
                    itemFormBean.getProducts().add(item);
                }
            }

            restClient.addRequestParameter("facilityId", getFacilityId());
            response = restClient.callRetailService("getUnmappedItems", true);
            responseHeader = (Map<String, Object>) response.get("responseHeader");

            if (responseHeader.get("status").toString().equals("UNMAPPED_ITEMS_FOUND")) {
                List unmappedItems = (List) response.get("responseBody");
                for (int i = 0; i < unmappedItems.size(); i++) {
                    Map<String, Object> unmappedItem = (Map) unmappedItems.get(i);

                    Item item = new Item();
                    item.setId(unmappedItem.get("itemId").toString());
                    item.setEan(unmappedItem.get("ean").toString());
                    if (unmappedItem.get("description") != null) {
                        item.setDescription(unmappedItem.get("description").toString());
                    }
                    if (unmappedItem.get("productName") != null) {
                        item.setProductName(unmappedItem.get("productName").toString());
                    }
                    if (unmappedItem.get("lotId") != null) {
                        item.setLotId(unmappedItem.get("lotId").toString());
                    }
                    item.setQuantity(Double.parseDouble(unmappedItem.get("quantity").toString()));
                    item.setMrp(Double.parseDouble(unmappedItem.get("mrp").toString()));
                    item.setSp(Double.parseDouble(unmappedItem.get("sp").toString()));
                    item.setCp(Double.parseDouble(unmappedItem.get("cp").toString()));
                    itemFormBean.getUnmappedItems().add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return model;
    }
}
