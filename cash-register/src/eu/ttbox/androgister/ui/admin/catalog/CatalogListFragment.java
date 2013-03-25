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
import eu.ttbox.androgister.domain.CatalogDao.Properties;
import eu.ttbox.androgister.ui.core.crud.EntityLazyListFragment;

public class CatalogListFragment extends EntityLazyListFragment<Catalog, CatalogDao> {

    private static final String TAG = "CatalogListFragment";

    public static final int PRODUCT_EDIT_REQUEST_CODE = 111;

    // Binding
    ListView listView;

    // ===========================================================
    // Constructor
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_calatog_list, container, false);
        // Binding
        listView = (ListView) v.findViewById(R.id.calalog_list);

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
    public void onEntityClick(Long id) {
        // TODO Auto-generated method stub

    }
}