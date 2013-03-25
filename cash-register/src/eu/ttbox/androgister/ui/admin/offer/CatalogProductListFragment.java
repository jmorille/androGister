package eu.ttbox.androgister.ui.admin.offer;

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
import eu.ttbox.androgister.domain.CatalogProduct;
import eu.ttbox.androgister.domain.CatalogProductDao;
import eu.ttbox.androgister.domain.CatalogProductDao.Properties;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.ui.core.crud.EntityLazyListFragment;

public class CatalogProductListFragment extends   EntityLazyListFragment<CatalogProduct,CatalogProductDao>  {

    
    private ListView listView;

    // ===========================================================
    // Constructor
    // ===========================================================


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
        View v =  inflater.inflate(R.layout.admin_offer_creator, container, false);
        // Binding
        listView = (ListView)v.findViewById(R.id.calalog_product_list);
        return v;
    }



    @Override
    public AdapterView<ListAdapter> getAdapterContainer() { 
        return listView;
    }


    // ===========================================================
    // Service
    // ===========================================================

    @Override
    public CatalogProductDao getEntityDao() { 
        return getDaoSession().getCatalogProductDao();
    }

    @Override
    public LazyListAdapter<CatalogProduct, ? extends Object> createListAdapter(LazyList<CatalogProduct> lazyList) { 
        return new CatalogProductListAdapter(getActivity(), lazyList);
    }





    @Override
    public QueryBuilder<CatalogProduct> createSearchQuery(CatalogProductDao entityDao) {
        QueryBuilder<CatalogProduct> query = entityDao.queryBuilder() //
               
//                .orderAsc(Properties.Name); //
                ;
//        query.
      
        return query;
    }




    // ===========================================================
    // Action
    // ===========================================================

    
    @Override
    public void onEntityClick(Long id) {
        // TODO Auto-generated method stub
        
    }

    public void onSelectCalalogId(Long catalogId) {
        // TODO Auto-generated method stub
        
    }






    
}