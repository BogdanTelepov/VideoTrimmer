package kg.dev.videoeditor.telegramUI;

import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.FrameLayout;

public class AndroidUtilities {

    public static final RectF rectTmp = new RectF();
    public static final Rect rectTmp2 = new Rect();

    public static float density = 1;

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }


    public static int clamp(int value, int maxValue, int minValue) {
        return Math.max(Math.min(value, maxValue), minValue);
    }

    public static long clamp(long value, long maxValue, long minValue) {
        return Math.max(Math.min(value, maxValue), minValue);
    }

    public static float clamp(float value, float maxValue, float minValue) {
        if (Float.isNaN(value)) {
            return minValue;
        }
        if (Float.isInfinite(value)) {
            return maxValue;
        }
        return Math.max(Math.min(value, maxValue), minValue);
    }

    public static float dpf2(float value) {
        if (value == 0) {
            return 0;
        }
        return density * value;
    }

    public static int getSize(float size) {
        return (int) (size < 0 ? size : AndroidUtilities.dp(size));
    }

    public static FrameLayout.LayoutParams createFrame(int width, int height, int gravity) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height), gravity);
    }

}
