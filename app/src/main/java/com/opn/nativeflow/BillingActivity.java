package com.opn.nativeflow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.palette.graphics.Palette;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONObject;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BillingActivity extends AppCompatActivity {

    private static final String TAG = "BillingActivity";

    private static final Set<String> BEFORE_KEYS = new HashSet<>(Arrays.asList(
            "headerInfo", "prelanderInfo", "prelanderTxt", "OTPTopHeaderInfo"
    ));
    private static final Set<String> MIDDLE_KEYS = new HashSet<>(Arrays.asList(
            "middleInfo"
    ));

    private MobiBoxApi api;
    private String sessionId = "";
    private String currentMsisdn = "";
    private String resolvedIp = "";
    private String countryDialCode = "";
    private int brandColor = 0;

    private NestedScrollView scrollView;
    private FrameLayout layoutLoading;
    private ImageView ivLogo;
    private MaterialCardView cardInput;
    private LinearLayout layoutMsisdnRow;
    private LinearLayout layoutDisclaimersBefore, layoutDisclaimersMiddle, layoutDisclaimersAfter;
    private LinearLayout layoutPinBoxes;
    private TilFields til;
    private TextInputEditText etMsisdn, etPin, etCountryCode;
    private TextView tvError;
    private MaterialButton btnAction;
    private LinearLayout layoutLinks;
    private TextView tvPrivacy, tvTerms, tvLinkDivider;
    private FrameLayout afScriptContainer;
    private HeaderPatternView headerPattern;
    private View logoGlowRing, logoGlowRingOuter, logoGroup;

    private static class TilFields {
        TextInputLayout msisdn, pin, countryCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        api = new MobiBoxApi();
        initViews();
        // Resolve public IP first, then initiate
        api.resolvePublicIp(ip -> {
            resolvedIp = ip;
            runOnUiThread(this::callInitiate);
        });
    }

    private void initViews() {
        scrollView = findViewById(R.id.scrollView);
        layoutLoading = findViewById(R.id.layoutLoading);
        ivLogo = findViewById(R.id.ivLogo);
        cardInput = findViewById(R.id.cardInput);
        layoutMsisdnRow = findViewById(R.id.layoutMsisdnRow);
        layoutDisclaimersBefore = findViewById(R.id.layoutDisclaimersBefore);
        layoutDisclaimersMiddle = findViewById(R.id.layoutDisclaimersMiddle);
        layoutDisclaimersAfter = findViewById(R.id.layoutDisclaimersAfter);
        layoutPinBoxes = findViewById(R.id.layoutPinBoxes);
        til = new TilFields();
        til.msisdn = findViewById(R.id.tilMsisdn);
        til.pin = findViewById(R.id.tilPin);
        til.countryCode = findViewById(R.id.tilCountryCode);
        etMsisdn = findViewById(R.id.etMsisdn);
        etPin = findViewById(R.id.etPin);
        etCountryCode = findViewById(R.id.etCountryCode);
        tvError = findViewById(R.id.tvError);
        btnAction = findViewById(R.id.btnAction);
        layoutLinks = findViewById(R.id.layoutLinks);
        tvPrivacy = findViewById(R.id.tvPrivacy);
        tvTerms = findViewById(R.id.tvTerms);
        tvLinkDivider = findViewById(R.id.tvLinkDivider);
        afScriptContainer = findViewById(R.id.afScriptContainer);
        headerPattern = findViewById(R.id.headerPattern);
        headerPattern.randomizeStyle();
        logoGlowRing = findViewById(R.id.logoGlowRing);
        logoGlowRingOuter = findViewById(R.id.logoGlowRingOuter);
        logoGroup = findViewById(R.id.logoGroup);
        startHeaderAnimations();
    }

    private void startHeaderAnimations() {
        // Glow ring pulses
        pulseView(logoGlowRing, 1f, 1.18f, 1f, 0.3f, 1400, 0);
        pulseView(logoGlowRingOuter, 1f, 1.12f, 0.7f, 0.15f, 2000, 400);

        // Entrance: logo group scales up + fades in with spring feel
        logoGroup.setScaleX(0.3f);
        logoGroup.setScaleY(0.3f);
        logoGroup.setAlpha(0f);
        logoGroup.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(800)
                .setStartDelay(150)
                .setInterpolator(new OvershootInterpolator(1.4f))
                .start();

        // Entrance: card slides up with bounce
        cardInput.setTranslationY(120f);
        cardInput.setAlpha(0f);
        cardInput.animate()
                .translationY(0f).alpha(1f)
                .setDuration(600)
                .setStartDelay(500)
                .setInterpolator(new DecelerateInterpolator(2f))
                .start();

        // Button subtle scale entrance
        btnAction.setScaleX(0.8f);
        btnAction.setScaleY(0.8f);
        btnAction.animate()
                .scaleX(1f).scaleY(1f)
                .setDuration(400)
                .setStartDelay(700)
                .setInterpolator(new OvershootInterpolator(2f))
                .start();
    }

    private void pulseView(View v, float fromScale, float toScale, float fromAlpha, float toAlpha, long duration, long delay) {
        ObjectAnimator sx = ObjectAnimator.ofFloat(v, "scaleX", fromScale, toScale);
        ObjectAnimator sy = ObjectAnimator.ofFloat(v, "scaleY", fromScale, toScale);
        ObjectAnimator a = ObjectAnimator.ofFloat(v, "alpha", fromAlpha, toAlpha);
        AnimatorSet out = new AnimatorSet();
        out.playTogether(sx, sy, a);
        out.setDuration(duration);
        out.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator sx2 = ObjectAnimator.ofFloat(v, "scaleX", toScale, fromScale);
        ObjectAnimator sy2 = ObjectAnimator.ofFloat(v, "scaleY", toScale, fromScale);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(v, "alpha", toAlpha, fromAlpha);
        AnimatorSet in = new AnimatorSet();
        in.playTogether(sx2, sy2, a2);
        in.setDuration(duration);
        in.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet loop = new AnimatorSet();
        loop.playSequentially(out, in);
        loop.setStartDelay(delay);
        loop.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(android.animation.Animator a) { loop.start(); }
        });
        loop.start();
    }

    // ---- API ----

    private String ua() { return WebSettings.getDefaultUserAgent(this); }

    private String firstBtnId() {
        String id = api.getFirstPageButtonID();
        return !id.isEmpty() ? id : "btnAction";
    }

    private String secondBtnId() {
        String id = api.getSecondPageButtonID();
        return !id.isEmpty() ? id : "btnAction";
    }

    private void callInitiate() {
        showLoading(true);
        api.callApi(1, "", "", "", ua(), resolvedIp, "btnAction", "btnAction",
                new MobiBoxApi.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject r) {
                        Log.e(TAG, "Response: " + r.toString());
                        runOnUiThread(() -> handleResponse(r));
                    }
                    @Override
                    public void onError(String e) {
                        runOnUiThread(() -> { showLoading(false); showError("Connection error."); });
                    }
                });
    }

    private void callAction(int action, String msisdn, String pin) {
        showLoading(true);
        hideError();
        if (msisdn != null && !msisdn.isEmpty()) currentMsisdn = msisdn;
        api.callApi(action, sessionId, msisdn != null ? msisdn : "", pin != null ? pin : "",
                ua(), resolvedIp, firstBtnId(), secondBtnId(),
                new MobiBoxApi.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject r) {
                        Log.e(TAG, "Response action " + action + ": " + r.toString());
                        runOnUiThread(() -> handleResponse(r));
                    }
                    @Override
                    public void onError(String e) {
                        runOnUiThread(() -> { showLoading(false); showError("Connection error."); });
                    }
                });
    }

    // ---- Response ----

    private String additionalQueryStringParams = "";

    private void handleResponse(JSONObject response) {
        showLoading(false);
        try { loadLogo(response); } catch (Exception e) { Log.e(TAG, "Logo error", e); }

        try {
            int error = response.optInt("Error", 0);
            String msg = response.optString("MessageToShow", "");
            String description = response.optString("Description", "");
            String sid = response.optString("SessionID", "");
            if (!sid.isEmpty()) sessionId = sid;

            String aqsp = response.optString("additionalQueryStringParams", "");
            if (!aqsp.isEmpty()) additionalQueryStringParams = aqsp;

            // Update country dial code dynamically
            String dial = api.getCountryDialCode();
            if (!dial.isEmpty() && !dial.equals(countryDialCode)) {
                countryDialCode = dial;
                etCountryCode.setText("+" + countryDialCode);
            }

            JSONObject payout = response.optJSONObject("Payout");
            if (payout != null) {
                Log.e(TAG, "Payout Rate: " + payout.optDouble("Rate", 0.0)
                        + " Currency: " + payout.optString("Currency", ""));
            }

            if (error == 1) {
                String errMsg = !msg.isEmpty() ? msg : (!description.isEmpty() ? description : "An error occurred");
                showError(errMsg);
                return;
            }

            JSONObject na = response.optJSONObject("NextAction");
            if (na == null) {
                Log.e(TAG, "Full response (no NextAction): " + response.toString());
                // Try case-insensitive search for NextAction
                Iterator<String> respKeys = response.keys();
                while (respKeys.hasNext()) {
                    String k = respKeys.next();
                    Log.e(TAG, "Response key: " + k + " = " + response.opt(k));
                }
                showError(!msg.isEmpty() ? msg : "No further action.");
                return;
            }

            if (error == 2 && !msg.isEmpty()) showError(msg);
            processNextAction(na.optInt("Action", 0), na);
        } catch (Exception e) {
            Log.e(TAG, "handleResponse error", e);
            showError("Error: " + e.getMessage());
        }
    }

    private void processNextAction(int actionId, JSONObject na) {
        hideAllSections();
        showAllDisclaimers(na);
        showLinks(na);
        loadAFScript(na);

        switch (actionId) {
            case 2: // SendPin — show MSISDN entry
                animateViewIn(cardInput);
                layoutMsisdnRow.setVisibility(View.VISIBLE);
                // Stagger MSISDN row
                layoutMsisdnRow.setAlpha(0f);
                layoutMsisdnRow.setTranslationX(-30f);
                layoutMsisdnRow.animate().alpha(1f).translationX(0f).setDuration(400)
                        .setStartDelay(150).setInterpolator(new FastOutSlowInInterpolator()).start();
                btnAction.setText("Subscribe");
                animateButtonEntrance();
                btnAction.setOnClickListener(v -> {
                    animateButtonPress(v);
                    String m = etMsisdn.getText() != null ? etMsisdn.getText().toString().trim() : "";
                    if (m.isEmpty()) { showError("Please enter your phone number"); return; }
                    String prefix = countryDialCode.isEmpty() ? "" : countryDialCode;
                    callAction(2, prefix + m, "");
                });
                break;
            case 3: // VerifyPin
                animateViewIn(cardInput);
                int pinLen = na.optInt("PincodeLength", 4);
                if (pinLen <= 0) pinLen = 4;
                buildPinBoxes(pinLen);
                animatePinBoxesEntrance();
                if (brandColor != 0) reapplyBrandColor();
                btnAction.setText("Verify");
                animateButtonEntrance();
                final int fLen = pinLen;
                btnAction.setOnClickListener(v -> {
                    animateButtonPress(v);
                    String p = collectPin(fLen);
                    if (p.length() < fLen) { showError("Please enter the complete PIN"); return; }
                    callAction(3, currentMsisdn, p);
                });
                break;
            case 4: // LoadURL
                String url = na.optString("URL", "");
                if (!url.isEmpty()) {
                    if (!additionalQueryStringParams.isEmpty())
                        url += (url.contains("?") ? "&" : "?") + additionalQueryStringParams;
                    openUrl(url);
                    finish();
                    overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_scale_out);
                }
                break;
            case 5: // SendSMS
                handleSendSms(na);
                break;
            case 6: // ClicksFlow
                animateViewIn(cardInput);
                btnAction.setText("Continue");
                animateButtonEntrance();
                btnAction.setOnClickListener(v -> {
                    animateButtonPress(v);
                    callAction(6, "", "");
                });
                break;
            case 7: // Close
                Toast.makeText(this, "Process completed", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_scale_out);
                break;
            case 8: // ClickToSMS
                animateViewIn(cardInput);
                btnAction.setText("Subscribe");
                animateButtonEntrance();
                btnAction.setOnClickListener(v -> {
                    animateButtonPress(v);
                    callAction(8, "", "");
                });
                break;
        }
    }

    /** Button press micro-interaction: quick scale down then back */
    private void animateButtonPress(View v) {
        v.animate().scaleX(0.93f).scaleY(0.93f).setDuration(80)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(150)
                        .setInterpolator(new OvershootInterpolator(2f)).start())
                .start();
    }

    /** Button entrance with scale overshoot */
    private void animateButtonEntrance() {
        btnAction.setScaleX(0.7f);
        btnAction.setScaleY(0.7f);
        btnAction.setAlpha(0f);
        btnAction.animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(400).setStartDelay(250)
                .setInterpolator(new OvershootInterpolator(2f)).start();
    }

    /** Stagger-animate PIN boxes one by one */
    private void animatePinBoxesEntrance() {
        for (int i = 0; i < layoutPinBoxes.getChildCount(); i++) {
            View box = layoutPinBoxes.getChildAt(i);
            box.setScaleX(0f);
            box.setScaleY(0f);
            box.setAlpha(0f);
            box.animate().scaleX(1f).scaleY(1f).alpha(1f)
                    .setDuration(300).setStartDelay(150 + i * 60L)
                    .setInterpolator(new OvershootInterpolator(2.5f)).start();
        }
    }

    // ---- AFScript: attach to layout so it actually runs ----

    private void loadAFScript(JSONObject na) {
        afScriptContainer.removeAllViews();
        String af = na.optString("AFScript", "");
        if (af.isEmpty()) return;
        WebView wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
        afScriptContainer.addView(wv);
        wv.loadUrl(af);
    }

    // ---- Disclaimers ----

    private void showAllDisclaimers(JSONObject na) {
        layoutDisclaimersBefore.removeAllViews();
        layoutDisclaimersMiddle.removeAllViews();
        layoutDisclaimersAfter.removeAllViews();

        JSONObject disclaimers = na.optJSONObject("Disclaimers");
        if (disclaimers == null) return;

        boolean hasBefore = false, hasMiddle = false, hasAfter = false;
        Iterator<String> keys = disclaimers.keys();
        int idx = 0;

        while (keys.hasNext()) {
            String key = keys.next();
            String val = disclaimers.optString(key, "");
            if (val.isEmpty()) continue;

            String decoded;
            try { decoded = URLDecoder.decode(val, "UTF-8"); }
            catch (Exception e) { decoded = val; }

            TextView tv = new TextView(this);
            tv.setText(Html.fromHtml(decoded, Html.FROM_HTML_MODE_COMPACT));
            tv.setTextSize(13);
            tv.setLineSpacing(0, 1.4f);
            tv.setPadding(0, 8, 0, 8);

            // Stagger fade-in for each disclaimer
            tv.setAlpha(0f);
            tv.setTranslationY(15f);
            int delay = 200 + idx * 80;
            tv.animate().alpha(1f).translationY(0f).setDuration(350)
                    .setStartDelay(delay).setInterpolator(new DecelerateInterpolator()).start();
            idx++;

            if (BEFORE_KEYS.contains(key)) {
                layoutDisclaimersBefore.addView(tv);
                hasBefore = true;
            } else if (MIDDLE_KEYS.contains(key)) {
                layoutDisclaimersMiddle.addView(tv);
                hasMiddle = true;
            } else {
                layoutDisclaimersAfter.addView(tv);
                hasAfter = true;
            }
        }

        layoutDisclaimersBefore.setVisibility(hasBefore ? View.VISIBLE : View.GONE);
        layoutDisclaimersMiddle.setVisibility(hasMiddle ? View.VISIBLE : View.GONE);
        layoutDisclaimersAfter.setVisibility(hasAfter ? View.VISIBLE : View.GONE);
    }

    // ---- OTP Pin Boxes ----

    private void buildPinBoxes(int count) {
        layoutPinBoxes.removeAllViews();
        layoutPinBoxes.setVisibility(View.VISIBLE);

        EditText[] boxes = new EditText[count];
        for (int i = 0; i < count; i++) {
            EditText box = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPx(46), dpToPx(52), 0);
            lp.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            box.setLayoutParams(lp);
            box.setGravity(android.view.Gravity.CENTER);
            box.setTextSize(22);
            box.setTypeface(null, android.graphics.Typeface.BOLD);
            box.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            box.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(1)});
            box.setPadding(0, 0, 0, 0);
            box.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);

            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            bg.setCornerRadius(dpToPx(8));
            bg.setColor(0xFFF8F8F8);
            bg.setStroke(dpToPx(1), 0xFFDDDDDD);
            box.setBackground(bg);

            boxes[i] = box;
            layoutPinBoxes.addView(box);

            final int idx = i;
            box.setOnFocusChangeListener((v, hasFocus) -> {
                android.graphics.drawable.GradientDrawable d = (android.graphics.drawable.GradientDrawable) box.getBackground();
                if (hasFocus) {
                    d.setStroke(dpToPx(2), brandColor != 0 ? brandColor : 0xFF333333);
                    d.setColor(0xFFFFFFFF);
                    box.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150)
                            .setInterpolator(new OvershootInterpolator(3f)).start();
                } else {
                    d.setStroke(dpToPx(1), 0xFFDDDDDD);
                    d.setColor(0xFFF8F8F8);
                    box.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                }
            });

            box.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
                @Override
                public void afterTextChanged(android.text.Editable s) {
                    if (s.length() == 1 && idx < count - 1) boxes[idx + 1].requestFocus();
                }
            });

            box.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL
                        && event.getAction() == android.view.KeyEvent.ACTION_DOWN
                        && box.getText().length() == 0 && idx > 0) {
                    boxes[idx - 1].requestFocus();
                    boxes[idx - 1].setText("");
                    return true;
                }
                return false;
            });
        }
        if (count > 0) boxes[0].requestFocus();
    }

    private String collectPin(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < layoutPinBoxes.getChildCount() && i < count; i++)
            sb.append(((EditText) layoutPinBoxes.getChildAt(i)).getText().toString());
        return sb.toString();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    // ---- Logo & Color ----
    private void loadLogo(JSONObject response) {
        String url = findImageUrl(response);
        if (url == null) {
            JSONObject na = response.optJSONObject("NextAction");
            if (na != null) url = findImageUrl(na);
        }
        if (url == null || url.isEmpty()) {
            if (brandColor != 0) reapplyBrandColor();
            return;
        }

        ivLogo.setVisibility(View.VISIBLE);
        ivLogo.setAlpha(0f);
        ivLogo.setScaleX(0.5f);
        ivLogo.setScaleY(0.5f);
        Glide.with(this).load(url).circleCrop()
                .into(new com.bumptech.glide.request.target.ImageViewTarget<android.graphics.drawable.Drawable>(ivLogo) {
                    @Override protected void setResource(android.graphics.drawable.Drawable resource) {
                        ivLogo.setImageDrawable(resource);
                        ivLogo.animate().alpha(1f).scaleX(1f).scaleY(1f)
                                .setDuration(500).setInterpolator(new OvershootInterpolator(1.5f)).start();
                    }
                });

        if (brandColor == 0) {
            Glide.with(this).asBitmap().load(url)
                    .into(new com.bumptech.glide.request.target.SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bmp,
                                com.bumptech.glide.request.transition.Transition<? super Bitmap> t) {
                            applyBrandColor(bmp);
                        }
                    });
        } else {
            reapplyBrandColor();
        }
    }

    private void reapplyBrandColor() {
        ColorStateList cl = ColorStateList.valueOf(brandColor);
        btnAction.setBackgroundTintList(cl);
        btnAction.setTextColor(Color.WHITE);
        til.msisdn.setBoxStrokeColor(brandColor);
        til.pin.setBoxStrokeColor(brandColor);
        til.countryCode.setBoxStrokeColor(brandColor);
        til.msisdn.setHintTextColor(cl);
        til.pin.setHintTextColor(cl);
        tvPrivacy.setTextColor(brandColor);
        tvTerms.setTextColor(brandColor);

        // Update header pattern with brand color
        headerPattern.setBrandColor(brandColor);

        // Tint glow rings
        int r = Color.red(brandColor), g = Color.green(brandColor), b = Color.blue(brandColor);
        GradientDrawable ring = new GradientDrawable();
        ring.setShape(GradientDrawable.OVAL);
        ring.setColor(Color.TRANSPARENT);
        ring.setStroke(dpToPx(2), Color.argb(60, r, g, b));
        logoGlowRing.setBackground(ring);

        GradientDrawable outerRing = new GradientDrawable();
        outerRing.setShape(GradientDrawable.OVAL);
        outerRing.setColor(Color.TRANSPARENT);
        outerRing.setStroke(dpToPx(1), Color.argb(30, r, g, b));
        logoGlowRingOuter.setBackground(outerRing);
    }

    private void applyBrandColor(Bitmap bmp) {
        Palette.from(bmp).generate(p -> {
            if (p == null) return;
            Palette.Swatch s = p.getVibrantSwatch();
            if (s == null) s = p.getLightVibrantSwatch();
            if (s == null) s = p.getMutedSwatch();
            if (s == null) s = p.getLightMutedSwatch();
            if (s == null) s = p.getDominantSwatch();
            if (s == null) return;

            double lum = (0.299 * Color.red(s.getRgb()) + 0.587 * Color.green(s.getRgb()) + 0.114 * Color.blue(s.getRgb())) / 255;
            if (lum < 0.15) {
                Palette.Swatch lighter = p.getLightVibrantSwatch();
                if (lighter == null) lighter = p.getVibrantSwatch();
                if (lighter == null) lighter = p.getMutedSwatch();
                if (lighter != null) s = lighter;
            }

            brandColor = s.getRgb();
            reapplyBrandColor();
        });
    }

    // ---- Links ----

    private void showLinks(JSONObject na) {
        String pu = na.optString("PrivacyPolicy", "");
        String tu = na.optString("TermsAndConditions", "");
        if (pu.isEmpty() && tu.isEmpty()) { layoutLinks.setVisibility(View.GONE); return; }
        layoutLinks.setAlpha(0f);
        layoutLinks.setVisibility(View.VISIBLE);
        layoutLinks.animate().alpha(1f).setDuration(400).setStartDelay(500).start();
        tvPrivacy.setVisibility(pu.isEmpty() ? View.GONE : View.VISIBLE);
        tvPrivacy.setText("Privacy Policy");
        tvTerms.setVisibility(tu.isEmpty() ? View.GONE : View.VISIBLE);
        tvTerms.setText("Terms & Conditions");
        tvLinkDivider.setVisibility((!pu.isEmpty() && !tu.isEmpty()) ? View.VISIBLE : View.GONE);
        tvPrivacy.setOnClickListener(v -> openUrl(pu));
        tvTerms.setOnClickListener(v -> openUrl(tu));
    }

    // ---- Helpers ----

    private String findImageUrl(JSONObject json) {
        String[] keys = {"Image", "ServiceImage", "Logo", "ImageURL", "image",
                "serviceImage", "logo", "imageUrl", "Icon", "icon", "ServiceLogo"};
        for (String k : keys) {
            String v = json.optString(k, "");
            if (!v.isEmpty() && v.startsWith("http")) return v;
        }
        Iterator<String> it = json.keys();
        while (it.hasNext()) {
            String k = it.next();
            String v = json.optString(k, "");
            if (v.startsWith("http") && (v.endsWith(".png") || v.endsWith(".jpg")
                    || v.endsWith(".jpeg") || v.endsWith(".webp"))) return v;
        }
        return null;
    }

    private void handleSendSms(JSONObject na) {
        try {
            String destination = na.optString("Destination", na.optString("SMSTo", ""));
            String message = na.optString("Message", na.optString("SMSBody", ""));
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("sms:" + destination));
            i.putExtra("sms_body", message);
            startActivity(i);
        } catch (Exception e) { showError("Unable to open SMS app"); }
    }

    private void hideAllSections() {
        cardInput.setVisibility(View.GONE);
        layoutMsisdnRow.setVisibility(View.GONE);
        til.pin.setVisibility(View.GONE);
        layoutPinBoxes.setVisibility(View.GONE);
        layoutDisclaimersBefore.setVisibility(View.GONE);
        layoutDisclaimersMiddle.setVisibility(View.GONE);
        layoutDisclaimersAfter.setVisibility(View.GONE);
        layoutLinks.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
    }

    private boolean firstLoad = true;

    private void showLoading(boolean show) {
        if (show) {
            if (firstLoad) {
                // Already visible from XML on first launch — don't re-animate
                layoutLoading.setVisibility(View.VISIBLE);
                layoutLoading.setAlpha(1f);
            } else {
                layoutLoading.setAlpha(0f);
                layoutLoading.setVisibility(View.VISIBLE);
                layoutLoading.animate().alpha(1f).setDuration(200).start();
            }
        } else {
            firstLoad = false;
            layoutLoading.animate().alpha(0f).setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator a) {
                            layoutLoading.setVisibility(View.GONE);
                            layoutLoading.animate().setListener(null);
                            scrollView.setVisibility(View.VISIBLE);
                            scrollView.setAlpha(0f);
                            scrollView.animate().alpha(1f).setDuration(300).start();
                        }
                    }).start();
        }
    }

    private void showError(String msg) {
        tvError.setText(msg);
        if (tvError.getVisibility() != View.VISIBLE) {
            tvError.setAlpha(0f);
            tvError.setTranslationY(-10f);
            tvError.setVisibility(View.VISIBLE);
            tvError.animate().alpha(1f).translationY(0f).setDuration(300)
                    .setInterpolator(new DecelerateInterpolator()).start();
            // Shake the card for error feedback
            ObjectAnimator shake = ObjectAnimator.ofFloat(cardInput, "translationX",
                    0, 12, -12, 10, -10, 6, -6, 0);
            shake.setDuration(500);
            shake.start();
        } else {
            tvError.setText(msg);
        }
        if (cardInput.getVisibility() != View.VISIBLE) {
            animateViewIn(cardInput);
        }
    }

    private void hideError() {
        if (tvError.getVisibility() == View.VISIBLE) {
            tvError.animate().alpha(0f).translationY(-10f).setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator a) {
                            tvError.setVisibility(View.GONE);
                            tvError.animate().setListener(null);
                        }
                    }).start();
        }
    }

    /** Animate a view appearing with slide-up + fade */
    private void animateViewIn(View v) {
        v.setAlpha(0f);
        v.setTranslationY(40f);
        v.setVisibility(View.VISIBLE);
        v.animate().alpha(1f).translationY(0f).setDuration(400)
                .setInterpolator(new FastOutSlowInInterpolator()).start();
    }

    /** Animate a view disappearing with fade-out */
    private void animateViewOut(View v) {
        if (v.getVisibility() != View.VISIBLE) return;
        v.animate().alpha(0f).setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(Animator a) {
                        v.setVisibility(View.GONE);
                        v.animate().setListener(null);
                    }
                }).start();
    }

    private void openUrl(String url) {
        try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }
        catch (Exception ignored) {}
    }
}
