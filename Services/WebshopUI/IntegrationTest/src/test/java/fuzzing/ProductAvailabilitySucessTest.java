package fuzzing;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProductAvailabilitySucessTest {

    public HttpResponse getHttpHelper(String url) throws Exception{
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("content-type", "application/json");
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    public JSONObject parseResponse(HttpResponse response) throws Exception {
        String respString = EntityUtils.toString(response.getEntity());
        JSONObject respJson = new JSONObject(respString);
        return respJson;
    }

    @Test
    public void productSuccessTest() throws Exception{
        String checkProdUrl = "http://localhost:8050/products";
        String avaiableUrl = "http://localhost:8070/products/";


//        Check if products exists
        HttpResponse response = getHttpHelper(checkProdUrl);
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        String respString = EntityUtils.toString(response.getEntity());
        JSONArray jsonarray = new JSONArray(respString);

        assertEquals(Integer.valueOf(200), responseCode);
        assertTrue(jsonarray.length() > 0);

        if (jsonarray.length()>0){
//            Check product availability
            String prodNumber = "1";
            String amount = "2";
            avaiableUrl =  avaiableUrl + prodNumber + "/availability?amount=" + amount;
            response = getHttpHelper(avaiableUrl);
            responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
            JSONObject respJson = parseResponse(response);

            assertEquals(Integer.valueOf(200), responseCode);
            assertEquals(true, respJson.get("available"));
            assertEquals(Integer.valueOf(prodNumber), respJson.get("productId"));
            assertEquals(Integer.valueOf(amount), respJson.get("requestedAmount"));

        }


    }
}
