package eu.ttbox.androgister.ui.admin.product;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.StateListDrawable;
import android.util.SparseArray;
import android.util.StateSet;

public class ProductUiHelper {

    private Context context;

    private SparseArray<Drawable> colorDrawables = new SparseArray<Drawable>();

    // ===========================================================
    // Constructor
    // ===========================================================

    public ProductUiHelper(Context context) {
        super();
        this.context = context;
    }

    private Drawable getCacheBackgroundGradientColor(int color) {
        Drawable grad = colorDrawables.get(color);
        if (grad == null) {
            grad = ProductUiHelper.getGradientDrawable(color);
            colorDrawables.put(color, grad);
        }
        return grad;
    }

    public Drawable getStateGradientDrawable(int color) {
        StateListDrawable state = new StateListDrawable();
        // int darkerColor = getColorDarker(color);
        // Drawable selected =
        // context.getResources().getDrawable(android.R.drawable.a);
        Drawable normal = getCacheBackgroundGradientColor(color);
        Drawable selected = getCacheBackgroundGradientColor(getColorDarker(color));
        // State
        state.addState(new int[] { android.R.attr.state_pressed }, selected);
        state.addState(new int[] { android.R.attr.state_checked }, selected);
        state.addState(new int[] { android.R.attr.state_activated }, selected);
        state.addState(StateSet.NOTHING, normal);
        return state;
    }

    // ===========================================================
    // Static
    // ===========================================================

    private static Drawable getGradientDrawable(int color) {
        GradientDrawable grad = new GradientDrawable(Orientation.BR_TL, new int[] { color, color - 0x88000000 });
        grad.setShape(GradientDrawable.RECTANGLE);
        grad.setCornerRadius(10);
        return grad;
    }

    private static int getColorDarker(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        int darkerColor = Color.HSVToColor(hsv);
        return darkerColor;
    }
}
