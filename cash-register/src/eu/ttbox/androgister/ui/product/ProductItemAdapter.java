package eu.ttbox.androgister.ui.product;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.product.OfferHelper;
import eu.ttbox.androgister.ui.admin.product.ProductUiHelper;

public class ProductItemAdapter extends ResourceCursorAdapter {

    private OfferHelper helper;

    private boolean isNotBinding = true;

    private HashMap<String, Drawable> mapColors;

    private Context context;
    private ProductUiHelper productColor;

    public ProductItemAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
        this.context = context;
        this.productColor = new ProductUiHelper(context);
    }

    private void intViewBinding(View view, Context context, Cursor cursor) {
        mapColors = new HashMap<String, Drawable>();
        mapColors.put("Boisson", productColor.getStateGradientDrawable(Color.GREEN));
        mapColors.put("Entrée", productColor.getStateGradientDrawable(Color.BLUE));
        mapColors.put("Plat", productColor.getStateGradientDrawable(Color.RED));
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
        ViewHolder holder = (ViewHolder) view.getTag();
        // Bind Value
        helper.setTextOfferName(holder.nameText, cursor)//
                .setTextOfferPrice(holder.priceText, cursor);
        // Bg color
        String tag = cursor.getString(helper.tagIdx);
        Drawable grad = mapColors.get(tag);
        if (grad != null) {
            view.setBackgroundDrawable(grad);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = super.newView(context, cursor, parent);
        // Then populate the ViewHolder
        ViewHolder holder = new ViewHolder();
        holder.nameText = (TextView) view.findViewById(R.id.product_list_item_name);
        holder.priceText = (TextView) view.findViewById(R.id.product_list_item_price);
        // and store it inside the layout.
        view.setTag(holder);
        return view;

    }

    static class ViewHolder {
        TextView nameText;
        TextView priceText;
    }
}
