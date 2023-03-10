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
public class CustomerCreditRatingFailureTest {

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
    public void creditFailureTest() throws Exception{

        String custUrl = "http://localhost:8000/customers";
        String creditRatingUrl = "http://localhost:8000/customers/";

//        check if customers exists
        HttpResponse response = getHttpHelper(custUrl);
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        String respString = EntityUtils.toString(response.getEntity());
        JSONArray jsonarray = new JSONArray(respString);

        assertEquals(Integer.valueOf(200), responseCode);
        assertTrue(jsonarray.length() > 0);

        if (jsonarray.length()>0){
//            check credit success customer
            String customerId = "1";
            creditRatingUrl = creditRatingUrl + customerId + "/credit-rating-check";
            response = getHttpHelper(creditRatingUrl);
            responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
            JSONObject respJson = parseResponse(response);

            System.out.println(respJson);

            assertEquals(Integer.valueOf(200), responseCode);
            assertEquals(false, respJson.get("acceptable"));
            assertEquals(Integer.valueOf(customerId), respJson.get("customerId"));

        }
        else{
            System.out.println("Not enough success customers in database. Test success");
        }

    }
}
