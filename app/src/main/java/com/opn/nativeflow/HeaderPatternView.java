package com.opn.nativeflow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

public class HeaderPatternView extends View {

    private static final int STYLE_COUNT = 11;
    private int brandColor = 0xFFE91E63;
    private int style = -1;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Random random = new Random();

    public HeaderPatternView(Context context) { super(context); }
    public HeaderPatternView(Context context, AttributeSet attrs) { super(context, attrs); }

    public void setBrandColor(int color) { brandColor = color; invalidate(); }
    public void setStyle(int s) { style = s; invalidate(); }
    public void randomizeStyle() { style = random.nextInt(STYLE_COUNT); invalidate(); }
    public int getStyle() { return style; }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (style < 0) style = random.nextInt(STYLE_COUNT);
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;
        canvas.drawColor(Color.WHITE);
        switch (style) {
            // ── existing styles ──────────────────────────────────────────
            case 0:  drawScatteredPolygons(canvas, w, h);       break;
            case 1:  drawInkWash(canvas, w, h);       break;
            case 2:  drawStarburstRings(canvas, w, h);            break;
            case 3:  drawHalftoneDots(canvas, w, h);             break;
            case 4:  drawBubblesTopRight(canvas, w, h);             break;
            case 5:  drawAuroraVeil(canvas, w, h);             break;
            case 6:  drawGlassVeil(canvas, w, h);             break;
            case 7:  drawFloatingDots(canvas, w, h);             break;
            case 8:  drawSoftOverlappingVeils(canvas, w, h);             break;
            case 9:  drawMosaicTiles(canvas, w, h);             break;
            case 10:  drawOrigamiTriangles(canvas, w, h);             break;

        }
    }

    // ── colour helpers ────────────────────────────────────────────────────

    private int a(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private int lit(int color, float f) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] *= (1f - f);
        hsv[2] = Math.min(hsv[2] + f * (1f - hsv[2]), 1f);
        return Color.HSVToColor(hsv);
    }

    private int drk(int color, float f) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= (1f - f);
        return Color.HSVToColor(hsv);
    }

    private void drawBubblesTopRight(Canvas canvas, int w, int h) {
        paint.setShader(new RadialGradient(w * 0.85f, h * 0.05f, w * 0.4f,
                a(brandColor, 200), a(brandColor, 120), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.85f, h * 0.05f, w * 0.4f, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.6f, h * 0.28f, w * 0.28f,
                a(brandColor, 170), a(lit(brandColor, 0.2f), 90), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.6f, h * 0.28f, w * 0.28f, paint);
        paint.setShader(null);

        paint.setColor(a(lit(brandColor, 0.3f), 100));
        canvas.drawCircle(w * 1.05f, h * 0.4f, w * 0.16f, paint);

        paint.setColor(a(lit(brandColor, 0.5f), 60));
        canvas.drawCircle(w * 0.15f, h * 0.08f, w * 0.08f, paint);
    }


    private void drawAuroraVeil(Canvas canvas, int w, int h) {
        int v1 = lit(brandColor, 0.65f);
        int v2 = lit(brandColor, 0.75f);
        int v3 = lit(brandColor, 0.55f);

        paint.setShader(new LinearGradient(0, 0, w * 0.6f, h * 0.5f,
                a(v1, 130), a(v2, 50), Shader.TileMode.CLAMP));
        Path s1 = new Path();
        s1.moveTo(0, 0);
        s1.lineTo(w * 0.65f, 0);
        s1.cubicTo(w * 0.6f, h * 0.15f, w * 0.4f, h * 0.35f, w * 0.2f, h * 0.42f);
        s1.cubicTo(w * 0.08f, h * 0.46f, 0, h * 0.4f, 0, h * 0.38f);
        s1.close();
        canvas.drawPath(s1, paint);
        paint.setShader(null);

        paint.setShader(new LinearGradient(w * 0.4f, 0, w, h * 0.4f,
                a(v3, 110), a(v2, 40), Shader.TileMode.CLAMP));
        Path s2 = new Path();
        s2.moveTo(w * 0.35f, 0);
        s2.lineTo(w, 0);
        s2.lineTo(w, h * 0.32f);
        s2.cubicTo(w * 0.85f, h * 0.42f, w * 0.65f, h * 0.35f, w * 0.5f, h * 0.2f);
        s2.cubicTo(w * 0.4f, h * 0.1f, w * 0.36f, h * 0.04f, w * 0.35f, 0);
        s2.close();
        canvas.drawPath(s2, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.35f, h * 0.2f, w * 0.25f,
                a(v2, 70), a(v2, 0), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.35f, h * 0.2f, w * 0.25f, paint);
        paint.setShader(null);
    }

    private void drawGlassVeil(Canvas canvas, int w, int h) {
        int v1 = lit(brandColor, 0.5f);
        int v2 = lit(brandColor, 0.6f);
        int v3 = lit(brandColor, 0.4f);

        paint.setShader(new LinearGradient(0, 0, w * 0.5f, h * 0.6f,
                a(v3, 180), a(v1, 80), Shader.TileMode.CLAMP));
        Path g1 = new Path();
        g1.moveTo(0, 0);
        g1.lineTo(w * 0.55f, 0);
        g1.cubicTo(w * 0.5f, h * 0.12f, w * 0.35f, h * 0.3f, w * 0.25f, h * 0.4f);
        g1.cubicTo(w * 0.15f, h * 0.5f, w * 0.05f, h * 0.48f, 0, h * 0.42f);
        g1.close();
        canvas.drawPath(g1, paint);
        paint.setShader(null);

        paint.setShader(new LinearGradient(w * 0.5f, 0, w, h * 0.5f,
                a(v1, 170), a(v2, 70), Shader.TileMode.CLAMP));
        Path g2 = new Path();
        g2.moveTo(w * 0.4f, 0);
        g2.lineTo(w, 0);
        g2.lineTo(w, h * 0.38f);
        g2.cubicTo(w * 0.88f, h * 0.48f, w * 0.7f, h * 0.42f, w * 0.55f, h * 0.3f);
        g2.cubicTo(w * 0.45f, h * 0.2f, w * 0.42f, h * 0.08f, w * 0.4f, 0);
        g2.close();
        canvas.drawPath(g2, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.4f, h * 0.18f, w * 0.22f,
                a(v2, 120), a(v2, 0), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.4f, h * 0.18f, w * 0.22f, paint);
        paint.setShader(null);

        paint.setColor(a(v1, 70));
        canvas.drawCircle(w * 0.7f, h * 0.55f, w * 0.15f, paint);
    }

    private void drawFloatingDots(Canvas canvas, int w, int h) {
        int bg = lit(brandColor, 0.92f);
        canvas.drawColor(bg);

        paint.setShader(new LinearGradient(0, 0, w, h * 0.5f,
                a(lit(brandColor, 0.82f), 120), a(bg, 0), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h * 0.5f, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.78f, h * 0.22f, w * 0.07f,
                a(lit(brandColor, 0.2f), 220), a(brandColor, 160), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.78f, h * 0.22f, w * 0.07f, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.12f, h * 0.38f, w * 0.055f,
                a(lit(brandColor, 0.25f), 210), a(brandColor, 150), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.12f, h * 0.38f, w * 0.055f, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.28f, h * 0.1f, w * 0.04f,
                a(lit(brandColor, 0.15f), 200), a(brandColor, 140), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.28f, h * 0.1f, w * 0.04f, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.92f, h * 0.55f, w * 0.035f,
                a(lit(brandColor, 0.2f), 190), a(brandColor, 130), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.92f, h * 0.55f, w * 0.035f, paint);
        paint.setShader(null);

        paint.setColor(a(brandColor, 180));
        canvas.drawCircle(w * 0.62f, h * 0.08f, w * 0.012f, paint);
        canvas.drawCircle(w * 0.88f, h * 0.42f, w * 0.01f, paint);
        canvas.drawCircle(w * 0.35f, h * 0.52f, w * 0.009f, paint);

        paint.setColor(a(brandColor, 130));
        canvas.drawCircle(w * 0.5f, h * 0.15f, w * 0.006f, paint);
        canvas.drawCircle(w * 0.18f, h * 0.12f, w * 0.005f, paint);
        canvas.drawCircle(w * 0.72f, h * 0.48f, w * 0.005f, paint);
        canvas.drawCircle(w * 0.42f, h * 0.42f, w * 0.004f, paint);
        canvas.drawCircle(w * 0.95f, h * 0.15f, w * 0.004f, paint);
        canvas.drawCircle(w * 0.08f, h * 0.6f, w * 0.006f, paint);

        paint.setShader(new RadialGradient(w * 0.5f, h * 0.7f, w * 0.2f,
                a(lit(brandColor, 0.7f), 60), a(lit(brandColor, 0.7f), 0), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.5f, h * 0.7f, w * 0.2f, paint);
        paint.setShader(null);
    }

    private void drawSoftOverlappingVeils(Canvas canvas, int w, int h) {
        canvas.drawColor(lit(brandColor, 0.94f));

        paint.setShader(new LinearGradient(0, 0, w * 0.7f, h,
                a(lit(brandColor, 0.55f), 160), a(lit(brandColor, 0.75f), 50), Shader.TileMode.CLAMP));
        Path v1 = new Path();
        v1.moveTo(0, 0);
        v1.lineTo(w * 0.3f, 0);
        v1.cubicTo(w * 0.35f, h * 0.25f, w * 0.5f, h * 0.5f, w * 0.75f, h * 0.7f);
        v1.cubicTo(w * 0.88f, h * 0.82f, w * 0.95f, h * 0.92f, w, h);
        v1.lineTo(w * 0.4f, h);
        v1.cubicTo(w * 0.3f, h * 0.75f, w * 0.15f, h * 0.45f, 0, h * 0.2f);
        v1.close();
        canvas.drawPath(v1, paint);
        paint.setShader(null);

        paint.setShader(new LinearGradient(w, 0, w * 0.2f, h,
                a(lit(brandColor, 0.6f), 140), a(lit(brandColor, 0.8f), 40), Shader.TileMode.CLAMP));
        Path v2 = new Path();
        v2.moveTo(w * 0.6f, 0);
        v2.lineTo(w, 0);
        v2.lineTo(w, h * 0.35f);
        v2.cubicTo(w * 0.85f, h * 0.55f, w * 0.6f, h * 0.65f, w * 0.3f, h * 0.8f);
        v2.cubicTo(w * 0.15f, h * 0.9f, w * 0.05f, h * 0.95f, 0, h);
        v2.lineTo(0, h * 0.75f);
        v2.cubicTo(w * 0.2f, h * 0.55f, w * 0.42f, h * 0.3f, w * 0.6f, 0);
        v2.close();
        canvas.drawPath(v2, paint);
        paint.setShader(null);
    }

    private void drawScatteredPolygons(Canvas canvas, int w, int h) {
        canvas.drawColor(Color.WHITE);

        float[][] shapes = {
                // cx, cy, radius, sides, rotation
                {0.78f, 0.12f, 0.18f, 6, 0.3f},
                {0.15f, 0.20f, 0.12f, 3, 0.8f},
                {0.55f, 0.38f, 0.10f, 4, 0.4f},
                {0.92f, 0.45f, 0.09f, 5, 0.1f},
                {0.35f, 0.06f, 0.07f, 6, 0.6f},
                {0.08f, 0.55f, 0.07f, 3, 0.2f},
                {0.68f, 0.60f, 0.06f, 4, 1.0f},
        };
        int[] alphas = {160, 100, 80, 70, 60, 50, 45};

        for (int s = 0; s < shapes.length; s++) {
            float cx = shapes[s][0] * w, cy = shapes[s][1] * h;
            float r   = shapes[s][2] * w;
            int sides = (int) shapes[s][3];
            float rot = shapes[s][4];

            Path poly = new Path();
            for (int i = 0; i < sides; i++) {
                double angle = 2 * Math.PI / sides * i + rot;
                float px = cx + r * (float) Math.cos(angle);
                float py = cy + r * (float) Math.sin(angle);
                if (i == 0) poly.moveTo(px, py); else poly.lineTo(px, py);
            }
            poly.close();

            paint.setColor(a(lit(brandColor, 0.25f), alphas[s]));
            canvas.drawPath(poly, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(w * 0.004f);
            paint.setColor(a(brandColor, (int)(alphas[s] * 0.6f)));
            canvas.drawPath(poly, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    private void drawInkWash(Canvas canvas, int w, int h) {
        canvas.drawColor(lit(brandColor, 0.95f));

        // Left blob
        paint.setShader(new LinearGradient(0, 0, w * 0.55f, h * 0.6f,
                a(lit(brandColor, 0.45f), 120), a(lit(brandColor, 0.45f), 0), Shader.TileMode.CLAMP));
        Path b1 = new Path();
        b1.moveTo(0, 0);
        b1.cubicTo(w * 0.5f, 0, w * 0.55f, h * 0.55f, 0, h * 0.6f);
        b1.close();
        canvas.drawPath(b1, paint);
        paint.setShader(null);

        // Right blob
        paint.setShader(new LinearGradient(w, 0, w * 0.45f, h * 0.55f,
                a(lit(brandColor, 0.55f), 100), a(lit(brandColor, 0.55f), 0), Shader.TileMode.CLAMP));
        Path b2 = new Path();
        b2.moveTo(w, 0);
        b2.cubicTo(w * 0.5f, h * 0.1f, w * 0.45f, h * 0.5f, w, h * 0.55f);
        b2.close();
        canvas.drawPath(b2, paint);
        paint.setShader(null);

        // Third mid-top blob
        paint.setShader(new LinearGradient(w * 0.3f, 0, w * 0.8f, h * 0.4f,
                a(lit(brandColor, 0.65f), 70), a(lit(brandColor, 0.65f), 0), Shader.TileMode.CLAMP));
        Path b3 = new Path();
        b3.moveTo(w * 0.3f, 0);
        b3.cubicTo(w * 0.7f, 0, w * 0.8f, h * 0.4f, w * 0.35f, h * 0.45f);
        b3.cubicTo(w * 0.1f, h * 0.4f, w * 0.1f, h * 0.1f, w * 0.3f, 0);
        b3.close();
        canvas.drawPath(b3, paint);
        paint.setShader(null);

        // Centre bloom
        paint.setShader(new RadialGradient(w * 0.42f, h * 0.3f, w * 0.28f,
                a(brandColor, 50), a(brandColor, 0), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
    }

    private void drawMosaicTiles(Canvas canvas, int w, int h) {
        canvas.drawColor(lit(brandColor, 0.93f));

        float tileW = w * 0.09f, tileH = h * 0.16f;
        float pad = w * 0.006f;
        int cols = (int)(w / tileW) + 2;
        int rows = (int)(h / tileH) + 2;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float tx = col * tileW + (row % 2) * tileW * 0.5f;
                float ty = row * tileH;
                float dist = (float) Math.hypot(tx - w * 0.85f, ty - h * 0.1f);
                int alpha = (int) Math.max(5, 60 - dist / (w * 0.012f));
                float lightness = Math.min(0.9f, 0.3f + dist / (w * 1.5f) * 0.5f);
                paint.setColor(a(lit(brandColor, lightness), alpha));
                canvas.drawRoundRect(tx + pad, ty + pad,
                        tx + tileW - pad, ty + tileH - pad, w * 0.012f, w * 0.012f, paint);
            }
        }

        // Hotspot glow
        paint.setShader(new RadialGradient(w * 0.85f, h * 0.1f, w * 0.4f,
                a(brandColor, 100), a(brandColor, 0), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
    }

    private void drawStarburstRings(Canvas canvas, int w, int h) {
        canvas.drawColor(Color.WHITE);
        float cx = w * 0.95f, cy = h * 0.05f;
        for (int i = 7; i >=1; i--) {
            float r = i * w * 0.11f;
            int alpha = Math.max(22, 120 - i * 14);
            paint.setColor(a(lit(brandColor, i * 0.12f), alpha));
            canvas.drawCircle(cx, cy, r, paint);
        }
        paint.setShader(new RadialGradient(cx, cy, w * 0.28f,
                a(brandColor, 180), a(brandColor, 0), Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, w * 0.28f, paint);
        paint.setShader(null);
    }

    private void drawHalftoneDots(Canvas canvas, int w, int h) {
        canvas.drawColor(lit(brandColor, 0.94f));
        float sp = w * 0.055f;
        float maxR = sp * 0.45f;
        for (float x = 0; x < w + sp; x += sp) {
            for (float y = 0; y < h + sp; y += sp) {
                float dist = (float) Math.hypot(x - w * 0.8f, y - h * 0.15f);
                float r = maxR * (1f - Math.min(1f, dist / (w * 0.7f)));
                if (r < 1f) continue;
                int alpha = Math.max(20, (int)(160 * (1f - dist / (w * 0.7f))));
                paint.setColor(a(brandColor, alpha));
                canvas.drawCircle(x, y, r, paint);
            }
        }
    }

    private void drawOrigamiTriangles(Canvas canvas, int w, int h) {
        canvas.drawColor(lit(brandColor, 0.94f));
        float[][][] tris = {
                {{0f,0f},{0.4f,0f},{0f,0.5f}},
                {{0.35f,0f},{0.75f,0f},{0.55f,0.45f}},
                {{0.65f,0f},{1f,0f},{1f,0.4f}},
                {{0f,0.45f},{0.25f,0f},{0.5f,0.45f}},
                {{0.5f,0.45f},{0.78f,0f},{1f,0.38f}},
                {{0f,0.5f},{0.4f,0.5f},{0.2f,0.85f}},
                {{0.6f,0.4f},{1f,0.38f},{0.85f,0.75f}},
        };
        float[] lights = {0.50f,0.60f,0.45f,0.65f,0.55f,0.70f,0.68f};
        int[]   alphas = { 130,  110,  140,   90,  100,   70,   75};

        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < tris.length; i++) {
            paint.setColor(a(lit(brandColor, lights[i]), alphas[i]));
            Path p = new Path();
            p.moveTo(tris[i][0][0] * w, tris[i][0][1] * h);
            p.lineTo(tris[i][1][0] * w, tris[i][1][1] * h);
            p.lineTo(tris[i][2][0] * w, tris[i][2][1] * h);
            p.close();
            canvas.drawPath(p, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(w * 0.003f);
            paint.setColor(a(Color.WHITE, 60));
            canvas.drawPath(p, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }
}