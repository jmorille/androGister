package eu.ttbox.androgister.ui.product;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.product.OfferHelper;

public class ProductItemAdapter extends ResourceCursorAdapter {

    private OfferHelper helper;

    private boolean isNotBinding = true;

    private HashMap<String, GradientDrawable> mapColors;

    public ProductItemAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    private GradientDrawable getGradientDrawable(int color) {
        GradientDrawable grad = new GradientDrawable(Orientation.BR_TL, new int[] { color, color - 0x88000000 });
        grad.setShape(GradientDrawable.RECTANGLE);
        grad.setCornerRadius(8);
        return grad;
    }

    private void intViewBinding(View view, Context context, Cursor cursor) {
        mapColors = new HashMap<String, GradientDrawable>();
        mapColors.put("Boisson", getGradientDrawable(Color.GREEN));
        mapColors.put("Entrée", getGradientDrawable(Color.BLUE));
        mapColors.put("Plat", getGradientDrawable(Color.RED));
        // Init Cursor
        helper = new OfferHelper().initWrapper(cursor);
        isNotBinding = false;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (isNotBinding) {
            intViewBinding(view, context, cursor);
        }
        // Bind View
        TextView nameText = (TextView) view.findViewById(R.id.product_list_item_name);
        TextView priceText = (TextView) view.findViewById(R.id.product_list_item_price);
        // Bind Value
        helper.setTextOfferName(nameText, cursor)//
                .setTextOfferPrice(priceText, cursor);
        // Bg color
        String tag = cursor.getString(helper.tagIdx);
        GradientDrawable grad = mapColors.get(tag);
        if (grad != null) {
            view.setBackgroundDrawable(grad);
        }

    }
}
