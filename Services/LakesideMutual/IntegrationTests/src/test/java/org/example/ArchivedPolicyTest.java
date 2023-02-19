package org.example;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArchivedPolicyTest {

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

    public JSONObject parseResponse(HttpResponse response) throws Exception {
        String respString = EntityUtils.toString(response.getEntity());
        JSONObject respJson = new JSONObject(respString);
        return respJson;
    }

    @Test
    public void archivedInsuranceTest() throws Exception{
        String email = "admin@example.com";
        String password = "1password";
        String authUrl = "http://localhost:8080/auth";
        String custIdUrl = "http://localhost:8080/user";
        String insuranceUrl = "http://localhost:8080/customers/";
        String archiveUrl = "http://localhost:8080/insurance-quote-requests/";

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

//      Fetch historic policies
        insuranceUrl = insuranceUrl + customerId + "/insurance-quote-requests";
        response = getHttpHelper(insuranceUrl, responseToken);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));

        String respString = EntityUtils.toString(response.getEntity());
        JSONArray respArray = new JSONArray(respString);
        assertEquals(Integer.valueOf(200), responseCode);

        if (respArray.length()!=0){
            JSONObject insurance = respArray.getJSONObject(0);
            Integer policyId = (Integer) insurance.get("id");

//            get the policy details
            archiveUrl = archiveUrl + policyId;
            response = getHttpHelper(archiveUrl, responseToken);
            responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
            respJson = parseResponse(response);

            assertEquals(Integer.valueOf(200), responseCode);
            assertEquals(policyId, respJson.get("id"));

        }
        else{
            System.out.println("No archived insurance. Test success");
        }

    }
}
