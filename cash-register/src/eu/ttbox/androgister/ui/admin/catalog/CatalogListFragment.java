package eu.ttbox.androgister.ui.admin.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Catalog;
import eu.ttbox.androgister.domain.CatalogDao;
import eu.ttbox.androgister.domain.Tag;
import eu.ttbox.androgister.domain.CatalogDao.Properties;
import eu.ttbox.androgister.ui.core.crud.EntityLazyListFragment;

public class CatalogListFragment extends EntityLazyListFragment<Catalog, CatalogDao> {

    private static final String TAG = "CatalogListFragment";

    public static final int PRODUCT_EDIT_REQUEST_CODE = 111;

    private static final Long UNSET_ID = Long.MIN_VALUE;
    private static final Long ADD_ID = Long.MAX_VALUE;

    // Binding
    ListView listView;

    // Listener

    private OnSelectCatalogListener onSelectCatalogListener;

    // ===========================================================
    // Listener
    // ===========================================================

    public void setOnSelectCatalogListener(OnSelectCatalogListener onSelectCatalogListener) {
        this.onSelectCatalogListener = onSelectCatalogListener;
    }

    public interface OnSelectCatalogListener {

        void onSelectCalalogId(Long catalogId);

    }

    // ===========================================================
    // Constructor
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_calatog_list, container, false);
        // Binding
        listView = (ListView) v.findViewById(R.id.calalog_list);
     // ListView Header
        View listViewHeader = inflater.inflate(R.layout.admin_calatog_list_item, container, false);
        Catalog headerData = new Catalog();
        headerData.setId(UNSET_ID);
        headerData.setName("All");
        listView.addHeaderView(listViewHeader, headerData, true);
        // ListView Footer
        View listViewFooter = inflater.inflate(R.layout.admin_footer_list_item_add, container, false);
        Catalog footerData = new Catalog();
        footerData.setId(ADD_ID);
        footerData.setName("Add");
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
    public CatalogDao getEntityDao() {
        return getDaoSession().getCatalogDao();
    }

    @Override
    public QueryBuilder<Catalog> createSearchQuery(CatalogDao entityDao) {
        QueryBuilder<Catalog> query = entityDao.queryBuilder() //
                .orderAsc(Properties.Name); //
        return query;
    }

    @Override
    public CatalogListAdapter createListAdapter(LazyList<Catalog> lazyList) {
        return new CatalogListAdapter(getActivity(), lazyList);
    }

    // ===========================================================
    // Action
    // ===========================================================

    @Override
    public void onEntityClick(Long catalogId) {
        if (onSelectCatalogListener != null) {
            onSelectCatalogListener.onSelectCalalogId(  catalogId);
        } 
    }

    public void onSelectProductId(Long[] productIds) {
        // TODO Auto-generated method stub
        
    }
}
