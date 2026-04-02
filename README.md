# NativeFlow - MobiBox User Acquisition SDK

Android native implementation of the [MobiBox Users Acquisition API](https://mobibox.atlassian.net/wiki/spaces/AFD/pages/192118785/MobiBox+Users+Aquisition+API).

## Overview

This app integrates with the MobiBox API to handle mobile operator subscription flows natively on Android. It supports multiple subscription types and guides the user step-by-step through the process.

## Supported Subscription Flows

| Flow | Description |
|------|-------------|
| **Pin Flow** | User enters MSISDN → receives PIN via SMS → enters PIN to verify |
| **SMS Flow** | User enters MSISDN → SMS composer opens to send subscription SMS |
| **Click to SMS** | No MSISDN entry → user clicks button → SMS composer opens |
| **2 Clicks Flow** | No MSISDN entry → user clicks button → redirected to operator page |
| **Consent Gateway** | Redirect to operator's consent page |

## Architecture

### Files

| File | Purpose |
|------|---------|
| `ActivityStartUp` | Splash/launcher → opens DashboardActivity |
| `DashboardActivity` | Start button → opens BillingActivity |
| `BillingActivity` | Main flow controller — handles full API interaction and UI |
| `MobiBoxApi` | API client — builds requests, calls endpoint, parses responses |
| `AESHelper` | AES-CBC encryption/decryption (key + IV from first 16 chars) |
| `HeaderPatternView` | Custom View — renders randomized decorative header patterns |

### API Flow

```
Action 1 (Initiate) → API returns NextAction
  ├── Action 2 (SendPin) → Show MSISDN entry → Send PIN
  │     ├── Action 3 (VerifyPin) → Show PIN entry → Verify
  │     ├── Action 4 (LoadURL) → Redirect to URL
  │     └── Action 5 (SendSMS) → Open SMS composer
  ├── Action 4 (LoadURL) → Redirect to consent gateway
  ├── Action 6 (ClicksFlow) → Show button → Click to proceed
  │     └── Action 4 (LoadURL) → Redirect
  ├── Action 8 (ClickToSMS) → Show button → Click
  │     └── Action 5 (SendSMS) → Open SMS composer
  └── Action 7 (Close) → Done
```

### API Request Structure

```json
{
  "DeviceInfo": {
    "LangCode": "en",
    "UserAgent": "...",
    "UserIP": "...",
    "gclid": "",
    "wbraid": "",
    "gbraid": ""
  },
  "Referrer": {
    "Affiliate": {
      "Campaign": "...",
      "ClickID": "",
      "Pub_ID": "",
      "Aff_ID": "",
      "extra": "",
      "extra1": "",
      "Country": "LB",
      "firstPageButtonID": "btnAction",
      "secondPageButtonID": "btnAction"
    }
  },
  "Request": {
    "Action": 1,
    "TransactionID": "uuid",
    "SessionID": "",
    "MSISDN": "",
    "PinCode": ""
  }
}
```

### API Response Fields

| Field | Description |
|-------|-------------|
| `Error` | 0 = OK, 1 = error (stop), 2 = already subscribed (continue) |
| `Description` | Error reason |
| `MessageToShow` | User-facing message |
| `SessionID` | Must be sent in all subsequent requests |
| `NextAction` | Object with `Action` ID and related data |
| `Payout` | `Rate` and `Currency` for conversions |
| `additionalQueryStringParams` | Anti-fraud params to append to URLs |

## UI Features

### Header Pattern Themes

The app displays a randomized decorative header pattern on each visit. There are **6 styles**:

| # | Style | Description |
|---|-------|-------------|
| 0 | **BubblesTopRight** | Gradient bubbles clustered in the top-right area |
| 1 | **GeometricCorner** | Layered gradient circles with elegant thin ring outlines and soft glow |
| 2 | **AuroraVeil** | Soft translucent flowing shapes from both top corners |
| 3 | **GlassVeil** | Glass-like translucent shapes with gradient overlaps |
| 4 | **FloatingDots** | Light tinted background with gradient spheres and scattered dots |
| 5 | **SoftOverlappingVeils** | Two translucent curved ribbons crossing from opposite corners |

All styles use the **dynamic brand color** extracted from the service logo via Palette API.

### Other UI Features

- **Dynamic brand color** — extracted from service logo, applied to button, inputs, links, header, glow rings
- **Logo with glow rings** — pulsing animated rings around the service logo
- **Entrance animations** — logo scales up with overshoot, card slides up
- **OTP pin boxes** — individual digit boxes with auto-focus advance
- **Disclaimers** — header, middle, footer sections from API (URL-decoded HTML)
- **Privacy Policy / Terms** links from API
- **AFScript** — cybersecurity script loaded in hidden WebView
- **Country dial code** — dynamically set from API response

## Tech Stack

- **Min SDK**: 24
- **Target SDK**: 36
- **Language**: Java
- **HTTP**: OkHttp 4.12
- **Images**: Glide 4.16
- **Color extraction**: AndroidX Palette
- **UI**: Material Components, CardView
- **Encryption**: AES/CBC/PKCS5Padding
