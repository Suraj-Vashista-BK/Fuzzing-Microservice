package org.example;

import org.json.JSONObject;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RejectInsuranceAdminTest {

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

    @Test
    public void rejectTest() throws Exception{
        JSONObject insuranceInfo = new JSONObject();
        insuranceInfo.put("amount", 2);
        insuranceInfo.put("currency", "CHF");

        JSONObject policyData = new JSONObject();
        policyData.put("amount", 3);
        policyData.put("currency", "CHF");

        JSONObject quoteData = new JSONObject();
        quoteData.put("expirationDate", "2023-03-19T04:59:00.000Z");
        quoteData.put("insurancePremium", insuranceInfo);
        quoteData.put("policyLimit", policyData);
        quoteData.put("status", "QUOTE_RECEIVED");

        System.out.println(quoteData);

        String quoteUrl = "http://localhost:8090/insurance-quote-requests/63";
        HttpResponse response = patchHttpHelper(quoteData, quoteUrl);

    }
}
