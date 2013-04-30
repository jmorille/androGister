package eu.ttbox.androgister.ui.product;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.PriceHelper;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.ProductCursorHelper;
import eu.ttbox.androgister.domain.Tag;
import eu.ttbox.androgister.domain.TagDao;
import eu.ttbox.androgister.ui.admin.product.ProductUiHelper;

public class ProductItemAdapter extends ResourceCursorAdapter {

    // private OfferHelper helper;

    // private boolean isNotBinding = true;

    // private HashMap<String, Drawable> mapColors;

    private Context context;

    private ProductCursorHelper helper;
    private ProductUiHelper productColor;
    private TagDao tagDao;

    private SparseIntArray tagColorCache = new SparseIntArray();

    public ProductItemAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
        this.context = context;
        // Init Dao
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
        ProductDao productDao = app.getDaoSession().getProductDao();
        tagDao = app.getDaoSession().getTagDao();
        // Init Helper
        this.productColor = new ProductUiHelper(context);
        helper = productDao.getCursorHelper(c);
    }

    private int getTagColor(long tagId) {
         int color = tagColorCache.get((int) tagId, Integer.MIN_VALUE);
        if (Integer.MIN_VALUE == color) {
            Tag tag = tagDao.load(tagId);
            if (tag!=null) { 
                tagColorCache.put((int)tagId,  tag.getColor() );
             } else {
                tagColorCache.put((int)tagId, android.R.color.transparent);
            }
        }
        return color;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (helper.isNotInit) {
            helper.initWrapper(cursor);
        }
        // Name
        ViewHolder holder = (ViewHolder) view.getTag();
        // Price
        holder.nameText.setText(helper.getName(cursor));
        String priceText = PriceHelper.getToStringPrice(helper.getPriceHT(cursor));
        holder.priceText.setText(priceText);
        // helper.setTextOfferName(holder.nameText, cursor)//
        // .setTextOfferPrice(holder.priceText, cursor);
        // Bg color
        long tagId = helper.getTagId(cursor);
        int tagColor = getTagColor(tagId);
        Drawable grad = productColor.getStateGradientDrawable(tagColor);
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
