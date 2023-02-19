package org.example;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;
public class EditInsuranceAdmin {

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

    public HttpResponse putHttpHelper(String url,  JSONObject data) throws Exception{
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut(url);
        StringEntity params = new StringEntity(data.toString());
        request.addHeader("content-type", "application/json");
        request.setEntity(params);
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    public JSONObject parseResponse(HttpResponse response) throws Exception {
        String respString = EntityUtils.toString(response.getEntity());
        JSONObject respJson = new JSONObject(respString);
        return respJson;
    }

    @Test
    public void editInsuranceTest() throws Exception{
        String customerFirstName = "Max";
        String customerLastName = "Mustermann";
        String getAllUrl = "http://localhost:8090/customers?filter=";
        String custUrl = "http://localhost:8090/customers/";
        String policyDetailUrl = "http://localhost:8090/policies/";

//      Check if there are insurances in database
        HttpResponse response = getHttpHelper(getAllUrl, "");
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        JSONObject respJson = parseResponse(response);

        assertEquals(Integer.valueOf(200), responseCode);
        assertTrue((Integer) respJson.get("size") > 0);

        if ((Integer) respJson.get("size")>0){
//          Search customer and get customer Id
            String name = customerFirstName + "%20" + customerLastName;

            getAllUrl = getAllUrl + name;
            response = getHttpHelper(getAllUrl, "");
            responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
            respJson = parseResponse(response);
            JSONArray custDetailsArray = respJson.getJSONArray("customers");
            JSONObject custDetails = custDetailsArray.getJSONObject(0);

            String customerID = (String) custDetails.get("customerId");
            assertEquals(Integer.valueOf(200), responseCode);
            assertTrue((Integer) respJson.get("size") > 0);
            assertEquals(customerFirstName, custDetails.get("firstname"));
            assertEquals(customerLastName, custDetails.get("lastname"));


//            Get customer details to verify its the same customer
            custUrl = custUrl + customerID;
            response = getHttpHelper(custUrl, "");
            responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
            respJson = parseResponse(response);

            assertEquals(Integer.valueOf(200), responseCode);
            assertEquals(customerID, respJson.get("customerId"));

//            get policy details
            String policyUrl = custUrl + "/policies";
            response = getHttpHelper(policyUrl, "");
            responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
            assertEquals(Integer.valueOf(200), responseCode);
            String respString = EntityUtils.toString(response.getEntity());
            JSONArray respArray = new JSONArray(respString);
            Integer policyCount = respArray.length();


            if (policyCount > 0){
                respJson = respArray.getJSONObject(0);

//                Edit the policy
                String policyId = (String) respJson.get("policyId");
                String policyType = (String) respJson.get("policyType");
                policyDetailUrl = policyDetailUrl + policyId;
                response = getHttpHelper(policyDetailUrl, "");
                respJson = parseResponse(response);

//              edit data
                JSONObject deductible = new JSONObject();
                deductible.put("amount", "500");
                deductible.put("currency", "CHF");

                JSONObject insurancePremium = new JSONObject();
                insurancePremium.put("amount", 2);
                insurancePremium.put("currency", "CHF");

                JSONObject agreementItems = new JSONObject();
                ArrayList arr = new ArrayList();
                agreementItems.put("agreementItems", arr);

                JSONObject policyLimit = new JSONObject();
                policyLimit.put("amount", 3);
                policyLimit.put("currency", "CHF");

                JSONObject policyPeriod = new JSONObject();
                policyPeriod.put("endDate", "2024-02-17T06:00:00.000Z");
                policyPeriod.put("startDate", "2023-02-17T06:00:00.000Z");

                JSONObject finalData = new JSONObject();
                finalData.put("customerId", customerID);
                finalData.put("deductible", deductible);
                finalData.put("insurancePremium", insurancePremium);
                finalData.put("insuringAgreement", agreementItems);
                finalData.put("policyLimit", policyLimit);
                finalData.put("policyPeriod", policyPeriod);
                finalData.put("policyType", policyType);

                response = putHttpHelper(policyDetailUrl, finalData);
                responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
                assertEquals(Integer.valueOf(200), responseCode);

//                verify

                response = getHttpHelper(policyDetailUrl, "");
                respJson = parseResponse(response);
                deductible = respJson.getJSONObject("deductible");
                assertEquals(500.0, deductible.get("amount"));
            }
        }


    }


}
