package com.mynative.sdkdemo;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MobiBoxApi {

    private static final String ENDPOINT = "https://swpll.com/UsersAquisition/";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String PREF_NAME = "nativeflow_prefs";
    private static final String KEY_DEVICE_ID = "device_id";

    private final OkHttpClient client = new OkHttpClient();

    // Dynamic values extracted from API responses
    private String countryDialCode = "";

    public String getCountryDialCode() { return countryDialCode; }

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public void callApi(int action, String sessionId, String msisdn, String pinCode,
                        String userAgent, String packageName, String deviceId,
                        String gpsAdid, String deeplink,
                        ApiCallback callback) {
        try {
            JSONObject body = buildRequestBody(action, sessionId, msisdn, pinCode,
                    userAgent, packageName, deviceId, gpsAdid, deeplink);
            android.util.Log.e("MobiBoxApi", "Request: " + body.toString());

            RequestBody requestBody = RequestBody.create(body.toString(), JSON);

            Request request = new Request.Builder()
                    .url(ENDPOINT)
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
                                        String pinCode, String userAgent, String packageName,
                                        String deviceId, String gpsAdid, String deeplink) throws Exception {
        JSONObject root = new JSONObject();

        // NewSDK flag
        root.put("NewSDK", true);

        // DeviceInfo
        JSONObject deviceInfo = new JSONObject();
        deviceInfo.put("PackageName", packageName != null ? packageName : "");
        deviceInfo.put("UserAgent", userAgent);
        deviceInfo.put("DeviceID", deviceId != null ? deviceId : "");
        deviceInfo.put("LangCode", java.util.Locale.getDefault().getLanguage());
        deviceInfo.put("gpsAdid", gpsAdid != null ? gpsAdid : "");
        deviceInfo.put("idfa", "");
        deviceInfo.put("idfv", "");
        root.put("DeviceInfo", deviceInfo);

        // Referrer
        JSONObject nativeRef = new JSONObject();
        nativeRef.put("IDService", -1);
        nativeRef.put("Deeplink", deeplink != null ? deeplink : "");
        // for Testing only
        nativeRef.put("Country",  "LB");

        JSONObject referrer = new JSONObject();
        referrer.put("Native", nativeRef);
        root.put("Referrer", referrer);

        // Request
        JSONObject request = new JSONObject();
        request.put("Action", String.valueOf(action));
        request.put("TransactionID", "TXN_" + UUID.randomUUID().toString().replace("-", ""));
        if (action != 1) request.put("SessionID", sessionId != null ? sessionId : "");
        request.put("MSISDN", msisdn != null ? msisdn : "");
        request.put("PinCode", pinCode != null ? pinCode : "");
        request.put("Data", "");
        root.put("Request", request);

        return root;
    }

    public static String getOrCreateDeviceId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String id = prefs.getString(KEY_DEVICE_ID, "");
        if (id.isEmpty()) {
            String guid = UUID.randomUUID().toString() + new Date().getTime();
            id = md5(guid);
            prefs.edit().putString(KEY_DEVICE_ID, id).apply();
        }
        return id;
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }
}
