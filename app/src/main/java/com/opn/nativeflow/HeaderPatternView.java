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

    private static final int STYLE_COUNT = 5;
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
            case 0: drawBubblesTopRight(canvas, w, h); break;
            case 1: drawGeometricCorner(canvas, w, h); break;
            case 2: drawAuroraVeil(canvas, w, h); break;
            case 3: drawGlassVeil(canvas, w, h); break;
            case 4: drawFloatingDots(canvas, w, h); break;
        }
    }

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

    private void drawGeometricCorner(Canvas canvas, int w, int h) {
        // Soft gradient wash across the top
        paint.setShader(new LinearGradient(0, 0, w, h * 0.6f,
                a(lit(brandColor, 0.75f), 80), a(lit(brandColor, 0.85f), 20), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h * 0.6f, paint);
        paint.setShader(null);

        // Large hero circle — top right, partially off-screen
        paint.setShader(new RadialGradient(w * 0.82f, -h * 0.08f, w * 0.48f,
                a(drk(brandColor, 0.05f), 240), a(brandColor, 130), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.82f, -h * 0.08f, w * 0.48f, paint);
        paint.setShader(null);

        // Medium circle — left side, overlapping edge
        paint.setShader(new RadialGradient(-w * 0.05f, h * 0.35f, w * 0.3f,
                a(brandColor, 210), a(lit(brandColor, 0.2f), 100), Shader.TileMode.CLAMP));
        canvas.drawCircle(-w * 0.05f, h * 0.35f, w * 0.3f, paint);
        paint.setShader(null);

        // Accent circle — center-right, adds depth between the two main circles
        paint.setShader(new RadialGradient(w * 0.55f, h * 0.15f, w * 0.18f,
                a(lit(brandColor, 0.15f), 140), a(lit(brandColor, 0.3f), 40), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.55f, h * 0.15f, w * 0.18f, paint);
        paint.setShader(null);

        // Soft glow — where circles overlap
        paint.setShader(new RadialGradient(w * 0.35f, h * 0.12f, w * 0.22f,
                a(lit(brandColor, 0.5f), 70), a(lit(brandColor, 0.5f), 0), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.35f, h * 0.12f, w * 0.22f, paint);
        paint.setShader(null);

        // Thin elegant ring — large
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(w * 0.004f);
        paint.setColor(a(brandColor, 50));
        canvas.drawCircle(w * 0.6f, h * 0.4f, w * 0.2f, paint);

        // Thin elegant ring — small
        paint.setStrokeWidth(w * 0.003f);
        paint.setColor(a(brandColor, 35));
        canvas.drawCircle(w * 0.2f, h * 0.08f, w * 0.09f, paint);
        paint.setStyle(Paint.Style.FILL);

        // Small solid accent dot
        paint.setColor(a(lit(brandColor, 0.1f), 160));
        canvas.drawCircle(w * 0.42f, h * 0.48f, w * 0.02f, paint);

        // Tiny dot — top area
        paint.setColor(a(lit(brandColor, 0.3f), 100));
        canvas.drawCircle(w * 0.15f, h * 0.55f, w * 0.015f, paint);
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

        // Large flowing shape — top-left to center
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

        // Flowing shape — top-right sweeping down
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

        // Soft center glow where shapes overlap
        paint.setShader(new RadialGradient(w * 0.4f, h * 0.18f, w * 0.22f,
                a(v2, 120), a(v2, 0), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.4f, h * 0.18f, w * 0.22f, paint);
        paint.setShader(null);

        // Bottom accent
        paint.setColor(a(v1, 70));
        canvas.drawCircle(w * 0.7f, h * 0.55f, w * 0.15f, paint);
    }

    private void drawFloatingDots(Canvas canvas, int w, int h) {
        // Soft tinted background
        int bg = lit(brandColor, 0.92f);
        canvas.drawColor(bg);

        // Subtle gradient wash top area
        paint.setShader(new LinearGradient(0, 0, w, h * 0.5f,
                a(lit(brandColor, 0.82f), 120), a(bg, 0), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h * 0.5f, paint);
        paint.setShader(null);

        // Large sphere — top right
        paint.setShader(new RadialGradient(w * 0.78f, h * 0.22f, w * 0.07f,
                a(lit(brandColor, 0.2f), 220), a(brandColor, 160), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.78f, h * 0.22f, w * 0.07f, paint);
        paint.setShader(null);

        // Large sphere — left
        paint.setShader(new RadialGradient(w * 0.12f, h * 0.38f, w * 0.055f,
                a(lit(brandColor, 0.25f), 210), a(brandColor, 150), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.12f, h * 0.38f, w * 0.055f, paint);
        paint.setShader(null);

        // Medium sphere — top left
        paint.setShader(new RadialGradient(w * 0.28f, h * 0.1f, w * 0.04f,
                a(lit(brandColor, 0.15f), 200), a(brandColor, 140), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.28f, h * 0.1f, w * 0.04f, paint);
        paint.setShader(null);

        // Medium sphere — right side
        paint.setShader(new RadialGradient(w * 0.92f, h * 0.55f, w * 0.035f,
                a(lit(brandColor, 0.2f), 190), a(brandColor, 130), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.92f, h * 0.55f, w * 0.035f, paint);
        paint.setShader(null);

        // Small dots scattered around
        paint.setColor(a(brandColor, 180));
        canvas.drawCircle(w * 0.62f, h * 0.08f, w * 0.012f, paint);
        canvas.drawCircle(w * 0.88f, h * 0.42f, w * 0.01f, paint);
        canvas.drawCircle(w * 0.35f, h * 0.52f, w * 0.009f, paint);

        // Tiny dots
        paint.setColor(a(brandColor, 130));
        canvas.drawCircle(w * 0.5f, h * 0.15f, w * 0.006f, paint);
        canvas.drawCircle(w * 0.18f, h * 0.12f, w * 0.005f, paint);
        canvas.drawCircle(w * 0.72f, h * 0.48f, w * 0.005f, paint);
        canvas.drawCircle(w * 0.42f, h * 0.42f, w * 0.004f, paint);
        canvas.drawCircle(w * 0.95f, h * 0.15f, w * 0.004f, paint);
        canvas.drawCircle(w * 0.08f, h * 0.6f, w * 0.006f, paint);

        // Soft glow behind center area (where logo sits)
        paint.setShader(new RadialGradient(w * 0.5f, h * 0.7f, w * 0.2f,
                a(lit(brandColor, 0.7f), 60), a(lit(brandColor, 0.7f), 0), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.5f, h * 0.7f, w * 0.2f, paint);
        paint.setShader(null);
    }

}
