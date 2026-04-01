package com.opn.nativeflow;

import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MobiBoxApi {

    private static final String ENDPOINT = "https://download-hd.net/UsersAquisition/";
    private static final String ACCESS_TOKEN = "8a0458dc-39f8-453e-b159-435983bdf892";
    private static final String CAMPAIGN = "4af82580-d7a2-4a24-8805-88bd1a3e2715";
    private static final String COUNTRY = "LB";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public void callApi(int action, String sessionId, String msisdn, String pinCode,
                        String userAgent, String userIp, ApiCallback callback) {
        try {
            JSONObject body = buildRequestBody(action, sessionId, msisdn, pinCode, userAgent, userIp);
            RequestBody requestBody = RequestBody.create(body.toString(), JSON);

            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .addHeader("AccessToken", ACCESS_TOKEN)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        JSONObject json = new JSONObject(responseBody);
                        callback.onSuccess(json);
                    } catch (Exception e) {
                        callback.onError(e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private JSONObject buildRequestBody(int action, String sessionId, String msisdn,
                                        String pinCode, String userAgent, String userIp) throws Exception {
        JSONObject root = new JSONObject();

        // DeviceInfo
        JSONObject deviceInfo = new JSONObject();
        deviceInfo.put("gbraid", "");
        deviceInfo.put("gclid", "");
        deviceInfo.put("LangCode", "en");
        deviceInfo.put("UserAgent", userAgent);
        deviceInfo.put("UserIP", userIp);
        deviceInfo.put("wbraid", "");
        root.put("DeviceInfo", deviceInfo);

        // Referrer
        JSONObject affiliate = new JSONObject();
        affiliate.put("Aff_ID", "");
        affiliate.put("Campaign", CAMPAIGN);
        affiliate.put("ClickID", "");
        affiliate.put("Country", COUNTRY);
        affiliate.put("extra", "");
        affiliate.put("extra1", "");
        affiliate.put("firstPageButtonID", "msisdn-btn");
        affiliate.put("Pub_ID", "");
        affiliate.put("secondPageButtonID", "pin-btn");

        JSONObject referrer = new JSONObject();
        referrer.put("Affiliate", affiliate);
        root.put("Referrer", referrer);

        // Request
        JSONObject request = new JSONObject();
        request.put("Action", action);
        request.put("MSISDN", msisdn != null ? msisdn : "");
        request.put("PinCode", pinCode != null ? pinCode : "");
        request.put("SessionID", sessionId != null ? sessionId : "");
        request.put("TransactionID", UUID.randomUUID().toString());
        root.put("Request", request);

        return root;
    }
}
