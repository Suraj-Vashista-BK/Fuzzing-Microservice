package org.example;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpResponse;

import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InsuranceQuoteRequestTest {

    public HttpResponse postHttpHelper(JSONObject data, String url, String token) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        StringEntity params = new StringEntity(data.toString());
        request.addHeader("content-type", "application/json");
        request.addHeader("X-Auth-Token", token);
        request.setEntity(params);
        HttpResponse response = httpClient.execute(request);
        return response;
    }


    public HttpResponse getHttpHelper(String url, String token) throws Exception{
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("content-type", "application/json");
        if (!token.equals("")){
            request.addHeader("X-Auth-Token", token);
        }
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    public JSONObject parseResponse(HttpResponse response) throws Exception {
        String respString = EntityUtils.toString(response.getEntity());
        JSONObject respJson = new JSONObject(respString);
        return respJson;
    }

    @Test
    public void quoteTest() throws Exception{

        String email = "admin@example.com";
        String password = "1password";
        String insuranceType = "Car Insurance";
        String status = "REQUEST_SUBMITTED";
        String authUrl = "http://localhost:8080/auth";
        String custIdUrl = "http://localhost:8080/user";
        String insUrl = "http://localhost:8080/insurance-quote-requests";


//      check authentication
        JSONObject credentials = new JSONObject();
        credentials.put("email", email);
        credentials.put("password",password);

        HttpResponse response = postHttpHelper(credentials, authUrl, "");

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


//      Request policy quote
        JSONObject billing = new JSONObject();
        billing.put("city", "Rapperswil");
        billing.put("postalCode", "8640");
        billing.put("streetAddress", "Oberseestrasse 10");

        JSONObject contact = new JSONObject();
        contact.put("city", "Rapperswil");
        contact.put("postalCode", "8640");
        contact.put("streetAddress", "Oberseestrasse 10");

        JSONObject deductible = new JSONObject();
        deductible.put("amount", 500);
        deductible.put("currency", "CHF");

        JSONObject custInfo = new JSONObject();
        custInfo.put("billingAddress", billing);
        custInfo.put("contactAddress", contact);
        custInfo.put("customerId", customerId);
        custInfo.put("firstname", "Max");
        custInfo.put("lastname", "Mustermann");

        JSONObject insuranceInfo = new JSONObject();
        insuranceInfo.put("deductible", deductible);
        insuranceInfo.put("insuranceType", "Car Insurance");
        insuranceInfo.put("startDate", "2023-02-17");

        JSONObject policyData = new JSONObject();
        policyData.put("customerInfo", custInfo);
        policyData.put("insuranceOptions", insuranceInfo);

        response = postHttpHelper(policyData, insUrl, responseToken);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        respJson = parseResponse(response);
        JSONArray jsonarray = (JSONArray) respJson.get("statusHistory");
        JSONObject statusObj = new JSONObject();
        statusObj = jsonarray.getJSONObject(0);
        JSONObject insurance = new JSONObject();
        insurance = respJson.getJSONObject("insuranceOptions");

        assertEquals(Integer.valueOf(200), responseCode);
        assertEquals(status, statusObj.get("status"));
        assertEquals(insuranceType, insurance.get("insuranceType"));

    }
}
