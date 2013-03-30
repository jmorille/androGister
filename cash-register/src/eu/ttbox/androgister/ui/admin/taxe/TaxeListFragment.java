package eu.ttbox.androgister.ui.admin.taxe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Taxe;
import eu.ttbox.androgister.domain.TaxeDao;
import eu.ttbox.androgister.domain.TaxeDao.Properties;
import eu.ttbox.androgister.ui.core.crud.EntityLazyListFragment;

public class TaxeListFragment extends EntityLazyListFragment<Taxe, TaxeDao> {

    private static final String TAG = "TaxeListFragment";

    public static final int PRODUCT_EDIT_REQUEST_CODE = 111;

    private static final Long UNSET_ID = Long.MIN_VALUE;
    private static final Long ADD_ID = Long.MAX_VALUE;

    // Binding
    ListView listView;

    // Listener

    private OnSelectTaxeListener mOnSelectTaxeListener;

    // ===========================================================
    // Listener
    // ===========================================================

    public void setOnSelectTaxeListener(OnSelectTaxeListener onSelectTaxeListener) {
        this.mOnSelectTaxeListener = onSelectTaxeListener;
    }

    public interface OnSelectTaxeListener {

        void onSelectTaxeId(Long tagId);

    }

    // ===========================================================
    // Constructor
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_calatog_list, container, false);
        // Binding
        listView = (ListView) v.findViewById(R.id.calalog_list);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Taxe item = (Taxe) parent.getItemAtPosition(position);
                Long entityId = item != null ? item.getId() : null;
                if (item != null && !UNSET_ID.equals(entityId) && !ADD_ID.equals(entityId)) {
                    onEntityEditClick(item.getId());
                    return true;
                } else {
                    return false;
                }
            }
        });

        // ListView Header
        View listViewHeader = inflater.inflate(R.layout.admin_calatog_list_item, container, false);
        Taxe headerData = new Taxe();
        headerData.setId(UNSET_ID);
        String allLabel = getString(R.string.all);
        headerData.setTitle(allLabel);
        ((TextView) listViewHeader).setText(allLabel);
        listView.addHeaderView(listViewHeader, headerData, true);
        // ListView Footer
        View listViewFooter = inflater.inflate(R.layout.admin_footer_list_item_add, container, false);
        Taxe footerData = new Taxe();
        footerData.setId(ADD_ID);
        String addLabel = getString(R.string.add);
        footerData.setTitle(addLabel);
        listView.addFooterView(listViewFooter, footerData, true);
        return v;
    }

    public AdapterView<ListAdapter> getAdapterContainer() {
        return listView;
    }

    // ===========================================================
    // Service
    // ===========================================================

    @Override
    public TaxeDao getEntityDao() {
        return getDaoSession().getTaxeDao();
    }

    @Override
    public QueryBuilder<Taxe> createSearchQuery(TaxeDao entityDao) {
        QueryBuilder<Taxe> query = entityDao.queryBuilder() //
                .orderAsc(Properties.Title); //
        return query;
    }

    @Override
    public TaxeListAdapter createListAdapter(LazyList<Taxe> lazyList) {
        return new TaxeListAdapter(getActivity(), lazyList);
    }

    // ===========================================================
    // Action
    // ===========================================================

    public void onEntityEditClick(Long entityId) {
        if (!UNSET_ID.equals(entityId) && !ADD_ID.equals(entityId)) {
            Intent intent = new Intent(getActivity(), TaxeEditActivity.class);
            intent.setAction(Intent.ACTION_EDIT);
            intent.putExtra(Intent.EXTRA_UID, entityId);
            startActivityForResult(intent, ENTITY_EDIT_REQUEST_CODE);
        }
    }

    @Override
    public void onEntityClick(Long selectTaxeId) {
        Log.d(TAG, "onSelectTaxeId : " + selectTaxeId + " / with listener = " + (mOnSelectTaxeListener != null));
        if (ADD_ID.equals(selectTaxeId)) {
            // Add
            Intent intent = new Intent(getActivity(), TaxeEditActivity.class);
            intent.setAction(Intent.ACTION_EDIT);
            // intent.putExtra(Intent.EXTRA_UID, entityId);
            startActivityForResult(intent, ENTITY_EDIT_REQUEST_CODE);
        } else if (mOnSelectTaxeListener != null) {
            Long entityId = selectTaxeId;
            if (UNSET_ID.equals(entityId)) {
                entityId = null;
            }
            mOnSelectTaxeListener.onSelectTaxeId(entityId);
        }

    }
    

  
}
