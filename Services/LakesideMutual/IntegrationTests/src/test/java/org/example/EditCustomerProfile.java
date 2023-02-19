package org.example;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EditCustomerProfile {

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
    public void profileEdit() throws Exception{

        String customerFirstName = "Max";
        String customerLastName = "Mustermann";
        String allUrl = "http://localhost:8100/customers?filter=&offset=0";
        String filterUrl = "http://localhost:8100/customers?filter=";
        String CustIdUrl = "http://localhost:8100/customers/";

//        Check if customers exists
        HttpResponse response = getHttpHelper(allUrl, "");
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        JSONObject respJson = parseResponse(response);

        assertEquals(Integer.valueOf(200), responseCode);
        assertTrue((Integer) respJson.get("size") > 0);

        if((Integer) respJson.get("size")>0){
//            search for customer
            filterUrl = filterUrl + customerFirstName + "%20" + customerLastName + "&offset=0";
            response = getHttpHelper(filterUrl, "");
            responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
            respJson = parseResponse(response);
            JSONArray custDetailsArray = respJson.getJSONArray("customers");
            JSONObject custDetails = custDetailsArray.getJSONObject(0);

            String customerID = (String) custDetails.get("customerId");
            assertEquals(Integer.valueOf(200), responseCode);
            assertTrue((Integer) respJson.get("size") > 0);
            assertEquals(customerFirstName, custDetails.get("firstname"));
            assertEquals(customerLastName, custDetails.get("lastname"));

            if ((Integer)respJson.get("size") > 0){
                CustIdUrl = CustIdUrl + customerID;
                response = getHttpHelper(CustIdUrl, "");
                responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
                respJson = parseResponse(response);

                assertEquals(Integer.valueOf(200), responseCode);
                assertEquals(customerID, respJson.get("customerId"));

//                Edit some value
                JSONObject newData = new JSONObject();
                newData.put("birthday", "2000-01-01");
                newData.put("city", "Urbana");
                newData.put("email", "admin@example.com");
                newData.put("firstname", "Max");
                newData.put("lastname", "Mustermann");
                newData.put("phoneNumber", "055 222 4111");
                newData.put("postalCode", "61801");
                newData.put("streetAddress", "Oberseestrasse 10");


                response = putHttpHelper(CustIdUrl, newData);
                responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
                respJson = parseResponse(response);
                assertEquals(Integer.valueOf(200), responseCode);

//                Verify the change
                response = getHttpHelper(CustIdUrl, "");
                responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
                respJson = parseResponse(response);

                assertEquals(Integer.valueOf(200), responseCode);
                assertEquals("Urbana", respJson.get("city"));
                assertEquals("61801", respJson.get("postalCode"));

            }
        }

    }
}
