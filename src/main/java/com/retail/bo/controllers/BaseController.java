package com.retail.bo.controllers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;

import com.retail.bo.services.RestClient;

@Controller
public abstract class BaseController {

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected Environment environment;
    
    private Map<String, Object> getParty() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        Map<String, Object> party = (Map) requestAttributes.getAttribute("party", RequestAttributes.SCOPE_SESSION);        
        return party;
    }

    protected String getPartyId() {
        Map<String, Object> party = getParty();
        String partyId = party.get("partyId").toString();
        return partyId;
    }

    protected String getFacilityId() {  
        Map<String, Object> party = getParty();
        List<Map<String, Object>> productStores = (List) party.get("productStores");
        Map<String, Object> productStore = productStores.get(0);
        String facilityId = productStore.get("inventoryFacilityId").toString();
        return facilityId;
    }
    
    protected String getProductStoreGroupId() {  
        Map<String, Object> party = getParty();
        String primaryStoreGroupId = "";
        List<Map<String, Object>> productStores = (List) party.get("productStores");
        Map<String, Object> productStore = productStores.get(0);
        if(productStore.get("primaryStoreGroupId") != null) {
        	primaryStoreGroupId = productStore.get("primaryStoreGroupId").toString();
        }
        return primaryStoreGroupId;
    }
    
    protected List<Map<String, Object>> getProductStores() {  
        Map<String, Object> party = getParty();
        List<Map<String, Object>> productStores = (List) party.get("productStores");
        return productStores;
    }
    
    protected String getCurrencyUomId() {  
        Map<String, Object> party = getParty();
        List<Map<String, Object>> productStores = (List) party.get("productStores");
        Map<String, Object> productStore = productStores.get(0);
        String currencyUomId = productStore.get("defaultCurrencyUomId").toString();
        return currencyUomId;
    }
    
    protected String getStoreId() {  
        Map<String, Object> party = getParty();
        List<Map<String, Object>> productStores = (List) party.get("productStores");
        Map<String, Object> productStore = productStores.get(0);
        String productStoreId = productStore.get("productStoreId").toString();
        return productStoreId;
    }
    
    protected String getStoreName() {  
        Map<String, Object> party = getParty();
        List<Map<String, Object>> productStores = (List) party.get("productStores");
        Map<String, Object> productStore = productStores.get(0);
        String storeName = productStore.get("companyName").toString();
        return storeName;
    }
    
    /**
     * MethodName: findStores
     * Method Description: get Stores list
     */
    @SuppressWarnings("unchecked")
    public List<Object> findStores() throws Exception {

        RestClient restClient = applicationContext.getBean(RestClient.class);
        restClient.addAuthentication(SecurityContextHolder.getContext().getAuthentication());

        restClient.addRequestParameter("entityName", "ProductStore");
        restClient.addRequestParameter("inputFields", new HashMap<>());
        restClient.addRequestParameter("noConditionFind", "Y");

        List<Object> facilityList = (List<Object>) restClient.callRetailService("performFindList", false).get("list");

        return facilityList;
    }
    
    protected void byPassSSLCertificate() throws Exception {
        TrustManager[] trustManagers = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        HostnameVerifier hostnameVerifier = (String hostname, SSLSession session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
    }
    
}
