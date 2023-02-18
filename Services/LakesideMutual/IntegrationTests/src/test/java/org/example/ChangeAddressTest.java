package org.example;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ChangeAddressTest {

    public HttpResponse postHttpHelper(JSONObject data, String url) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        StringEntity params = new StringEntity(data.toString());
        request.addHeader("content-type", "application/json");
        request.setEntity(params);
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    public HttpResponse getHttpHelper(String url, String token) throws Exception{
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("content-type", "application/json");
        request.addHeader("X-Auth-Token", token);
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    public HttpResponse putHttpHelper(String url, String token, JSONObject data) throws Exception{
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut(url);
        StringEntity params = new StringEntity(data.toString());
        request.addHeader("content-type", "application/json");
        request.addHeader("X-Auth-Token", token);
        request.setEntity(params);
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    public JSONObject parseResponse(HttpResponse response) throws Exception {
        String respString = EntityUtils.toString(response.getEntity());
        JSONObject respJson = new JSONObject(respString);
        return respJson;
    }

    public JSONObject parseArrayResponse(HttpResponse response) throws Exception{
        String respString = EntityUtils.toString(response.getEntity());
        JSONArray jsonarray = new JSONArray(respString);
        JSONObject json = new JSONObject();
        json = jsonarray.getJSONObject(0);
        return json;
    }

    @Test
    public void AddressTest() throws Exception {

//      Set variables and URL
        String email = "admin@example.com";
        String password = "1password";
        String customerFirstName = "Max";
        String customerLastName = "Mustermann";
        String customerPhone = "055 222 4111";
        String customerPostalCode = "8640";
        String customerStreet = "Oberseestrasse 10";
        String customerInsuranceType = "Health Insurance";
        Double customerInsuranceAmount = 250.0;
        String customerInsuranceCurrency = "CHF";
        String authUrl = "http://localhost:8080/auth";
        String custIdUrl = "http://localhost:8080/user";
        String custDetailsUrl = "http://localhost:8080/customers/";
        String policyUrl = "http://localhost:8090/customers/";
        String custAddUrl = "http://localhost:8080/customers/";


//      check authentication
        JSONObject credentials = new JSONObject();
        credentials.put("email", email);
        credentials.put("password",password);

        HttpResponse response = postHttpHelper(credentials, authUrl);

        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        JSONObject respJson = parseResponse(response);
        String responseEmail = (String) respJson.get("email");
        String responseToken = (String) respJson.get("token");

        assertEquals(email, responseEmail);
        assertEquals(Integer.valueOf(200), responseCode);
        assertNotNull(responseToken);


//      test customer id
        response = getHttpHelper(custIdUrl, responseToken);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        respJson = parseResponse(response);
        String customerId = (String) respJson.get("customerId");
        responseEmail = (String) respJson.get("email");

        assertEquals(email, responseEmail);
        assertEquals(Integer.valueOf(200), responseCode);
        assertNotNull(customerId);


//      test customer details
        custDetailsUrl =  custDetailsUrl + customerId;
        response = getHttpHelper(custDetailsUrl, responseToken);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        respJson = parseResponse(response);

        assertEquals(customerFirstName, respJson.get("firstname"));
        assertEquals(customerLastName, respJson.get("lastname"));
        assertEquals(customerId, respJson.get("customerId"));
        assertEquals(email, respJson.get("email"));
        assertEquals(customerPhone, respJson.get("phoneNumber"));
        assertEquals(customerStreet, respJson.get("streetAddress"));
        assertEquals(customerPostalCode, respJson.get("postalCode"));
        assertEquals(Integer.valueOf(200), responseCode);

//      test existing policy
        policyUrl =  policyUrl+customerId+ "/policies";
        response = getHttpHelper(policyUrl, responseToken);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        respJson = parseArrayResponse(response);
        JSONObject policyJson = (JSONObject) respJson.get("insurancePremium");

        assertEquals(Integer.valueOf(200), responseCode);
        assertEquals(customerId, respJson.get("customer"));
        assertEquals(customerInsuranceType, respJson.get("policyType"));
        assertEquals(customerInsuranceAmount, policyJson.get("amount"));
        assertEquals(customerInsuranceCurrency, policyJson.get("currency"));

//      Change address
        JSONObject newAddress = new JSONObject();
        newAddress.put("city", "Urbana");
        newAddress.put("postalCode","61801");
        newAddress.put("streetAddress", "Stoughton");

        custAddUrl = custAddUrl + customerId + "/address";
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        response = putHttpHelper(custAddUrl, responseToken, newAddress);
        respJson = parseResponse(response);

        assertEquals(Integer.valueOf(200), responseCode);
        assertEquals("Stoughton", respJson.get("streetAddress"));
        assertEquals("61801", respJson.get("postalCode"));
        assertEquals("Urbana", respJson.get("city"));


//      reset address
        newAddress.put("city", "Rapperswil");
        newAddress.put("postalCode","8640");
        newAddress.put("streetAddress", "Oberseestrasse 10");

        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        response = putHttpHelper(custAddUrl, responseToken, newAddress);
        respJson = parseResponse(response);

        assertEquals(Integer.valueOf(200), responseCode);
        assertEquals("Oberseestrasse 10", respJson.get("streetAddress"));
        assertEquals("8640", respJson.get("postalCode"));
        assertEquals("Rapperswil", respJson.get("city"));

    }
}
