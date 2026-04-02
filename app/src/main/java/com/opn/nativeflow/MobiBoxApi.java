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
    private static final String ENCRYPTION_KEY = "DVpqooCzLNOMUhFxAdRKF6iY6pWz0plq";
    private static final String CAMPAIGN = "4af82580-d7a2-4a24-8805-88bd1a3e2715";
    private static final String COUNTRY = "LB";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final AESHelper aes = new AESHelper(ENCRYPTION_KEY);

    // Dynamic values extracted from API responses
    private String countryDialCode = "";
    private String firstPageButtonID = "";
    private String secondPageButtonID = "";

    public String getCountryDialCode() { return countryDialCode; }
    public String getFirstPageButtonID() { return firstPageButtonID; }
    public String getSecondPageButtonID() { return secondPageButtonID; }

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public interface IpCallback {
        void onResult(String ip);
    }

    public void resolvePublicIp(IpCallback callback) {
        Request request = new Request.Builder().url("https://api.ipify.org").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onResult("");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String ip = response.body() != null ? response.body().string().trim() : "";
                callback.onResult(ip);
            }
        });
    }

    public void callApi(int action, String sessionId, String msisdn, String pinCode,
                        String userAgent, String userIp,
                        String firstBtnId, String secondBtnId,
                        ApiCallback callback) {
        try {
            JSONObject body = buildRequestBody(action, sessionId, msisdn, pinCode,
                    userAgent, userIp, firstBtnId, secondBtnId);
            android.util.Log.e("MobiBoxApi", "Request: " + body.toString());

            // Send plain JSON (encryption not required by this endpoint)
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
                    android.util.Log.e("MobiBoxApi", "Network failure: " + e.getMessage(), e);
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        android.util.Log.e("MobiBoxApi", "HTTP " + response.code() + " Raw: " + responseBody);

                        JSONObject json = new JSONObject(responseBody);

                        // Extract dynamic country dial code from response if present
                        String dial = json.optString("CountryDialCode", "");
                        if (!dial.isEmpty()) countryDialCode = dial;

                        // Extract button IDs from NextAction if present
                        JSONObject na = json.optJSONObject("NextAction");
                        if (na != null) {
                            String fbid = na.optString("firstPageButtonID", "");
                            String sbid = na.optString("secondPageButtonID", "");
                            if (!fbid.isEmpty()) firstPageButtonID = fbid;
                            if (!sbid.isEmpty()) secondPageButtonID = sbid;
                        }

                        callback.onSuccess(json);
                    } catch (Exception e) {
                        android.util.Log.e("MobiBoxApi", "Parse error: " + e.getMessage(), e);
                        callback.onError(e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private JSONObject buildRequestBody(int action, String sessionId, String msisdn,
                                        String pinCode, String userAgent, String userIp,
                                        String firstBtnId, String secondBtnId) throws Exception {
        JSONObject root = new JSONObject();

        // DeviceInfo
        JSONObject deviceInfo = new JSONObject();
        deviceInfo.put("LangCode", java.util.Locale.getDefault().getLanguage());
        deviceInfo.put("UserAgent", userAgent);
        deviceInfo.put("UserIP", userIp);
        deviceInfo.put("gclid", "");
        deviceInfo.put("wbraid", "");
        deviceInfo.put("gbraid", "");
        root.put("DeviceInfo", deviceInfo);

        // Referrer
        JSONObject affiliate = new JSONObject();
        affiliate.put("Campaign", CAMPAIGN);
        affiliate.put("ClickID", "");
        affiliate.put("Pub_ID", "");
        affiliate.put("Aff_ID", "");
        affiliate.put("extra", "");
        affiliate.put("extra1", "");
        affiliate.put("Country", COUNTRY);
        affiliate.put("firstPageButtonID", firstBtnId);
        affiliate.put("secondPageButtonID", secondBtnId);

        JSONObject referrer = new JSONObject();
        referrer.put("Affiliate", affiliate);
        root.put("Referrer", referrer);

        // Request
        JSONObject request = new JSONObject();
        request.put("Action", action);
        request.put("TransactionID", UUID.randomUUID().toString());
        request.put("SessionID", sessionId != null ? sessionId : "");
        request.put("MSISDN", msisdn != null ? msisdn : "");
        request.put("PinCode", pinCode != null ? pinCode : "");
        root.put("Request", request);

        return root;
    }
}
