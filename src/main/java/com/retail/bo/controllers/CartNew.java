package com.retail.bo.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.retail.bo.util.HashMapResponse;

@Controller
public class CartNew extends BaseController{
	
	@Autowired
    private ApplicationContext context;
	
	@Autowired
	private Environment env;
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
    private HttpSession httpSession;
	
	@RequestMapping(value = "newPayment", method = RequestMethod.POST)
    public ModelAndView newPayment(@RequestParam("preceiptId") String receiptId,
    		@RequestParam("pdayId") String dayId,
    		@RequestParam("isHold") String isHold,
    		@RequestParam("pcontactNumber") String pcontactNumber,
    		@RequestParam("pcustomerName") String pcustomerName,
    		RedirectAttributes redirectAttributes) {
    	
    	httpSession.setAttribute("receiptId", receiptId);
    	httpSession.setAttribute("dayId", dayId);
    	httpSession.setAttribute("isHold", isHold);
    	httpSession.setAttribute("contactNumber", pcontactNumber);
    	httpSession.setAttribute("pcustomerName", pcustomerName);
    	
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
            	modelAndView.addObject("returnAmount", actualObj.get("returnAmount"));
                //modelAndView.addObject("cartItems", actualObj.get("cartItems"));
                modelAndView.addObject("cartItems", cartItemList);
                modelAndView.addObject("itemTax", cartObj.get("itemTax"));
                //NodeBean toValue = mapper.readValue(parser, NodeBean.class);
            }
		} catch (Exception e) {
			
		}
        return new ModelAndView("redirect:/cartBilling");
    }
	
	@RequestMapping(value = "cartBilling", method = RequestMethod.GET)
    public ModelAndView newCartPayment() {
    	
    	String receiptId = (String) httpSession.getAttribute("receiptId");
    	String dayId = (String) httpSession.getAttribute("dayId");
    	String posTerminalId = (String) httpSession.getAttribute("posTerminalId");
		String username = (String) httpSession.getAttribute("username");
		String isHold = (String) httpSession.getAttribute("isHold");
		String contactNumber = (String) httpSession.getAttribute("contactNumber");
		
		final String dayPosCartItemsUri = env.getProperty("getPosCartItems");
		final String holdDayPosCartItemsUri = env.getProperty("getHoldPosCartItems");
		final String cartViewPaymentsUri = env.getProperty("cartViewPayments");
		final String getCustomerCreditLimitAmtUri = env.getProperty("getCustomerCreditLimitAmt");
		final String customerTotCreditDueUri = env.getProperty("getCustomerTotalCreditDue");
		final String activeCustomerCreditNoteUri = env.getProperty("getActiveCreditNoteList");
		final String getCustomerPrevBillUri = env.getProperty("getCustomerPrevBill");
		final String getTaxInvoiceUri = env.getProperty("getTaxInvoice");
		final String getAddlCartDetailsUri = env.getProperty("getAdditionalCartTxn");
		
		String customerMobileNo = "", firstName = "";
		BigDecimal grandTotal = BigDecimal.ZERO;
		BigDecimal spGrandTotal = BigDecimal.ZERO;
		float discAmount = (float) 0.0;
		String billAmount = "";
    	ModelAndView modelAndView = new ModelAndView();
    	Hashtable<String, String> params = new Hashtable<String, String>();
    	try {
    		byPassSSLCertificate();
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Content-Type", "application/json");
    		HttpEntity request = new HttpEntity(params, headers);
    		ObjectMapper mapper = new ObjectMapper();
    		//code for checking tax enabled or not
    		Hashtable<String, Object> taxEnableParams = new Hashtable<String, Object>();
    		taxEnableParams.put("receiptId", receiptId);
    		
    		HttpEntity taxEnableRequest = new HttpEntity(taxEnableParams, headers);
			HashMapResponse taxEnableResult = restTemplate.postForObject(getTaxInvoiceUri,
					taxEnableRequest, HashMapResponse.class);
			HashMap<String, Object> taxEnableList = taxEnableResult.getResponse();
			modelAndView.addObject("taxEnable", taxEnableList.get("isChecked"));
    		
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
            modelAndView.addObject("addlTotalDue", viewPaymentObj.get("addlTotalDue"));
    		//End of code for getting Payments
            System.out.println("came into get cart Items");
            UriComponentsBuilder builder = null;
            if(isHold != null) {
            	builder = UriComponentsBuilder.fromHttpUrl(holdDayPosCartItemsUri).
                		queryParam("receiptId", receiptId).
                		queryParam("posTerminalId", posTerminalId).
                		queryParam("dayId", dayId).
                		queryParam("USERNAME", username);
            } else {
            	builder = UriComponentsBuilder.fromHttpUrl(dayPosCartItemsUri).
                		queryParam("receiptId", receiptId).
                		queryParam("posTerminalId", posTerminalId).
                		queryParam("dayId", dayId).
                		queryParam("USERNAME", username);
            }
            
            
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
            		
            		cartItemList.add(cartItemsMap.get("cartItemsMap"));
            	}
				if(actualObj.get("customerMobileNo") != null) {
					customerMobileNo = actualObj.get("customerMobileNo").toString().substring(1, actualObj.get("customerMobileNo").toString().length()-1);
				}
				if(actualObj.get("firstName") != null) {
					firstName = actualObj.get("firstName").toString().substring(1, actualObj.get("firstName").toString().length()-1);
				}
				billAmount = actualObj.get("subTotal").toString();
            	modelAndView.addObject("totalDue", actualObj.get("totalDue"));
            	modelAndView.addObject("subTotal", actualObj.get("subTotal"));
            	modelAndView.addObject("totalSalesTax", actualObj.get("totalSalesTax"));
            	modelAndView.addObject("cartSize", actualObj.get("cartSize"));
            	modelAndView.addObject("displayGrandTotal", actualObj.get("displayGrandTotal"));
            	modelAndView.addObject("balanceAmount", actualObj.get("balanceAmount"));
            	modelAndView.addObject("returnAmount", actualObj.get("returnAmount"));
            	modelAndView.addObject("holdCustName", firstName);
            	modelAndView.addObject("holdCustMobileNo", customerMobileNo);
            	
            	modelAndView.addObject("TOT_CGST_TAX", TOT_CGST_TAX);
            	modelAndView.addObject("TOT_SGST_TAX", TOT_SGST_TAX);
            	modelAndView.addObject("TOT_IGST_TAX", TOT_IGST_TAX);
            	modelAndView.addObject("TOT_AMT", TOT_AMT);
            	
            	if(actualObj.get("displayMRPGrandTotal") != null) {
                    grandTotal = (BigDecimal) actualObj.get("displayMRPGrandTotal").decimalValue();
                }
                if(actualObj.get("displaySPGrandTotal") != null) {
                	spGrandTotal = (BigDecimal) actualObj.get("displaySPGrandTotal").decimalValue();
                }
                    discAmount = (grandTotal.floatValue() - spGrandTotal.floatValue());
                if(discAmount >= 0)
                	modelAndView.addObject("discAmount", discAmount);
                else
                	modelAndView.addObject("discAmount", 0.0f);
                    
                //modelAndView.addObject("cartItems", actualObj.get("cartItems"));
                modelAndView.addObject("cartItems", cartItemList);
                modelAndView.addObject("itemTax", cartObj.get("itemTax"));
                //NodeBean toValue = mapper.readValue(parser, NodeBean.class);
                //code for getting totCreditDueAmt & totCreditAmt
                Hashtable<String, Object> customerTotCreditAmtParams = new Hashtable<String, Object>();
        		customerTotCreditAmtParams.put("contactNumber", contactNumber);
        		
        		Hashtable<String, Object> customerTotCreditDueParams = new Hashtable<String, Object>();
        		customerTotCreditDueParams.put("contactNumber", contactNumber);
        		
        		Hashtable<String, Object> customerTotCreditNoteParams = new Hashtable<String, Object>();
        		customerTotCreditNoteParams.put("contactNumber", contactNumber);
        		
        		Hashtable<String, Object> customerActiveCreditNoteParams = new Hashtable<String, Object>();
        		customerActiveCreditNoteParams.put("contactNumber", contactNumber);
        		
        		Hashtable<String, Object> customerPrevBillParams = new Hashtable<String, Object>();
        		customerPrevBillParams.put("contactNumber", contactNumber);
        		
        		try {
        			headers.add("Content-Type", "application/json");
        			
        			HttpEntity customerCreditLimitRequest = new HttpEntity(customerTotCreditAmtParams, headers);
        			HashMapResponse customerCreditLimitAmtResult = restTemplate.postForObject(getCustomerCreditLimitAmtUri,
        					customerCreditLimitRequest, HashMapResponse.class);
        			HashMap<String, Object> list = customerCreditLimitAmtResult.getResponse();
        			modelAndView.addObject("creditLimitAmt", list.get("creditLimitAmt"));
        			
        			HttpEntity customerTotCreditDueRequest = new HttpEntity(customerTotCreditDueParams, headers);
        			HashMapResponse customerTotCreditDueResult = restTemplate.postForObject(customerTotCreditDueUri,
        					customerTotCreditDueRequest, HashMapResponse.class);
        			HashMap<String, Object> creditDuelist = customerTotCreditDueResult.getResponse();
        			modelAndView.addObject("totCreditDueAmt", creditDuelist.get("totCreditDueAmt"));
        			
        			HttpEntity customerActiveCreditNoteRequest = new HttpEntity(customerActiveCreditNoteParams, headers);
        			HashMapResponse customerActiveCreditNoteResult = restTemplate.postForObject(activeCustomerCreditNoteUri,
        					customerActiveCreditNoteRequest, HashMapResponse.class);
        			HashMap<String, Object> creditActiveNotelist = customerActiveCreditNoteResult.getResponse();
        			modelAndView.addObject("creditNoteList", creditActiveNotelist.get("creditNoteList"));
        			
        			HttpEntity customerPrevBillRequest = new HttpEntity(customerPrevBillParams, headers);
        			HashMapResponse customerPrevBillResult = restTemplate.postForObject(getCustomerPrevBillUri,
        					customerPrevBillRequest, HashMapResponse.class);
        			HashMap<String, Object> creditPrevBilllist = customerPrevBillResult.getResponse();
        			modelAndView.addObject("billReturnMap", creditPrevBilllist.get("billReturnMap"));
        			
        		} catch (Exception e) {
        			// TODO: handle exception
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
                //End of Code for getting totCreditDueAmt & totCreditAmt
            }
		} catch (Exception e) {
			
		}
        return new ModelAndView("cart/payment_new", modelAndView.getModel());
    }
	/**
     * Method Name: getCreditNoteAmtByCreditId 
     * Method Description: Method to get CreditNote Amt by creditid
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "getCreditNoteAmtCreditId", method = RequestMethod.GET)
	public ResponseEntity<?> getCreditNoteAmtCreditId(@RequestParam("creditId") String creditId,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
    	
    	final String uri = env.getProperty("getCreditNoteAmtByCreditId");
    	ObjectMapper mapper = new ObjectMapper();
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		Double creditNoteAmount  = 0.0;
		HashMap<String, Object> list = new HashMap<String, Object>();
		params.put("creditId", creditId);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity request = new HttpEntity(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		HashMapResponse result = restTemplate.postForObject(uri, request, HashMapResponse.class);

		String jsonString = "";
		try {
			list = result.getResponse();
			creditNoteAmount = (Double) list.get("creditNoteAmount");
            if(creditNoteAmount.equals(null) || creditNoteAmount.equals("") || creditNoteAmount.equals(" ")) {
            	return new ResponseEntity<>(jsonString, HttpStatus.BAD_REQUEST);
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(creditNoteAmount, HttpStatus.OK);
	}
}
