package org.example;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class DeleteRequestAdmin {

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

    public HttpResponse deleteHttpHelper(String url) throws Exception{
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpDelete request = new HttpDelete(url);
        request.addHeader("content-type", "application/json");
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    public JSONObject parseResponse(HttpResponse response) throws Exception {
        String respString = EntityUtils.toString(response.getEntity());
        JSONObject respJson = new JSONObject(respString);
        return respJson;
    }

    public JSONObject parseResponseInsurance(HttpResponse response) throws Exception {
        String respString = EntityUtils.toString(response.getEntity());
        JSONArray respArray = new JSONArray(respString);
        JSONObject respJson = respArray.getJSONObject(0);
        return respJson;
    }

    @Test
    public void deleteRequestTest() throws Exception{
        String customerFirstName = "Max";
        String customerLastName = "Mustermann";
        String getAllUrl = "http://localhost:8090/customers?filter=";
        String custUrl = "http://localhost:8090/customers/";
        String deleteUrl = "http://localhost:8090/policies/";


        HttpResponse response = getHttpHelper(getAllUrl, "");
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        JSONObject respJson = parseResponse(response);

        assertEquals(Integer.valueOf(200), responseCode);
        assertTrue((Integer) respJson.get("size") > 0);

        String name = customerFirstName +"%20"+ customerLastName;

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


        custUrl = custUrl + customerID;
        response = getHttpHelper(custUrl, "");
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        respJson = parseResponse(response);

        assertEquals(Integer.valueOf(200), responseCode);
        assertEquals(customerID, respJson.get("customerId"));

        String policyUrl = custUrl + "/policies";
        response = getHttpHelper(policyUrl, "");
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        String respString = EntityUtils.toString(response.getEntity());
        JSONArray respArray = new JSONArray(respString);
        Integer policyCount = respArray.length();

        if (policyCount>0) {
            respJson = respArray.getJSONObject(0);

            String policyId = (String) respJson.get("policyId");

            assertEquals(Integer.valueOf(200), responseCode);
            assertEquals(customerID, respJson.get("customer"));

            deleteUrl = deleteUrl + policyId;
            response = deleteHttpHelper(deleteUrl);
            assertEquals(Integer.valueOf(200), responseCode);
        }

//        verify
        response = getHttpHelper(policyUrl, "");
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        respString = EntityUtils.toString(response.getEntity());
        respArray = new JSONArray(respString);
        Integer policyCount2 = respArray.length();

        if (policyCount>0){
            assertEquals(Integer.valueOf(200), responseCode);
            assertEquals(Integer.valueOf(policyCount - 1), policyCount2);
        }

    }
}
