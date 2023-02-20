package org.example;


import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AcceptRequestAdminTest {

    HttpResponse patchHttpHelper(JSONObject data, String url) throws Exception{
        HttpClient httpClient = HttpClient.newBuilder().build();

        HttpRequest.BodyPublisher param = HttpRequest.BodyPublishers.ofString(data.toString());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("PATCH", param)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        return response;
    }

    HttpResponse postHttpHelper(JSONObject data, String url, String token) throws Exception{
        HttpClient httpClient = HttpClient.newBuilder().build();

        HttpRequest.BodyPublisher param = HttpRequest.BodyPublishers.ofString(data.toString());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("POST", param)
                .header("Content-Type", "application/json")
                .header("X-Auth-Token", token)
                .build();

        HttpResponse response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
        return response;
    }


    public HttpResponse getHttpHelper(String url, String token) throws Exception{
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest request;
        if (!token.equals("")) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("X-Auth-Token", token)
                    .build();
        }
        else {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
        }
        HttpResponse response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public JSONObject parseResponse(HttpResponse response) throws Exception {
        JSONObject respJson = new JSONObject(response.body().toString());
        return respJson;
    }

    public JSONObject parseResponseInsurance(HttpResponse response) throws Exception {
        JSONArray respArray = new JSONArray(response.body().toString());
        JSONObject respJson = new JSONObject();
        respJson = respArray.getJSONObject(0);
        return respJson;
    }

    @Test
    public void AcceptTest() throws Exception{
        String email = "admin@example.com";
        String password = "1password";
        String insuranceType = "Car Insurance";
        String status = "REQUEST_SUBMITTED";
        String status2 = "QUOTE_RECEIVED";
        String authUrl = "http://localhost:8080/auth";
        String custIdUrl = "http://localhost:8080/user";
        String insUrl = "http://localhost:8080/insurance-quote-requests";
        String quoteUrl = "http://localhost:8090/insurance-quote-requests";


//      check authentication
        JSONObject credentials = new JSONObject();
        credentials.put("email", email);
        credentials.put("password",password);

        HttpResponse response = postHttpHelper(credentials, authUrl, "");
        Integer responseCode = Integer.valueOf(String.valueOf(response.statusCode()));
        JSONObject respJson = parseResponse(response);
        String responseEmail = (String) respJson.get("email");
        String responseToken = (String) respJson.get("token");

        assertEquals(email, responseEmail);
        assertEquals(Integer.valueOf(200), responseCode);
        assertNotNull(responseToken);


//      test customer id
        response = getHttpHelper(custIdUrl, responseToken);
        responseCode = Integer.valueOf(String.valueOf(response.statusCode()));
        respJson = parseResponse(response);
        String customerId = (String) respJson.get("customerId");
        responseEmail = (String) respJson.get("email");

        assertEquals(email, responseEmail);
        assertEquals(Integer.valueOf(200), responseCode);
        assertNotNull(customerId);


//      Request policy quote from customer
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
        responseCode = Integer.valueOf(String.valueOf(response.statusCode()));
        respJson = parseResponse(response);
        JSONArray jsonarray = (JSONArray) respJson.get("statusHistory");
        JSONObject statusObj = new JSONObject();
        statusObj = jsonarray.getJSONObject(0);
        JSONObject insurance = new JSONObject();
        insurance = respJson.getJSONObject("insuranceOptions");

        assertEquals(Integer.valueOf(200), responseCode);
        assertEquals(status, statusObj.get("status"));
        assertEquals(insuranceType, insurance.get("insuranceType"));

        Integer requestId = (Integer) respJson.get("id");

        Thread.sleep(5000);


//      Use request ID to see if admin received request
        response = getHttpHelper(quoteUrl, "");
        responseCode = Integer.valueOf(String.valueOf(response.statusCode()));
        respJson = parseResponseInsurance(response);
        insurance = respJson.getJSONObject("insuranceOptions");

        assertEquals(Integer.valueOf(200), responseCode);
        assertEquals(requestId, respJson.get("id"));
        assertEquals(insuranceType, insurance.get("insuranceType"));


//      Admin accept quote
        insuranceInfo = new JSONObject();
        insuranceInfo.put("amount", 2);
        insuranceInfo.put("currency", "CHF");

        policyData = new JSONObject();
        policyData.put("amount", 3);
        policyData.put("currency", "CHF");

        JSONObject quoteData = new JSONObject();
        quoteData.put("expirationDate", "2023-03-19T04:59:00.000Z");
        quoteData.put("insurancePremium", insuranceInfo);
        quoteData.put("policyLimit", policyData);
        quoteData.put("status", "QUOTE_RECEIVED");

        String patchUrl = quoteUrl + "/" + requestId;
        response = patchHttpHelper(quoteData, patchUrl);
        responseCode = Integer.valueOf(String.valueOf(response.statusCode()));
        assertEquals(Integer.valueOf(200), responseCode);


//        Verify status
        respJson = parseResponse(response);
        jsonarray = (JSONArray) respJson.get("statusHistory");
        statusObj = new JSONObject();
        statusObj = jsonarray.getJSONObject(1);
        insurance = new JSONObject();
        insurance = respJson.getJSONObject("insuranceOptions");

        assertEquals(Integer.valueOf(200), responseCode);
        assertEquals(status2, statusObj.get("status"));
        assertEquals(insuranceType, insurance.get("insuranceType"));
        System.out.println(statusObj.get("status"));
        System.out.println(insurance.get("insuranceType"));
    }
}
