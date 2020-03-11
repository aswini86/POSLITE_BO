package com.retail.bo.util;

import java.util.HashMap;

public class HashMapResponse {

    HashMap<String, Object> response;

    public HashMap<String, Object> getResponse() {
        return response;
    }

    public void setResponse(HashMap<String, Object> response) {
        this.response = response;
    }

    public HashMapResponse() {
        response = new HashMap<String, Object>();
    }
}
